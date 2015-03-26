<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://example.org/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:edm="http://www.europeana.eu/schemas/edm/"
                xmlns:wgs84_pos="http://www.w3.org/2003/01/geo/wgs84-pos"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:ebucore="http://www.ebu.ch/metadata/ontologies/ebucore#"
                xmlns:eexcess="http://eexcess.eu/schema/"
                xmlns:ore="http://www.openarchives.org/ore/terms/"
                xmlns:dyn="http://exslt.org/dynamic"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:mpeg7="urn:mpeg:mpeg7:schema:2004"
                xmlns:ma="http://www.w3.org/ns/ma-ont#"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
                xmlns:owl="http://www.w3.org/2002/07/owl#"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                extension-element-prefixes="dyn"
                version="2.0"
                xml:base="http://example.org/">
	  <xsl:output indent="yes" method="xml"/>
	  <xsl:template match="/">

		    <xsl:element name="rdf:RDF">

			      <xsl:attribute name="xml:base">http://api.mendeley.com/edm/</xsl:attribute>

                <xsl:element name="owl:Ontology">
                    <xsl:element name="owl:imports">
                        <xsl:attribute name="rdf:resource">http://www.europeana.eu/schemas/edm/</xsl:attribute>
                    </xsl:element>

                    <xsl:element name="owl:imports">
                        <xsl:attribute name="rdf:resource">http://www.openarchives.org/ore/1.0/terms</xsl:attribute>
                    </xsl:element>
                </xsl:element>


			      <xsl:for-each select="/o/documents/e">

			      		<xsl:variable name="mainURI">
			      			   <xsl:call-template name="Main.URI"/>
			      		</xsl:variable>

				        <xsl:element name="ore:Aggregation">
					          <xsl:attribute name="rdf:about">
						            <xsl:value-of select="concat($mainURI,'/aggregation/')"/>
					          </xsl:attribute>

					          <xsl:element name="edm:provider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">http://api.mendeley.com/</xsl:attribute>
							              <xsl:element name="foaf:name">mendeley</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:dataProvider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">http://api.mendeley.com/</xsl:attribute>
							              <xsl:element name="foaf:name">mendeley</xsl:element>
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

					          <xsl:call-template name="Main.collectionName"/>
					          <xsl:element name="edm:aggregatedCHO">

						            <xsl:element name="eexcess:Object">
							              <xsl:attribute name="rdf:about">
				                    <xsl:call-template name="Main.URI"/>
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
			                  <xsl:call-template name="Main.URI"/>
						            </xsl:attribute>	
					          </xsl:element>
					
					
					          <xsl:element name="ore:proxyIn">
						            <xsl:attribute name="rdf:resource">
							              <xsl:value-of select="concat($mainURI,'/aggregation/')"/>
						            </xsl:attribute>	
					          </xsl:element>
					
					          <xsl:element name="edm:europeanaProxy">false</xsl:element>

					          <xsl:call-template name="Main.Identifier"/>
					          <xsl:call-template name="Main.Title"/>
					          <xsl:call-template name="Main.Description"/>
					          <xsl:call-template name="Main.Date"/>
					          <xsl:call-template name="Main.Creator"/>
					
					
				        </xsl:element>
					


			      </xsl:for-each>

		    </xsl:element>



	  </xsl:template>



	  <xsl:template name="Main.Title">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m2" select="title"/>
   </xsl:template>
	  <xsl:template name="Main.Description">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m6"
                       select="abstract"/>
   </xsl:template>
	  <xsl:template name="Main.Date">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m3" select="year"/>
   </xsl:template>
	  <xsl:template name="Main.Identifier">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m1" select="id"/>
   </xsl:template>
	  <xsl:template name="Main.Creator">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m5"
                       select="authorsString"/>
   </xsl:template>
	  <xsl:template name="Main.isShownAt">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m4"
                       select="link"/>
   </xsl:template>
	  <xsl:template name="Main.collectionName">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m7"
                       select="source"/>
   </xsl:template>
	  <xsl:template name="Main.URI">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m0"
                       select="link"/>
   </xsl:template>

   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="link"
             mode="m0">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
   <xsl:template name="StringToString">
      <xsl:value-of select="."/>
   </xsl:template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="id" mode="m1">
      <element name="dc:identifier">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="title" mode="m2">
      <element name="dc:title">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="year" mode="m3">
      <element name="dcterms:date">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="link"
             mode="m4">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="authorsString"
             mode="m5">
      <element name="dc:creator">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="abstract"
             mode="m6">
      <element name="dc:description">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="source"
             mode="m7">
      <element name="edm:collectionName">
         <call-template name="StringToString"/>
      </element>
   </template>
</xsl:stylesheet>
