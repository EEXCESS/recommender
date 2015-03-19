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

			      <xsl:attribute name="xml:base">https://api.econbiz.de//edm/</xsl:attribute>
      
			      <xsl:element name="owl:Ontology">
				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.europeana.eu/schemas/edm/</xsl:attribute>
				        </xsl:element>

				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.openarchives.org/ore/1.0/terms</xsl:attribute>
				        </xsl:element>
			      </xsl:element>


			      <xsl:for-each select="/doc/hits/hit/article | /doc/hits/hit/book | /doc/hits/hit/corporate | /doc/hits/hit/database | /doc/hits/hit/event | /doc/hits/hit/journal | /doc/hits/hit/other | /doc/hits/hit/portal | /doc/hits/hit/website">

   		       <xsl:variable name="tempMainURI">
   			         <xsl:call-template name="Main.URI"/>
   		       </xsl:variable>
   		       <xsl:variable name="mainURI">
	              <xsl:value-of select="concat('http://www.econbiz.de/Record/',$tempMainURI)"/>
   		       </xsl:variable>

				        <xsl:element name="ore:Aggregation">
					          <xsl:attribute name="rdf:about">
						            <xsl:value-of select="concat($mainURI,'/aggregation/')"/>
					          </xsl:attribute>

					          <xsl:element name="edm:provider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">https://api.econbiz.de/</xsl:attribute>
							              <xsl:element name="foaf:name">ZBW</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:dataProvider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">https://api.econbiz.de/</xsl:attribute>
							              <xsl:element name="foaf:name">ZBW</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:rights">
						            <xsl:attribute name="rdf:resource">http://creativecommons.org/publicdomain/mark/1.0/</xsl:attribute>
					          </xsl:element>

					          <xsl:element name="edm:isShownBy">
						            <xsl:element name="edm:WebResource">
							              <xsl:attribute name="rdf:about">
								                <xsl:value-of select="concat($mainURI,'/webresource/')"/>
							              </xsl:attribute>
							              <xsl:element name="edm:rights">
								                <xsl:attribute name="rdf:resource">http://creativecommons.org/publicdomain/mark/1.0/</xsl:attribute>
							              </xsl:element>
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

					          <xsl:element name="edm:aggregatedCHO">

						            <xsl:element name="eexcess:Object">
							              <xsl:attribute name="rdf:about">
				                    <xsl:value-of select="concat($mainURI,'')"/>
							              </xsl:attribute>

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
			                  <xsl:value-of select="concat($mainURI,'')"/>
						            </xsl:attribute>	
					          </xsl:element>
					
					
					          <xsl:element name="ore:proxyIn">
						            <xsl:attribute name="rdf:resource">
							              <xsl:value-of select="concat($mainURI,'/aggregation/')"/>
						            </xsl:attribute>	
					          </xsl:element>
					
					          <xsl:element name="edm:europeanaProxy">false</xsl:element>

		 			         <xsl:element name="edm:language">de</xsl:element>

					          <xsl:call-template name="Main.Identifier"/>
					          <xsl:call-template name="Main.Title"/>
					          <xsl:call-template name="Main.Description"/>
					          <xsl:call-template name="Main.Date"/>
					          <xsl:call-template name="Main.Subject"/>					
					          <xsl:call-template name="Main.Latitude"/>	
					          <xsl:call-template name="Main.Longitude"/>	
					          <xsl:call-template name="Main.Creator"/>

				        </xsl:element>
					


			      </xsl:for-each>

		    </xsl:element>



	  </xsl:template>



	  <xsl:template name="Main.Title">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m0" select="title"/>
   </xsl:template>
	  <xsl:template name="Main.Description"/>
	  <xsl:template name="Main.Date">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m5" select="date"/>
   </xsl:template>
	  <xsl:template name="Main.Identifier">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m3" select="id"/>
   </xsl:template>
	  <xsl:template name="Main.Subject">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m2" select="subject"/>
   </xsl:template>
	  <xsl:template name="Main.URI">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m7" select="id"/>
   </xsl:template>
	  <xsl:template name="Main.isShownAt"/>
	  <xsl:template name="Main.previewImage"/>
	  <xsl:template name="Main.Latitude">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m6" select="lat"/>
   </xsl:template>
	  <xsl:template name="Main.Longitude">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m1" select="lng"/>
   </xsl:template>
	  <xsl:template name="Main.Creator">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m4"
                       select="creatorString"/>
   </xsl:template>

   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="title" mode="m0">
      <element name="dc:title">
         <call-template name="StringToString"/>
      </element>
   </template>
   <xsl:template name="StringToString">
      <xsl:value-of select="."/>
   </xsl:template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="lng" mode="m1">
      <element name="wgs84:long">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="subject" mode="m2">
      <element name="dc:subject">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="id" mode="m3">
      <element name="dc:identifier">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="creatorString"
             mode="m4">
      <element name="dc:creator">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="date" mode="m5">
      <element name="dcterms:date">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="lat" mode="m6">
      <element name="wgs84:lat">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="id" mode="m7">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
</xsl:stylesheet>
