/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.dataformats;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * Reflects a partner domain given in partner-configuration.
 * 
 * @author Raoul Rubien
 *
 */
public class PartnerDomain implements Serializable {

	private static final long serialVersionUID = -3022570752393633726L;

	@XmlElement(name = "domainName")
	public String domainName;

	@XmlElement(name = "weight")
	public Double weight;

	public PartnerDomain(String domainName, double weight) {
		this.domainName = domainName;
		this.weight = weight;
	}

	public PartnerDomain() {
	}
}
