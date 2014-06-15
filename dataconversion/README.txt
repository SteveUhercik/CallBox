Jak používat tento modul
------------------------

Modul obstarává dvojí funkcionalitu:

1) Převod seznamu veřerných automatů z CSV do XML - cz.muni.fi.pb138.dataconversion.CsvToXml

Návod k použití: 
CsvToXml "cesta ke vstupnímu CSV souboru" "cesta k výstupnímu XML"

Příklad argumentů použitý v projektu:
../data/callBoxList.csv ../data/callBoxes.xml

2) Získání geokódování k telefonním automatům - cz.muni.fi.pb138.dataconversion.GeocodingUtils

Návod k použití:
GeocodingUtils "cesta k vstupnímu XML" "cesta pro uložení výstupního XML" "XSD schema výstupního XML" "počíteční index" "koncový index"

Google Geocoding API dovoluje pouze 2500 dotazů za 24 hodin z jedné IP adresy. 
Proto rozdíl mezi počátečním indexem a koncovým indexem by neměl být větší než 2500.

Příklad argumentů použitý v projektu:
../data/callBoxList.xml ../data/callBoxListWithGeocoding_4801-6000.xml ../data/callBoxesWithGeocoding.xsd 4801 6000

