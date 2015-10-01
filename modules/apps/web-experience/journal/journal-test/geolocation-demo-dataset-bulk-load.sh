#!/bin/sh

set -o errexit ; set -o nounset

ACTION=exampleSearch
#ACTION=loadGeolocationDemoDataset

../../../../../gradlew testIntegration --tests com.liferay.demo.geolocation.GeolocationDemoDatasetBulkLoaderMainAsArquillianTest.${ACTION}
