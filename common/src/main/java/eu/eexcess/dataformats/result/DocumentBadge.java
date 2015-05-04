package eu.eexcess.dataformats.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
/**
 * Document identifier for each document.
 * Can be returned as list to get the details of the documents back from the server.
 * @author hziak
 *
 */
public class DocumentBadge implements Serializable {

	private static final long serialVersionUID = -8449927031599693461L;
	
	public DocumentBadge() {
	}
	
	public DocumentBadge(String id) {
		this.id = id;
	}
	
	public DocumentBadge(String id, String uri, String provider) {
		this.id = id;
		this.uri = uri;
		this.provider = provider;
	}

	@XmlElement(name = "id")
	public String id;
	@XmlElement(name = "uri")
	public String uri;
	@XmlElement(name = "provider")
	public String provider;
	
	@XmlElement(name = "detail")
	public String detail = null;
	
	@Override
	public String toString() {
		return "DocumentBadge [id=" + id + ", uri=" + uri + ", provider="
				+ provider + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((provider == null) ? 0 : provider.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		DocumentBadge other = (DocumentBadge) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
	
	
}
