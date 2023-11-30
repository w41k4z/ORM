package proj.w41k4z.orm.database;

public interface Repository<T> {

    public T[] findAll();

    public T[] findAll(DatabaseConnection connection);

    public T findById();

    public T findById(DatabaseConnection connection);

}
