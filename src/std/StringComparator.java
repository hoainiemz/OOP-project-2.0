package std;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
    @Override
    public int compare(String p1, String p2) {
        return p1.compareTo(p2);
    }
}
