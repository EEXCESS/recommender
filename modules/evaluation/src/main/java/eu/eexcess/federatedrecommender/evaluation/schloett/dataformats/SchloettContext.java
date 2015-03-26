/**
 * 
 */
package eu.eexcess.federatedrecommender.evaluation.schloett.dataformats;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author hziak
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchloettContext implements Serializable {

	private static final long serialVersionUID = -4212198238229541907L;
	public String url;
	public List<String> paragraphs;
	public String seltectedText;
	
}
