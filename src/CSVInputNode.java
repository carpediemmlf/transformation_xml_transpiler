import java.util.ArrayList;

public class CSVInputNode extends PentNode {

    private String filename;
    private String separator = null;
    private String enclosure = null;
    private ArrayList<CSVInputField> fields = new ArrayList<>();

    public CSVInputNode (String name, String type, String filename){
        super(name,type);
        this.filename= filename;
    }
    // setters
    public void addField (String fieldName){
        CSVInputField field = new CSVInputField(fieldName);
        fields.add(field);
    }

    public void setSeparator (String separator){
        this.separator=separator;
    }

    public void setEnclosure (String enclosure){
        this.enclosure=enclosure;
    }

    // getters

    public String getFilename() {
        return filename;
    }

    public String getSeparator (){
        return separator;
    }

    public String getEnclosure (){
        return enclosure;
    }

    public ArrayList<CSVInputField> getFields() {
        return fields;
    }

    ///
    public class CSVInputField {

        private String name;
        private String type;
        private String format;
        private String currency;
        private String deciaml;
        private String group;
        private String length;
        private String precision;
        private String trim_type;

        public CSVInputField (String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
