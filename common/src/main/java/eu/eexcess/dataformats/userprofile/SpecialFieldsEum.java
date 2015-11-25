package eu.eexcess.dataformats.userprofile;

/**
 * Created by hziak on 23.11.15.
 */

/**
 * enum for fields
 */
public enum SpecialFieldsEum {
    WHO("WHO"), WHERE("WHERE"), WHAT("WHAT");

    public final String fieldName;

    SpecialFieldsEum(String fieldname) {
        this.fieldName = fieldname;
    }

}

