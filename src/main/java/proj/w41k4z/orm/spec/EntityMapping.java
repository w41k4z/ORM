package proj.w41k4z.orm.spec;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import proj.w41k4z.helpers.java.JavaClass;

public abstract class EntityMapping {

    public static Object[] map(ResultSet resultSet, Class<?> type)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException,
            InstantiationException, IllegalArgumentException, SecurityException {
        /*
         * Map key: entityClass.entityIdValue
         * Map value: entity
         */
        Map<String, Object> entities = new HashMap<>();
        /*
         * Map key: entityClass.entityIdValue_entityChildIdClass
         * Map value: entityChild as a list
         */
        Map<String, List<Object>> entitiesChildren = new HashMap<>();

        // The entity metadata containing all the entity fields and related children
        EntityMetadata entityMetadata = EntityAccess.getEntityMetadata(type);
        EntityChild[] entityRelatedChildren = entityMetadata.getRelatedEntityChildren();
        // The entity id field needed for the entities key
        EntityField entityFieldId = EntityAccess.getId(type, null);
        // The entity children's id field needed for the entities children key
        EntityField[] entityFieldChildrenId = new EntityField[entityRelatedChildren.length];
        int index = 0;
        for (EntityChild entityChild : entityRelatedChildren) {
            entityFieldChildrenId[index++] = EntityAccess.getId(entityChild.getEntityClass(), null);
        }
        // The entity children's fields
        EntityField[][] entityChildrenFields = new EntityField[entityRelatedChildren.length][];
        index = 0;
        for (EntityChild entityChild : entityRelatedChildren) {
            entityChildrenFields[index++] = EntityAccess.getAllEntityFields(entityChild.getEntityClass(), null);
        }

        while (resultSet.next()) {
            Object entityRowId = resultSet.getObject(entityFieldId.getAliasColumnName());
            String entityMapKey = entityFieldId.getTableName() + "." + entityRowId.toString();

            // Adding the entity to the map if it doesn't exist
            if (!entities.containsKey(entityMapKey)) {
                Object entity = type.getConstructor().newInstance();
                entities.put(entityMapKey, entity);
                // Set the entity column field value
                for (EntityField entityField : entityMetadata.getAllEntityFields()) {
                    JavaClass.setObjectFieldValue(entity, resultSet.getObject(entityField.getAliasColumnName()),
                            entityField.getField());
                }
            }

            // The entity related children
            for (int i = 0; i < entityFieldChildrenId.length; i++) {
                Object entityChild = null;
                Object entityChildId = resultSet.getObject(
                        entityFieldChildrenId[i].getAliasColumnName() + "_" + entityRelatedChildren[i].getRank());
                // Skipping operation if the entity child id is null
                if (entityChildId == null) {
                    continue;
                }

                String entityChildMapKey = entityMapKey + "_" + entityRelatedChildren[i].getRank() + "_"
                        + entityFieldChildrenId[i].getTableName();
                Class<?> entityChildClass = entityRelatedChildren[i].getField().getType().isArray()
                        ? entityRelatedChildren[i]
                                .getField().getType().getComponentType()
                        : entityRelatedChildren[i].getField().getType();
                entityChild = entityChildClass.getConstructor().newInstance();
                // Set the entityChild column field value
                for (EntityField entityField : entityChildrenFields[i]) {
                    JavaClass.setObjectFieldValue(entityChild, resultSet.getObject(entityField.getAliasColumnName()),
                            entityField.getField());
                }
                if (entitiesChildren.containsKey(entityChildMapKey)) {
                    entitiesChildren.get(entityChildMapKey).add(entityChild);
                } else {
                    ArrayList<Object> entityChildren = new ArrayList<>();
                    entitiesChildren.put(entityChildMapKey, entityChildren);
                    entityChildren.add(entityChild);
                }
            }
        }

        List<Object> entitiesList = new ArrayList<>();
        for (Map.Entry<String, Object> entity : entities.entrySet()) {
            entitiesList.add(entity.getValue());
            index = 0;
            for (EntityChild entityChild : entityRelatedChildren) {
                List<Object> entityChildren = entitiesChildren
                        .get(entity.getKey() + "_" + entityFieldChildrenId[index++].getTableName());
                String relationshipTypeClassName = entityChild.getRelationshipAnnotation().getSimpleName();
                if (relationshipTypeClassName.equals("OneToOne") || relationshipTypeClassName.equals("ManyToOne")) {
                    // Single object relationship
                    JavaClass.setObjectFieldValue(entity.getValue(), entityChildren.get(0), entityChild.getField());
                } else {
                    // Multiple objects relationship
                    JavaClass.setObjectFieldValue(entity.getValue(), entityChildren.toArray(), entityChild.getField());
                }
            }
        }
        return entitiesList.toArray();
    }
}
