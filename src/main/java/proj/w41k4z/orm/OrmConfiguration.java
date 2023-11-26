package proj.w41k4z.orm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import proj.w41k4z.fcr.ConfigurationFile;
import proj.w41k4z.fcr.PropertiesFile;

import proj.w41k4z.orm.database.DataSource;
import proj.w41k4z.orm.database.DefaultDataSource;
import proj.w41k4z.orm.database.Dialect;

/**
 * This class is used to manage the ORM configuration from the orm.properties
 * file.
 * This file must be located in the root of the classpath when using this ORM.
 * This configuration file must contains the following properties by default:
 * <ul>
 * <li>database.url: The URL of the database</li>
 * <li>database.userName: The username of the database</li>
 * <li>database.password: The password of the database</li>
 * </ul>
 * For a different DataSource implementation:
 * <ul>
 * <li>datasource.class: The class of the DataSource implementation</li>
 * </ul>
 * And for a different Dialect implementation:
 * <ul>
 * <li>dialect.class: The class of the Dialect implementation</li>
 * </ul>
 */
public final class OrmConfiguration {

    public static final String CONFIG_FILE_NAME = "orm.properties";
    public static final String CONFIG_DATASOURCE_CLASS_PROPERTY_NAME = "datasource.class";
    public static final String CONFIG_DIALECT_CLASS_PROPERTY_NAME = "dialect.class";

    /**
     * This method is used to get the DataSource from the configuration file.
     * 
     * @return The DataSource object
     * @throws IOException               If the configuration file cannot be loaded
     * @throws ClassNotFoundException    If the DataSource class cannot be found
     * @throws SecurityException         If the DataSource class cannot be accessed
     * @throws IllegalArgumentException  If the DataSource class is not a subclass
     *                                   of DataSource
     * @throws InstantiationException    If the DataSource class cannot be
     *                                   instantiated
     * @throws IllegalAccessException    If the DataSource class cannot be accessed
     * @throws InvocationTargetException If the DataSource class cannot be invoked
     * @throws NoSuchMethodException     If the constructor of the DataSource class
     *                                   cannot be found
     */
    public static DataSource getDataSource()
            throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, IllegalArgumentException, SecurityException {
        PropertiesFile configFile = new PropertiesFile();
        try {
            configFile.load(CONFIG_FILE_NAME);
            Class<?> dataSource = (String) configFile.getConfig().get(CONFIG_DATASOURCE_CLASS_PROPERTY_NAME) == null
                    ? DefaultDataSource.class
                    : Class.forName((String) configFile.getConfig().get(CONFIG_DATASOURCE_CLASS_PROPERTY_NAME));
            return DataSource.loadFrom(dataSource, configFile);
        } catch (FileNotFoundException e) {
            throw new UnsupportedOperationException("The configuration file " + CONFIG_FILE_NAME
                    + " was not found on the root path of this project. Check the documentation for more information.");
        }
    }

    public static Dialect getDialect() throws IOException {
        PropertiesFile configFile = new PropertiesFile();
        try {
            configFile.load(CONFIG_FILE_NAME);
            String dialectClassString = (String) configFile.getConfig().get(CONFIG_DIALECT_CLASS_PROPERTY_NAME);
            Dialect dialect = null;
            if (dialectClassString == null) {

            } else {

            }
            return dialect;
        } catch (FileNotFoundException e) {
            throw new UnsupportedOperationException("The configuration file " + CONFIG_FILE_NAME
                    + " was not found on the root path of this project. Check the documentation for more information.");
        }
    }

    private static String getSupportedCorrespondingDialect(String type) {
        switch (type) {
            case "org.postgresql.Driver":
                return "proj.w41k4z.orm.database.dialect.PostgreSqlDialect";
            case "com.mysql.cj.jdbc.Driver":
                return "proj.w41k4z.orm.database.dialect.MySqlDialect";
            case "org.mariadb.jdbc.Driver":
                return "proj.w41k4z.orm.database.dialect.MariaDbDialect";
            case "org.h2.Driver":
                return "proj.w41k4z.orm.database.dialect.H2Dialect";
            case "org.sqlite.JDBC":
                return "proj.w41k4z.orm.database.dialect.SqliteDialect";
            default:
                throw new UnsupportedOperationException("The database type " + type + " is not supported.");
        }
    }
}
