/* 
 * Loads xml file with geocoding
 */

function loadCallboxesXML()
{
filename = "/CallBox/data/callBoxListWithGeocoding_0-2000.xml";
if (window.XMLHttpRequest)
  {
  xhttp=new XMLHttpRequest();
  }
else // code for IE5 and IE6
  {
  xhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
xhttp.open("GET",filename,false);
xhttp.send();
return xhttp.responseXML;
}