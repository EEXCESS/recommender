<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://example.org/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:edm="http://www.europeana.eu/schemas/edm/"
                xmlns:wgs84_pos="http://www.w3.org/2003/01/geo/wgs84-pos"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:ebucore="http://www.ebu.ch/metadata/ontologies/ebucore#"
                xmlns:eexcess="http://eexcess.eu/schema"
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

			      <xsl:attribute name="xml:base">http://www.joanneum.at/edm/</xsl:attribute>
      
			      <xsl:element name="owl:Ontology">
				        <xsl:attribute name="rdf:about"/>

				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.ebu.ch/metadata/ontologies/ebucore</xsl:attribute>
				        </xsl:element>

				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.europeana.eu/schemas/edm/</xsl:attribute>
				        </xsl:element>

				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.openarchives.org/ore/1.0/terms</xsl:attribute>
				        </xsl:element>

				        <xsl:element name="owl:imports">
					          <xsl:attribute name="rdf:resource">http://www.w3.org/ns/ma-ont</xsl:attribute>
				        </xsl:element>
			      </xsl:element>


			      <xsl:for-each select="/results/result|/objects/object">

				        <xsl:element name="ore:Aggregation">
					          <xsl:attribute name="rdf:about">
						            <xsl:value-of select="concat('aggregation/',generate-id())"/>
					          </xsl:attribute>

					          <xsl:element name="edm:provider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">http://www.wissens-server.com</xsl:attribute>
							              <xsl:element name="foaf:name">wissenmedia</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:dataProvider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">http://www.wissens-server.com</xsl:attribute>
							              <xsl:element name="foaf:name">wissenmedia</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:rights">
						            <xsl:attribute name="rdf:resource">http://creativecommons.org/publicdomain/mark/1.0/</xsl:attribute>
					          </xsl:element>

					          <xsl:element name="edm:isShownBy">
						            <xsl:element name="edm:WebResource">
							              <xsl:attribute name="rdf:about">
								                <xsl:value-of select="concat('webresource/',generate-id())"/>
							              </xsl:attribute>
							              <xsl:element name="edm:rights">
								                <xsl:attribute name="rdf:resource">http://creativecommons.org/publicdomain/mark/1.0/</xsl:attribute>
							              </xsl:element>
						            </xsl:element>
					          </xsl:element>



					          <xsl:element name="edm:aggregatedCHO">

						            <xsl:element name="eexcess:MuseumsObject">
							              <xsl:attribute name="rdf:about">
								                <xsl:value-of select="concat('aggregatedCHO/',generate-id())"/>
							              </xsl:attribute>

							              <xsl:element name="edm:type">
								                <xsl:attribute name="rdf:datatype">http://www.w3.org/2001/XMLSchema#string</xsl:attribute>
								Museumsobject </xsl:element>



					


						            </xsl:element>

						            <!-- ende cho -->
					          </xsl:element>

				        </xsl:element>



				        <xsl:element name="eexcess:Proxy">
					          <xsl:attribute name="rdf:about">
						            <xsl:value-of select="concat('proxy/',generate-id())"/>
					          </xsl:attribute>
					
					          <xsl:element name="ore:proxyFor">
						            <xsl:attribute name="rdf:resource">
							              <xsl:value-of select="concat('aggregatedCHO/',generate-id())"/>
						            </xsl:attribute>	
					          </xsl:element>
					
					
					          <xsl:element name="ore:proxyIn">
						            <xsl:attribute name="rdf:resource">
							              <xsl:value-of select="concat('aggregation/',generate-id())"/>
						            </xsl:attribute>	
					          </xsl:element>
					
					          <xsl:element name="edm:europeanaProxy">false</xsl:element>
					
					          <xsl:call-template name="Main.Title"/>
					
					
				        </xsl:element>
					


			      </xsl:for-each>

		    </xsl:element>



	  </xsl:template>



	  <xsl:template name="Main.Title">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m0" select="title"/>
   </xsl:template>

   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="title" mode="m0">
      <element name="dc:title">
         <call-template name="StringToString"/>
      </element>
   </template>
   <xsl:template name="StringToString">
      <xsl:value-of select="."/>
   </xsl:template>
</xsl:stylesheet>
