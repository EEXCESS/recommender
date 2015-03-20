
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
    "category",
    "id",
    "label",
    "latitude",
    "longitude",
    "match",
    "media",
    "preview",
    "subtitle",
    "thumbnail",
    "title",
    "type",
    "view"
})
public class Doc {

    @JsonProperty("category")
    private String category;
    @JsonProperty("id")
    private String id;
    @JsonProperty("label")
    private String label;
    @JsonProperty("latitude")
    private Object latitude;
    @JsonProperty("longitude")
    private Object longitude;
    @JsonProperty("match")
    private List<Object> match = new ArrayList<Object>();
    @JsonProperty("media")
    private String media;
    @JsonProperty("preview")
    private String preview;
    @JsonProperty("subtitle")
    private String subtitle;
    @JsonProperty("thumbnail")
    private String thumbnail;
    @JsonProperty("title")
    private String title;
    @JsonProperty("type")
    private String type;
    @JsonProperty("view")
    private List<Object> view = new ArrayList<Object>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The category
     */
    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    /**
     * 
     * @param category
     *     The category
     */
    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    public Doc withCategory(String category) {
        this.category = category;
        return this;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Doc withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * 
     * @return
     *     The label
     */
    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    /**
     * 
     * @param label
     *     The label
     */
    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    public Doc withLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * 
     * @return
     *     The latitude
     */
    @JsonProperty("latitude")
    public Object getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The latitude
     */
    @JsonProperty("latitude")
    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public Doc withLatitude(Object latitude) {
        this.latitude = latitude;
        return this;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    @JsonProperty("longitude")
    public Object getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The longitude
     */
    @JsonProperty("longitude")
    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    public Doc withLongitude(Object longitude) {
        this.longitude = longitude;
        return this;
    }

    /**
     * 
     * @return
     *     The match
     */
    @JsonProperty("match")
    public List<Object> getMatch() {
        return match;
    }

    /**
     * 
     * @param match
     *     The match
     */
    @JsonProperty("match")
    public void setMatch(List<Object> match) {
        this.match = match;
    }

    public Doc withMatch(List<Object> match) {
        this.match = match;
        return this;
    }

    /**
     * 
     * @return
     *     The media
     */
    @JsonProperty("media")
    public String getMedia() {
        return media;
    }

    /**
     * 
     * @param media
     *     The media
     */
    @JsonProperty("media")
    public void setMedia(String media) {
        this.media = media;
    }

    public Doc withMedia(String media) {
        this.media = media;
        return this;
    }

    /**
     * 
     * @return
     *     The preview
     */
    @JsonProperty("preview")
    public String getPreview() {
        return preview;
    }

    /**
     * 
     * @param preview
     *     The preview
     */
    @JsonProperty("preview")
    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Doc withPreview(String preview) {
        this.preview = preview;
        return this;
    }

    /**
     * 
     * @return
     *     The subtitle
     */
    @JsonProperty("subtitle")
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * 
     * @param subtitle
     *     The subtitle
     */
    @JsonProperty("subtitle")
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Doc withSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    /**
     * 
     * @return
     *     The thumbnail
     */
    @JsonProperty("thumbnail")
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * 
     * @param thumbnail
     *     The thumbnail
     */
    @JsonProperty("thumbnail")
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Doc withThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    /**
     * 
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public Doc withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 
     * @return
     *     The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    public Doc withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 
     * @return
     *     The view
     */
    @JsonProperty("view")
    public List<Object> getView() {
        return view;
    }

    /**
     * 
     * @param view
     *     The view
     */
    @JsonProperty("view")
    public void setView(List<Object> view) {
        this.view = view;
    }

    public Doc withView(List<Object> view) {
        this.view = view;
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

    public Doc withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(category).append(id).append(label).append(latitude).append(longitude).append(match).append(media).append(preview).append(subtitle).append(thumbnail).append(title).append(type).append(view).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Doc) == false) {
            return false;
        }
        Doc rhs = ((Doc) other);
        return new EqualsBuilder().append(category, rhs.category).append(id, rhs.id).append(label, rhs.label).append(latitude, rhs.latitude).append(longitude, rhs.longitude).append(match, rhs.match).append(media, rhs.media).append(preview, rhs.preview).append(subtitle, rhs.subtitle).append(thumbnail, rhs.thumbnail).append(title, rhs.title).append(type, rhs.type).append(view, rhs.view).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
