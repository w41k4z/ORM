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

public abstract class Repository<E> {

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
        return databaseConnection == null ? this.findAll() : this.findAll(databaseConnection, null);
    }

    public E[] findAll()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        DatabaseConnection databaseConnection = new DatabaseConnection(OrmConfiguration.getDataSource());
        E[] results = this.findAll(databaseConnection);
        databaseConnection.close();
        return results;
    }

    public E findById(DatabaseConnection connection) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, IllegalArgumentException, SecurityException, SQLException,
            ClassNotFoundException, IOException {
        if (connection == null) {
            return this.findById();
        }
        String tableName = EntityAccess.getTableName(this.getClass());
        EntityField columnId = EntityAccess.getId(this.getClass(), null);
        Object columnIdValue = JavaClass.getObjectFieldValue(this, columnId.getField());
        String column = tableName + "." + columnId.getColumnName();
        E[] results = this.findAll(connection, Condition.WHERE(column, Operator.E, columnIdValue));
        return results.length > 0 ? results[0] : null;
    }

    public E findById()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        DatabaseConnection databaseConnection = new DatabaseConnection(OrmConfiguration.getDataSource());
        E result = this.findById(databaseConnection);
        databaseConnection.close();
        return result;
    }
}
