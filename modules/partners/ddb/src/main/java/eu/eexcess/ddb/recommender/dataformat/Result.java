
package eu.eexcess.ddb.recommender.dataformat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "docs",
    "name",
    "numberOfDocs"
})
public class Result {

    @JsonProperty("docs")
    private List<Doc> docs = new ArrayList<Doc>();
    @JsonProperty("name")
    private String name;
    @JsonProperty("numberOfDocs")
    private Integer numberOfDocs;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The docs
     */
    @JsonProperty("docs")
    public List<Doc> getDocs() {
        return docs;
    }

    /**
     * 
     * @param docs
     *     The docs
     */
    @JsonProperty("docs")
    public void setDocs(List<Doc> docs) {
        this.docs = docs;
    }

    public Result withDocs(List<Doc> docs) {
        this.docs = docs;
        return this;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Result withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The numberOfDocs
     */
    @JsonProperty("numberOfDocs")
    public Integer getNumberOfDocs() {
        return numberOfDocs;
    }

    /**
     * 
     * @param numberOfDocs
     *     The numberOfDocs
     */
    @JsonProperty("numberOfDocs")
    public void setNumberOfDocs(Integer numberOfDocs) {
        this.numberOfDocs = numberOfDocs;
    }

    public Result withNumberOfDocs(Integer numberOfDocs) {
        this.numberOfDocs = numberOfDocs;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Result withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(docs).append(name).append(numberOfDocs).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Result) == false) {
            return false;
        }
        Result rhs = ((Result) other);
        return new EqualsBuilder().append(docs, rhs.docs).append(name, rhs.name).append(numberOfDocs, rhs.numberOfDocs).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
