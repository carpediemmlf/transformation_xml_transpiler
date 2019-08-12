import java.util.ArrayList;
import java.util.HashMap;

public class SortNode extends PentNode {

    private ArrayList<SortField> fields = new ArrayList<>();

    public SortNode (String name, String type){
        super(name,type);
    }
    // setters
    public void addField (String fieldName, String ascending, String case_sensitive){
        SortField field = new SortField(fieldName, ascending, case_sensitive);
        fields.add(field);
    }

    // getters
    public ArrayList<SortField> getFields() {
        return fields;
    }

    ///
    public class SortField {

        private String name;
        private String ascending;
        private String case_sensitive;
        private String collator_enabled = null;
        private String collator_strength = null;
        private String presorted = null;
        HashMap<String, String> fieldInfo = new HashMap<>();

        public SortField (String name, String ascending, String case_sensitive){
            this.name = name;
            this.ascending = ascending;
            this.case_sensitive = case_sensitive;
            fieldInfo.put("name", name);
            fieldInfo.put("ascending", ascending);
            fieldInfo.put("case_sensitive", case_sensitive);

        }

        // setters
        public void setAscending(String ascending) {
            this.ascending = ascending;
            fieldInfo.put("ascending", ascending);
        }

        public void setCase_sensitive(String case_sensitive) {
            this.case_sensitive = case_sensitive;
            fieldInfo.put("case_sensitive", case_sensitive);
        }

        // getters

        public String getName() {
            return name;
        }

        public String getAscending() {
            return ascending;
        }

        public String getCase_sensitive() {
            return case_sensitive;
        }

        public HashMap<String, String> getFieldInfo() {
            return fieldInfo;
        }
    }
}
