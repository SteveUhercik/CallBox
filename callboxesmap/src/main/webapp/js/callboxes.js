                    
    var map;
    var markersArray = [];
    var areaTypes = ['region', 'district', 'municipality'];
    var lastSelectedKey = null;
    var lastSelectedValue = null;

    function removeComboboxesToTheRight(comboBoxId) {
        $.each($("#" + comboBoxId).nextAll(".area-type"), function(index, value) {
            $(value).remove();
        });
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
    
    function setInfoMessage(text){
        $("#info-message").text(text);
    }

    $(document).ready(function() {
        initializeMap();
        initializeForm();

        $("#callBoxForm").on("change", ".area-type", function(event) {
            var currentArea = $(this).attr("id");
            var vybranaHodnota = $(this).val();
            areaTypeIndex = areaTypes.indexOf(currentArea);
            if (areaTypeIndex < areaTypes.length + 1) {
                areaTypeIndex += 1;
            }
            lastSelectedKey = currentArea;
            lastSelectedValue = vybranaHodnota;
            $.ajax({
                url: 'ajaxservlet',
                data: {
                    'area-type': areaTypes[areaTypeIndex],
                    'selected' : vybranaHodnota
                },
                success: function(generatedCombobox) {
                    removeComboboxesToTheRight(currentArea);
                    $(generatedCombobox).insertAfter("#" + currentArea);
                }
            });

        });

        $("#submit").click(function() {
            var params = {'key' : lastSelectedKey, 'value': lastSelectedValue};
            setInfoMessage("");
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
        
        $("#gpsSubmit").click(function(){
            var params = {
                'latitude':$("#latitudeInput").val(),
                'longitude':$("#longitudeInput").val(),
                'diameter':$("#diameterInput").val(),
                'format':$("#formatInput option:selected").val()
            };
            
            setInfoMessage("");
            
            $.ajax({
                url:'gpsservlet',
                data:params,
                success: function(gpsData) {
                    var gps = parseGps(gpsData);
                    callBoxesCount = gps.length - 1;
                    setInfoMessage(callBoxesCount + " callboxes found.");
                    clearOverlays();
                    createMarkers(gps);
                },
                error:function(a,b,c){
                    setInfoMessage("Missing or ncorrectly enterred data!!!");
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