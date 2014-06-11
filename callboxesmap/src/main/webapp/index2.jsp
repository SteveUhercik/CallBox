<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
  <%@include file="page_head.jspf" %>
  <script type="text/javascript" src="js/callboxes2.js"></script>
  <body>
    <div class="menu">
      <form id="callBoxForm">
        <input type="button" id="checkMyPosition" value="Check my position">
        <a href="index.jsp" class="pull-right"> Verze 1 </a>
      </form>
    </div>
    <input id="pac-input" class="controls" type="text" placeholder="Search Box">
    <div id="map-canvas"></div>
  </body>
</html>
