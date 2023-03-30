import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        ConnectToDB jdbc = new ConnectToDB();

        Cell cell = new Cell();
        SheetsandJava java = new SheetsandJava();

        String currentDay = cell.readCell();
        System.out.println(currentDay);
        boolean access = java.readGoogleDocs(currentDay);
        if (access) {
            String previousDay = cell.changeDayOnPrevious(currentDay);
            String dayBeforePrevious = cell.changeDayOnPrevious(previousDay);
            String nextDay = cell.changeCell(currentDay);
            List<List<Integer>> columns = jdbc.connectionToPostgreSql();
            switch (currentDay) {
                case "AA2:AA14":
                    java.updatingGoogleDocs(currentDay,columns.get(0));
                    break;
                case "AB2:AB14":
                    java.updatingGoogleDocs(currentDay,columns.get(0));
                    java.updatingGoogleDocs(previousDay,columns.get(1));
                    break;
                default:
                    java.updatingGoogleDocs(currentDay,columns.get(0));
                    java.updatingGoogleDocs(previousDay,columns.get(1));
                    java.updatingGoogleDocs(dayBeforePrevious,columns.get(2));
            }
            cell.writeCell(nextDay);
        }
        else System.out.println("It is a bad!");


       FilesDownloader filesDownloader = new FilesDownloader();
        String url = "https://xn--j1ab.xn--h1ae9a.xn--p1ai/auth/?backurl=%2Fdocs%2Fmaterials%2Fpath%2F%25D0%259C%25D0%25BE%25D0%25BD%25D0%25B8%25D1%2582%25D0%25BE%25D1%2580%25D0%25B8%25D0%25BD%25D0%25B3%2520%25D0%25BA%25D0%25BB%25D1%258E%25D1%2587%25D0%25B5%25D0%25B2%25D1%258B%25D1%2585%2520%25D0%25BF%25D0%25BE%25D0%25BA%25D0%25B0%25D0%25B7%25D0%25B0%25D1%2582%25D0%25B5%25D0%25BB%25D0%25B5%25D0%25B9%2F";
        filesDownloader.getHtmlCode(url);
                for (String s : FilesDownloader.urls) {
                    System.out.println(filesDownloader.parseString(filesDownloader.getHtmlCode(s)));
               }
      filesDownloader.downloadFiles();



        SftpClient sftp = new SftpClient();
        sftp.connect();
        filesDownloader.downloadFromScmks();

        filesDownloader.downloadFiles();
    }
}
