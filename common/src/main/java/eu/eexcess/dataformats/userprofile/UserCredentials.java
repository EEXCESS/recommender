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

/**
 * The Users Credentials to lock into partner systems
 * 
 * @author hziak
 *
 */
public class UserCredentials implements Serializable {

    private static final long serialVersionUID = -8327953285762134337L;
    private String systemId;
    private String login;
    private String securityToken;

    @Override
    public String toString() {
        return "UserCredentials [systemId=" + getSystemId() + ", login=" + getLogin() + ", securityToken=" + getSecurityToken() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLogin() == null) ? 0 : getLogin().hashCode());
        result = prime * result + ((getSecurityToken() == null) ? 0 : getSecurityToken().hashCode());
        result = prime * result + ((getSystemId() == null) ? 0 : getSystemId().hashCode());
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
        UserCredentials other = (UserCredentials) obj;
        if (getLogin() == null) {
            if (other.getLogin() != null)
                return false;
        } else if (!getLogin().equals(other.getLogin()))
            return false;
        if (getSecurityToken() == null) {
            if (other.getSecurityToken() != null)
                return false;
        } else if (!getSecurityToken().equals(other.getSecurityToken()))
            return false;
        if (getSystemId() == null) {
            if (other.getSystemId() != null)
                return false;
        } else if (!getSystemId().equals(other.getSystemId()))
            return false;
        return true;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

}