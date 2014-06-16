/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.callboxesmap;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author steve
 */
public class GpsServlet extends HttpServlet {

    private CallboxesXMLClass callboxesXMLClass = new CallboxesXMLClass();

    private static final String FORMAT_DD_DD = "DD.DD";
    private static final String FORMAT_DD_MM_SS = "DD-MM-SS";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lat = req.getParameter("latitude");
        String lng = req.getParameter("longitude");
        String diam = req.getParameter("diameter");
        String format = req.getParameter("format");

        double latitude;
        double longitude;
        if(diam.length()==0){
            throw new IOException("diameter is empty");
        }
        long diameter = Long.parseLong(diam);

        if (format.equals(FORMAT_DD_DD)) {
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lng);
        } else if (format.equals(FORMAT_DD_MM_SS)) {
            latitude = transformGpsCords(lat);
            longitude = transformGpsCords(lng);
        } else{
            throw new IOException("unsupported coordinates format");
        }

        String nearestCallBoxes = callboxesXMLClass.nearestCallBoxes(latitude,longitude,diameter);

        PrintWriter writer = resp.getWriter();
        writer.append(nearestCallBoxes);
        writer.flush();
    }

    private double transformGpsCords(String input) {
        if(input.length()<7){
            throw new RuntimeException("wrong coordinates format");
        }
        Double degrees = Double.parseDouble(input.substring(0, 2));
        Double minutes = Double.parseDouble(input.substring(3, 5));
        Double seconds = Double.parseDouble(input.substring(6));
        
        System.out.println("degrees:"+degrees);
        System.out.println("minutes:"+minutes);
        System.out.println("seconds:"+seconds);

        Double result = degrees + minutes / 60 + seconds / 3600;

        System.out.println("result:"+result);
        
        return result;
    }

}
