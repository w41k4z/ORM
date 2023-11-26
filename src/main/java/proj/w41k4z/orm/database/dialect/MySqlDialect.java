package proj.w41k4z.orm.database.dialect;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import proj.w41k4z.orm.database.Dialect;

public class MySqlDialect implements Dialect {

    @Override
    public String formatDate(Date date) {
        return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "'";
    }

    @Override
    public String formatTime(Time time) {
        return "'" + new SimpleDateFormat("HH:mm:ss").format(time) + "'";
    }

    @Override
    public String formatTimestamp(Timestamp timestamp) {
        return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp) + "'";
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
    public String getSequenceNextValString(String sequenceName) {
        return "NEXTVAL('" + sequenceName + "')";
    }

    @Override
    public String getSequenceCurrentValString(String sequenceName) {
        return "CURRVAL('" + sequenceName + "')";
    }

}
