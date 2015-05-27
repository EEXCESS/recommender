<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://example.org/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:edm="http://www.europeana.eu/schemas/edm/"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:ore="http://www.openarchives.org/ore/terms/"
                xmlns:eexcess="http://eexcess.eu/schema/"
                xmlns:dyn="http://exslt.org/dynamic"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:ns2="http://www.deutsche-digitale-bibliothek.de/institution"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mpeg7="urn:mpeg:mpeg7:schema:2004"
                xmlns:ns4="http://www.deutsche-digitale-bibliothek.de/item"
                xmlns:ns3="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
                xmlns:owl="http://www.w3.org/2002/07/owl#"
                xmlns:wgs84="http://www.w3.org/2003/01/geo/wgs84-pos/"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                exclude-result-prefixes="ns2 ns3 ns4"
                extension-element-prefixes="dyn"
                version="2.0"
                xml:base="http://example.org/">
	  <xsl:output indent="yes" method="xml"/>
	  <xsl:template match="/">

		    <xsl:element name="rdf:RDF">

			      <xsl:attribute name="xml:base">https://www.deutsche-digitale-bibliothek.de/</xsl:attribute>
      
			      <xsl:element name="owl:Ontology">
				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.europeana.eu/schemas/edm/</xsl:attribute>
				        </xsl:element>

				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.openarchives.org/ore/1.0/terms</xsl:attribute>
				        </xsl:element>
			      </xsl:element>


			      <xsl:for-each select=".">
			      		<xsl:variable name="mainURI">
			      			   <xsl:call-template name="Main.URI"/>
			      		</xsl:variable>

				        <xsl:element name="ore:Aggregation">
					          <xsl:attribute name="rdf:about">
						            <xsl:value-of select="concat($mainURI,'/aggregation/')"/>
					          </xsl:attribute>

					          <xsl:element name="edm:provider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">https://www.deutsche-digitale-bibliothek.de/</xsl:attribute>
							              <xsl:element name="foaf:name">Deutsche Digitale Bibliothek</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:dataProvider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">https://www.deutsche-digitale-bibliothek.de/</xsl:attribute>
							              <xsl:element name="foaf:name">Deutsche Digitale Bibliothek</xsl:element>
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
				        </xsl:element>
					


			      </xsl:for-each>

		    </xsl:element>



	  </xsl:template>



	  <xsl:template name="Main.Language"/>
	  <xsl:template name="Main.Title">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m2"
                       select="/cortex/view/ns4item/ns4title"/>
   </xsl:template>
	  <xsl:template name="Main.Description"/>
	  <xsl:template name="Main.Date"/>
	  <xsl:template name="Main.Identifier">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m3"
                       select="/cortex/properties/item-id"/>
   </xsl:template>
	  <xsl:template name="Main.isShownAt">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m0"
                       select="/cortex/properties/item-id"/>
   </xsl:template>
	  <xsl:template name="Main.collectionName"/>
	  <xsl:template name="Main.previewImage">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m4"
                       select="thumbnail"/>
   </xsl:template>
	  <xsl:template name="Main.Subject"/>
	  <xsl:template name="Main.URI">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m5"
                       select="/cortex/properties/item-id"/>
   </xsl:template>
	  <xsl:template name="Main.concept"/>
	  <xsl:template name="Main.Country"/>
	  <xsl:template name="Main.License"/>
	  <xsl:template name="Main.Latitude">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m6"
                       select="latitude"/>
   </xsl:template>
	  <xsl:template name="Main.Longitude">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m1"
                       select="longitude"/>
   </xsl:template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="/cortex/properties/item-id"
             mode="m0">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
   <xsl:template name="StringToString">
      <xsl:value-of select="."/>
   </xsl:template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="longitude"
             mode="m1">
      <element name="wgs84:long">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="/cortex/view/ns4item/ns4title"
             mode="m2">
      <element name="dc:title">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="/cortex/properties/item-id"
             mode="m3">
      <element name="dc:identifier">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="thumbnail"
             mode="m4">
      <element name="previewImage">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="/cortex/properties/item-id"
             mode="m5">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="latitude" mode="m6">
      <element name="wgs84:lat">
         <call-template name="StringToString"/>
      </element>
   </template>
</xsl:stylesheet>
