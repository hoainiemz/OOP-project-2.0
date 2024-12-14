package std;

public class StringFunction {
    public static int sToI(String str) {
        int s = 0;
        for (int i = 0; i < str.length(); i++) {
            if ('0' <= str.charAt(i) && str.charAt(i) <= '9') {
                s = s * 10 + str.charAt(i) - '0';
            }
        }
        return s;
    }

    public static int nthIndexOf(String str, int ch, int n) {
        int i = 0;
        for (int j = 0; j < n; j++) {
            i = str.indexOf(ch, i + 1);
            if (i == -1) {
                return -1;
            }
        }
        return i;
    }

    public static String iToS(int x) {
        StringBuilder str =  new StringBuilder();
        while (x != 0) {
            str.insert(0, x % 10);
            x /= 10;
        }
        return str.toString();
    }

    static public String getJSONFilePath(String user) {
        return "data/crawled/" + user + ".json";
    }
}
