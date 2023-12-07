package proj.w41k4z.orm.database.query;

/**
 * An enumeration of all supported query method
 */
public enum QueryType {
    /**
     * Aka SELECT
     */
    GET,
    /**
     * Aka INSERT
     */
    ADD,
    /**
     * Aka UPDATE
     */
    CHANGE,
    /**
     * Aka DELETE
     */
    REMOVE
}
