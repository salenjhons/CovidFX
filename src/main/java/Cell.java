import java.io.*;
import java.util.Calendar;
import java.util.Date;

public class Cell {
    private String filePath = "path\\data.txt";

    public String changeCell(String string) {
        String cell = string.substring(0,2);
        int b = (cell.charAt(0)-65);
        StringBuilder sb = new StringBuilder();
        outerLoop:
        for (int i = b; i < 26; i++) {
            for (int j =0; j <26 ; j++) {
                char jChar = (char)(65+j);
                if (j==25) {
                    sb.append("").append((char)(65+(i+1))).append((char)(65));
                    break outerLoop;

                }
                if (jChar==cell.charAt(1)){
                    sb.append("").append(cell.charAt(0)).append((char)(65+(j+1)));
                    break outerLoop;
                }

            }
        }
        return String.format("%s%d:%s%d",sb.toString(),2,sb.toString(),14);
    }

    public String changeCellonPrevious(String string) {
        String cell = string.substring(0,2);
        int b = (cell.charAt(0)-65);
        StringBuilder sb = new StringBuilder();
        outerLoop:
        for (int i = b; i < 26; i++) {
            for (int j =0; j <26 ; j++) {
                char jChar = (char)(65+j);
                if (j==25) {
                    sb.append("").append((char)(65+(i-1))).append((char)(65));
                    break outerLoop;

                }
                if (jChar==cell.charAt(1)){
                    sb.append("").append(cell.charAt(0)).append((char)(65+(j-1)));
                    break outerLoop;
                }

            }
        }
        return sb.toString();
    }

    public String changeDayOnPrevious(String string) {
        String cell = string.substring(0,2);
        int b = (cell.charAt(0)-65);
        StringBuilder sb = new StringBuilder();
        outerLoop:
        for (int i = b; i < 26; i++) {
            for (int j =0; j <26 ; j++) {
                char jChar = (char)(65+j);
                if (jChar=='A' && cell.charAt(1)==jChar) {
                    sb.append("").append((char)(65+(i-1))).append((char)(90));
                    break outerLoop;

                }
                if (jChar==cell.charAt(1)){
                    sb.append("").append(cell.charAt(0)).append((char)(65+(j-1)));
                    break outerLoop;
                }

            }
        }
        return String.format("%s%d:%s%d",sb.toString(),2,sb.toString(),14);

    }

    public  void writeCell(String cell) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(cell.substring(0,2));
        writer.close();
    }


    public String readCell () throws IOException {
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String currentLine = reader.readLine();
        String range = String.format("%s%d:%s%d",currentLine,2,currentLine,14);
        reader.close();
        return range;
    }
}
