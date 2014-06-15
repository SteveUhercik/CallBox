package cz.muni.fi.pb138.callboxesmap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan Uhercik (stefan.uhercik@ibacz.eu)
 * @see javax.servlet.http.HttpServlet
 */
public class AjaxServlet extends HttpServlet {
    
    private final CallboxesXMLClass callboxes = new CallboxesXMLClass();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding(CallboxesXMLClass.CHARACTER_ENCODING);
        req.setCharacterEncoding(CallboxesXMLClass.CHARACTER_ENCODING);
        PrintWriter writer = resp.getWriter();
        String areaType = req.getParameter("area-type");
        String selected = req.getParameter("selected");
        writer.append(constructCombobox(areaType, selected));
    }
    
    /**
   * Returns a string of code for constructing HTML combobox
   * @param areaType area type of options (e.g "municipality")
   * @param parent value of parent area type (e.g "Brno")
   * @return 
   */
    private String constructCombobox(String areaType, String parent) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<select class='area-type' id='");
        buffer.append(areaType);
        buffer.append("'>");
        
        Collection<String> options = getOptions(areaType, parent);
        buffer.append("<option>-- Select ");
        buffer.append(areaType);
        buffer.append(" --</option>");
        for (String option : options) {
            buffer.append("<option>");
            buffer.append(option);
            buffer.append("</option>");
        }
        buffer.append("</select>");
        return buffer.toString();
    }
    
   /**
   * Returns collection of options in current area type with specific parent
   * @param areaType area type of options (e.g "municipality")
   * @param parent value of parent area type (e.g "Brno")
   * @return 
   */
    private Collection<String> getOptions(String areaType, String parent) {
        if (areaType.equals("region")) {
            return this.callboxes.getRegions();
        } else {
            return this.callboxes.getChildOptions(areaType, parent);
        } 
    }

}
