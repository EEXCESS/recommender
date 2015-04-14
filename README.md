recommender
===========

This package includes the federated recommender and partner recommender aswell.
Although every partner recommender can be build and installed on an appropriate system,
partners will perhabs not return results since many partners apis need a license with api key and password.
If you need a license get in touch with the corresponding partner.

The recommender is a REST-Api based System which communicates over json.
The appropriate calls and json formats are descripted in the wiki:
https://github.com/EEXCESS/eexcess/wiki/Federated-Recommender-Service
https://github.com/EEXCESS/eexcess/wiki/json-exchange-format




Tools needed:
Tomcat: to deploy the generated war files
Maven (>3.0.5): to build the project


Deployment
===========

To configure the project you have to change entries in the main configuration file:
code/modules/recommender/federated-recommender-web-service/src/main/resources/federatedRecommenderConfig.json
(e.g. "federatedRecommenderBaseUri":"http://localhost:8100")
as in the partner recommenders file:
code/modules/partners/mendeley/src/main/resources/partner-config.json

Set a system variable EEXCESS_PARTNER_KEY_FILE with a path to the keyfile

The file has to look like:
{"partners":[

{"systemId":"Wissenmedia",

"userName": "",

"password": ""},

{"systemId":"Europeana",

"apiKey": ""

},

{"systemId":"Mendeley",

"userName": "",

"password": ""}]
}

You can also set a second system variable EEXCESS_FEDERATED_RECOMMENDER_CONFIG_FILE 
to set the path of the config file.
If the varibale is not set the service will take the file from the resoucres in the war file.

In the main directory (code/ ) just call
$ mvn package -Dmaven.test.skip=true
and deploy the generated war files in the your tomcat instance. 
Keep in mind that the abovementioned config has to be changed or 
for convenience just let your tomcat run on port "80".
After deploying these files you should be able to call the federated recommender
like descriped in the wiki links above.



