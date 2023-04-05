import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SheetsandJava {
    public static Sheets sheetsService;
    public static String APPLICATION_NAME = "Covid19 Docs";
    public static String SPREADSHEET_ID = "ID";


    private static Credential authorize () throws IOException, GeneralSecurityException {
        InputStream in = SheetsandJava.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),new InputStreamReader(in)
        );

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets,scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline")
                .build();
        Credential credential  = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");
        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public boolean readingGoogleDocs(String previousDay) throws IOException, GeneralSecurityException {
        boolean acces = false;
        String cell = String.format("%s:%s",previousDay.substring(0,2),previousDay.substring(0,2));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String previousDate ="[" +dateFormat.format(calendar.getTime())+"]";
        sheetsService = getSheetsService();

        ValueRange response = SheetsandJava.sheetsService.spreadsheets().values()
                .get(SheetsandJava.SPREADSHEET_ID, cell)
                .execute();
        List<List<Object>> values = response.getValues();
        String value = null;
        if (values == null || values.isEmpty()) {
            System.out.println("Данные не найдены");
        } else {
            for (List row : values) {
                value = String.valueOf(row);
                if (value.equals(previousDate)) acces = true;
            }

        }
        return acces;
    }


    public boolean readGoogleDocs(String currentDay) throws IOException, GeneralSecurityException {
        boolean acces = false;
        String cell = String.format("%s:%s",currentDay.substring(0,2),currentDay.substring(0,2));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String tomorrow ="[" +dateFormat.format(calendar.getTime())+"]";
        sheetsService = getSheetsService();

        ValueRange response = SheetsandJava.sheetsService.spreadsheets().values()
                .get(SheetsandJava.SPREADSHEET_ID, cell)
                .execute();
        List<List<Object>> values = response.getValues();
        String value = null;
        if (values == null || values.isEmpty()) {
            System.out.println("Данные не найдены");
        } else {
            for (List row : values) {
                value = String.valueOf(row);
                if (value.equals(tomorrow)) acces = true;
            }

        }
        return acces;
    }

    public void updatingGoogleDocs (String range,List<Integer> myData ) throws IOException, GeneralSecurityException {

        List<List<Object>> writeData = new ArrayList<>();
        for (Integer someData: myData) {
            List<Object> dataRow = new ArrayList<>();
            dataRow.add(someData);
            writeData.add(dataRow);
        }

        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");

        UpdateValuesResponse result = getSheetsService().spreadsheets().values()
                .update(SPREADSHEET_ID, range, vr)
                .setValueInputOption("RAW")
                .execute();
        System.out.printf("%s UPDATED\n", result.getUpdatedRange());
    }
}
