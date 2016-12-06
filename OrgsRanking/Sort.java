
import java.io.*;
import java.util.*;

/**
 * Created by zackpeng on 12/6/16.
 */
public class Sort {

    public static void main(String[] args) throws IOException {
        //1. file read
        BufferedReader br = new BufferedReader(new FileReader("地址"));

        List<String> text_content_list = new ArrayList<>();
        while (br.readLine() != null) {
            text_content_list.add(br.readLine());
        }

        br.close();

        //2. sort
        Collections.sort(text_content_list, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                String cnt1 = str1.split("\t")[1].trim();
                String cnt2 = str2.split("\t")[1].trim();
                return cnt1.compareTo(cnt2);
            }
        });

        //3. file write out
        BufferedWriter bw = new BufferedWriter(new FileWriter("sortedCompanyList"));
        for (String eachLine : text_content_list) {
            bw.write(eachLine + "\n");
        }
        bw.close();

        return;
    }
}
