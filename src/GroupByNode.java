import java.util.ArrayList;
import java.util.HashMap;

public class GroupByNode extends PentNode {

    private ArrayList<String> fieldsToGroupBy = new ArrayList<>();
    private ArrayList<GroupByField> fields = new ArrayList<>();

    public GroupByNode (String name, String type){
        super(name,type);
    }
    // setters
    public void addFieldToGroupBy (String fieldName){
        fieldsToGroupBy.add(fieldName);
    }

    public void addAggregateField (String aggregate, String subject, String type){
        GroupByField field = new GroupByField(aggregate, subject, type);
        fields.add(field);
    }

    // getters
    public ArrayList<String> getFieldsToGroupBy() {
        return fieldsToGroupBy;
    }

    public ArrayList<GroupByField> getFields() {
        return fields;
    }

    ///
    public class GroupByField {

        private String aggregate;
        private String subject;
        private String type;
        private String valuefield;

        HashMap<String, String > groupByFieldInfo = new HashMap<>();

        public GroupByField (String aggregate, String subject, String type){
            this.aggregate = aggregate;
            this.subject = subject;
            this.type = type;
            groupByFieldInfo.put("aggregate",aggregate);
            groupByFieldInfo.put("subject", subject);
            groupByFieldInfo.put("type", type);
        }

        // getters
        public String getAggregate() {
            return aggregate;
        }

        public String getSubject() {
            return subject;
        }

        public String getType() {
            return type;
        }

        public HashMap<String, String> getGroupByFieldInfo() {
            return groupByFieldInfo;
        }
    }
}

