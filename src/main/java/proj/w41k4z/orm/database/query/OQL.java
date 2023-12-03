package proj.w41k4z.orm.database.query;

import java.lang.reflect.Field;

import proj.w41k4z.orm.spec.EntityField;
import proj.w41k4z.orm.spec.EntityManager;

public class OQL {

    private QueryType queryType;
    private Class<?> entityClass;
    private StringBuilder objectQuery;

    private OQL() {
        objectQuery = new StringBuilder();
    }

    public OQL(QueryType type, Class<?> entity) {
        this();
    }

    public String translate() {
        return null;
    }

    private void build() {
        objectQuery.append(queryType.toString().concat(" "));
    }

    private void buildGETQuery() {
        EntityField[] columns = EntityManager.getEntityColumns(entityClass);
    }
}

// GET entity.Parent p WITH p.child1, p.child2 WHERE <condition>