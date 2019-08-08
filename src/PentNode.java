import java.util.ArrayList;
import java.util.HashMap;

public class PentNode {
    private String name;
    private String type;
    private String xLoc;
    private String yLoc;
    private HashMap<String,String> simpleInfo = new HashMap<>();

    ////           WHERE ARE WE DEALING WITH SIMILAR NAMES

    protected PentNode (String name, String type){
        this.name=name;
        this.type=type;
        simpleInfo.put("name",name);
        simpleInfo.put("type",type);
    }

    @Override
    public String toString() {
        return name;
    }

    // Getters
    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }

    public String getxLoc() {
        return xLoc;
    }

    public String getyLoc() {
        return yLoc;
    }

    public HashMap<String, String> getSimpleInfo() {
        return simpleInfo;
    }

    /* public String getDescription() {
        return description;
    }

    public String getFilename() {
        return filename;
    }

    public String getSeparator() {
        return separator;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public ArrayList<String> getFields() {
        return fields;
    }*/

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setxLoc(String xLoc) {
        this.xLoc = xLoc;
    }

    public void setyLoc(String yLoc) { this.yLoc = yLoc; }

    public void setDescription(String description) {
        simpleInfo.put("description",description);
    }

    public void setFilename(String filename) {
        simpleInfo.put("filename",filename);
    }

    public void setSeparator(String separator) {
        simpleInfo.put("separator",separator);
    }

    public void setEnclosure(String enclosure) {
        simpleInfo.put("enclosure",enclosure);
    }

    public void addSimpleInfo (String infoName, String data){
        simpleInfo.put(infoName,data);
    }

   /* public void setFields (ArrayList<String> fields){
        simpleInfo.put("fields",fields);
    }*/
}