import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class CSVHandle {

    public static Map<String, String> getMapFromFile(String filepath) {
        Map<String, String> mymap = new HashMap<String, String>();

        try {
            BufferedReader csvReader = new BufferedReader(
                new FileReader(filepath)
            );
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] KV = row.split(",");
                mymap.put(KV[0], KV[1]);
            }
            csvReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not Found. Sorry!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mymap;
    }

    public static int saveCSVfromMap(Map<String, String> mymap, String filename) {
        FileWriter writer;

        try {
            writer = new FileWriter(filename);
            for (Map.Entry<String, String> myentry : mymap.entrySet()) {
                String row = myentry.toString().replace("=", ",");
                writer.append(row);
                writer.append("\n");
            }
            writer.close();
        } catch (IOException e) {
            return 1;
        }

        return 0;
    }

    public static void main(String[] args) {
        String path = "./accountinfo.csv";
        Map<String,String> info = new HashMap<String, String>();
        info.put("hola", "adios");
        info.put("1", "834791");
        info.put("2", "~~~~~~");

        saveCSVfromMap(info, "./test.csv");
    }
}