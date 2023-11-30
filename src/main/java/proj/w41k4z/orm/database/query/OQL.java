package proj.w41k4z.orm.database.query;

public class OQL {

    private QueryType queryType;
    private StringBuilder objectQuery;

    private OQL() {
        objectQuery = new StringBuilder();
    }

    public String translate() {
        return null;
    }

    private void build() {
        objectQuery.append(queryType.toString().concat(" "));
    }
}

// GET entity.Parent p WITH p.child1, p.child2 WHERE <condition>