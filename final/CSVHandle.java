import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class CSVHandle {

    public static TreeMap<Integer, Integer> getMapFromFile(String filepath) {
        TreeMap<Integer, Integer> mymap = new TreeMap<Integer, Integer>();

        try {
            BufferedReader csvReader = new BufferedReader(
                new FileReader(filepath)
            );
            String row;
            while ((row = csvReader.readLine()) != null) {
                String[] KV = row.split(",");
                int key = Integer.parseInt(KV[0]);
                int value = Integer.parseInt(KV[1]);
                mymap.put(key, value);
            }
            csvReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not Found. Sorry!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mymap;
    }

    public static int saveCSVfromMap(TreeMap<Integer, Integer> mymap, String filename) {
        FileWriter writer;

        try {
            writer = new FileWriter(filename);
            for (Map.Entry<Integer, Integer> myentry : mymap.entrySet()) {
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

    public static ArrayList<TreeMap<Integer, Integer>>
      splitMap(TreeMap<Integer, Integer> mymap, int nnodes)
        {
        ArrayList<TreeMap<Integer, Integer>> splitted = new ArrayList<TreeMap<Integer, Integer>>();
        int total_sz = mymap.size();
        int chunk_ceil = (int) Math.ceil((float) total_sz / nnodes);
        int chunk_floor = (int) Math.floor((float) total_sz / nnodes);
        
        for (int i = 0, j = 1; i < 1000; i = i + chunk_ceil, j++) {
            if (j != nnodes){
                TreeMap<Integer, Integer> tmp_map = new TreeMap<>(mymap.subMap(i, i + chunk_ceil));
                splitted.add(tmp_map);
            }
            else {
                TreeMap<Integer, Integer> tmp_map = new TreeMap<>(mymap.subMap(i, i + chunk_floor + 1));
                splitted.add(tmp_map);
            }

        }

        return splitted;
    }

    public static void main(String[] args) {
        String path = "./accountinfo.csv";
        TreeMap<Integer, Integer> info = new TreeMap<Integer, Integer>();
        info = getMapFromFile(path);

        System.out.println(info);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        ArrayList<TreeMap<Integer, Integer>> arrList = splitMap(info, 11);
        System.out.println(arrList.get(0));
        System.out.println("********");
        System.out.println(arrList.get(9));
        System.out.println("------------");
        System.out.println(arrList.get(10));
        System.out.println("+++++++++++++");
        System.out.println("sum[0]: "+ arrList.get(0).values().stream().reduce(0, Integer::sum));
        int range = (int) Math.floor((float) 11 * 909 / 1000);
        System.out.println(range);

        List<String> l = Arrays.asList(
                "0-A",
                "321",
                "123",
                "4000"
            );
            String cmd_orig = String.join(";", l);
            System.out.println(cmd_orig);
    }
}