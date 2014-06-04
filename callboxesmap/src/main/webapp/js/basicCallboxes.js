/*
 * Basic functionality
 * - show all callboxes - 100%
 * - search nearest
 * 
 *   
 */

function loadAllCallboxes() {
  $.get("callboxesservlet")
    .done(function(data){
      showMarkers(data);
    });
}

function showMarkers(data) {
  data = JSON.parse(data);
  var markers = [];
  for (var index in data.callboxes.callbox) {
    var callbox = data.callboxes.callbox[index];
    var location = new google.maps.LatLng(callbox.location.lat, callbox.location.lng);

    var contentString = "<div><b>Callbox number " + callbox.vtaid
      + "</b><br>Region: " + callbox.region 
      + "<br>District: " + callbox.district
      + "<br>Part: " + callbox.part
      + (callbox.note ? "<br>Note: " + callbox.note : "")
      + "</div>"
    ;

    var infowindow = new google.maps.InfoWindow({
      content: contentString,
      maxWidth: 300,
      maxHeight: 200
    });

    var marker = new google.maps.Marker({
      position: location,
      map: map,
      title: contentString
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