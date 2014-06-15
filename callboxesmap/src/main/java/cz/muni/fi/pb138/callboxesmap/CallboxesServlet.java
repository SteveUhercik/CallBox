/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pb138.callboxesmap;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

/**
 *
 * @author Eduard Tomek
 * @see javax.servlet.http.HttpServlet
 */
public class CallboxesServlet extends HttpServlet {
    
  private final CallboxesXMLClass callboxes = new CallboxesXMLClass();
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setCharacterEncoding(CallboxesXMLClass.CHARACTER_ENCODING);
    PrintWriter writer = resp.getWriter();
    writer.append(getJsonCallboxes().toString());
  }
  
  private JSONObject getJsonCallboxes() {
    Document document = callboxes.getDocument();
    DOMSource domSource = new DOMSource(document);
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    TransformerFactory tf = TransformerFactory.newInstance();
    try {
      Transformer transformer = tf.newTransformer();
      transformer.transform(domSource, result);
    }
    catch(TransformerException ex) {
       ex.printStackTrace();
    }
    
    JSONObject json = XML.toJSONObject(writer.toString());
    return json;
  }
  
}
