package proj.w41k4z.orm.database.dialect;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import proj.w41k4z.orm.database.Dialect;

/**
 * This is the PostgreSQL dialect. It is used to format the different types of
 * data in the PostgreSQL database.
 */
public class PostgreSqlDialect implements Dialect {

    @Override
    public String formatDate(Date date) {
        return "TO_CHAR(" + date.getTime() + ", 'YYYY-MM-DD HH24:MI:SS')";
    }

    @Override
    public String formatTime(Time time) {
        return "TO_CHAR('" + new SimpleDateFormat("HH:mm:ss").format(time) + "', 'HH24:MI:SS')";
    }

    @Override
    public String formatTimestamp(Timestamp timestamp) {
        return "TO_CHAR('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp)
                + "', 'YYYY-MM-DD HH24:MI:SS')";
    }

    @Override
    public String formatBoolean(boolean value) {
        return value ? "TRUE" : "FALSE";
    }

    @Override
    public String formatNumber(Number value) {
        return String.valueOf(value);
    }

    @Override
    public String formatString(String value) {
        return "'" + value + "'";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "NEXTVAL('" + sequenceName + "')";
    }

    @Override
    public String getSequenceCurrentValString(String sequenceName) {
        return "CURRVAL('" + sequenceName + "')";
    }

}
