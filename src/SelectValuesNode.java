import java.util.ArrayList;

public class SelectValuesNode extends PentNode {

    private ArrayList<SelectValuesField> fields = new ArrayList<>();

    public SelectValuesNode (String name, String type){
        super(name,type);
    }
    // setters
    public void addField (String fieldName, String fieldRename){
        SelectValuesField field = new SelectValuesField(fieldName, fieldRename);
        fields.add(field);
    }

    // getters
    public ArrayList<SelectValuesField> getFields() {
        return fields;
    }

    ///
    public class SelectValuesField {

        private String name;
        private String rename;

        public SelectValuesField (String name, String rename){
            this.name = name;
            this.rename = rename;
        }

        public String getName() {
            return name;
        }

        public String getRename() {
            return rename;
        }
    }
}
