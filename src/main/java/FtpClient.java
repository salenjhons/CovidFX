import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FtpClient {

    private FTPClient ftpClient = null;

    public void connect() {
        String username = "username";
        String host = "host";
        int portnumber = 21;
        String password = "password";

        ftpClient = new FTPClient();

        try {
            ftpClient.connect(host, portnumber);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("Ошибка соединения с FTP сервером.");
            }
            ftpClient.login(username, password);
            System.out.println("Соедение с FTP сервером установлено.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void donwload() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String previousDate = dateFormat.format(new Date(calendar.getTimeInMillis()));
        String remoteSource = String.format("/kc_bit_dud/VATS_%s.csv", previousDate);
        String vatsFile = String.format("path\\VATS_%s.csv", previousDate);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(vatsFile));
            boolean succes = ftpClient.retrieveFile(remoteSource, fos);
            System.out.println("Загружаю файл с FTP сервера.");
            if (succes) {
                System.out.println("Файл с FTP сервера загружен.");
            }else System.err.println("Ошибка. Файл не загружен. Проверь наличие файла на FTP сервере!");
            fos.close();
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    if (ftpClient.isConnected()) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
