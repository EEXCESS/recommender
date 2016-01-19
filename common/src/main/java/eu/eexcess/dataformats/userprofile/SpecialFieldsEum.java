package eu.eexcess.dataformats.userprofile;

/**
 * Created by hziak on 23.11.15.
 */

/**
 * enum for fields
 */
public enum SpecialFieldsEum {
    Person("Person"), Location("Location"), Organization("Organization"), Misc("Misc");

    public final String fieldName;

    SpecialFieldsEum(String fieldname) {
        this.fieldName = fieldname;
    }

}

