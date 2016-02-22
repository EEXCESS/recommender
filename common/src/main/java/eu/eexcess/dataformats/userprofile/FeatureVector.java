package eu.eexcess.dataformats.userprofile;

/**
 * Created by hziak on 10.12.15.
 */
public class FeatureVector {

    private Double text = 0.0;
    private Double video = 0.0;
    private Double picture = 0.0;
    private Double openLicence = 0.0;
    private Double dateExisting = 0.0;
    private Double expertLevel = 0.0;

    public Double getText() {
        return text;
    }

    public void setText(Double text) {
        this.text = text;
    }

    public Double getVideo() {
        return video;
    }

    public Double getExpertLevel() {
        return expertLevel;
    }

    public void setExpertLevel(Double expertLevel) {
        this.expertLevel = expertLevel;
    }

    public void setVideo(Double video) {
        this.video = video;
    }

    public Double getPicture() {
        return picture;
    }

    public void setPicture(Double picture) {
        this.picture = picture;
    }

    public Double getOpenLicence() {
        return openLicence;
    }

    public void setOpenLicence(Double openLicence) {
        this.openLicence = openLicence;
    }

    public Double getDateExisting() {
        return dateExisting;
    }

    public void setDateExisting(Double dateExisting) {
        this.dateExisting = dateExisting;
    }

    public Double[] getVector() {
        Double[] vector = new Double[6];
        vector[0] = text;
        vector[1] = video;
        vector[2] = picture;
        vector[3] = openLicence;
        vector[4] = dateExisting;
        vector[5] = expertLevel;
        return vector;
    }

}
