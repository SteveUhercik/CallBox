package cz.muni.fi.pb138.callboxesmap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

 
public class CallboxesXMLClass {
    
  private Document doc;
  private final char separatorChar = File.separatorChar; //system separator
  public static final String CHARACTER_ENCODING = "UTF-8";
 
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
  }
  
   public ArrayList<String> getRegions() {
      
    Set<String> regions = new HashSet<String>();         
    NodeList el =  this.doc.getElementsByTagName("callbox");      
    for (int i = 0; i < el.getLength(); i++) {
        Element callbox = (Element) el.item(i);
        Element region = (Element) callbox.getElementsByTagName("region").item(0);
        regions.add(region.getTextContent());
    }
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(regions);
    return list;
   }
   
   public ArrayList<String> getDistricts(String parent) {
      
    Set<String> districts = new HashSet<String>();         
    NodeList el =  this.doc.getElementsByTagName("callbox");      
    for (int i = 0; i < el.getLength(); i++) {
        Element callbox = (Element) el.item(i);
        Element region = (Element) callbox.getElementsByTagName("region").item(0);
        Element district = (Element) callbox.getElementsByTagName("district").item(0);
        
        System.out.println(parent + "-> parent");
        System.out.println(region.getTextContent() + "-> region");
        
        
        if(parent.equalsIgnoreCase(region.getTextContent())){
            
            districts.add(district.getTextContent());
        }
    }
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(districts);
    return list;
   }
   
   public ArrayList<String> getMunicipalities(String parent) {
      
    Set<String> municipalities = new HashSet<String>();         
    NodeList el =  this.doc.getElementsByTagName("callbox");      
    for (int i = 0; i < el.getLength(); i++) {
        Element callbox = (Element) el.item(i);
        Element municipality = (Element) callbox.getElementsByTagName("municipality").item(0);
        Element district = (Element) callbox.getElementsByTagName("district").item(0);
        if(parent.equalsIgnoreCase(district.getTextContent())){
            municipalities.add(municipality.getTextContent());
        }
    }
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(municipalities);
    return list;
   }
    
   public ArrayList<String> getParts(String parent) {
      
    Set<String> parts = new HashSet<String>();         
    NodeList el =  this.doc.getElementsByTagName("callbox");      
    for (int i = 0; i < el.getLength(); i++) {
        Element callbox = (Element) el.item(i);
        Element municipality = (Element) callbox.getElementsByTagName("municipality").item(0);
        Element part = (Element) callbox.getElementsByTagName("part").item(0);
        if(parent.equalsIgnoreCase(municipality.getTextContent())){
            parts.add(part.getTextContent());
        }
    }
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(parts);
    return list;
   }
   
   public ArrayList<String> getStreets(String parent) {
      
    Set<String> streets = new HashSet<String>();         
    NodeList el =  this.doc.getElementsByTagName("callbox");      
    for (int i = 0; i < el.getLength(); i++) {
        Element callbox = (Element) el.item(i);
        Element street = (Element) callbox.getElementsByTagName("street").item(0);
        Element part = (Element) callbox.getElementsByTagName("part").item(0);
        if(parent.equalsIgnoreCase(street.getTextContent())){
            streets.add(street.getTextContent());
        }
    }
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(streets);
    return list;
   }
   
   public String callboxesByArea(String areaType, String area){
        
        
        StringBuilder buffer = new StringBuilder();
        
        NodeList el =  this.doc.getElementsByTagName("callbox");
        for(int i=0; i<el.getLength(); i++){
            Element callbox = (Element) el.item(i);
            Element searched = (Element) callbox.getElementsByTagName(areaType).item(0);
            
            if(area.equals(searched.getTextContent())){
                Element location = (Element) searched.getElementsByTagName("location").item(0);
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
        
        System.out.println(buffer.toString());
        
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
    NodeList el = this.doc.getElementsByTagName("callbox");
    for (int i = 0; i < el.getLength(); i++){
        Element callBox = (Element) el.item(i);
        double boxLat = Double.parseDouble(callBox.getElementsByTagName("lat").item(0).getTextContent());
        double boxLng = Double.parseDouble(callBox.getElementsByTagName("lng").item(0).getTextContent());
        double latMeterDifference = (Math.abs(boxLat - pointLat)) * latDegree;
        double lngMeterDifference = (Math.abs(boxLng - pointLng)) * lngDegree;
        double distance = Math.sqrt(latMeterDifference*latMeterDifference + lngMeterDifference*lngMeterDifference);  
        if (distance <= pointDiameter){
            System.out.println(distance);
            System.out.println(callBox.getElementsByTagName("municipality").item(0).getTextContent());
            builder.append(boxLat).append(",").append(boxLng).append(";");
        }
    }
    System.out.println("out: " + builder.toString());
    return builder.toString();
  }
  
  public Document getDocument() {
    return doc;
  }
   
}