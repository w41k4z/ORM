package proj.w41k4z.orm;

public interface DataAccessObject<E, ID> {

    public E[] findAll() throws Exception;

    public E findById(ID id) throws Exception;

    public Integer create() throws Exception;

    public Integer update() throws Exception;

    public Integer delete() throws Exception;
}
