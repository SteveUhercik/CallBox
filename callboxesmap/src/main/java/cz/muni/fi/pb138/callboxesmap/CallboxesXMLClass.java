package cz.muni.fi.pb138.callboxesmap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

 
public class CallboxesXMLClass {
    
  private Document doc;
  private final char separatorChar = File.separatorChar; //system separator
  public static final String CHARACTER_ENCODING = "UTF-8";
  private Map<String, String> childParentPairs;
  private static final String CALLBOX = "callbox";
 
  public CallboxesXMLClass() {
    try {
        String parentDir = (new File(CallboxesXMLClass.class.getProtectionDomain().getCodeSource()  //universal relative path
                .getLocation().getPath()).getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getParentFile().getPath());
	File fXmlFile;
        fXmlFile = new File(parentDir + separatorChar + "data" + separatorChar + "callBoxesWithGeocoding.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        this.doc = dBuilder.parse(fXmlFile);
	doc.getDocumentElement().normalize();
 
    } catch (Exception e) {
	e.printStackTrace();
    }
    childParentPairs = new HashMap<String, String>();
    childParentPairs.put("district", "region");
    childParentPairs.put("municipality", "district");
    childParentPairs.put("part", "municipality");
    childParentPairs.put("street", "part");
  }
  
  public Collection<String> getChildOptions(String childTag, String parentTextContent) {
    Set<String> childOptions = new HashSet<String>();         
    NodeList el =  this.doc.getElementsByTagName(CALLBOX);   
    String parentTag = childParentPairs.get(childTag);
    for (int i = 0; i < el.getLength(); i++) {
        Element callbox = (Element) el.item(i);
        Element child = (Element) callbox.getElementsByTagName(childTag).item(0);
        Element parent = (Element) callbox.getElementsByTagName(parentTag).item(0);
        if(parentTextContent.equalsIgnoreCase(parent.getTextContent())){
            childOptions.add(child.getTextContent());
        }
    }
    return childOptions;
  }
  
   public Collection<String> getRegions() {
    Set<String> regions = new HashSet<String>();         
    NodeList el =  this.doc.getElementsByTagName(CALLBOX);      
    for (int i = 0; i < el.getLength(); i++) {
        Element callbox = (Element) el.item(i);
        Element region = (Element) callbox.getElementsByTagName("region").item(0);
        regions.add(region.getTextContent());
    }
    return regions;
   }
   
   public String callboxesByArea(String areaType, String area){
        
        StringBuilder buffer = new StringBuilder();
        
        NodeList el =  this.doc.getElementsByTagName(CALLBOX);
        for(int i=0; i<el.getLength(); i++){
            Element callbox = (Element) el.item(i);
            NodeList nodeList = callbox.getElementsByTagName(areaType);
            if (nodeList == null || nodeList.getLength() == 0) {
              continue;
            }
            Element searched = (Element) nodeList.item(0);
            if(searched.getTextContent() != null && area.equals(searched.getTextContent())){
                Element location = (Element) callbox.getElementsByTagName("location").item(0);
                Element latitude = (Element) location.getElementsByTagName("lat").item(0);
                Element longitude = (Element) location.getElementsByTagName("lng").item(0);
                buffer.append(Double.parseDouble(latitude.getTextContent()));
                buffer.append(",");
                buffer.append(Double.parseDouble(longitude.getTextContent()));
                if(i!=el.getLength()-1){
                    buffer.append(";");
                }
            }
        }
        return buffer.toString();
    }  
   
/**
 * Returns locations of all callboxes within specified radius from specified point 
 * @param lat point lattitude (String in degrees decimal format, e.g. "49.2354")
 * @param lng point longitude (String in degrees decimal format, e.g. "16.05534")
 * @param diameter radius in meters (String in decimal format, e.g. "1000.0")
 * @return 
 */
public String nearestCallBoxes(String lat, String lng, String diameter){
    final double latDegree = 111221.9; //one degree of latitude is 111221.9 meters 
    final double lngDegree = 71930.5; //one degree of longitude is 71930.5 meters
    double pointLat = Double.parseDouble(lat);
    double pointLng = Double.parseDouble(lng);
    double pointDiameter = Double.parseDouble(diameter);
    StringBuilder builder = new StringBuilder();
    NodeList el = this.doc.getElementsByTagName(CALLBOX);
    for (int i = 0; i < el.getLength(); i++){
        Element callBox = (Element) el.item(i);
        double boxLat = Double.parseDouble(callBox.getElementsByTagName("lat").item(0).getTextContent());
        double boxLng = Double.parseDouble(callBox.getElementsByTagName("lng").item(0).getTextContent());
        double latMeterDifference = (Math.abs(boxLat - pointLat)) * latDegree;
        double lngMeterDifference = (Math.abs(boxLng - pointLng)) * lngDegree;
        double distance = Math.sqrt(latMeterDifference*latMeterDifference + lngMeterDifference*lngMeterDifference);  
        if (distance <= pointDiameter){
            builder.append(boxLat).append(",").append(boxLng).append(";");
        }
    }
    return builder.toString();
  }
  
  public Document getDocument() {
    return doc;
  }
   
}