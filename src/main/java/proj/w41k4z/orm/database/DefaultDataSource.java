package proj.w41k4z.orm.database;

import proj.w41k4z.orm.annotation.ConfigProperty;

/**
 * This class is the default implementation of the DataSource class.
 * When working with this ORM, this will be the default DataSource.
 */
public final class DefaultDataSource extends DataSource {

    @ConfigProperty("database.url")
    private String url;

    @ConfigProperty("database.userName")
    private String userName;

    @ConfigProperty("database.password")
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
