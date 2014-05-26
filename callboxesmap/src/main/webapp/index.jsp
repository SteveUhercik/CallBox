<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
        <style type="text/css">
            html { height: 100% }
            body { height: 95%; margin: 0; padding: 0 }
            #map-canvas { height: 100% }
        </style>
        <script type="text/javascript"
                src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAAYhISYt4sECnlNCbtFC68HAfW0P8V_gc&sensor=false">
        </script>
        <script type="text/javascript"
                src="https://maps.googleapis.com/maps/api/js?libraries=places,drawing,geometry&sensor=false">
        </script>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
        <script>
                    
    var map;
    var markersArray = [];
    var areaTypes = ['region', 'district', 'municipality', 'part', 'street']

    function initializeMap() {

        var defaultLocation = {
            latitude: "49.8",
            longitude: "15.5"
        };

        var mapOptions = {
            center: new google.maps.LatLng(defaultLocation.latitude, defaultLocation.longitude),
            zoom: 8
        };
        map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
        var myLatlng = new google.maps.LatLng(defaultLocation.latitude, defaultLocation.longitude);
    }

    function removeComboboxesToTheRight(comboBoxId) {
        $.each($("#" + comboBoxId).nextAll(".area-type"), function(index, value) {
            $(value).remove();
        });
    }

    function createJsonParams() {
        var json = {}
        $.each($(".area-type"), function(index, value) {
            console.log(value);
            var key = $(value).attr("id");
            var val = $(value).children(":selected").text();
            json[key] = val;
        });
        return json;
    }


    function clearOverlays() {
        for (var i = 0; i < markersArray.length; i++ ) {
          markersArray[i].setMap(null);
        }
        markersArray.length = 0;
    }


    function createMarkers(gps) {
        for(var i=0;i<gps.length;i++){
            var marker = new google.maps.Marker({
                position: gps[i],
                map: map
            });
            markersArray.push(marker);
        }
    }

    function parseGps(gpsData){
        var tempGps = gpsData.split(";");
        var gps = new Array();
        for(var i=0;i<tempGps.length;i++){
            var items = tempGps[i].split(",");
            var myLatlng = new google.maps.LatLng(parseFloat(items[0]),parseFloat(items[1]));
            gps.push(myLatlng);
        }
        return gps;
    }
    
    /*
     * Generates xpath expression for searching by areatype
     * 
     * @param {areaType} area type
     * @param {value} value of searched area type
     * @returns {String} xpath expression
     */
    function searchByAddress (areaType, value) {

        var xpath = "";

        if(areaType == "district"){
            xpath = "/callboxes/callbox[region/text()='" + value + "']/location";
        }
        if(areaType == "department"){
            xpath = "/callboxes/callbox[district/text()='" + value + "']/location";
        }
        if(areaType == "city"){
            xpath = "/callboxes/callbox[municipality/text()='" + value + "']/location";
        }
        if(areaType == "ward"){
            xpath = "/callboxes/callbox[part/text()='" + value + "']/location";
        }
        if(areaType == "street"){
            xpath = "/callboxes/callbox[street/text()='" + value + "']/location";
        }

        return xpath;

        }
        
    /*
     * Generates xpath expression for searching callboxes in cca 500 m circle
     * @param {latitude} 
     * @param {longitude}
     * @returns {String} xpath expression
     */
    function searchByLocation(latitude, longitude){
        var xpath = "";
        area = 0.05; // for 500m circle
        var maxlat = latitude + area;
        var minlat = latitude - area;
        var maxlng = longitude + area;
        var minlng = longitude - area;

        xpath = "/callboxes/callbox/location[lat<" + maxlat + "and lat>" + minlat + "and lng<" + maxlng + "and lng>" + minlng + "]";

        return xpath;
    }
        
        

    function initializeForm(){
        $.ajax({
            url: 'ajaxservlet',
            data: {
                'area-type': areaTypes[0]
            },
            success: function(generatedCombobox) {
                $("#callBoxForm").prepend(generatedCombobox);
            }
        });
    }



    $(document).ready(function() {

        initializeMap();

        initializeForm();

        $("#callBoxForm").on("change", ".area-type", function(event) {
            var currentArea = $(this).attr("id");
            areaTypeIndex = areaTypes.indexOf(currentArea);
            if (areaTypeIndex < areaTypes.length + 1) {
                areaTypeIndex += 1;
            }
            $.ajax({
                url: 'ajaxservlet',
                data: {
                    'area-type': areaTypes[areaTypeIndex]
                },
                success: function(generatedCombobox) {
                    removeComboboxesToTheRight(currentArea);
                    $(generatedCombobox).insertAfter("#" + currentArea);
                }
            });

        });

        $("#submit").click(function() {
            var params = createJsonParams();
            $.ajax({
                url: 'markersservlet',
                data: params,
                success: function(gpsData) {
                    var gps = parseGps(gpsData);
                    clearOverlays();
                    createMarkers(gps);
                }
            });
        });

    });
    
    
    /* GEOCODER EXAMPLE
     
     
     var geocoder = new google.maps.Geocoder();
        geocoder.geocode(
                {address: 'Brno'}, function(results, status) {

            if (status == google.maps.GeocoderStatus.OK) {
                console.log("suradnice brna su");
                console.log(results);
                var marker = new google.maps.Marker({
                    map: map,
                    position: results[0].geometry.location
                });
            }
            else {
                console.log("Brno nenajdene");
            }
        }
        );
     
     
     */
    
        </script>
    </head>
    <body>
        <div style="height: 5%;background-color: #dddddd;">
            <form id="callBoxForm">
                <input type="button" id="submit">
            </form>
        </div>
        <div id="map-canvas" class="helloworld"/>
    </body>
</html>
