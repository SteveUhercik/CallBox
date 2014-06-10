package cz.muni.fi.pb138.callboxesmap;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Stefan Uhercik (stefan.uhercik@ibacz.eu)
 */
public class MarkersServlet extends HttpServlet{

    private Random random = new Random();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Enumeration parameterNames = req.getParameterNames();
        while(parameterNames.hasMoreElements()){
            System.out.println("parameter name:"+parameterNames.nextElement());
        }
        //TODO
        //metodu generateGPS nahradit metodami nearestCallboxes a callboxesByArea podla typu vyhladavania
        //TODO
        resp.setCharacterEncoding(CallboxesXMLClass.CHARACTER_ENCODING);
        resp.getWriter().append(generateGps());
    }
    
    private String generateGps(){
        double lowerLat = 49;
        double lowerLong = 14.0;
        
        StringBuffer buffer = new StringBuffer();
        int itemsCount = 3+random.nextInt(5);
        for(int i=0;i<itemsCount;i++){
            buffer.append(lowerLat + random.nextDouble());
            buffer.append(",");
            buffer.append(lowerLong + random.nextDouble()*3);
            if(i!=itemsCount-1){
                buffer.append(";");
            }
        }
        
        System.out.println(buffer.toString());
        
        return buffer.toString();
    }
    
    
    
    
    
}
