package cz.muni.fi.pb138.callboxesmap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

 
public class CallboxesXMLClass {
    
  private Document doc;
 
  public CallboxesXMLClass() {
    try {
 
	File fXmlFile;
        fXmlFile = new File("C:\\Users\\Tomáš\\Documents\\GitHub\\CallBox\\data\\callBoxListWithGeocoding_0-2400.xml");
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
        //if(parent == region.getTextContent()){
            districts.add(district.getTextContent());
        //}
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
        //if(parent.equalsIgnoreCase(district.getTextContent())){
            municipalities.add(municipality.getTextContent());
        //}
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
        //if(parent.equalsIgnoreCase(municipality.getTextContent())){
            parts.add(part.getTextContent());
        //}
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
        //if(parent.equalsIgnoreCase(street.getTextContent())){
            streets.add(street.getTextContent());
        //}
    }
    ArrayList<String> list = new ArrayList<String>();
    list.addAll(streets);
    return list;
   }
   
   public String generateGps(String areaType, String area){
        
        
        StringBuffer buffer = new StringBuffer();
        
        NodeList el =  this.doc.getElementsByTagName("callbox");
        for(int i=0; i<el.getLength(); i++){
            Element callbox = (Element) el.item(i);
            Element searched = (Element) callbox.getElementsByTagName(areaType).item(0);
            
            if(area == searched.getTextContent()){
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
   
}