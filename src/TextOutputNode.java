import java.util.ArrayList;
import java.util.HashMap;

public class TextOutputNode extends PentNode {

    private String filename;
    private String extension;
    private String separator = null;
    private String enclosure = null;
    private ArrayList<TextOutputField> fields = new ArrayList<>();
    private HashMap<String, String> fileInfo = new HashMap<>();

    public TextOutputNode (String name, String type, String filename){
        super(name,type);
        this.filename= filename;
        fileInfo.put("name", filename);
    }
    // setters
    public void addField (String fieldName){
            TextOutputField field = new TextOutputField(fieldName);
        fields.add(field);
    }

    public void setSeparator (String separator){
        this.separator=separator;
        getSimpleInfo().put("separator", separator);
    }

    public void setEnclosure (String enclosure){
        this.enclosure=enclosure;
        getSimpleInfo().put("enclosure", enclosure);
    }

    public void setExtension(String extension) {
        this.extension = extension;
        fileInfo.put("extension", extension);
    }

    public void setAppend (String append){
        fileInfo.put("append", append);
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

    public ArrayList<TextOutputField> getFields() {
        return fields;
    }

    public HashMap<String, String> getFileInfo() {
        return fileInfo;
    }

    ///
    public class TextOutputField {

        private String name;
        private String type;
        private String format;
        private String currency;
        private String decimal;
        private String group;
        private String length;
        private String nullif;
        private String precision;
        private String trim_type;

        HashMap<String,String> fieldInfo = new HashMap<>();

        public TextOutputField (String name){
            this.name = name;
            fieldInfo.put("name", name);
        }

        public String getName() {
            return name;
        }

        public HashMap<String, String> getFieldInfo() {
            return fieldInfo;
        }
    }
}
