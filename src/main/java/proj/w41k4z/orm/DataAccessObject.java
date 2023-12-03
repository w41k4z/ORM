package proj.w41k4z.orm;

public interface DataAccessObject<E, ID> {

    public E[] findAll();

    public E findById(ID id);

    public E create();

    public E update();

    public E delete();
}
