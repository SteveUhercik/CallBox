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