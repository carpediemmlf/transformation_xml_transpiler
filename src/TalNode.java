import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TalNode extends XMLNode {
    private String name;
    private String type;
    private String posX;
    private String posY;
    private HashMap<String, String> simpleInfo = new HashMap<>();
    private ArrayList<HashMap<String, ArrayList<String>>> tableInfo = new ArrayList<>();

    private PentNode headPentNode;
    private PentNode tailPentNode;
    private boolean headPentNodeSet = false;
    private boolean tailPentNodeSet = false;
    private String fileName;

    public void setHeadPentNode(PentNode inputHeadPentNode) {
        headPentNode = inputHeadPentNode;
        headPentNodeSet = true;
    }

    public void setTailPentNode(PentNode inputTailPentNode) {
        tailPentNode = inputTailPentNode;
        tailPentNodeSet = true;
    }

    public PentNode getHeadPentNode() {
        try {
            if (headPentNodeSet) {
                return headPentNode;
            }
            else {
                throw new ExceptionInInitializerError("Head PentNode not set.");
            }

        } catch (ExceptionInInitializerError e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public PentNode getTailPentNode() {
        try {
            if (tailPentNodeSet) {
                return tailPentNode;
            }
            else {
                throw new ExceptionInInitializerError("Tail PentNode not set.");
            }

        } catch (ExceptionInInitializerError e) {
            System.out.println(e.toString());
        }
        return null;
    }

    protected TalNode(String name, String type) {
        this.name = name;
        this.type = type;
        simpleInfo.put("name", name);
        simpleInfo.put("type", type);
    }

    public void setPosition (String posX, String posY){
        this.posX = posX;
        this.posY = posY;
    }

    public String getPosX() {
        return posX;
    }
    public String getPosY() {
        return posY;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    public void setType(String type) {
        this.type = type;
    }
    @Override
    public String getType() { return type; }

    public void addSimpleInfo (String key, String data){
        simpleInfo.put(key, data);
    }
    public HashMap<String, String> getSimpleInfo() {
        return simpleInfo;
    }

    public ArrayList<HashMap<String, ArrayList<String>>> getTableInfo() {
        return tableInfo;
    }

    public HashMap<String, ArrayList<String>> addTable (String tableName){
        HashMap<String, ArrayList<String>> table = new HashMap<>();
        ArrayList<String> nameArr = new ArrayList<>();
        nameArr.add(tableName);
        table.put("TableName", nameArr);
        tableInfo.add(table);
        return table;
    }
    public void addTableInfo (HashMap<String, ArrayList<String>> table, String key, String data){
        ArrayList<String> arr = new ArrayList<>();
        arr.add(data);
        table.put(key, arr);
    }
    public void printTable (){
        for (HashMap h : tableInfo){
            Iterator it = h.keySet().iterator();
            System.out.println("                      NEW TABLE");
            System.out.println("======================================================");
            while (it.hasNext()){
                String ref = (String) it.next();
                ArrayList<String> arrayList = (ArrayList<String>) h.get(ref);
                System.out.println(ref);
                for (String s : arrayList){
                    System.out.println(s);
                }
            }
            System.out.println("========================================================");
        }
    }

    @Override
    public String toString() {
        return name;
    }

    //public String getFileName(){return;}

}
