package proj.w41k4z.orm;

import proj.w41k4z.orm.database.DatabaseConnection;
import proj.w41k4z.orm.database.Repository;

public abstract class EntityManager<T> implements Repository<T> {

    public T[] findAll(DatabaseConnection connection) {

        return null;
    }
}
