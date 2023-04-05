
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesDownloader {

    private InputStream in = null;
    private FileOutputStream fos = null;
    private HttpClient client = null;
    HttpGet auth=null;
    HttpGet request = null;
    private final String mainUrl = "main_url";

    public static final String[] urls = ["url1", "url2", "url3", "url4"];


    public FilesDownloader () {
        this.client = HttpClientBuilder.create().build();
    }

    public   String getHtmlCode(String url) {
        BufferedReader bin = null;
        StringBuilder sb = new StringBuilder();
        try {
            if (request==null) {
                request = authentication(url);
            }
            else {
                request.setURI(URI.create(url));
            }
            HttpResponse response = this.client.execute(request);
            // System.out.println(response.getStatusLine());
            in = response.getEntity().getContent();
            bin = new BufferedReader(new InputStreamReader(in));
            String str;
            while ((str = bin.readLine()) != null) {
                sb.append(str);
            }
            in.close();
            bin.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            {
                try {
                    if (in != null) in.close();
                    if (bin != null) bin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    private HttpGet getAuth (String url){
        if (auth==null) return new HttpGet(url);
        else {
            return this.auth;
        }
    }

    public HttpGet authentication (String url) {
        String login = "login";
        String password = "password";

        HttpGet request = getAuth(url);
        String auth = login + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        return request;
    }

    public  String createLocalDir () {
        String path = "path";
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String donwloadDir = dateFormat.format(new Date(System.currentTimeMillis()));
        File theDir = new File(path+donwloadDir);

        if (!theDir.exists()){
            theDir.mkdirs();
        }
        return theDir.getPath()+"\\";

    }

    private void downloadFile(String url,String filename) {

        String filePath = createLocalDir()+filename;
        try {
            request.setURI(URI.create(url));
            HttpResponse response = this.client.execute(request);
            File file = new File(filePath);
            this.in = response.getEntity().getContent();
            this.fos = new FileOutputStream(file);
            int read;
            byte[] buffer = new byte[1024];
            while ((read = this.in.read(buffer)) != -1) {
                this.fos.write(buffer, 0, read);
            }
            if(file.canRead()){
                System.out.println(String.format("%s DOWNLOADED!",filename));
            }else {
                System.out.println(String.format("File is %s is damaged", filename));
                file.delete();
            }

            this.in.close();
            this.fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            {
                try {
                    if (this.in != null) this.in.close();
                    if (this.fos != null) this.fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void downloadFromScmks() throws InterruptedException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy_MM_dd");
        String date = sd.format(createDate(-1));
        boolean done = false;
        String downloadFilepath = createLocalDir();
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\CovidFX\\chromedriver.exe");
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.setExperimentalOption("prefs", chromePrefs);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        String login = "login";
        String password = "password";
        WebDriver driver = new ChromeDriver(options);
        System.out.println("Connecting to SCMKS.RU");
        String url = "url";
        driver.get(url);
        driver.findElement(By.xpath("//*[@id=\"Username\"]")).sendKeys(login);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//*[@id=\"Password\"]")).sendKeys(password);
        driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div/form/fieldset/div[3]/div/button")).click();
        Thread.sleep(300);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div/header/div[2]/div/div/div[2]/div/a[3]")).click();
        Thread.sleep(500);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div/header/div[2]/div/div/div[2]/div/a[3]")).click();
        Thread.sleep(700);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div/main/div/div/div[1]/div/div[2]/div/a[3]")).click();
        Thread.sleep(300);
        Actions action = new Actions(driver);
        action.moveToElement(driver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div/div/div[2]/div[2]/form/div/div[1]/div[1]/div[2]/div[1]/div/input"))).click().perform();
        Thread.sleep(300);
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[2]/div/div[3]/button[2]")).click();
        Thread.sleep(300);
        action.moveToElement(driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/main/div/div/div[2]/div[2]/form/div/div[4]/div/button/span"))).click().perform();
        if (Files.exists(Paths.get(downloadFilepath + String.format("Обращения_%s.xlsx", date)))) {
            System.out.println("The SCMKS FILE IS ALREADY EXISTS");
            driver.close();
            driver.quit();
        } else {
            System.out.println("DOWNLOADING FILE");
            while (!done) {
                if (Files.exists(Paths.get(downloadFilepath + String.format("Обращения_%s.xlsx", date)))) {
                    done = true;
                }
                Thread.sleep(300);
            }
            System.out.println("FILE FROM SCMKS.RU DOWNLOADED!");
            driver.close();
            driver.quit();
        }

    }
    public  String parseString(String link)  {

        String patterns [] = {
                //койки
                "https://xn--j1ab.xn/\\d{6}/\\?&amp;ncc=1&amp;ts=\\d{10}&amp;filename=[%\\w]*[\\+[%\\w]*]*\\+COVID-19[\\+[%\\w]*]*[\\+\\s\\d\\.]*[%\\w]*\\.xlsx",
                // пневмония лабт
                "https://xn--j1ab.xn/\\d{6}/\\?&amp;ncc=1&amp;ts=\\d{10}&amp;filename=[%\\w]*[\\+[%\\w]*]*[\\+\\s\\d\\.]*[%\\w]*\\.xlsx",
                //отчет кадры
                "https://xn--j1ab.xn/\\d{6}/\\?&amp;ncc=1&amp;ts=\\d{10}&amp;filename=2+[\\+[%\\w]*]*[\\+\\s\\d\\.]*[%\\w]*\\.xlsx"

        };

        int start = 0;
        int end = 0;

        for (String str :patterns) {
            Pattern pattern = Pattern.compile(str);
            Matcher matcher = pattern.matcher(link);
            while (matcher.find()) {
                start = matcher.start();
                end = matcher.end();
                // System.out.println("Найдено совпадение : " + link.substring(start, end) + " с " + start + " по " + (end - 1) + " позицию");
                if (link.equals(urls[3])) break;
            }
        }

        return link.substring(start,end);
    }


    public String parseDate (String link) {
        Pattern pattern = Pattern.compile("\\d\\d\\.\\d\\d.20\\d{2}");
        int start = 0;
        int end = 0;
        Matcher matcher = pattern.matcher(link);
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            // System.out.println("Найдено совпадение " + link.substring(start, end) + " с " + start + " по " + (end - 1) + " позицию");
            break;
        }
        return link.substring(start,end);
    }


    private   Date createDate(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        Date date =calendar.getTime();
        return date;
    }


    private String labtOneWeekAgo(String labt) throws ParseException {
        String oneWeekAgo = "oneWeekAgo";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(labt,dtf);
        LocalDate weekBefore =localDate.minusWeeks(1);
        String date =  weekBefore.format(dtf);
        return oneWeekAgo + date + "/";

    }


    public void downloadFiles() throws ParseException {
        getHtmlCode(mainUrl);
        String labtDate = "";
        for (String url :urls) {
            String str =  getHtmlCode(url);
            String link = parseString(str);
            String date = parseDate(link);
            String filename = "";
            if (url.equals(urls[0])){

                filename = "койки "+date.replace(".","")+".xlsx";
            }
            if (url.equals(urls[1])){
                filename = "пневмония "+date.replace(".","")+".xlsx";
            }
            if (url.equals(urls[2])){
                labtDate = date;
                filename = "лабт " + date.replace(".","") + ".xlsx";
            }
            if (url.equals(urls[3])){
                filename = "2 Отчёт кадры " + date + ".xlsx";
            }
            downloadFile(link,filename);
        }

        String pastLabt = labtOneWeekAgo(labtDate);
        String labtPage = getHtmlCode(pastLabt);
        String labtLink = parseString(labtPage);
        String labt1 = parseDate(labtLink);
        String labtname = "лабт "+labt1.replace(".","")+".xlsx";
        downloadFile(labtLink,labtname);
    }



    public boolean deleteDirectory() {
        File directoryToBeDeleted = new File(createLocalDir());
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                file.delete();
            }
        }
        return directoryToBeDeleted.delete();
    }
}
