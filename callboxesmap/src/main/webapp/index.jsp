<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <%@include file="page_head.jspf" %>
    <script type="text/javascript" src="js/callboxes.js"></script>
    <body>
        <div class="menu">
            <div id="banner">
                CALL-<br>
                BOXES<br>
                MAP
            </div>
            <div id="gps-locations">
                <form>
                    <table>
                        <tr>
                            <td colspan="2">
                                <b>Searching by coordinates</b>
                            </td>
                            <td>
                                Format:
                            </td>
                            <td>
                                <select id="formatInput">
                                    <option value='DD-MM-SS'>DD-MM-SS</option>
                                    <option value='DD.DD'>DD.DD</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td>Latitude:</td>
                            <td><input type="text" id="latitudeInput"/></td> 
                            <td>Diameter:</td>
                            <td>
                                <input type="text" id="diameterInput"/>
                                <span style="width:5px;"/>
                                metres
                            </td> 
                        </tr>
                        <tr>
                            <td>Longitude</td> 
                            <td><input type="text" id="longitudeInput"/></td>
                            <td colspan="2">
                                <div id="error-message" style="display:none">
                                    Missing or ncorrectly enterred data!!!
                                </div>
                            </td> 
                        </tr>
                        <tr>
                            <td><input type="button" value="Search" id="gpsSubmit"/></td>
                        </tr>
                    </table>
                </form>
            </div>
            <div id="area-selection">
                <b>Searching by area</b>
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
