
$(document).ready(function() {
  initializeMap();
  loadAllCallboxes();
  initCheckPosition();
  initSearchBox();
});

/**
 * Adds a search box to a map, using the Google Place Autocomplete feature
 */
function initSearchBox() {
  // Create the search box and link it to the UI element.
  var input = (
          document.getElementById('pac-input'));
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  var searchBox = new google.maps.places.SearchBox(input);

  // Listen for the event fired when the user selects an item from the
  // pick list. Retrieve the matching places for that item.
  google.maps.event.addListener(searchBox, 'places_changed', function() {
    var places = searchBox.getPlaces();
    if (places[0]) {
      var bounds = new google.maps.LatLngBounds();
      bounds.extend(places[0].geometry.location);
      map.fitBounds(bounds);
      map.setZoom(14);
    }
  });

  // Bias the SearchBox results towards places that are within the bounds of the
  // current map's viewport.
  google.maps.event.addListener(map, 'bounds_changed', function() {
    var bounds = map.getBounds();
    searchBox.setBounds(bounds);
  });
}

/**
 * Finds users current position and zooms the map there
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
  map.setZoom(14);
}

function error(msg) {
  alert("Error while determining your position: \n" + msg);
}

function loadAllCallboxes() {
  $.get("callboxesservlet")
          .done(function(data) {
            showMarkers(data);
          });
}

/**
 * Adds markers with info window into map
 * 
 * @param {json} data all callboxes data
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
    google.maps.event.addListener(marker, 'click', function() {
      infowindow.setContent(this.html);
      infowindow.open(map, this);
    });
  }
}
