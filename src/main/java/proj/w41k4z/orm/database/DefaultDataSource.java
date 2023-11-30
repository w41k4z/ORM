package proj.w41k4z.orm.database;

import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.annotation.ConfigProperty;

/**
 * This class is the default implementation of the DataSource class.
 * This class can not be extended, You have to create your own implementation of
 * the DataSource for specific need
 */
public final class DefaultDataSource extends DataSource {

    @ConfigProperty(OrmConfiguration.CONFIG_DEFAULT_DB_URL_PROPERTY)
    private String url;

    @ConfigProperty(OrmConfiguration.CONFIG_DEFAULT_DB_USER_PROPERTY)
    private String userName;

    @ConfigProperty(OrmConfiguration.CONFIG_DEFAULT_DB_PASSWORD_PROPERTY)
    private String password;

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
