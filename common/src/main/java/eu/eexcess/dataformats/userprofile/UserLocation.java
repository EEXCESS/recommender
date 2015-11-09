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

import javax.xml.bind.annotation.XmlAttribute;

/**
 * location of the user for the secure user profile
 * 
 * @author hziak
 *
 */
public class UserLocation implements Serializable {

    private static final long serialVersionUID = 8758273092953023320L;

    @XmlAttribute
    public Double longitude;

    @XmlAttribute
    public Double latitude;

    @XmlAttribute
    public Double accuracy;

    @XmlAttribute
    public Date timestamp;

    public UserLocation(double longitude, double latitude, double acurracy, Date timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.accuracy = acurracy;
        this.timestamp = timestamp;
    }

    public UserLocation() {

    }

    @Override
    public String toString() {
        return "UserLocation [longitude=" + longitude + ", latitude=" + latitude + ", accuracy=" + accuracy + ", timestamp=" + timestamp + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accuracy == null) ? 0 : accuracy.hashCode());
        result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
        result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
        UserLocation other = (UserLocation) obj;
        if (accuracy == null) {
            if (other.accuracy != null)
                return false;
        } else if (!accuracy.equals(other.accuracy))
            return false;
        if (latitude == null) {
            if (other.latitude != null)
                return false;
        } else if (!latitude.equals(other.latitude))
            return false;
        if (longitude == null) {
            if (other.longitude != null)
                return false;
        } else if (!longitude.equals(other.longitude))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }

}
