
import java.io.*;
import java.util.*;

/**
 * Created by zackpeng on 12/6/16.
 */
public class Sort {

    public static void main(String[] args) throws IOException {
        //1. file read
        BufferedReader br = new BufferedReader(new FileReader("/Users/yangmeng/BigDataProgramming/GitHubOrgsEmotion/EmotionAnalysis/orgsrank.txt"));

        List<String> text_content_list = new ArrayList<>();
        while (br.readLine() != null) {
            text_content_list.add(br.readLine());
        }

        br.close();

        //2. sort
        Collections.sort(text_content_list, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                Long cnt1 = Long.parseLong(str1.split(" +")[1].trim());
                Long cnt2 = Long.parseLong(str2.split(" +")[1].trim());
                if (-cnt1 + cnt2 < 0) {
                    return -1;
                } else if (-cnt1 + cnt2 == 0) {
                    return 0;
                } else {
                    return 1;
                }
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
