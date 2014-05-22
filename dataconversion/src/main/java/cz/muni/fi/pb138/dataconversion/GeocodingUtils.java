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
 * Geocode adresses using google geocoding api
 * 
 * @author Eduard Tomek
 */
public class GeocodingUtils {
  public static Logger log = Logger.getLogger(GeocodingUtils.class.getSimpleName());

  public static final String GOOGLE_GEOCODE_URL = "http://maps.googleapis.com/maps/api/geocode/";
  public static final String GOOGLE_GEOCODE_URL_ADDRESS = "?&sensor=false&address=";
  public static final String RESPONSE_TYPE = "xml";
  public static final String LINE_SEPARATOR = "\n";
  
  public static final String XSD_FILE_NAME = "callBoxesWithGeolocation.xsd";

  public static final String CALLBOXES = "callboxes";
  public static final String CALLBOX = "callbox";
  
  public static final int ARGS_INPUT_XML_PATH = 0;
  public static final int ARGS_OUTPUT_XML_PATH = 1;
  public static final int ARGS_OUTPUT_XSD_PATH = 2;
  public static final int ARGS_START_IDX = 3;
  public static final int ARGS_END_IDX = 4;

  /**
   * Geocode callboxes
   * 
   * @param inputCallBoxes document with callboxes
   * @param startElementIndex determines at which elements index will geocoding start
   * @param endElementIndex determines at which elements index will geocoding end
   * @return geocoded document
   * @throws ParserConfigurationException 
   */
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
        log.log(Level.SEVERE, "Cannot geocode " + i + "th element. Skipping."); 
      }
      else {
        result.adoptNode(geocodedCallBox);
        newCallBoxes.appendChild(geocodedCallBox);
      }
    }
    
    return result;
  }
  
  /**
   *  Create full url for http request to google geocoding
   * @param callBox single callbox element
   * @return full string url
   */
  private static String getFullUrl(Element callBox) {
    String fullUrl = GOOGLE_GEOCODE_URL + RESPONSE_TYPE + GOOGLE_GEOCODE_URL_ADDRESS + getUrlParameters(callBox);
    fullUrl = fullUrl.replaceAll(" ", "+");
    return normalizeToAscii(fullUrl);
  }

  /**
   * Geocode single callbox
   * 
   * @param outputDocument output document
   * @param callBox callbox to be geocoded
   * @return geocoded callbox
   */
  private static Element geocodeCallBox(Document outputDocument, Element callBox) {
    String fullUrl = getFullUrl(callBox);
    log.log(Level.INFO, fullUrl);
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

    } catch (IOException | ParserConfigurationException | SAXException ex) {
      log.log(Level.SEVERE, "Exception raised while processing url " + fullUrl, ex);
    } finally {
      try {
        is.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    return callBox;
  }

  /**
   * Normalize string (get rid of non ASCII characters and replace them by their ASCII equivalents)
   * @param input input string
   * @return normalized string
   */
  private static String normalizeToAscii(String input) {
    return Normalizer
            .normalize(input, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "");
  }

  /**
   * Get parameters part of url
   * @param callBox callbox
   * @return url parameters string created by callbox
   */
  private static String getUrlParameters(Element callBox) {
    String result = "";

    // get district text content
    NodeList districtNodeList = callBox.getElementsByTagName("district");
    Element districtEl = (Element) districtNodeList.item(0);
    String district = null;
    if (districtEl != null) {
      district = districtEl.getTextContent();
    }
    //get municipality text content
    NodeList municipalityNodeList = callBox.getElementsByTagName("municipality");
    Element municipalityEl = (Element) municipalityNodeList.item(0);
    String municipality = null;
    if (municipalityEl != null) {
      municipality = municipalityEl.getTextContent();
    }
    //get street text content
    NodeList streetNodeList = callBox.getElementsByTagName("street");
    Element streetEl = (Element) streetNodeList.item(0);
    String street = null;
    if (streetEl != null) {
      street = streetEl.getTextContent();
    }

    //create result (district+municipality+street)
    boolean addPlus = false;
    if (district != null && !district.isEmpty()) {
      result += district;
      addPlus = true;
    }
    if (municipality != null & !municipality.isEmpty()) {
      if (addPlus) {
        result += "+";
      }
      result += municipality;
      addPlus = true;
    }
    if (street != null && !street.isEmpty()) {
      if (addPlus) {
        result += "+";
      }
      result += street;
      addPlus = true;
    }
    
    return result;
  }

  /**
   * Creates new document 
   * @param xsdFilePath XSD schema of document
   * @return new document
   * @throws ParserConfigurationException if a DocumentBuilder cannot be created
   */
  private static Document generateNewDocument(String xsdFilePath) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.newDocument();
    Element rootElement = document.createElement(CALLBOXES);
    rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation", xsdFilePath);
    document.appendChild(rootElement);
    return document;
  }
  
  /**
   * Validate XML by XSD
   * 
   * @param xmlFilePath XML file path 
   * @param xsdFilePath XSD file path
   * @throws SAXException if SAX error ocurs during parsing
   * @throws IOException files dont exist or other IO problem
   */
  private static void validateCallBoxesWithGeocoding(String xmlFilePath, String xsdFilePath) throws SAXException, IOException {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema;
    schema = factory.newSchema(new File(xsdFilePath));
    Validator validator = schema.newValidator();
    validator.validate(new StreamSource(new File(xmlFilePath)));
  }

  /**
   * Main method
   * 
   * @param args arguments
   */
  public static void main(String[] args) {
    //args input xml location, output xml location, output xsd schema, int start, int end
    if (args.length != 5) {
      System.err.println("Incorrect number of arguments. Arguments: inputXmlPath "
              + "outputXmlPath outputXmlXsd startIndex endIndex.\n"
              + "NOTE: Google allows only 2500 requests per day, \n"
              + "difference between startIndex and endIndex shouldnt be greater than this limit");
    }
    
    try {
      Document inputDocument = FileUtils.loadXmlFromFile(args[ARGS_INPUT_XML_PATH]);
      Document outputDocument = 
              geocodeCallBoxes(inputDocument, Integer.valueOf(args[ARGS_START_IDX]), Integer.valueOf(args[ARGS_END_IDX]));
      
      if (outputDocument.getElementsByTagName(CALLBOXES).item(0).hasChildNodes()) {
        FileUtils.serializetoXML(args[ARGS_OUTPUT_XML_PATH], outputDocument);
        validateCallBoxesWithGeocoding(args[ARGS_OUTPUT_XML_PATH], args[ARGS_OUTPUT_XSD_PATH]);
      }
    } catch (IOException | ParserConfigurationException | SAXException | TransformerException ex) {
      ex.printStackTrace();
    }
  }
}
