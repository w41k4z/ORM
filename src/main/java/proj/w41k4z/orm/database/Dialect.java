package proj.w41k4z.orm.database;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public abstract class Dialect {

    public abstract String formatDate(Date date);

    public abstract String formatTime(Time time);

    public abstract String formatTimestamp(Timestamp timestamp);

    public abstract String formatBoolean(boolean value);

    public abstract String formatString(String value);

    public abstract String formatNumber(Number value);

    public abstract String getSequenceNextValString(String sequenceName);

    public abstract String getSequenceCurrentValString(String sequenceName);
}
