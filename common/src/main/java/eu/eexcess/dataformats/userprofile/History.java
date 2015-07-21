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
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

/**
 * History class
 * 
 * @author hziak
 *
 */
public class History implements Serializable {
    private static final long serialVersionUID = 4418756496505672081L;

    @XmlElement(name = "lastVisitTime")
    public Date lastVisitTime;
    @XmlElement(name = "title")
    public String title;
    @XmlElement(name = "typedCount")
    public Integer typedCount;
    @XmlElement(name = "visitCount")
    public Integer visitCount;
    @XmlElement(name = "url")
    public String url;

    public History(Date lastVisitTime, String title, Integer typedCount, Integer visitCount, String url) {
        super();
        this.lastVisitTime = lastVisitTime;
        this.title = title;
        this.typedCount = typedCount;
        this.visitCount = visitCount;
        this.url = url;
    }

    public History() {

    }

    public History(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "History [lastVisitTime=" + lastVisitTime + ", title=" + title + ", typedCount=" + typedCount + ", visitCount=" + visitCount + ", url=" + url + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lastVisitTime == null) ? 0 : lastVisitTime.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((typedCount == null) ? 0 : typedCount.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((visitCount == null) ? 0 : visitCount.hashCode());
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
        History other = (History) obj;
        if (lastVisitTime == null) {
            if (other.lastVisitTime != null)
                return false;
        } else if (!lastVisitTime.equals(other.lastVisitTime))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (typedCount == null) {
            if (other.typedCount != null)
                return false;
        } else if (!typedCount.equals(other.typedCount))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (visitCount == null) {
            if (other.visitCount != null)
                return false;
        } else if (!visitCount.equals(other.visitCount))
            return false;
        return true;
    }
}
