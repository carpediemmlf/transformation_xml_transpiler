package com.hitachivantara.talendtopentaho.nodetype;

import java.util.ArrayList;

public class MergeNode extends PentNode {

    private String join_type;
    private String step1;
    private String step2;
    private ArrayList<String> key_1 = new ArrayList<>();
    private ArrayList<String> key_2 = new ArrayList<>();

    public MergeNode (String name, String type, String join_type, String step1, String step2, String key_1, String key_2){
        super(name,type);
        this.join_type = join_type;
        this.step1 = step1;
        this.step2 = step2;
        this.key_1.add(key_1);
        this.key_2.add(key_2);
        getSimpleInfo().put("join_type", join_type);
        getSimpleInfo().put("step1", step1);
        getSimpleInfo().put("step2", step2);
        getSimpleInfo().put("key_1", key_1);
        getSimpleInfo().put("key_2", key_2);
    }

    public MergeNode (String name, String type){
        super(name,type);
    }

    public void setJoin_type(String join_type) {
        this.join_type = join_type;
    }

    // getters
    public ArrayList<String> getKey_1() {
        return key_1;
    }

    public ArrayList<String> getKey_2() {
        return key_2;
    }

    // setters
    public void addKey_1 (String key){
        key_1.add(key);
        getSimpleInfo().put("key_1", key);
    }
    public void addKey_2 (String key){
        key_2.add(key);
        getSimpleInfo().put("key_2", key);
    }
}

