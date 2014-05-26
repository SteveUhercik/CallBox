package cz.muni.fi.pb138.callboxesmap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan Uhercik (stefan.uhercik@ibacz.eu)
 */
public class AjaxServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        String areaType = req.getParameter("area-type");
        writer.append(constructCombobox(areaType));
    }

    private String constructCombobox(String areaType) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<select class='area-type' id='");
        buffer.append(areaType);
        buffer.append("'>");
        List<String> options = getFakeOptions(areaType);
        System.out.println("options count is:"+options.size());
        for (String option : options) {
            buffer.append("<option>");
            buffer.append(option);
            buffer.append("</option>");
        }
        buffer.append("</select>");
        return buffer.toString();
    }

    private List<String> getFakeOptions(String areaType) {
        if (areaType.equals("district")) {
            return new ArrayList<String>() {
                {
                    add("Vysocina");
                    add("Jihomoravsk");
                    add("Slezk");
                }
            };
        } else if (areaType.equals("department")) {
            return new ArrayList<String>() {
                {
                    add("Brno");
                    add("Praha");
                    add("Ostrava");
                }
            };
        } else if (areaType.equals("city")) {
            return new ArrayList<String>() {
                {
                    add("Brno");
                    add("Praha");
                    add("Ostrava");
                }
            };
        } else if (areaType.equals("ward")) {
            return new ArrayList<String>() {
                {
                    add("Brno - stred");
                    add("Bohunice");
                    add("Stary liskovec");
                    add("Kralovo pole");
                }
            };
        } else{
            return new ArrayList<String>() {
                {
                    add("Hrncirska");
                    add("Masarykova");
                    add("Benesova");
                    add("Husitska");
                }
            };
        }
    }

}
