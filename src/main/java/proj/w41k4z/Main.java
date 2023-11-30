package proj.w41k4z;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import proj.w41k4z.fcr.ConfigurationFile;
import proj.w41k4z.fcr.PropertiesFile;
import proj.w41k4z.fcr.XMLFile;
import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.database.DataSource;
import proj.w41k4z.orm.database.request.Condition;
import proj.w41k4z.orm.database.request.Operator;

public class Main {
    public static void main(String[] args) throws Exception {
        // String val1 = "1";
        // String val2 = "2";
        // String val3 = "3";
        // String val4 = "4";
        // System.out.println(Condition.WHERE("z", Operator.E, val1)
        // .OR("a", Operator.ENW, val2)
        // .AND("b", Operator.G, val3)
        // .AND("c", Operator.L, val4).getCondition());

        test();
    }

    public static void test() throws ClassNotFoundException {
        Class.forName("OQL");
    }
}