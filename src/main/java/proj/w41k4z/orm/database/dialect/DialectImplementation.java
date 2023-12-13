package proj.w41k4z.orm.database.dialect;

import proj.w41k4z.orm.database.Dialect;

/**
 * This is an abstract class implementation of the {@link Dialect} interface. It
 * is used to format the different types of data in the database.
 */
public abstract class DialectImplementation implements Dialect {

    public String format(Object object) {
        switch (object.getClass().getName()) {
            case "java.lang.String":
                return this.formatString((String) object);
            case "java.sql.Timestamp":
                return this.formatTimestamp((java.sql.Timestamp) object);
            case "java.sql.Date":
                return this.formatDate((java.sql.Date) object);
            case "java.sql.Time":
                return this.formatTime((java.sql.Time) object);
            case "java.lang.Boolean":
                return this.formatBoolean((Boolean) object);
            default:
                return this.formatNumber((Number) object);
        }
    }

}
