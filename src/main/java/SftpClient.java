import com.jcraft.jsch.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SftpClient {

    private Session session = null;
    private FilesDownloader downloader;

    public void connect() {
        String username = "username";
        String host = "ip";
        int portnumber = 22;
        String password = "password";
        try {
            JSch jSch = new JSch();
            session = jSch.getSession(username, host, portnumber);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(20000);
            System.out.println("Connection is established");
        } catch (JSchException e) {
            e.printStackTrace();
        }

    }

    public void upload() {

        String dist = "/opt/ftp-data/minzdrav/upload";
        File directory = new File(downloader.createLocalDir());
        File[] allContents = directory.listFiles();
        Channel channel = null;
        ChannelSftp sftpChannel = null;
        System.out.println("FILES IS BEING UPLOADED!");
        if (allContents != null) {
            for (File file : allContents) {
            try {
                channel = session.openChannel("sftp");
                channel.connect();
                sftpChannel = (ChannelSftp) channel;
                sftpChannel.put(dist,file.getAbsolutePath());
            } catch (JSchException | SftpException e) {
                e.printStackTrace();
            }
        }
          if (sftpChannel!=null){
              channel.disconnect();
              sftpChannel.disconnect();}
          if (session != null) session.disconnect();
            System.out.println("ALL FILES ARE UPLOADED");
        }
    }
}
