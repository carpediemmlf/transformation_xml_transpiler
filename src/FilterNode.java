import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;

public class FilterNode extends PentNode {

    private String send_true_to;
    private String send_false_to;
    private ArrayList<Condition> conditions = new ArrayList<>();

    public FilterNode (String name, String type){
        super(name,type);
    }
    // setters
    public void addCondition (String negated, String leftValue, String function, String rightValue){
        Condition condition = new Condition(negated, leftValue, function, rightValue);
        conditions.add(condition);
    }

    public void setSend_true_to(String send_true_to) {
        this.send_true_to = send_true_to;
        getSimpleInfo().put("send_true_to", send_true_to);
    }

    public void setSend_false_to(String send_false_to) {
        this.send_false_to = send_false_to;
        getSimpleInfo().put("send_false_to", send_false_to);
    }

    // getters
    public ArrayList<Condition> getConditons() {
        return conditions;
    }

    ///
    public class Condition {

        /*private String negated;
        private String function;
        private String leftvalue;
        private String rightvalue;*/
        HashMap<String, String> conditionInfo = new HashMap<>();

        public Condition (String negated, String leftValue, String function, String rightValue){
            /*this.negated = negated;
            this.leftvalue = leftvalue;
            this.function = function;
            this.rightvalue = rightvalue;*/
            conditionInfo.put("negated", negated);
            conditionInfo.put("leftvalue", leftValue);
            conditionInfo.put("function", function);
            conditionInfo.put("rightvalue", rightValue);

        }

        public HashMap<String, String> getConditionInfo() {
            return conditionInfo;
        }
    }
}
