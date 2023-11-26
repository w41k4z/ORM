package proj.w41k4z.orm.database;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public interface Dialect {

    public String formatDate(Date date);

    public String formatTime(Time time);

    public String formatTimestamp(Timestamp timestamp);

    public String formatBoolean(boolean value);

    public String formatNumber(Number value);

    public String getSequenceNextValString(String sequenceName);

    public String getSequenceCurrentValString(String sequenceName);
}
