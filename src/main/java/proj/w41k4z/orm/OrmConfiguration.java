package proj.w41k4z.orm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import proj.w41k4z.fcr.PropertiesFile;

import proj.w41k4z.orm.database.DataSource;
import proj.w41k4z.orm.database.DefaultDataSource;
import proj.w41k4z.orm.database.Dialect;
import proj.w41k4z.orm.database.connectivity.ConnectionPoolingConfiguration;
import proj.w41k4z.orm.database.connectivity.DefaultConnectionPoolingConfiguration;

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

    /**
     * The ORM configuration file name at the root path
     */
    public static final String CONFIG_FILE_NAME = "orm.properties";

    /**
     * The default property name of the database URL from the configuration file
     */
    public static final String CONFIG_DEFAULT_DB_URL_PROPERTY = "primary.database.url";

    /**
     * The default property name of the database username from the configuration
     * file
     */
    public static final String CONFIG_DEFAULT_DB_USER_PROPERTY = "primary.database.userName";

    /**
     * The default property name of the database password from the configuration
     */
    public static final String CONFIG_DEFAULT_DB_PASSWORD_PROPERTY = "primary.database.password";

    /**
     * The property name of the custom DataSource implementation from the
     * configuration
     */
    public static final String CONFIG_DATASOURCE_CLASS_PROPERTY_NAME = "datasource.class";

    /**
     * The property name of the custom Dialect implementation from the configuration
     */
    public static final String CONFIG_DIALECT_CLASS_PROPERTY_NAME = "dialect.class";

    /**
     * The property name of the custom Dialect implementation from the configuration
     */
    public static final String CONFIG_CONNECTION_POOLING_CLASS_PROPERTY_NAME = "connection.pooling.configuration.class";

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
    public static DataSource getDataSource() {
        PropertiesFile configFile = new PropertiesFile();
        InputStream inputStream = null;
        try {
            try {
                // standalone mode
                inputStream = new FileInputStream(CONFIG_FILE_NAME);
            } catch (FileNotFoundException e) {
                // server environment mode
                inputStream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("/" + CONFIG_FILE_NAME);
                if (inputStream == null) {
                    throw new FileNotFoundException("The configuration file " + CONFIG_FILE_NAME
                            + " was not found on the root path of this project. Check the documentation for more information.");
                }
            }
            configFile.load(inputStream);
            String dataSourceClassName = (String) configFile.getConfig().get(CONFIG_DATASOURCE_CLASS_PROPERTY_NAME);
            Class<?> dataSource = dataSourceClassName == null
                    ? DefaultDataSource.class
                    : Class.forName(dataSourceClassName);
            return DataSource.loadFrom(dataSource, configFile);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Something went wrong with the ORM dependency. Source: "
                    + e.getClass().getName() + " - " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // closing the stream is not important
                }
            }
        }
    }

    public static ConnectionPoolingConfiguration getConnectionPoolingConfiguration() {
        PropertiesFile configFile = new PropertiesFile();
        InputStream inputStream = null;
        try {
            try {
                // standalone mode
                inputStream = new FileInputStream(CONFIG_FILE_NAME);
            } catch (FileNotFoundException e) {
                // server environment mode
                inputStream = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("/" + CONFIG_FILE_NAME);
                if (inputStream == null) {
                    throw new FileNotFoundException("The configuration file " + CONFIG_FILE_NAME
                            + " was not found on the root path of this project. Check the documentation for more information.");
                }
            }
            configFile.load(inputStream);
            String connectionPoolingConfigurationClassName = (String) configFile.getConfig()
                    .get(CONFIG_CONNECTION_POOLING_CLASS_PROPERTY_NAME);
            Class<?> connectionPoolingConfiguration = connectionPoolingConfigurationClassName == null
                    ? DefaultConnectionPoolingConfiguration.class
                    : Class.forName(connectionPoolingConfigurationClassName);
            return (ConnectionPoolingConfiguration) connectionPoolingConfiguration.getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Something went wrong with the ORM dependency. Source: "
                    + e.getClass().getName() + " - " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // closing the stream is not important
                }
            }
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
