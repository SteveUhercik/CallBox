/*
 * Basic functionality
 * - show all callboxes - 100%
 * - users current position and zoom - 100%
 * - map - search by address - 0%
 * 
 *   
 */

/**
 * Finds users current position and zooms the map there
 * 
 * @returns {undefined}
 */
function initCheckPosition() {
  $("#checkMyPosition").click(function() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(success, error);
    } else {
     alert('This feature is not supported by your browser');
    }
  });  
}

function success(data) {
  map.setCenter({lat: data.coords.latitude, lng: data.coords.longitude});
  map.setZoom(15);
  //todo fill places search box with address
}

function error(msg) {
  alert("Error while determining your position: \n" + msg);
}

function loadAllCallboxes() {
  $.get("callboxesservlet")
    .done(function(data){
      showMarkers(data);
    });
}

/**
 * Adds markers with info window into map
 * 
 * @param {type} data
 * @returns {undefined}
 */
function showMarkers(data) {
  data = JSON.parse(data);
  var markers = [];
  for (var index in data.callboxes.callbox) {
    var callbox = data.callboxes.callbox[index];
    var location = new google.maps.LatLng(callbox.location.lat, callbox.location.lng);

    var contentString = "<div><b>Callbox number " + callbox.vtaid
      + "</b><br>Region: " + callbox.region 
      + "<br>District: " + callbox.district
      + "<br>Municipality: " + callbox.part
      + (callbox.part ? "<br>Part: " + callbox.part : "")
      + (callbox.street ? "<br>Street: " + callbox.street : "")
      + (callbox.note ? "<br>Note: " + callbox.note : "")
      + "</div>"
    ;

    var infowindow = new google.maps.InfoWindow({
      content: contentString,
      maxWidth: 400,
      maxHeight: 300
    });

    var marker = new google.maps.Marker({
      position: location,
      map: map
    });
    
    marker.html = contentString;
    markers.push(marker);
  }
  
  for (var i = 0; i < markers.length; i++) {
    var marker = markers[i];
    //see http://you.arenot.me/2010/06/29/google-maps-api-v3-0-multiple-markers-multiple-infowindows/
    google.maps.event.addListener(marker, 'click', function () {
      infowindow.setContent(this.html);
      infowindow.open(map, this);
    });
  }
}
