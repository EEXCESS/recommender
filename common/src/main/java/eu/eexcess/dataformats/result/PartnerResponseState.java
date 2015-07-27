package eu.eexcess.dataformats.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

/**
 * The partners response state
 * 
 * @author hziak
 *
 */
public class PartnerResponseState implements Serializable {

    private static final long serialVersionUID = -5684391973830630216L;

    @XmlElement(name = "systemID")
    public String systemID;
    @XmlElement(name = "success")
    public Boolean success;
    @XmlElement(name = "errorMessage")
    public String errorMessage;

    @Override
    public String toString() {
        return "PartnerResponseState [systemID=" + systemID + ", success=" + success + ", errorMessage=" + errorMessage + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
        result = prime * result + ((success == null) ? 0 : success.hashCode());
        result = prime * result + ((systemID == null) ? 0 : systemID.hashCode());
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
        PartnerResponseState other = (PartnerResponseState) obj;
        if (errorMessage == null) {
            if (other.errorMessage != null)
                return false;
        } else if (!errorMessage.equals(other.errorMessage))
            return false;
        if (success == null) {
            if (other.success != null)
                return false;
        } else if (!success.equals(other.success))
            return false;
        if (systemID == null) {
            if (other.systemID != null)
                return false;
        } else if (!systemID.equals(other.systemID))
            return false;
        return true;
    }

}
