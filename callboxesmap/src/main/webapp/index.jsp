<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <%@include file="page_head.jspf" %>
    <script type="text/javascript" src="js/callboxes.js"></script>
    <body>
        <div class="menu">
            <div id="gps-locations" method="GET" action="gpsservlet">
                <form>
                    <div>
                        <div class="form-element">Latitude:</div>
                        <div class="form-element"><input type="text" id="latitudeInput"/></div>
                    </div>
                    <div>
                        <div class="form-element">Longitude:</div>
                        <div class="form-element"><input type="text" id="longitudeInput"/></div>                    
                    </div>
                    <div>
                        <div class="form-element">Diameter:</div>
                        <div class="form-element"><input type="text" id="diameterInput"/></div>                    
                    </div>
                    <div>
                        <div class="form-element"><input type="button" value="Search" id="gpsSubmit"/></div>
                    </div>
                </form>
            </div>
            <div id="area-selection">
                <form id="callBoxForm">
                </form>
                <form>
                    <input type="button" value="Search" id="submit">              
                </form>
            </div>
            <a href="index2.jsp" class="pull-right"> Verze 2 </a>
        </div>
        <div id="map-canvas"></div>
    </body>
</html>
