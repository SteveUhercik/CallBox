package cz.muni.fi.pb138.dataconversion;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/** Class performs translation from input .csv file to .xml file with 
 * defined structure. Basic filtering is performed, so that most 
 * common .csv file format errors will not propagate into .xml errors.
 *
 * @author Adam Krejci
 */
public class CsvToXml {
  
  public static final String CALLBOXES_XSD_FILE_LOCATION = "../data/callboxes.xsd";

    /**
     * @param args the command line arguments
     */
    private Document document;

    /**
     * Generates new document containin an empty root element "callboxes"
     * @throws ParserConfigurationException 
     */
    private void generateNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.newDocument();
        Element rootElement = document.createElement("callboxes");
        rootElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation", CALLBOXES_XSD_FILE_LOCATION);
        document.appendChild(rootElement);
    }

    /**
     * Adds new callbox into document. id, region, district and municipality
     * are mandatory and can not be null or empty Strings.
     * @param id callbox id. Mandatory non-empty String.
     * @param region callbox location region (e.g. "Jihočeský"). Mandatory non-empty String
     * @param district callbox location district (e.g. "České Budějovice"). Mandatory non-empty String
     * @param municipality callbox location municipality (e.g. "Dříteň"). Mandatory non-empty String
     * @param part callbox location part (e.g. "Dolní dříteň").
     * @param street callbox location street (e.g. "Schodová").
     * @param note callbox location note (e.g. "Naproti Jednotě").
     * @throws NullPointerException if any of id, region district or municipality
     *          is null or an empty String.
     */
    public void addCallBox(String id, String region, String district,
            String municipality, String part, String street,
            String note) {
        if (id == null || region == null || district == null || municipality == null
                || id.trim().equals("") || region.trim().equals("") 
                || district.trim().equals("") || municipality.trim().equals("")) {
            throw new NullPointerException("Error: id, region, disctrict and municipality"
                    + "are mandatory and can't be null or empty String.");
        }
        Element newCallBox = document.createElement("callbox");
        newCallBox.setAttribute("vtaid", id);

        Element regionElement = document.createElement("region");
        regionElement.setTextContent(region);
        newCallBox.appendChild(regionElement);
        Element districtElement = document.createElement("district");
        districtElement.setTextContent(district);
        newCallBox.appendChild(districtElement);
        Element municipalityElement = document.createElement("municipality");
        municipalityElement.setTextContent(municipality);
        newCallBox.appendChild(municipalityElement);
        
        if (part != null && !(part.trim().equals(""))){
            Element partElement = document.createElement("part");
            partElement.setTextContent(part);
            newCallBox.appendChild(partElement);
        }

        if (street != null && !(street.trim().equals(""))) {
            Element streetElement = document.createElement("street");
            streetElement.setTextContent(street);
            newCallBox.appendChild(streetElement);
        }
        if (note != null && !(note.trim().equals(""))) {
            Element noteElement = document.createElement("note");
            noteElement.setTextContent(note);
            newCallBox.appendChild(noteElement);
        }
        Element root = document.getDocumentElement();
        root.appendChild(newCallBox);
    }

    /**
     * Processes a .csv file and parses its contents into document.
     * @param fileName path to .csv file to be parsed
     * @throws IOException when an error while reading .csv file occurs.
     */
    public void processFile(String fileName) throws IOException {
        BufferedReader reader = null;
        File file = new File(fileName);
        
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader r = new InputStreamReader(fileInputStream, getFileEncoding(file));
            reader = new BufferedReader(r);
            String line = null;
            reader.readLine(); //header
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(";");
                if (splitLine.length >= 5 && splitLine.length < 7){
                    String[] newSplitLine = new String[7];
                    System.arraycopy(splitLine, 0, newSplitLine, 0, splitLine.length);
                    splitLine = newSplitLine;
                }
                if (splitLine.length >= 7) {
                    try {
                        addCallBox(splitLine[0], splitLine[1], splitLine[2],
                                splitLine[3], splitLine[4], splitLine[5], splitLine[6]);
                    } catch (NullPointerException e) {
                        System.err.println("Warning: Incorrect line: " + line + " will not be added.");
                    }
                } else {
                    System.err.println("Warning: Incorrect line: " + line + " will not be added.");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                reader.close();
            } catch(NullPointerException e){
                throw new IOException("Error while closing file: " + fileName);
            }
        }
    }
    
    private String getFileEncoding(File file) throws IOException {
      String fileEncoding = null;

      FileInputStream fileInputStream = new FileInputStream(file);
      CharsetDetector cd = new CharsetDetector();
      byte[] fileContent = new byte[(int) file.length()];
      fileInputStream.read(fileContent);
      cd.setText(fileContent);
      CharsetMatch cm = cd.detect();
      fileEncoding = cm.getName();
      fileInputStream.close();

      return fileEncoding;
    }

    /**
     * Constructor. Generates new document with a root element.
     */
    private CsvToXml() throws ParserConfigurationException {
        generateNewDocument();
    }

    /*
     * Generates .xml file from document in DOM format and writes it into
       output file.
     */
    public void serializetoXML(String fileName) throws IOException,
        TransformerConfigurationException, TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new FileOutputStream(fileName));
        transformer.transform(source, result);
    }


    public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException, TransformerException {
        if (args.length < 2){
            throw new IOException("Wrong input. Exactly two arguments are needed. First parameter"
                    + "is path to input .csv file, second is path to resulting .xml file.");
        }
        if (args.length > 2){
            System.err.println("Warning: omitting all expect first two arguments.");
        }
        System.err.println("Reading input file...");
        CsvToXml domTest = new CsvToXml(); 
        domTest.processFile(args[0]);
        System.err.println("Writing output...");
        domTest.serializetoXML(args[1]);
        System.err.println("Ready.");
    }

}
