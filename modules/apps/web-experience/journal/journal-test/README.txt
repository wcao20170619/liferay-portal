********************************************************************************
GEOLOCATION DEMO DATASET BULK LOAD
********************************************************************************

Pull the branch

ant all

rebuild-mysql

Start Liferay Portal

geolocation-demo-dataset-bulk-load.sh

http://localhost:9200/_all?pretty

Find in page:
__geolocation_en_US" : {

Copy the geo_point field name, e.g. ddm__keyword__20733__geolocation_en_US 

Search Portlet Configuration

For an uncluttered demo experience, uncheck "Modified Date"

geodistance: expand "Configure"

Enter the geo_point field name from above into "geolocation-field-name"

Save

Search Portlet... demo away

********************************************************************************
SOURCE URLS
********************************************************************************

https://data.cityofboston.gov/City-Services/311-Service-Requests/awu8-dc52
https://data.cityofboston.gov/browse?limitTo=datasets&utf8=✓
https://data.cityofboston.gov/resource/awu8-dc52.json

********************************************************************************
APIS
********************************************************************************

https://github.com/socrata/datasync
https://github.com/socrata/soda-java

********************************************************************************
APP URL
********************************************************************************

http://www.cityofboston.gov/doit/apps/citizensconnect.asp

