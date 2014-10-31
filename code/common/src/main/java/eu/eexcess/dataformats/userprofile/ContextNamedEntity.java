/* Copyright (C) 2014
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.dataformats.userprofile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
/**
 * Named entities of the users previous context
 * @author hziak
 *
 */
public class ContextNamedEntity   implements Serializable{
	

    /**
	 * 
	 */
	private static final long serialVersionUID = 2438315047046353265L;

	@XmlElementWrapper(name="locations")
    @XmlElement(name="locations")
    public List<ContextNamedEntitiesElement> locations = new ArrayList<ContextNamedEntitiesElement>();
    
    @XmlElementWrapper(name="persons")
    @XmlElement(name="persons")
    public List<ContextNamedEntitiesElement> persons = new ArrayList<ContextNamedEntitiesElement>();

    @XmlElementWrapper(name="organizations")
    @XmlElement(name="organizations")
    public List<ContextNamedEntitiesElement> organizations = new ArrayList<ContextNamedEntitiesElement>();

    @XmlElementWrapper(name="misc")
    @XmlElement(name="misc")
    public List<ContextNamedEntitiesElement> misc = new ArrayList<ContextNamedEntitiesElement>();

    @XmlElementWrapper(name="topics")
    @XmlElement(name="topics")
    public List<ContextNamedEntitiesElement> topics = new ArrayList<ContextNamedEntitiesElement>();

	@Override
	public String toString() {
		return "ContextNamedEntitie [locations=" + locations + ", persons="
				+ persons + ", organizations=" + organizations + ", misc="
				+ misc + ", topics=" + topics + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((locations == null) ? 0 : locations.hashCode());
		result = prime * result + ((misc == null) ? 0 : misc.hashCode());
		result = prime * result
				+ ((organizations == null) ? 0 : organizations.hashCode());
		result = prime * result + ((persons == null) ? 0 : persons.hashCode());
		result = prime * result + ((topics == null) ? 0 : topics.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContextNamedEntity other = (ContextNamedEntity) obj;
		if (locations == null) {
			if (other.locations != null)
				return false;
		} else if (!locations.equals(other.locations))
			return false;
		if (misc == null) {
			if (other.misc != null)
				return false;
		} else if (!misc.equals(other.misc))
			return false;
		if (organizations == null) {
			if (other.organizations != null)
				return false;
		} else if (!organizations.equals(other.organizations))
			return false;
		if (persons == null) {
			if (other.persons != null)
				return false;
		} else if (!persons.equals(other.persons))
			return false;
		if (topics == null) {
			if (other.topics != null)
				return false;
		} else if (!topics.equals(other.topics))
			return false;
		return true;
	}
    
    
}

    
    