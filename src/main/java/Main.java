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
        String url = "url";
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
