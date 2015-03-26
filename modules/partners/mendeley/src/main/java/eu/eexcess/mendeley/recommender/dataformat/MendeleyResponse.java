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
package eu.eexcess.mendeley.recommender.dataformat;
/**
 * 
 * @author hziak
 *
 */
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

public class MendeleyResponse {
    @XmlElementWrapper(name="documents")
    private List<MendeleyDocs> documents = new ArrayList<MendeleyDocs>();

    public MendeleyResponse(List<MendeleyDocs> documents) {
        this.documents = documents;
    }

    public List<MendeleyDocs> getDocuments() {
        return documents;
    }

    public void limitNumDocuments(int limit) {
        if (limit > 0 && limit < documents.size())
            documents = documents.subList(0, limit);
    }

	@Override
	public String toString() {
		return "MendeleyResponse [documents=" + documents + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((documents == null) ? 0 : documents.hashCode());
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
		MendeleyResponse other = (MendeleyResponse) obj;
		if (documents == null) {
			if (other.documents != null)
				return false;
		} else if (!documents.equals(other.documents))
			return false;
		return true;
	}
	
}
