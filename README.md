Recommender
===========

This package includes both the federated recommender and partner recommender.
Although each partner recommender can be built and installed on an appropriate system,
most partners will probably not return results as partner APIs typically require a license with API key and password.
If you need a license get in touch with the corresponding partner.

The recommender is a REST-API based system which communicates over json.
The appropriate calls and json formats are descripted in the wiki:

https://github.com/EEXCESS/eexcess/wiki/Federated-Recommender-Service

https://github.com/EEXCESS/eexcess/wiki/json-exchange-format


Tools needed
-------------
- Tomcat: to deploy the generated war files
- Maven (>3.0.5): to build the project
- A Java IDE such as IntelliJ IDEA to debug and modify the project


Deployment
===========

To configure the project you should first modify the entries in the main configuration file:

    /modules/recommender/federated-recommender-web-service/src/main/resources/federatedRecommenderConfig.json

Parameter               |   Explanation
------------------------|---------------
solrServerUri           |   the endpoint to your own Solr server. However, this is not currently used, so it can be left as it is.
wikipediaIndexDir       |   the location of a Wikipedia index used for query expansion. This is not currently used, so it can be omitted or left as it is.
evaluationQueriesFile   |   not currently used
statsLogDatabase        |   location of the sqlite db file for storing query statistics

By default, the service will read this file from the resources folder in the .war file deployed when you build the project.
However, you can also set the system variable

    EEXCESS_FEDERATED_RECOMMENDER_CONFIG_FILE

to set the path to an external config file which will override this default file.

Set a system variable

    EEXCESS_PARTNER_KEY_FILE

with the path to the key file that contains details of the partner systems that the federated recommender can query.

The EEXCESS_PARTNER_KEY_FILE is a JSON file with the following format for each partner:

Parameter               |   Explanation
------------------------|---------------
systemId                |   The unique partner identifier. This must match the systemId used in the corresponding partner-config.json file for that partner
userName                |   If the partner has username/password access to its API, specify the user name here
password                |   If the partner has username/password access to its API, specify the password here
apiKey                  |   If the partner has provided an API key, enter this here

Here's an example EEXCESS_PARTNER_KEY_FILE:

    {"partners":[

    {"systemId":"Wissenmedia",

    "userName": "wissenmedia-api-user",

    "password": "wissenmedia-api-user-password"},


    {"systemId":"Europeana",

    "apiKey": "your-europeana-api-key"

    },

    {"systemId":"Mendeley",

    "userName": "mendeley-api-user",

    "password": "mendeley-api-password"}]
    }


Partner recommender
--------------------

To test an individual partner recommender, ensure valid userName/password/apiKey values are present in the partner-config.json file for that partner.
You can then run the PartnerStandaloneServer in

    modules/partners/<partner>/src/main/java/eu/eexcess/<partner>/webservice/tool

NB: If the federated recommender endpoint defined in federatedRecommenderURI in partner-config.json is not running (see below)
then PartnerStandaloneServer will throw a 'Connection refused' exception but will otherwise continue to run.

NB: In the current development build, if WordNet is not installed in the default location of /wordnet
then PartnerStandaloneServer will throw a 'Dictionary path does not exist: /wordnet/data/wn31' exception but will otherwise continue to run.

You can then query the PartnerStandaloneServer, for example if you have the Mendeley PartnerStandaloneServer running on port 8100 then

    localhost:8100/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/debugDumpProfile

will generate a sample profile query XML file, which you can then POST to

    localhost:8100/eexcess/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/recommend

to get recommendations from Mendeley.

For example, using curl to get the sample profile:

    curl http://127.0.0.1:8100/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/debugDumpProfile > debugDumpProfile.xml

And then using curl to POST the profile to the partner recommender:

    curl -H 'Content-Type: application/xml' -X POST -d @debugDumpProfile.xml http://127.0.0.1:8100/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/recommend


Federated recommender
----------------------
To build the complete project, change into the project's root directory (i.e. the location of this README)
Execute the following maven command:

    mvn package -Dmaven.test.skip=true

If your maven is already set up to use a different repository then you can create a separate Maven settings.xml file
for this project, containing the EEXCESS Maven repo url

    <mirrors>
        <mirror>
            <id>kc_external</id>
            <mirrorOf>*</mirrorOf>
            <url>http://nexus.know-center.tugraz.at/content/repositories/public/</url>
        </mirror>
    </mirrors>

and point to this during the build process via

    mvn -s /path/to/eexcess/settings/settings.xml  package -Dmaven.test.skip=true

Once the build is complete, you can deploy the generated .war files to the webapps directory in your Tomcat instance.
Keep in mind that the following fields

    partnerConnectorEndpoint

    federatedRecommenderURI

in partner-config.json for each partner has to be changed to point to the correct host and port for each partner service.
Or, for convenience, leave these values at their defaults and just have Tomcat run on port "80".
After deploying the .war files to Tomcat you should be able to call the federated recommender as described in the wiki links above.



