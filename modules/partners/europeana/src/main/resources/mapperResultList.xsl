<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://example.org/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:edm="http://www.europeana.eu/schemas/edm/"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:eexcess="http://eexcess.eu/schema/"
                xmlns:ore="http://www.openarchives.org/ore/terms/"
                xmlns:dyn="http://exslt.org/dynamic"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mpeg7="urn:mpeg:mpeg7:schema:2004"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
                xmlns:owl="http://www.w3.org/2002/07/owl#"
                xmlns:wgs84="http://www.w3.org/2003/01/geo/wgs84-pos/"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                extension-element-prefixes="dyn"
                version="2.0"
                xml:base="http://example.org/">
	  <xsl:output indent="yes" method="xml"/>
	  <xsl:template match="/">

		    <xsl:element name="rdf:RDF">

			      <xsl:attribute name="xml:base">http://www.europeana.eu/edm/</xsl:attribute>
      
			      <xsl:element name="owl:Ontology">
				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.europeana.eu/schemas/edm/</xsl:attribute>
				        </xsl:element>

				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.openarchives.org/ore/1.0/terms</xsl:attribute>
				        </xsl:element>
			      </xsl:element>


			      <xsl:for-each select="/o/items/e">
			      		<xsl:variable name="mainURI">
			      			   <xsl:call-template name="Main.URI"/>
			      		</xsl:variable>

				        <xsl:element name="ore:Aggregation">
					          <xsl:attribute name="rdf:about">
						            <xsl:value-of select="concat($mainURI,'/aggregation/')"/>
					          </xsl:attribute>

					          <xsl:element name="edm:provider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">http://www.europeana.eu/</xsl:attribute>
							              <xsl:element name="foaf:name">Europeana</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:dataProvider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">http://www.europeana.eu/</xsl:attribute>
							              <xsl:element name="foaf:name">Europeana</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:isShownBy">
						            <xsl:element name="edm:WebResource">
							              <xsl:attribute name="rdf:about">
								                <xsl:value-of select="concat($mainURI,'/webresource/')"/>
							              </xsl:attribute>
						            </xsl:element>
					          </xsl:element>


                    <xsl:element name="edm:isShownAt">
						            <xsl:attribute name="rdf:resource">
							              <xsl:call-template name="Main.isShownAt"/>
						            </xsl:attribute>	
					          </xsl:element>

                    <xsl:element name="edm:preview">
						            <xsl:attribute name="rdf:resource">
							              <xsl:call-template name="Main.previewImage"/>
						            </xsl:attribute>	
					          </xsl:element>

					          <xsl:call-template name="Main.collectionName"/>


					          <xsl:element name="edm:aggregatedCHO">

						            <xsl:element name="eexcess:Object">
							              <xsl:attribute name="rdf:about">
				                    <xsl:call-template name="Main.URI"/>
							              </xsl:attribute>

                     <!--							<xsl:element name="edm:type">
								<xsl:attribute name="rdf:datatype"
									>http://www.w3.org/2001/XMLSchema#string</xsl:attribute>
								Museumsobject </xsl:element>
-->


					


						            </xsl:element>

						            <!-- ende cho -->
					          </xsl:element>

				        </xsl:element>



				        <xsl:element name="eexcess:Proxy">
					          <xsl:attribute name="rdf:about">
						            <xsl:value-of select="concat($mainURI,'/proxy/')"/>
					          </xsl:attribute>
					
					          <xsl:element name="ore:proxyFor">
						            <xsl:attribute name="rdf:resource">
			                  <xsl:call-template name="Main.URI"/>
						            </xsl:attribute>	
					          </xsl:element>
					
					
					          <xsl:element name="ore:proxyIn">
						            <xsl:attribute name="rdf:resource">
							              <xsl:value-of select="concat($mainURI,'/aggregation/')"/>
						            </xsl:attribute>	
					          </xsl:element>
					
					          <xsl:element name="edm:europeanaProxy">false</xsl:element>

					          <xsl:call-template name="Main.Language"/>
					          <xsl:call-template name="Main.Identifier"/>
					          <xsl:call-template name="Main.Title"/>
					          <xsl:call-template name="Main.Description"/>
					          <xsl:call-template name="Main.Date"/>
					          <xsl:call-template name="Main.Subject"/>	
					          <xsl:call-template name="Main.concept"/>	
					          <xsl:call-template name="Main.Country"/>	
					          <xsl:call-template name="Main.License"/>	
					          <xsl:call-template name="Main.Latitude"/>	
					          <xsl:call-template name="Main.Longitude"/>	
					          <xsl:call-template name="Main.Type"/>	
				        </xsl:element>
					


			      </xsl:for-each>

		    </xsl:element>



	  </xsl:template>



	  <xsl:template name="Main.Language">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m4"
                       select="language"/>
   </xsl:template>
	  <xsl:template name="Main.Title">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m8" select="title"/>
   </xsl:template>
	  <xsl:template name="Main.Description"/>
	  <xsl:template name="Main.Date">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m6" select="year/e"/>
   </xsl:template>
	  <xsl:template name="Main.Identifier">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m13" select="id"/>
   </xsl:template>
	  <xsl:template name="Main.isShownAt">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m1"
                       select="edmIsShownAt"/>
   </xsl:template>
	  <xsl:template name="Main.collectionName">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m11"
                       select="europeanaCollectionName"/>
   </xsl:template>
	  <xsl:template name="Main.previewImage">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m2"
                       select="edmPreview"/>
   </xsl:template>
	  <xsl:template name="Main.Subject"/>
	  <xsl:template name="Main.URI">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m5"
                       select="edmIsShownAt"/>
   </xsl:template>
	  <xsl:template name="Main.concept">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m0"
                       select="edmConcept/e/en/e"/>
   </xsl:template>
	  <xsl:template name="Main.Country">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m7"
                       select="edmCountry"/>
   </xsl:template>
	  <xsl:template name="Main.License">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m12" select="rights"/>
   </xsl:template>
	  <xsl:template name="Main.Latitude">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m3"
                       select="edmPlaceLatitude"/>
   </xsl:template>
	  <xsl:template name="Main.Longitude">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m10"
                       select="edmPlaceLongitude"/>
   </xsl:template>
	  <xsl:template name="Main.Type">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m9" select="type"/>
   </xsl:template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="edmConcept/e/en/e"
             mode="m0">
      <element name="edm:concept">
         <call-template name="StringToString"/>
      </element>
   </template>
   <xsl:template name="StringToString">
      <xsl:value-of select="."/>
   </xsl:template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="edmIsShownAt"
             mode="m1">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="edmPreview"
             mode="m2">
      <element name="previewImage">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="edmPlaceLatitude"
             mode="m3">
      <element name="wgs84:lat">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="language" mode="m4">
      <element name="edm:language">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="edmIsShownAt"
             mode="m5">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="year/e" mode="m6">
      <element name="dcterms:date">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="edmCountry"
             mode="m7">
      <element name="edm:Country">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="title" mode="m8">
      <element name="dc:title">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="type" mode="m9">
      <element name="edm:type">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="edmPlaceLongitude"
             mode="m10">
      <element name="wgs84:long">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="europeanaCollectionName"
             mode="m11">
      <element name="edm:collectionName">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="rights" mode="m12">
      <element name="edm:rights">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="id" mode="m13">
      <element name="dc:identifier">
         <call-template name="StringToString"/>
      </element>
   </template>
</xsl:stylesheet>
