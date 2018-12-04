package RealTime.UI.New;

import java.util.Arrays;
import java.util.List;

public class Sample {

    public static void main(String[] args) {
        List<String> c = Arrays.asList("sankar", "muthu", "ganesh");

        c.stream().forEach(System.out::println);
    }

}
