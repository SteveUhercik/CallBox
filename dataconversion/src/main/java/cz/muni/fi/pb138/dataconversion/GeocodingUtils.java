package cz.muni.fi.pb138.dataconversion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Eduard Tomek
 */
public class GeocodingUtils {

  public static final String GOOGLE_GEOCODE_URL = "http://maps.googleapis.com/maps/api/geocode/";
  public static final String GOOGLE_GEOCODE_URL_ADDRESS = "?&sensor=false&address=";
  public static final String RESPONSE_TYPE = "xml";
  public static final String LINE_SEPARATOR = "\n";
  
  public static final String XSD_FILE_NAME = "callBoxesWithGeolocation.xsd";

  public static final String CALLBOXES = "callboxes";
  public static final String CALLBOX = "callbox";

  public static Document geocodeCallBoxes(Document inputCallBoxes, int startElementIndex, 
          int endElementIndex) throws ParserConfigurationException {
    Document result = generateNewDocument(XSD_FILE_NAME);
    NodeList callBoxesNodeList = result.getElementsByTagName(CALLBOXES);
    Element newCallBoxes = (Element) callBoxesNodeList.item(0);

    NodeList inputCallBoxesNodeList = inputCallBoxes.getElementsByTagName("callbox");

    int maxInputCallBoxesIndex = inputCallBoxesNodeList.getLength() - 1;
    if (maxInputCallBoxesIndex < endElementIndex) {
      endElementIndex = maxInputCallBoxesIndex;
    }
    if (startElementIndex < 0) {
      startElementIndex = 0;
    }
    for (int i = startElementIndex; i < endElementIndex; i++) {
      Element callBox = (Element) inputCallBoxesNodeList.item(i);
      Element geocodedCallBox = geocodeCallBox(inputCallBoxes, callBox);
      if (geocodedCallBox == null) {
        Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, "Cannot geocode " + i + "th element. Skipping."); 
      }
      else {
        result.adoptNode(geocodedCallBox);
        newCallBoxes.appendChild(geocodedCallBox);
      }
    }
    
    
    return result;
  }
  
  //todo prozkoumat moznosti pouziti parametru components v url
  private static String getFullUrl(Element callBox) {
    String fullUrl = GOOGLE_GEOCODE_URL + RESPONSE_TYPE + GOOGLE_GEOCODE_URL_ADDRESS + getUrlParameters(callBox);
    fullUrl = fullUrl.replaceAll(" ", "+");
    return normalizeToAscii(fullUrl);
  }

  private static Element geocodeCallBox(Document outputDocument, Element callBox) {
    String fullUrl = getFullUrl(callBox);
    InputStream is = null;
    try {
      URL url = new URL(fullUrl);
      is = url.openStream();
      Document d = DocumentBuilderFactory
              .newInstance()
              .newDocumentBuilder()
              .parse(is);
      NodeList location = d.getElementsByTagName("location");

      Element responseLocationElement = (Element) location.item(0);

      Element newLocation = outputDocument.createElement("location");
      Element newLat = outputDocument.createElement("lat");
      newLat.setTextContent(responseLocationElement.getElementsByTagName("lat").item(0).getTextContent());
      Element newLng = outputDocument.createElement("lng");
      newLng.setTextContent(responseLocationElement.getElementsByTagName("lng").item(0).getTextContent());
      newLocation.appendChild(newLat);
      newLocation.appendChild(newLng);
      callBox.appendChild(newLocation);

    } catch (MalformedURLException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        is.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return callBox;
  }

  private static String normalizeToAscii(String input) {
    return Normalizer
            .normalize(input, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "");
  }

  private static String getUrlParameters(Element callBox) {
    String result = "";
    NodeList districtNodeList = callBox.getElementsByTagName("district");
    Element districtEl = (Element) districtNodeList.item(0);
    String district = null;
    if (districtEl != null) {
      district = districtEl.getTextContent();
    }
    NodeList streetNodeList = callBox.getElementsByTagName("street");
    Element streetEl = (Element) streetNodeList.item(0);
    String street = null;
    if (streetEl != null) {
      street = streetEl.getTextContent();
    }

    boolean addPlus = false;
    if (street != null && !street.isEmpty()) {
      result += street;
      addPlus = true;
    }
    if (district != null && !district.isEmpty()) {
      if (addPlus) {
        result += "+";
      }
      result += district;
    }

    return result;
  }

  private static Document generateNewDocument(String xsdFileLocation) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.newDocument();
    Element rootElement = document.createElement(CALLBOXES);
    rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation", xsdFileLocation);
    document.appendChild(rootElement);
    return document;
  }
  
  private static void validateCallBoxesWithGeocoding(String xmlFileLocation, String xsdFileLocation) throws SAXException, IOException {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema;
    schema = factory.newSchema(new File(xsdFileLocation));
    Validator validator = schema.newValidator();
    validator.validate(new StreamSource(new File(xmlFileLocation)));
  }

  public static void main(String[] args) {
    //args input xml location, output xml location, output xsd schema, int start, int end
    if (args.length != 5) {
      System.err.println("Incorrect number of arguments. Arguments: inputXmlPath "
              + "outputXmlPath outputXmlXsd startIndex endIndex.\n"
              + "NOTE: Google allows only 2500 requests per day, \n"
              + "difference between startIndex and endIndex shouldnt be greater than this limit");
    }
    
    try {
      Document inputDocument = FileUtils.loadXmlFromFile(args[0]);
      Document outputDocument = 
              geocodeCallBoxes(inputDocument, Integer.valueOf(args[3]), Integer.valueOf(args[4]));
      
      if (outputDocument.getElementsByTagName(CALLBOXES).item(0).hasChildNodes()) {
        FileUtils.serializetoXML(args[1], outputDocument);
        validateCallBoxesWithGeocoding(args[1], args[2]);
      }
    } catch (IOException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ParserConfigurationException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SAXException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } catch (TransformerException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
