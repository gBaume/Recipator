package cooking;

/**
 * Created by Blume Till on 08.09.2016.
 */
public class Step {

    private String object;
    private String action;

    public Step(String object, String action) {
        this.object = object;
        this.action = action;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "(" + action + "," + object + ")";
    }
}
