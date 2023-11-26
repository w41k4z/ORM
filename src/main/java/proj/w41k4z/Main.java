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

public class Main {
    public static void main(String[] args) throws Exception {
        DataSource dataSource = OrmConfiguration.getDataSource();
        System.out.println(dataSource.getUrl());
        System.out.println(dataSource.getUserName());
        System.out.println(dataSource.getPassword());
        System.out.println(dataSource.getDialect().getClass().getName());
    }

    public static void tableMetadata() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/stock_management",
                "w41k4z", "w41k4z")) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Retrieve tables
            ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("Table: " + tableName);

                // Retrieve columns for each table
                ResultSet columns = metaData.getColumns(null, null, tableName, null);

                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");

                    System.out.println("   Column: " + columnName + " - Type: " + columnType);
                }

                columns.close();
            }

            tables.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void tableRelation() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/stock_management",
                "w41k4z", "w41k4z")) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Retrieve tables
            ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("Table: " + tableName);

                // Retrieve foreign keys (imported keys) for each table
                ResultSet importedKeys = metaData.getImportedKeys(null, null, tableName);

                while (importedKeys.next()) {
                    String fkTableName = importedKeys.getString("FKTABLE_NAME");
                    String fkColumnName = importedKeys.getString("FKCOLUMN_NAME");
                    String pkTableName = importedKeys.getString("PKTABLE_NAME");
                    String pkColumnName = importedKeys.getString("PKCOLUMN_NAME");

                    System.out.println("   Foreign Key: " + fkTableName + "." + fkColumnName +
                            " references " + pkTableName + "." + pkColumnName);
                }

                importedKeys.close();
            }

            tables.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}