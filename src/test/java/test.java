import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author: owo
 * @version: 1.0
 * @description:
 * @date: 2024-08-14  18:17
 */
public class test {
    public static void main(String[] args) {

        HashMap<Object, Object> map = new HashMap<>();
        map.put("1", "1");
        map.put("2", "2");
        map.forEach((o, o2) -> {

        });

        Set<Map.Entry<Object, Object>> entries = map.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            System.out.println(entry);
        }

    }
}
