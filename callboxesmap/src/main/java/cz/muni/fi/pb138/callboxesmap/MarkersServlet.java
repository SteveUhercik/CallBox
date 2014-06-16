package cz.muni.fi.pb138.callboxesmap;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan Uhercik (stefan.uhercik@ibacz.eu)
 * @see javax.servlet.http.HttpServlet
 */
public class MarkersServlet extends HttpServlet{

	private final CallboxesXMLClass callboxes = new CallboxesXMLClass();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String key = req.getParameter("key");
        String value = req.getParameter("value");
        String parent = req.getParameter("pVal");
        
        
        
        resp.setCharacterEncoding(CallboxesXMLClass.CHARACTER_ENCODING);
        resp.getWriter().append(callboxes.callboxesByArea(key, value, parent));
    }
    
    
    
    
    
    
}
