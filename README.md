CallBox - Mapa telefonních automatů
===================================

1. Oficiální zadání
-------------------

Webová aplikace pro zobrazení mapy telefonních automatů a vyhledání podle adresy nebo nejbližších automatů. Data převeďte do XML formátu - http://www.ctu.cz/otevrena-data/katalog-otevrenych-dat-ctu.html?action=detail&data_id=5

2. Návrh aplikace
-----------------

Maven aplikace bude sestávat z více modulů:

**2.1 Převod CSV do XML**
*
Oficiální dokument obsahující adresy telefonních automatů bude třeba převést z formátu CSV do XML. Tato funkcionalita bude implementována v modulu dataconversion. K vygenerovanému XML bude vytvořeno i XML schéma.

**2.2 Doplnění geokování do XML obsahujícího adresy**
*
Aby bylo možné používat javascriptové Google Maps API. Bude třeba do dat převedených z XML doplnit souřadnice lokací. Toho bude dosaženo pomocí Google Geocoding API. Tato funkcionalita bude implementována v modulu dataconversion.
Data obsahují více než 5000 adres telefonních automatů, ale Google Geocoding API umožňuje maximálně 2500 dotazů za 24 hodin, proto tyto data budou získávána postupně, až nakonec dostaneme geolokace všech automatů.
K vygenerovanému XML bude i zde vytvořeno odpovídající XML schéma.

**2.3 Webová aplikace na zobrazení mapy**
*
Java webová aplikace, kde za pomocí Google Maps API budou v mapě zaznačeny telefonní automaty. Aplikace bude v souladu se zadáním umožňovat vyhledávání podle adresy nebo nejbližších telefonních automatů.

3. Aktuální postup prací
------------------------

100% - Převod CSV do XML
 90% - Doplnění geokódování do XML obsahujícího adresy
 10% - Webová aplikace na zobrazení mapy

4. Členové týmu
---------------

**Mgr. Adam Krejčí, učo 324635**

**Bc. Tomáš Lestyan, učo 35935**

**Bc. Eduard Tomek, učo 324681**

**Bc. Štefan Uherčík, učo 374375**
