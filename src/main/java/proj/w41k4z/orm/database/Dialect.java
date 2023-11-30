package proj.w41k4z.orm.database;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This is an interface for the dialects. A dialect is used to format the
 * different types of data in the database.
 */
public interface Dialect {

    /**
     * This method is used to format a date.
     * 
     * @param date The date to format
     * @return The string representation of the date according to the dialect
     */
    public String formatDate(Date date);

    /**
     * This method is used to format a time.
     * 
     * @param time The time to format
     * @return The string representation of the time according to the dialect
     */
    public String formatTime(Time time);

    /**
     * This method is used to format a timestamp.
     * 
     * @param timestamp The timestamp to format
     * @return The string representation of the timestamp according to the dialect
     */
    public String formatTimestamp(Timestamp timestamp);

    /**
     * This method is used to format a string.
     * 
     * @param value The string to format
     * @return The string representation of the string according to the dialect
     */
    public String formatBoolean(boolean value);

    /**
     * This method is used to format a number.
     * 
     * @param value The number to format
     * @return The string representation of the number according to the dialect
     */
    public String formatNumber(Number value);

    /**
     * This method is used to get the SQL method request for getting a sequence next
     * value.
     * 
     * @param sequenceName The name of the sequence
     * @return The SQL method request for getting a sequence next value according to
     *         the dialect
     */
    public String getSequenceNextValString(String sequenceName);

    /**
     * This method is used to get the SQL method request for getting a sequence
     * current value.
     * 
     * @param sequenceName The name of the sequence
     * @return The SQL method request for getting a sequence current value according
     *         to the dialect
     */
    public String getSequenceCurrentValString(String sequenceName);
}
