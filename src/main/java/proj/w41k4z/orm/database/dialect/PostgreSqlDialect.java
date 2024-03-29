package proj.w41k4z.orm.database.dialect;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the PostgreSQL dialect. It is used to format the different types of
 * data in the PostgreSQL database.
 */
public class PostgreSqlDialect extends DialectImplementation {

    @Override
    public String formatDate(Date date) {
        return "TO_DATE('" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "', 'YYYY-MM-DD')";
    }

    @Override
    public String formatTime(Time time) {
        return "'" + new SimpleDateFormat("HH:mm:ss").format(time) + "'";
    }

    @Override
    public String formatTimestamp(Timestamp timestamp) {
        return "TO_TIMESTAMP('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp)
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
