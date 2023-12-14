package proj.w41k4z.orm.database;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.OrmConfiguration;
import proj.w41k4z.orm.database.query.OQL;
import proj.w41k4z.orm.database.query.QueryType;
import proj.w41k4z.orm.database.request.Condition;
import proj.w41k4z.orm.database.request.NativeQueryBuilder;
import proj.w41k4z.orm.database.request.Operator;
import proj.w41k4z.orm.spec.EntityAccess;
import proj.w41k4z.orm.spec.EntityField;
import proj.w41k4z.orm.spec.EntityMapping;

public abstract class Repository<E, ID> {

    @SuppressWarnings("unchecked")
    public E[] findAll(DatabaseConnection databaseConnection, Condition condition)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException,
            IllegalArgumentException, SecurityException, SQLException, ClassNotFoundException, IOException {
        if (databaseConnection == null) {
            return this.findAll(condition);
        }
        OQL objectQueryLanguage = new OQL(QueryType.GET, this, databaseConnection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        nativeQueryBuilder.appendCondition(condition);
        QueryExecutor queryExecutor = new QueryExecutor();
        Object[] result = EntityMapping.map(
                (ResultSet) queryExecutor.executeRequest(nativeQueryBuilder.getRequest().toString(),
                        databaseConnection.getConnection()),
                this.getClass());
        E[] entities = (E[]) Array.newInstance(this.getClass(), result.length);
        for (int i = 0; i < result.length; i++) {
            entities[i] = (E) result[i];
        }
        return entities;
    }

    public E[] findAll(Condition condition)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        DatabaseConnection databaseConnection = new DatabaseConnection(OrmConfiguration.getDataSource());
        E[] results = this.findAll(databaseConnection, condition);
        databaseConnection.close();
        return results;
    }

    public E[] findAll(DatabaseConnection databaseConnection) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, IllegalArgumentException, SecurityException, SQLException,
            ClassNotFoundException, IOException {
        return this.findAll(databaseConnection, null);
    }

    public E[] findAll()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        DatabaseConnection databaseConnection = new DatabaseConnection(OrmConfiguration.getDataSource());
        E[] results = this.findAll(databaseConnection);
        databaseConnection.close();
        return results;
    }

    public E findOne(DatabaseConnection connection, Condition condition)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException,
            IllegalArgumentException, SecurityException, ClassNotFoundException, SQLException, IOException {
        if (connection == null) {
            return this.findOne(condition);
        }
        E[] results = this.findAll(connection, condition);
        return results.length > 0 ? results[0] : null;
    }

    public E findOne(Condition condition)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        DatabaseConnection databaseConnection = new DatabaseConnection(OrmConfiguration.getDataSource());
        E result = this.findOne(databaseConnection, condition);
        databaseConnection.close();
        return result;
    }

    public E findById(DatabaseConnection connection, ID id) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, IllegalArgumentException, SecurityException, SQLException,
            ClassNotFoundException, IOException {
        if (connection == null) {
            return this.findById(id);
        }
        String tableName = EntityAccess.getTableName(this.getClass());
        String column = EntityAccess.getId(this.getClass(), null).getColumnName() + "__of__" + tableName;
        return this.findOne(connection, Condition.WHERE(column, Operator.E, id));
    }

    public E findById(ID id) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, IllegalArgumentException, SecurityException, SQLException,
            ClassNotFoundException, IOException {
        DatabaseConnection databaseConnection = new DatabaseConnection(OrmConfiguration.getDataSource());
        E result = this.findById(databaseConnection, id);
        databaseConnection.close();
        return result;
    }

    @SuppressWarnings("unchecked")
    public E findById()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        EntityField entityId = EntityAccess.getId(this.getClass(), null);
        Object idValue = JavaClass.getObjectFieldValue(this, entityId.getField());
        return this.findById((ID) idValue);
    }
}
