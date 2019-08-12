import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;

public class TalNode {
    private String name;
    private String type;
    private HashMap<String, String> talendInfo = new HashMap<>();
    private String fileName;
    ReadXMLFile talInfo = new ReadXMLFile();


    public void getMaps(String fileName) throws IOException, SAXException, ParserConfigurationException {
        talendInfo = talInfo.getVertices(fileName);
    }


    public TalNode() {
    }

    protected TalNode(String name, String type) {
        this.name = name;
        this.type = type;
        talendInfo.put("name", name);
        talendInfo.put("type", type);
    }


    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }


    //public String getFileName(){return;}

}
