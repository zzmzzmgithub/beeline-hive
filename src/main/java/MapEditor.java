import java.util.Map;

/**
 * Created by 1085950 on 6/1/2016.
 */
public class MapEditor {
    private Map<String, String> result;

    public MapEditor(Map<String, String> result) {
        this.result = result;
    }

    public void editMap() {
        this.result.put("application_id", "application_12978374_3423");
    }
}
