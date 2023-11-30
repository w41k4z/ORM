package proj.w41k4z.orm.database.request;

/**
 * This class is used to add custom condition to database request.
 */
public class Condition {

    private StringBuilder condition;
    private boolean isRaw = false;

    /**
     * This class can not be instantiated.
     * You can only get an instance of it from one of its static methods
     */
    private Condition() {
    }

    /**
     * This method is used to get the condition as a StringBuilder.
     * 
     * @return The condition as a StringBuilder
     */
    public StringBuilder getCondition() {
        return condition;
    }

    /**
     * This method is used to get an instance of a Condition from a raw condition.
     * 
     * @param condition The raw condition(e.g. " WHERE id IN (1, 2, 3, 4)")
     * @return The Condition object
     */
    public static Condition raw(String condition) {
        Condition newCondition = new Condition();
        newCondition.condition = new StringBuilder(condition);
        newCondition.isRaw = true;
        return newCondition;
    }

    public static Condition WHERE(String field, Operator operator, Object value) {
        try {
            StringBuilder requestCondition = new StringBuilder(" WHERE ");
            requestCondition.append(field);
            requestCondition.append(operator.value.replace("{value}", value.toString()));

            Condition newCondition = new Condition();
            newCondition.condition = requestCondition;
            return newCondition;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(
                    "The operand value can not be null. Field: `" + field + "` after the WHERE clause");
        }
    }

    public Condition AND(String field, Operator operator, Object value) {
        if (this.isRaw) {
            throw new UnsupportedOperationException(
                    "Raw condition are not customizable");
        }
        try {
            StringBuilder requestCondition = new StringBuilder(" AND ");
            requestCondition.append(field);
            requestCondition.append(operator.value.replace("{value}", value.toString()));

            this.condition.append(requestCondition);
            return this;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(
                    "The operand value can not be null. Field: `" + field + "` after the AND statement");
        }
    }

    public Condition OR(String field, Operator operator, Object value) {
        if (this.isRaw) {
            throw new UnsupportedOperationException(
                    "Raw condition are not customizable");
        }
        try {
            StringBuilder requestCondition = new StringBuilder(" OR ");
            requestCondition.append(field);
            requestCondition.append(operator.value.replace("{value}", value.toString()));

            this.condition.append(requestCondition);
            return this;
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(
                    "The operand value can not be null. Field: `" + field + "` after the OR statement");
        }
    }
}
