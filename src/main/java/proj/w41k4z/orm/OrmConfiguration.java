package proj.w41k4z.orm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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
            String dataSourceClassName = (String) configFile.getConfig().get(CONFIG_DATASOURCE_CLASS_PROPERTY_NAME);
            Class<?> dataSource = dataSourceClassName == null
                    ? DefaultDataSource.class
                    : Class.forName(dataSourceClassName);
            return DataSource.loadFrom(dataSource, configFile);
        } catch (FileNotFoundException e) {
            throw new UnsupportedOperationException("The configuration file " + CONFIG_FILE_NAME
                    + " was not found on the root path of this project. Check the documentation for more information.");
        }
    }

    /**
     * This method is used to get the supported Dialect according to the database
     * URL.
     * 
     * @param databaseUrl The URL of the database
     * @return The Dialect object
     */
    public static Dialect getSupportedCorrespondingDialect(String databaseUrl) {
        String type = databaseUrl.split(":")[1];
        try {
            switch (type) {
                case "postgresql":
                    return (Dialect) Class.forName("proj.w41k4z.orm.database.dialect.PostgreSqlDialect")
                            .getConstructor()
                            .newInstance();
                case "mysql":
                    return (Dialect) Class.forName("proj.w41k4z.orm.database.dialect.MySqlDialect").getConstructor()
                            .newInstance();
                default:
                    throw new UnsupportedOperationException("The database type " + type + " is not supported.");
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            /*
             * These exceptions should not be thrown because the dialects are already
             * implemented
             */
            throw new UnsupportedOperationException("Something went wrong with the ORM dependency");
        }
    }
}
