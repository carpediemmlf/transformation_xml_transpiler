import java.util.ArrayList;

public class SortNode extends PentNode {

    private ArrayList<SortField> fields = new ArrayList<>();

    public SortNode (String name, String type){
        super(name,type);
    }
    // setters
    public void addField (String fieldName){
        SortField field = new SortField(fieldName);
        fields.add(field);
    }

    // getters
    public ArrayList<SortField> getFields() {
        return fields;
    }

    ///
    public class SortField {

        private String name;
        private String ascending = null;
        private String case_sensitive = null;
        private String collator_enabled = null;
        private String collator_strength = null;
        private String presorted = null;

        public SortField (String name){
            this.name = name;
        }

        // setters
        public void setAscending(String ascending) {
            this.ascending = ascending;
        }

        public void setCase_sensitive(String case_sensitive) {
            this.case_sensitive = case_sensitive;
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
    }
}
