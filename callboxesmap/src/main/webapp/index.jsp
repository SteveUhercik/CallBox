<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
  "http://www.w3.org/TR/html4/loose.dtd">

<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <script type="text/javascript"
            src="https://maps.googleapis.com/maps/api/js?libraries=places">
    </script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script type="text/javascript" src="js/callboxes.js"></script>
    <script type="text/javascript" src="js/basicCallboxes.js"></script>
    <link rel="stylesheet" href="css/style.css" type="text/css">
  </head>
  <body>
    <div class="menu">
      <form id="callBoxForm">
        <input type="button" id="submit">
        <input type="button" id="checkMyPosition" class="pull-right" value="Check my position">
      </form>
    </div>
    <input id="pac-input" class="controls" type="text" placeholder="Search Box">
    <div id="map-canvas"></div>
  </body>
</html>
