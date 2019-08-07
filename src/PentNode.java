public class PentNode {
    private String name;
    private String type;

    public PentNode (){}

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName(){
        return name;
    }
    public String getType(){
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
}
