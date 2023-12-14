package proj.w41k4z.orm.database.query;

import java.lang.reflect.InvocationTargetException;

import proj.w41k4z.helpers.java.JavaClass;
import proj.w41k4z.orm.annotation.Column;
import proj.w41k4z.orm.annotation.DiscriminatorColumn;
import proj.w41k4z.orm.annotation.DiscriminatorValue;
import proj.w41k4z.orm.annotation.relationship.Inheritance;
import proj.w41k4z.orm.annotation.relationship.Join;
import proj.w41k4z.orm.annotation.relationship.OneToMany;
import proj.w41k4z.orm.database.Dialect;
import proj.w41k4z.orm.database.request.NativeQueryBuilder;
import proj.w41k4z.orm.enumeration.InheritanceType;
import proj.w41k4z.orm.spec.EntityAccess;
import proj.w41k4z.orm.spec.EntityChild;
import proj.w41k4z.orm.spec.EntityField;
import proj.w41k4z.orm.spec.EntityMetadata;

/**
 * OQL (Object Query Language) is a class used to generate query on object,
 * specially for entity object.
 * As we all know, it is difficult to query directly on the target table when
 * dealing with relationship and inheritance. The purpose of this class is to
 * simplify and solve this kind of problem.
 * 
 * EAGER loading is intentionally not supported (Limited to one layer).
 */
public class OQL {

    private QueryType queryType;
    private Object entity;
    private EntityMetadata entityMetadata;
    private Dialect dialect;
    private StringBuilder objectQuery;

    public OQL(QueryType type, Object entity, Dialect dialect) {
        this.objectQuery = new StringBuilder();
        this.queryType = type;
        this.entity = entity;
        this.entityMetadata = EntityAccess.getEntityMetadata(entity.getClass());
        this.dialect = dialect;
    }

