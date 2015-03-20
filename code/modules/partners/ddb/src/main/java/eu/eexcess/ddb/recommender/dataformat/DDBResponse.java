
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
    "numberOfResults",
    "randomSeed",
    "results"
})
public class DDBResponse {

    @JsonProperty("numberOfResults")
    private Integer numberOfResults;
    @JsonProperty("randomSeed")
    private String randomSeed;
    @JsonProperty("results")
    private List<Result> results = new ArrayList<Result>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The numberOfResults
     */
    @JsonProperty("numberOfResults")
    public Integer getNumberOfResults() {
        return numberOfResults;
    }

    /**
     * 
     * @param numberOfResults
     *     The numberOfResults
     */
    @JsonProperty("numberOfResults")
    public void setNumberOfResults(Integer numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public DDBResponse withNumberOfResults(Integer numberOfResults) {
        this.numberOfResults = numberOfResults;
        return this;
    }

    /**
     * 
     * @return
     *     The randomSeed
     */
    @JsonProperty("randomSeed")
    public String getRandomSeed() {
        return randomSeed;
    }

    /**
     * 
     * @param randomSeed
     *     The randomSeed
     */
    @JsonProperty("randomSeed")
    public void setRandomSeed(String randomSeed) {
        this.randomSeed = randomSeed;
    }

    public DDBResponse withRandomSeed(String randomSeed) {
        this.randomSeed = randomSeed;
        return this;
    }

    /**
     * 
     * @return
     *     The results
     */
    @JsonProperty("results")
    public List<Result> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    @JsonProperty("results")
    public void setResults(List<Result> results) {
        this.results = results;
    }

    public DDBResponse withResults(List<Result> results) {
        this.results = results;
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

    public DDBResponse withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(numberOfResults).append(randomSeed).append(results).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DDBResponse) == false) {
            return false;
        }
        DDBResponse rhs = ((DDBResponse) other);
        return new EqualsBuilder().append(numberOfResults, rhs.numberOfResults).append(randomSeed, rhs.randomSeed).append(results, rhs.results).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
