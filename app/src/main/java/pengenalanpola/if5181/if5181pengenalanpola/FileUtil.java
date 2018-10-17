package pengenalanpola.if5181.if5181pengenalanpola;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {

    private static final String DIRECTORY = "/storage/emulated/0/Documents";
    public static void write(String filename, String[] text) {
        File file = new File(DIRECTORY, filename);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for(String s : text) {
                bufferedWriter.write(s);
                bufferedWriter.write("\r\n");
            }
            bufferedWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public static String[] load(String filename) {
        String line;
        File file = new File(DIRECTORY, filename);
        ArrayList<String> text = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null) {
                text.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
        }
        return text.toArray(new String[text.size()]);
    }
}
