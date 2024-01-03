package proj.w41k4z.orm.database;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.DataAccessObject;
import proj.w41k4z.orm.annotation.Generated;
import proj.w41k4z.orm.database.connectivity.ConnectionManager;
import proj.w41k4z.orm.database.connectivity.DatabaseConnection;
import proj.w41k4z.orm.database.query.OQL;
import proj.w41k4z.orm.database.query.QueryType;
import proj.w41k4z.orm.database.request.Condition;
import proj.w41k4z.orm.database.request.NativeQueryBuilder;
import proj.w41k4z.orm.database.request.Operator;
import proj.w41k4z.orm.enumeration.GenerationType;
import proj.w41k4z.orm.spec.EntityAccess;
import proj.w41k4z.orm.spec.EntityField;
import proj.w41k4z.orm.spec.EntityMapping;

public abstract class Repository<E, ID> implements DataAccessObject<E, ID> {

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
        DatabaseConnection databaseConnection = ConnectionManager.getDatabaseConnection();
        E[] results = this.findAll(databaseConnection, condition);
        databaseConnection.close();
        return results;
    }

    public E[] findAll(DatabaseConnection databaseConnection) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, IllegalArgumentException, SecurityException, SQLException,
            ClassNotFoundException, IOException {
        return this.findAll(databaseConnection, null);
    }

    @Override
    public E[] findAll()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException, IllegalArgumentException, SecurityException, SQLException, IOException {
        DatabaseConnection databaseConnection = ConnectionManager.getDatabaseConnection();
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
        DatabaseConnection databaseConnection = ConnectionManager.getDatabaseConnection();
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

    @Override
    public E findById(ID id) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, IllegalArgumentException, SecurityException, SQLException,
            ClassNotFoundException, IOException {
        DatabaseConnection databaseConnection = ConnectionManager.getDatabaseConnection();
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

    public Integer create(DatabaseConnection connection)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            ClassNotFoundException, InstantiationException, SecurityException, IOException, SQLException {
        if (connection == null) {
            return this.create();
        }
        QueryExecutor queryExecutor = new QueryExecutor();
        EntityField entityId = EntityAccess.getId(this.getClass(), null);
        if (entityId.isGenerated()) {
            // Generation auto type are just ignored
            if (entityId.getField().getAnnotation(Generated.class).type().equals(GenerationType.SEQUENCE)) {
                String sequenceName = entityId.getField().getAnnotation(Generated.class).sequenceName();
                if (sequenceName.equals("")) {
                    throw new IllegalArgumentException(
                            "The sequence name cannot be empty for generation type SEQUENCE. Source: `"
                                    + this.getClass().getSimpleName() + "." + entityId.getField().getName() + "`");
                }
                String idPrefix = entityId.getField().getAnnotation(Generated.class).pkPrefix();
                int idLength = entityId.getField().getAnnotation(Generated.class).pkLength();
                ResultSet result = (ResultSet) queryExecutor.executeRequest(
                        "SELECT " + connection.getDataSource().getDialect().getSequenceNextValString(sequenceName),
                        connection.getConnection());
                result.next();
                String generatedId = result.getString(1);
                StringBuilder idValue = new StringBuilder(idPrefix);
                for (int i = 0; i < idLength - generatedId.length(); i++) {
                    idValue.append("0");
                }
                idValue.append(generatedId);
                JavaClass.setObjectFieldValue(this, idValue.toString(), entityId.getField());
            }
        }
        OQL objectQueryLanguage = new OQL(QueryType.ADD, this, connection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        Integer[] results = new Integer[] { -1, -1 };
        try {
            results = (Integer[]) queryExecutor.executeRequest(nativeQueryBuilder.getRequest().toString(),
                    connection.getConnection());
            // A generated id was returned
            if (results[1] != -1) {
                JavaClass.setObjectFieldValue(this, results[1], entityId.getField());
            }
        } catch (SQLException e) {
            if (connection != null && connection.getConnection() != null) {
                connection.rollback();
            }
        }
        return results[0];
    }

    @Override
    public Integer create()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            ClassNotFoundException, InstantiationException, SecurityException, IOException, SQLException {
        Integer result = -1;
        DatabaseConnection databaseConnection = null;
        try {
            databaseConnection = ConnectionManager.getDatabaseConnection();
            result = this.create(databaseConnection);
            databaseConnection.commit();
        } catch (SQLException e) {
            if (databaseConnection != null && databaseConnection.getConnection() != null) {
                databaseConnection.rollback();
            }
        } finally {
            if (databaseConnection != null && databaseConnection.getConnection() != null) {
                databaseConnection.close();
            }
        }
        return result;
    }

    public Integer update(DatabaseConnection connection)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            ClassNotFoundException, InstantiationException, SecurityException, IOException, SQLException {
        if (connection == null) {
            return this.update();
        }
        OQL objectQueryLanguage = new OQL(QueryType.CHANGE, this, connection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        QueryExecutor queryExecutor = new QueryExecutor();
        Integer[] results = new Integer[] { -1, -1 };
        try {
            results = (Integer[]) queryExecutor.executeRequest(nativeQueryBuilder.getRequest().toString(),
                    connection.getConnection());
        } catch (SQLException e) {
            if (connection != null && connection.getConnection() != null) {
                connection.rollback();
            }
        }
        return results[0];
    }

    @Override
    public Integer update()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            ClassNotFoundException, InstantiationException, SecurityException, IOException, SQLException {
        DatabaseConnection databaseConnection = null;
        Integer result = -1;
        try {
            databaseConnection = ConnectionManager.getDatabaseConnection();
            result = this.update(databaseConnection);
            databaseConnection.commit();
        } catch (SQLException error) {
            if (databaseConnection != null) {
                databaseConnection.rollback();
            }
        } finally {
            if (databaseConnection != null) {
                databaseConnection.close();
            }
        }
        return result;
    }

    public Integer delete(DatabaseConnection connection)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            ClassNotFoundException, InstantiationException, SecurityException, IOException, SQLException {
        if (connection == null) {
            return this.delete();
        }
        Integer[] results = new Integer[] { -1, -1 };
        OQL objectQueryLanguage = new OQL(QueryType.REMOVE, this, connection.getDataSource().getDialect());
        NativeQueryBuilder nativeQueryBuilder = objectQueryLanguage.toNativeQuery();
        QueryExecutor queryExecutor = new QueryExecutor();
        try {
            results = (Integer[]) queryExecutor.executeRequest(nativeQueryBuilder.getRequest().toString(),
                    connection.getConnection());
        } catch (SQLException error) {
            if (connection != null && connection.getConnection() != null) {
                connection.rollback();
            }
        }
        return results[0];
    }

    @Override
    public Integer delete()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            ClassNotFoundException, InstantiationException, SecurityException, IOException, SQLException {
        DatabaseConnection databaseConnection = null;
        Integer result = -1;
        try {
            databaseConnection = ConnectionManager.getDatabaseConnection();
            result = this.delete(databaseConnection);
            databaseConnection.commit();
        } catch (SQLException error) {
            if (databaseConnection != null) {
                databaseConnection.rollback();
            }
        } finally {
            if (databaseConnection != null) {
                databaseConnection.close();
            }
        }
        return result;
    }
}