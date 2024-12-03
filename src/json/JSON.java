package json;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class JSON {
    private static String directory = "data/";

    public static String getDirectory() {
        return directory;
    }

    public static void setDirectory(String directory) {
        JSON.directory = directory;
    }

    public static <T> ArrayList<T> loadArrayFromJSON(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<T> list = (ArrayList)mapper.readValue(new File(directory + file), ArrayList.class);
        System.out.println("JSON file " + file + " loaded!");
        return list;
    }

    public static Object loadObjectFromJSON(String file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Object res = mapper.readValue(new File(directory + file), Object.class);
        System.out.println("JSON file " + file + " loaded!");
        return res;
    }

    public static <T> void dumpToJSON(ArrayList<T> lst, String file) throws IOException {
        File myFile = new File(directory + file);
        myFile.createNewFile();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        objectMapper.writer(prettyPrinter).writeValue(new File(directory + file), lst);

//        System.out.println("JSON file " + file + " created!");
    }

    /**
     * check if file exist
     * @param file
     * @return
     */
    public static boolean exists(String file) {
        return (new File(directory + file)).exists();
    }
}
