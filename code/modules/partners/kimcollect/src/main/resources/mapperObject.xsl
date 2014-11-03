<!--  Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
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

			      <xsl:attribute name="xml:base">https://kgdb.bl.ch/kim-kgs/edm/</xsl:attribute>
      
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


			      <xsl:for-each select="objects/object">

			      		<xsl:variable name="mainURI">
			      			   <xsl:call-template name="Main.URI"/>
			      		</xsl:variable>

				        <xsl:element name="ore:Aggregation">
					          <xsl:attribute name="rdf:about">
									         <xsl:value-of select="concat($mainURI,'/aggregation/')"/>
					          </xsl:attribute>

					          <xsl:element name="edm:provider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">https://kgdb.bl.ch/kim-kgs/</xsl:attribute>
							              <xsl:element name="foaf:name">KIM.Collect</xsl:element>
						            </xsl:element>
					          </xsl:element>

					          <xsl:element name="edm:dataProvider">
						            <xsl:element name="edm:Agent">
							              <xsl:attribute name="rdf:about">https://kgdb.bl.ch/kim-kgs/</xsl:attribute>
							              <xsl:element name="foaf:name">KIM.Collect</xsl:element>
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
					
					        <xsl:element name="edm:rights">
							    <xsl:value-of select="http://creativecommons.org/licenses/by-nc-sa/4.0/"/>
							</xsl:element>
							<xsl:element name="edm:europeanaProxy">false</xsl:element>

		 			         <xsl:element name="edm:language">de</xsl:element>

							        <!-- <xsl:call-template name="Main.Language"/> -->
							        <xsl:call-template name="Main.Identifier"/>
							        <xsl:call-template name="Main.Title"/>
							        <xsl:call-template name="Main.Description"/>
							        <xsl:call-template name="Main.Date"/>
					                <xsl:call-template name="Main.Subject"/>					
				        </xsl:element>
					


			      </xsl:for-each>

		    </xsl:element>



	  </xsl:template>


	  <!-- <xsl:template name="Main.Language" /> -->
	  <xsl:template name="Main.Title">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m7"
                       select="Objektbezeichnung"/>
   </xsl:template>
	  <xsl:template name="Main.Description">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m0"
                       select="Beschreibung"/>
   </xsl:template>
	  <xsl:template name="Main.Date"/>
	  <xsl:template name="Main.Identifier">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m6" select="InvNr"/>
   </xsl:template>
	  <xsl:template name="Main.isShownAt">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m5" select="uri"/>
   </xsl:template>
	  <xsl:template name="Main.previewImage">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m2"
                       select="BilderzumObjekt/image[1]/imagePath/@URI"/>
   </xsl:template>
	  <xsl:template name="Main.URI">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform" mode="m3" select="uri"/>
   </xsl:template>
	  <xsl:template name="Main.collectionName">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m4"
                       select="Sammlung/collection/name"/>
   </xsl:template>
	  <xsl:template name="Main.Subject">
      <apply-templates xmlns="http://www.w3.org/1999/XSL/Transform"
                       mode="m1"
                       select="Sachgruppe"/>
   </xsl:template>

   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="Beschreibung"
             mode="m0">
      <element name="dc:description">
         <call-template name="StringToString"/>
      </element>
   </template>
   <xsl:template name="StringToString">
      <xsl:value-of select="."/>
   </xsl:template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="Sachgruppe"
             mode="m1">
      <element name="dc:subject">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="BilderzumObjekt/image[1]/imagePath/@URI"
             mode="m2">
      <element name="previewImage">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="uri" mode="m3">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="Sammlung/collection/name"
             mode="m4">
      <element name="edm:collectionName">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="uri" mode="m5">
      <element name="uri">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform" match="InvNr" mode="m6">
      <element name="dc:identifier">
         <call-template name="StringToString"/>
      </element>
   </template>
   <template xmlns="http://www.w3.org/1999/XSL/Transform"
             match="Objektbezeichnung"
             mode="m7">
      <element name="dc:title">
         <call-template name="StringToString"/>
      </element>
   </template>
</xsl:stylesheet>
