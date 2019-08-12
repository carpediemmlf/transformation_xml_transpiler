import java.util.ArrayList;

public class MergeNode extends PentNode {

    private String join_type;
    private String step1;
    private String step2;
    private String key_1;
    private String key_2;

    public MergeNode (String name, String type, String join_type, String step1, String step2, String key_1, String key_2){
        super(name,type);
        this.join_type = join_type;
        this.step1 = step1;
        this.step2 = step2;
        this.key_1 = key_1;
        this.key_2 = key_2;
    }

    // getters
    public String getJoin_type() {
        return join_type;
    }

    public String getStep1() {
        return step1;
    }

    public String getStep2() {
        return step2;
    }

    public String getKey_1() {
        return key_1;
    }

    public String getKey_2() {
        return key_2;
    }
}