    /**
     * This method is used to translate the OQL to a Native Query
     * 
     * @return a NativeQueryBuilder according to the translated
     *         OQL
     * @throws InvocationTargetException if the getter (for DMQ) method is not
     *                                   accessible
     * @throws IllegalArgumentException  if the getter (for DMQ) method is not
     *                                   accessible
     * @throws IllegalAccessException    if the getter (for DMQ) method is not
     *                                   accessible
     * @throws NoSuchMethodException     if the getter (for DMQ) method is not found
     */
    public NativeQueryBuilder toNativeQuery()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.build();
        String tableName = this.entityMetadata.getTableName();
        switch (this.queryType) {
            case GET:
                return new NativeQueryBuilder(tableName,
                        this.objectQuery.toString().replaceAll("GET", "SELECT").replaceAll("OF", "FROM"));
            case ADD:
                return new NativeQueryBuilder(tableName,
                        this.objectQuery.toString().replaceAll("ADD", "INSERT").replaceAll("TO", "INTO"));
            case CHANGE:
                return new NativeQueryBuilder(tableName,
                        this.objectQuery.toString().replaceAll("CHANGE", "UPDATE").replaceAll("REPLACE", "SET"));
            case REMOVE:
                return new NativeQueryBuilder(tableName,
                        this.objectQuery.toString().replaceAll("REMOVE", "DELETE"));
            default:
                return null;
        }
    }

    /**
     * This method builds the object query according to the query type
     * 
     * @throws InvocationTargetException if the getter method is not accessible
     * @throws IllegalArgumentException  if the getter method is not accessible
     * @throws IllegalAccessException    if the getter method is not accessible
     * @throws NoSuchMethodException     if the getter method is not found
     */
    private void build()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // query type clause
        this.objectQuery.append(this.queryType.toString().concat(" "));
        switch (this.queryType) {
            case GET:
                this.buildGETQuery();
                break;
            case ADD:
                this.buildADDQuery();
                break;
            case CHANGE:
                this.buildCHANGEQuery();
                break;
            case REMOVE:
                this.buildREMOVEQuery();
                break;
        }
    }

    /**
     * This method build a GET query
     */
    private void buildGETQuery() {
        // target column clause
        this.objectQuery.append(this.GETColumnTarget());
        // perimeter clause
        this.objectQuery.append(" OF " + this.GETPerimeterClause());
        // restriction clause
        this.objectQuery.append(this.GETRestrictionClause());
    }

    /**
     * This method build an ADD query
     * 
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private void buildADDQuery()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // perimeter clause
        this.objectQuery.append("TO " + this.entityMetadata.getTableName());
        // target column clause
        this.objectQuery.append(this.ADDColumnTarget());
        // value clause
        this.objectQuery.append(" VALUES " + this.ADDColumnValue());
    }

    /**
     * This method build a CHANGE query
     * 
     * @throws NoSuchMethodException     if the getter method is not found
     * @throws IllegalAccessException    if the getter method is not accessible
     * @throws IllegalArgumentException  if the getter method is not accessible
     * @throws InvocationTargetException if the getter method is not accessible
     */
    private void buildCHANGEQuery()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // target column clause
        this.objectQuery.append(this.entityMetadata.getTableName());
        // paired column value clause
        this.objectQuery.append(" REPLACE " + this.CHANGEPairedColumnValue());
        // restriction clause
        this.objectQuery.append(this.MQRestrictionClause());
    }

    /**
     * This method build a REMOVE query
     * 
     * @throws NoSuchMethodException     if the getter method is not found
     * @throws IllegalAccessException    if the getter method is not accessible
     * @throws IllegalArgumentException  if the getter method is not accessible
     * @throws InvocationTargetException if the getter method is not accessible
     */
    private void buildREMOVEQuery()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // perimeter clause
        this.objectQuery.append("FROM " + this.entityMetadata.getTableName());
        // restriction clause
        this.objectQuery.append(this.MQRestrictionClause());
    }

    /**
     * This method returns the column target of the query. Mainly used with GET
     * QueryType
     * 
     * @return a String concatenated of all concerned column
     */
    private String GETColumnTarget() {
        StringBuilder columnTarget = new StringBuilder();

        // inherited + current column fields;
        for (EntityField column : this.entityMetadata.getAllEntityFields()) {
            columnTarget.append(column.getFullColumnName() + ", ");
        }

        // child columns
        for (EntityChild child : this.entityMetadata.getRelatedEntityChildren()) {
            EntityField[] childColumns = EntityAccess.getAllEntityFields(child.getEntityClass(), null);
            for (EntityField column : childColumns) {
                columnTarget.append(
                        "_" + child.getRank() + "_" + column.getFullColumnName() + "_" + child.getRank() + ", ");
            }
        }
        columnTarget.delete(columnTarget.length() - 2, columnTarget.length());

        return columnTarget.toString();
    }

    /**
     * This method returns the perimeter clause of the query. Only used with GET
     * QueryType
     * 
     * @return a String concatenated of all the concerned table
     */
    private String GETPerimeterClause() {
        StringBuilder perimeterClause = new StringBuilder();

        Class<?>[] mainEntityClasses = this.entityMetadata.getRelatedEntityClasses();
        String mainTableName = this.entityMetadata.getTableName();
        String mainTableIdColumnName = EntityAccess.getId(mainEntityClasses[0], null).getColumnName();
        perimeterClause.append(mainTableName);
        // This part only applies on JOINED_TABLE inheritance type
        for (int i = 1; i < mainEntityClasses.length; i++) {
            String relatedTableName = EntityAccess.getTableName(mainEntityClasses[i]);
            String relatedTableIdColumnName = EntityAccess.getId(mainEntityClasses[i], null).getColumnName();

            perimeterClause.append(" LEFT JOIN ");
            perimeterClause.append(relatedTableName);
            perimeterClause.append(" ON ");
            perimeterClause.append(mainTableName + "." + mainTableIdColumnName);
            perimeterClause.append(" = ");
            perimeterClause.append(relatedTableName + "." + relatedTableIdColumnName);
        }

        // Joining children
        for (EntityChild child : this.entityMetadata.getRelatedEntityChildren()) {
            Class<?>[] childMainEntityClasses = EntityAccess.getRelatedEntityClasses(child.getEntityClass());
            String childMainTableName = EntityAccess.getTableName(childMainEntityClasses[0]);
            String childMainTableNameAlias = "_" + child.getRank() + "_" + childMainTableName;
            String childIdColumnName = EntityAccess.getId(child.getEntityClass(), null).getColumnName();

            // Operation depends on the relationship type
            if (EntityField.isRelatedEntityField(child.getField())) {
                /* Single Object Relationship */
                String joinColumnName = child.getField().getAnnotation(Column.class).name();

                perimeterClause.append(" LEFT JOIN ");
                perimeterClause.append(childMainTableName + " " + childMainTableNameAlias);
                perimeterClause.append(" ON ");
                perimeterClause.append(mainTableName + "." + joinColumnName);
                perimeterClause.append(" = ");
                perimeterClause.append(childMainTableNameAlias + "." + childIdColumnName);
            } else {
                /* Collection Object RelationShip */
                String joinTableName = child.getField().getAnnotation(Join.class).table();
                String joinColumnName = child.getField().getAnnotation(Join.class).joinColumn();
                String inverseJoinColumnName = child.getField().getAnnotation(Join.class).inverseJoinColumn();

                perimeterClause.append(" LEFT JOIN ");
                if (child.getRelationshipAnnotation().equals(OneToMany.class)) {
                    // One to many
                    if (inverseJoinColumnName.equals("")) {
                        throw new UnsupportedOperationException("The field `" + child.getField().getName()
                                + "` @Join annotation is missing the inverseJoinColumn attribute. This attribute is mandatory for OneToMany relationship. Source: `"
                                + child.getField().getDeclaringClass().getSimpleName() + "`");
                    }

                    perimeterClause.append(childMainTableName + " " + childMainTableNameAlias);
                    perimeterClause.append(" ON ");
                    perimeterClause.append(mainTableName + "." + mainTableIdColumnName);
                    perimeterClause.append(" = ");
                    perimeterClause.append(childMainTableNameAlias + "." + inverseJoinColumnName);
                } else {
                    // Many to many
                    if (joinTableName.equals("") || joinColumnName.equals("") || inverseJoinColumnName.equals("")) {
                        throw new UnsupportedOperationException("The field `" + child.getField().getName()
                                + "` @Join annotation is missing one of the attributes. All the attributes are mandatory for ManyToMany relationship. Source: `"
                                + child.getField().getDeclaringClass().getSimpleName() + "`");
                    }

                    // Joining the join table
                    perimeterClause.append(joinTableName);
                    perimeterClause.append(" ON ");
                    perimeterClause.append(mainTableName + "." + mainTableIdColumnName);
                    perimeterClause.append(" = ");
                    perimeterClause.append(joinTableName + "." + joinColumnName);
                    // Joining the child main table
                    perimeterClause.append(" LEFT JOIN ");
                    perimeterClause.append(childMainTableName + " " + childMainTableNameAlias);
                    perimeterClause.append(" ON ");
                    perimeterClause.append(joinTableName + "." + inverseJoinColumnName);
                    perimeterClause.append(" = ");
                    perimeterClause.append(childMainTableNameAlias + "." + childIdColumnName);
                }
            }
            /*
             * No other option as an EntityChild can only be instantiated wether with a
             * * @OneToMany, a @ManyToMany, @OneToOne or @ManyToOne annotation.
             */

            // This part only applies on JOINED_TABLE inheritance type
            for (int i = 1; i < childMainEntityClasses.length; i++) {
                String relatedTableName = EntityAccess.getTableName(childMainEntityClasses[i]);
                String relatedTableIdColumnName = EntityAccess.getId(childMainEntityClasses[i], null).getColumnName();

                perimeterClause.append(" LEFT JOIN ");
                perimeterClause.append(relatedTableName);
                perimeterClause.append(" ON ");
                perimeterClause.append(relatedTableName + "." + relatedTableIdColumnName);
                perimeterClause.append(" = ");
                perimeterClause.append(childMainTableName + "." + childIdColumnName);
            }
        }

        return perimeterClause.toString();
    }

    /**
     * This method returns the restriction clause of the query.
     * This mainly applies on SAME_TABLE inheritance type
     * 
     * @return a String concatenated of the restriction
     */
    private String GETRestrictionClause() {
        StringBuilder restrictionClause = new StringBuilder("");
        Class<?> entityClass = this.entityMetadata.getEntityClass();
        Class<?> superClass = this.entityMetadata.getEntityClass().getSuperclass();

        // This part only applies on SAME_TABLE inheritance type
        if (!superClass.equals(Object.class) && superClass.isAnnotationPresent(Inheritance.class)) {
            if (superClass.getAnnotation(Inheritance.class).type().equals(InheritanceType.SAME_TABLE)) {
                // Checking if the super class entity is annotated with @DiscriminatorColumn
                if (!superClass.isAnnotationPresent(DiscriminatorColumn.class)) {
                    throw new UnsupportedOperationException("The entity `" + superClass.getSimpleName()
                            + "` is not annotated with @DiscriminatorColumn. SAME_TABLE inheritance needs this annotation.");
                }
                // Checking if the entity is annotated with @DiscriminatorValue
                if (!entityClass.isAnnotationPresent(DiscriminatorValue.class)) {
                    throw new UnsupportedOperationException("The entity `" + entityClass.getSimpleName()
                            + "` is not annotated with @DiscriminatorValue. SAME_TABLE inheritance needs this annotation to be able to work with this entity.");
                }
                String discriminatorColumnName = superClass.getAnnotation(DiscriminatorColumn.class).value();
                String discriminatorColumnValue = entityClass.getAnnotation(DiscriminatorValue.class).value();

                restrictionClause.append(" WHERE ");
                restrictionClause.append(discriminatorColumnName);
                restrictionClause.append(" = '");
                restrictionClause.append(discriminatorColumnValue);
                restrictionClause.append("'");
            }
        }

        return restrictionClause.toString();
    }

    /**
     * This method returns the column target. Only used with ADD QueryType
     * 
     * @return a String concatenated of all concerned column
     */
    private String ADDColumnTarget() {
        StringBuilder columnTarget = new StringBuilder("(");

        for (EntityField column : this.entityMetadata.getEntityFields()) {
            columnTarget.append(column.getColumnName() + ", ");
        }
        // Only occurs on SAME_TABLE inheritance type
        Class<?> superClass = this.entityMetadata.getEntityClass().getSuperclass();
        if (!superClass.equals(Object.class) && superClass.isAnnotationPresent(Inheritance.class)) {
            if (superClass.getAnnotation(Inheritance.class).type().equals(InheritanceType.SAME_TABLE)) {
                // Checking if the super class entity is annotated with @DiscriminatorColumn
                if (!superClass.isAnnotationPresent(DiscriminatorColumn.class)) {
                    throw new UnsupportedOperationException("The entity `" + superClass.getSimpleName()
                            + "` is not annotated with @DiscriminatorColumn. SAME_TABLE inheritance needs this annotation.");
                }
                columnTarget.append(superClass.getAnnotation(DiscriminatorColumn.class).value() + ", ");
            }
        }
        columnTarget.delete(columnTarget.length() - 2, columnTarget.length());
        columnTarget.append(")");

        return columnTarget.toString();
    }

    /**
     * This method returns the column value. Only used with ADD QueryType
     * 
     * @return a String concatenated of all concerned column value
     * @throws NoSuchMethodException     if the getter method is not found
     * @throws IllegalAccessException    if the getter method is not accessible
     * @throws IllegalArgumentException  if the getter method is not accessible
     * @throws InvocationTargetException if the getter method is not accessible
     */
    private String ADDColumnValue()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        StringBuilder columnValue = new StringBuilder("(");

        for (EntityField column : this.entityMetadata.getEntityFields()) {
            Object fieldValue = null;
            if (EntityField.isRelatedEntityField(column.getField())) {
                Object relatedEntityFieldValue = JavaClass.getObjectFieldValue(this.entity, column.getField());
                fieldValue = relatedEntityFieldValue == null ? null
                        : JavaClass.getObjectFieldValue(relatedEntityFieldValue,
                                EntityAccess.getId(column.getEntityClass(), null).getField());
            } else {
                fieldValue = JavaClass.getObjectFieldValue(this.entity, column.getField());
            }

            String value = "NULL";
            if (fieldValue != null) {
                value = this.dialect.format(fieldValue);
            }

            if (!EntityField.isNullable(column.getField()) && value.equals("NULL")) {
                throw new UnsupportedOperationException("The field `" + column.getField().getName()
                        + "` is not nullable. You can't set it to null.");
            }

            columnValue.append(value + ", ");
        }
        // Only occurs on SAME_TABLE inheritance type
        Class<?> superClass = this.entityMetadata.getEntityClass().getSuperclass();
        Class<?> entityClass = this.entityMetadata.getEntityClass();
        if (!superClass.equals(Object.class) && superClass.isAnnotationPresent(Inheritance.class)) {
            if (superClass.getAnnotation(Inheritance.class).type().equals(InheritanceType.SAME_TABLE)) {
                // Checking if the super class entity is annotated with @DiscriminatorColumn
                if (!entityClass.isAnnotationPresent(DiscriminatorValue.class)) {
                    throw new UnsupportedOperationException("The entity `" + superClass.getSimpleName()
                            + "` is not annotated with @DiscriminatorValue. SAME_TABLE inheritance needs this annotation to be able to work with this entity.");
                }
                columnValue.append("'" + entityClass.getAnnotation(DiscriminatorValue.class).value() + "', ");
            }
        }
        columnValue.delete(columnValue.length() - 2, columnValue.length());
        columnValue.append(")");

        return columnValue.toString();
    }

    /**
     * This method returns the paired column value. Only used with CHANGE QueryType
     * 
     * @return a String concatenated of all concerned column value
     * @throws NoSuchMethodException     if the getter method is not found
     * @throws IllegalAccessException    if the getter method is not accessible
     * @throws IllegalArgumentException  if the getter method is not accessible
     * @throws InvocationTargetException if the getter method is not accessible
     */
    private String CHANGEPairedColumnValue() throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        StringBuilder pairedColumnValue = new StringBuilder();
        EntityField id = EntityAccess.getId(this.entityMetadata.getEntityClass(), null);

        for (EntityField column : this.entityMetadata.getEntityFields()) {
            if (column.getField().getName().equals(id.getField().getName())) {
                continue;
            }

            Object fieldValue = null;
            if (EntityField.isRelatedEntityField(column.getField())) {
                Object relatedEntityFieldValue = JavaClass.getObjectFieldValue(this.entity, column.getField());
                fieldValue = relatedEntityFieldValue == null ? null
                        : JavaClass.getObjectFieldValue(relatedEntityFieldValue,
                                EntityAccess.getId(column.getEntityClass(), null).getField());
            } else {
                fieldValue = JavaClass.getObjectFieldValue(this.entity, column.getField());
            }

            // NULL value are ignored
            if (fieldValue == null) {
                continue;
            } else {
                String value = this.dialect.format(fieldValue);
                pairedColumnValue.append(column.getColumnName() + " = " + value + ", ");
            }
        }
        if (pairedColumnValue.toString().endsWith(", ")) {
            pairedColumnValue.delete(pairedColumnValue.length() - 2, pairedColumnValue.length());
        }
        return pairedColumnValue.toString();
    }

    /**
     * This method returns the CHANGE query restriction clause.
     * 
     * @return a String concatenated of the restriction
     * @throws NoSuchMethodException     if the getter method is not found
     * @throws IllegalAccessException    if the getter method is not accessible
     * @throws IllegalArgumentException  if the getter method is not accessible
     * @throws InvocationTargetException if the getter method is not accessible
     */
    private String MQRestrictionClause()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        EntityField id = EntityAccess.getId(this.entity.getClass(), null);
        Object idValue = JavaClass.getObjectFieldValue(this.entity, id.getField());
        if (idValue == null) {
            throw new UnsupportedOperationException("The field `" + id.getField().getName()
                    + "` is null. You can't do changes on an entity with a null id.");
        }
        String value = this.dialect.format(idValue);
        return " WHERE " + EntityAccess.getId(this.entity.getClass(), null).getColumnName() + " = " + value;
    }
}