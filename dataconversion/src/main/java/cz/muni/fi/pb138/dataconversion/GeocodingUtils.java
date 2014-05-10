package cz.muni.fi.pb138.dataconversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Eduard Tomek
 */
public class GeocodingUtils {
  
  public static final String GOOGLE_GEOCODE_URL = "http://maps.googleapis.com/maps/api/geocode/";
  public static final String GOOGLE_GEOCODE_URL_ADDRESS = "?&sensor=false&address=";
  public static final String RESPONSE_TYPE = "xml";
  
  public static Document geocodeCallBoxes(Document callBoxes) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document geocodedCallBoxes = builder.newDocument();
    NodeList callBoxesNodeList = callBoxes.getElementsByTagName("callbox");
    
    //todo omezeno kvuli vykonu v dobe vyvoje, google take omezuje na 2500 dotazu na den
    //for (int i = 0; i < callboxes.getLength(); i++) {
    for (int i = 0; i < 1; i++) {
      Element callBox = (Element) callBoxesNodeList.item(i);
      geocodeCallBox(callBox);
    }
    
    
    return geocodedCallBoxes;
  }

  //todo prozkoumat moznosti pouziti parametru components
  private static String geocodeCallBox(Element callBox) {
    String fullUrl = GOOGLE_GEOCODE_URL + RESPONSE_TYPE +GOOGLE_GEOCODE_URL_ADDRESS + getUrlParameters(callBox);
    fullUrl = fullUrl.replaceAll(" ", "+");
    fullUrl = normalizeToAscii(fullUrl);
    System.out.println(fullUrl);
    BufferedReader br = null;
    try {
      URL url = new URL(fullUrl);
      InputStream is = url.openStream();
      br = new BufferedReader(new InputStreamReader(is));
      String line = null;
      while ((line = br.readLine()) != null) {
        //todo
        System.out.println(line);
      }
    } catch (MalformedURLException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        br.close();
      } catch (IOException ex) {
        Logger.getLogger(GeocodingUtils.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return  "";
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
} 
