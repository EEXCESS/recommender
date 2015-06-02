package eu.eexcess.dataformats.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
/**
 * The partners response state
 * @author hziak
 *
 */
public class PartnerResponseState implements Serializable {

	private static final long serialVersionUID = -5684391973830630216L;

	@XmlElement(name = "systemID")
	private String systemID;
	@XmlElement(name = "success")
	private Boolean success;
	@XmlElement(name = "errorMessage")
	private String errorMessage;
	@Override
	public String toString() {
		return "PartnerResponseState [systemID=" + getSystemID() + ", success="
				+ getSuccess() + ", errorMessage=" + getErrorMessage() + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getErrorMessage() == null) ? 0 : getErrorMessage().hashCode());
		result = prime * result + ((getSuccess() == null) ? 0 : getSuccess().hashCode());
		result = prime * result
				+ ((getSystemID() == null) ? 0 : getSystemID().hashCode());
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
		if (getErrorMessage() == null) {
			if (other.getErrorMessage() != null)
				return false;
		} else if (!getErrorMessage().equals(other.getErrorMessage()))
			return false;
		if (getSuccess() == null) {
			if (other.getSuccess() != null)
				return false;
		} else if (!getSuccess().equals(other.getSuccess()))
			return false;
		if (getSystemID() == null) {
			if (other.getSystemID() != null)
				return false;
		} else if (!getSystemID().equals(other.getSystemID()))
			return false;
		return true;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getSystemID() {
		return systemID;
	}
	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}
	
	
}
