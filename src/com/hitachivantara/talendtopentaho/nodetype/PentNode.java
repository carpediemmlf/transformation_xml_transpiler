package com.hitachivantara.talendtopentaho.nodetype;

import java.util.ArrayList;
import java.util.HashMap;

public class PentNode extends XMLNode {
    private String name;
    private String type;
    private String xLoc;
    private String yLoc;
    private HashMap<String,String> simpleInfo = new HashMap<>();

    ////           WHERE ARE WE DEALING WITH SIMILAR NAMES

    public PentNode(String name, String type){
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
    @Override
    public String getName(){
        return name;
    }

    @Override
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
        simpleInfo.put("name", name);
    }

    public void setType(String type) {
        this.type = type;
        simpleInfo.put("type", type);
    }

    public void setxLoc(String xLoc) {
        this.xLoc = xLoc;
    }

    public void setyLoc(String yLoc) { this.yLoc = yLoc; }

//    public void addSimpleInfo (String infoName, String data){
//        simpleInfo.put(infoName,data);
//    }

   /* public void setFields (ArrayList<String> fields){
        simpleInfo.put("fields",fields);
    }*/
}