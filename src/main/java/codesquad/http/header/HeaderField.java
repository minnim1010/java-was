package codesquad.http.header;

public enum HeaderField {

    // -------------------------------------------------------------- General Header Fields

    // -------------------------------------------------------------- Request Header Fields
    ACCEPT("Accept"),

    // -------------------------------------------------------------- Response Header Fields
    DATE("Date"),

    // -------------------------------------------------------------- Entity Header Fields
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),

    // -------------------------------------------------------------- Additional Header Fields

    // -------------------------------------------------------------- Unknown Header Fields
    UNKNOWN("Unknown");


    private final String fieldName;

    HeaderField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
