package proj.w41k4z.orm.database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import proj.w41k4z.fcr.ConfigurationFile;
import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.annotation.ConfigProperty;

/**
 * A class needed to configure the database connection (URL, Username,
 * Password...).
 * This class is used to load the DataSource from the configuration file by
 * default, using its implementation {@link DefaultDataSource}.
 * You can always create your own DataSource implementation and load it from the
 * configuration file using the method
 * {@link DataSource#loadFrom(Class, ConfigurationFile) loadFrom(Class,
 * ConfigurationFile)} or set it manually.
 */
public abstract class DataSource {

    protected DataSource() {
    }

    /**
     * 
     * This method is used to load the DataSource from the configuration file.
     * 
     * @param dataSourceImplClass The class of the DataSource implementation
     * @param configFile          The configuration file
     * @return The DataSource object
     * @throws NoSuchMethodException     If the constructor of the DataSource
     *                                   implementation is not found
     * @throws InvocationTargetException If the constructor of the DataSource
     *                                   implementation cannot be invoked
     * @throws IllegalAccessException    If the constructor of the DataSource
     *                                   implementation cannot be accessed
     * @throws InstantiationException    If the DataSource implementation cannot be
     *                                   instantiated
     * @throws IllegalArgumentException  If the DataSource implementation is not a
     *                                   subclass of DataSource
     * @throws SecurityException         If the DataSource implementation cannot be
     *                                   accessed
     */
    public static DataSource loadFrom(Class<?> dataSourceImplClass, ConfigurationFile configFile)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException,
            IllegalArgumentException, SecurityException {
        if (!DataSource.class.isAssignableFrom(dataSourceImplClass)) {
            throw new IllegalArgumentException("The class must be a subclass of DataSource");
        }
        Map<String, Object> dataSourceProperties = configFile.getConfig();
        Field[] fields = JavaClass.getFieldByAnnotation(dataSourceImplClass, ConfigProperty.class);
        DataSource dataSource = (DataSource) dataSourceImplClass.getConstructor().newInstance();
        for (Field field : fields) {
            String propertyName = field.getAnnotation(ConfigProperty.class).value();
            JavaClass.setObjectFieldValue(dataSource, dataSourceProperties.get(propertyName), field);
        }
        return dataSource;
    }

    /**
     * This method is used to get the URL of the database.
     * 
     * @return The URL of the database
     */
    public abstract String getUrl();

    /**
     * This method is used to get the username to be used when connecting to the
     * database.
     * 
     * @return The username to access the database
     */
    public abstract String getUserName();

    /**
     * This method is used to get the password to be used when connecting to the
     * database.
     * 
     * @return The password to access the database
     */
    public abstract String getPassword();
}
