package proj.w41k4z.orm.database.query;

import proj.w41k4z.orm.annotation.DiscriminatorColumn;
import proj.w41k4z.orm.annotation.DiscriminatorValue;
import proj.w41k4z.orm.annotation.relationship.Inheritance;
import proj.w41k4z.orm.annotation.relationship.Key;
import proj.w41k4z.orm.database.request.NativeQueryBuilder;
import proj.w41k4z.orm.spec.EntityChild;
import proj.w41k4z.orm.spec.EntityField;
import proj.w41k4z.orm.spec.EntityManager;

/**
 * OQL (Object Query Language) is a class used to generate query on object,
 * specially for entity object.
 * As we all know, it is difficult to query directly on the target table when
 * dealing with relationship and inheritance. The purpose of this class is to
 * simplify and solve this kind of problem
 */
public class OQL {

    private QueryType queryType;
    private Class<?> entityClass;
    private StringBuilder objectQuery;

    public OQL(QueryType type, Class<?> entity) {
        this.objectQuery = new StringBuilder();
        this.queryType = type;
        this.entityClass = entity;
        this.build();
    }

    /**
     * This method is used to translate the OQL to a Native Query
     * 
     * @return a NativeQueryBuilder according to the translated
     *         OQL
     */
    public NativeQueryBuilder toNativeQuery() {
        return new NativeQueryBuilder(this.objectQuery.toString().replaceAll("GET", "SELECT").replaceAll("OF", "FROM"));
    }

    /**
     * This method returns the column target of the query. Only used with GET
     * QueryType
     * 
     * @return a String concatenated of all concerned column
     */
    private String getColumnTarget() {
        StringBuilder columnTarget = new StringBuilder();

        // main columns
        EntityField[] mainColumns = EntityManager.getEntityColumns(this.entityClass, null);
        for (EntityField column : mainColumns) {
            columnTarget.append(column.getFullColumnName() + ", ");
        }

        // child columns
        EntityChild[] entityChilds = EntityManager.getEntityChild(this.entityClass);
        for (EntityChild child : entityChilds) {
            EntityField[] chilcColumns = EntityManager.getEntityColumns(child.getEntityClass(), null);
            for (EntityField column : chilcColumns) {
                columnTarget.append(column.getFullColumnName() + ", ");
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
    private String getPerimeterClause() {
        StringBuilder perimeterClause = new StringBuilder();

        // Only applies on JOINED_TABLE inheritance type
        Class<?>[] mainTables = EntityManager.getRelatedEntityTableClasses(this.entityClass);
        String mainTableName = EntityManager.getEntityTableName(mainTables[0]);
        perimeterClause.append(mainTableName);
        for (int i = 1; i < mainTables.length; i++) {
            String parentTable = EntityManager.getEntityTableName(mainTables[i]);
            perimeterClause.append(" LEFT JOIN " + EntityManager.getEntityTableName(mainTables[i]));
            perimeterClause
                    .append(" ON " + mainTableName + "." + EntityManager.getEntityId(mainTables[0]).getColumnName()
                            + " = " + parentTable + "." + EntityManager.getEntityId(mainTables[i]).getColumnName());
        }

        // Child tables
        EntityChild[] children = EntityManager.getEntityChild(this.entityClass);
        for (EntityChild child : children) {
            // main table
            Class<?>[] childMainTableClasses = EntityManager.getRelatedEntityTableClasses(child.getEntityClass());
            String childMainTable = EntityManager.getEntityTableName(childMainTableClasses[0]);
            perimeterClause.append(" LEFT JOIN " + childMainTable);
            perimeterClause.append(" ON " + childMainTable + "."
                    + EntityManager.getEntityId(child.getEntityClass()).getColumnName() + " = " + mainTableName + "."
                    + child.getField().getAnnotation(Key.class).column());

            // child table dependency table
            for (int i = 1; i < childMainTableClasses.length; i++) {
                String parentTable = EntityManager.getEntityTableName(childMainTableClasses[i]);
                perimeterClause.append(" LEFT JOIN " + parentTable);
                perimeterClause
                        .append(" ON " + childMainTable + "."
                                + EntityManager.getEntityId(childMainTableClasses[0]).getColumnName()
                                + " = " + parentTable + "."
                                + parentTable);
            }
        }

        return perimeterClause.toString();
    }

    /**
     * This method returns the restriction clause of the query.
     * 
     * @return a String concatenated of the restriction
     */
    private String getRestrictionClause() {
        StringBuilder restrictionClause = new StringBuilder("");
        if (!this.entityClass.getSuperclass().equals(Object.class)
                && this.entityClass.getSuperclass().isAnnotationPresent(Inheritance.class)) {
            switch (this.entityClass.getSuperclass().getAnnotation(Inheritance.class).type()) {
                case SAME_TABLE:
                    if (!this.entityClass.getSuperclass().isAnnotationPresent(DiscriminatorColumn.class)) {
                        throw new UnsupportedOperationException("The entity "
                                + this.entityClass.getSuperclass().getSimpleName()
                                + " is not annotated with @DiscriminatorColumn. SAME_TABLE inheritance needs this annotation.");
                    }
                    if (!this.entityClass.isAnnotationPresent(DiscriminatorValue.class)) {
                        throw new UnsupportedOperationException("The entity " + this.entityClass.getSimpleName()
                                + " is not annotated with @DiscriminatorValue. SAME_TABLE inheritance needs this annotation to be able to work with this entity.");
                    }
                    restrictionClause.append(" WHERE ");
                    restrictionClause
                            .append(this.entityClass.getSuperclass().getAnnotation(DiscriminatorColumn.class).value());
                    restrictionClause.append(" = '");
                    restrictionClause.append(this.entityClass.getAnnotation(DiscriminatorValue.class).value());
                    restrictionClause.append("'");
                    break;

                default:
                    break;
            }
        }

        return restrictionClause.toString();
    }

    /**
     * This method builds the object query according to the query type
     */
    private void build() {
        // query type clause
        this.objectQuery.append(this.queryType.toString().concat(" "));
        switch (this.queryType) {
            case GET:
                this.buildGETQuery();
                break;
            default:
                throw new UnsupportedOperationException("Unimplemented code.");
        }
    }

    /**
     * This method build a GET query
     */
    private void buildGETQuery() {
        // target column clause
        this.objectQuery.append(this.getColumnTarget());
        this.objectQuery.append(" OF " + this.getPerimeterClause());
        this.objectQuery.append(this.getRestrictionClause());
    }
}