/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pb138.dataconversion;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.xml.sax.SAXException;

/**
 *
 * @author Eduard Tomek
 */
public class FileUtils {
    public static String getFileEncoding(File file) throws IOException {
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
    
 /*
  * Generates .xml file from document in DOM format and writes it into output file.
  */
  public static void serializetoXML(String fileName, Document document) throws IOException,
          TransformerConfigurationException, TransformerException {
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    DOMSource source = new DOMSource(document);
    StreamResult result = new StreamResult(new FileOutputStream(fileName));
    transformer.transform(source, result);
  }
  
  public static Document loadXmlFromFile(String fileName) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new File(fileName));    
  }
  
}
