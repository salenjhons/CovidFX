import javax.xml.validation.Validator;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;

public class ConnectToDB {

    static final String DB_URL = "db_url";
    static final String USERNAME = "username";
    static final String PASSWORD = "password";
    static Connection connection = null;
    static Statement statement = null;
    static ResultSet result = null;
    StringBuilder sb1 = new StringBuilder();
    StringBuilder sb2 = new StringBuilder();
    StringBuilder sb3 = new StringBuilder();

    List<Integer> column1 = new ArrayList<>();
    List<Integer> column2 = new ArrayList<>();
    List<Integer> column3 = new ArrayList<>();
    List<List<Integer>> columns = new ArrayList<>();


    static final String[] queriesArr = {
            "\"statistics\".statistic_russia_for_dashboard",
            "\"rawdata\".minzdrav_beds",
            "\"rawdata\".p_2_minzdrav_labs_tests",
            "rawdata.minzdrav_need_for_medworkers",
            "rawdata.incentive_payments_report",
            "rawdata.medical_complaints",
            "rawdata.medical_complaints_800",
            "\"rawdata\".medical_complaints_result",
            "\"rawdata\".minzdrav_pneumonia_patients",
            "rawdata.minzdrav_register_covid_ventilator_beds",
            "\"rawdata\".minzdrav_forecast_number_patients_optimistic_pessimistic",
            "rawdata.covid19_hotline_calls",
            "rawdata.vats_calls"};

    public List<List<Integer>> connectionToPostgreSql() throws ParseException {
        StringBuilder loadBar = new StringBuilder();
        ArrayList<Date> data = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
            System.exit(0);
        }

        if (connection != null) {
            System.out.println("CONNECTION WITH DATABASE  ESTABLISHED");
        } else {
            System.out.println("Ошибка подключения к БД");
        }
        try {
            int count;
            statement = connection.createStatement();
            int counter = 0;
            System.out.println("PROCESSING THE DATA");
            for (String str : queriesArr) {
                //  System.out.println(str);
                loadBar.append(".");
                System.out.print(loadBar);
                result = statement.executeQuery(
                        String.format("select date_time , count (1) \n" +
                                "from %s\n" +
                                "group by date_time \n" +
                                "order by date_time desc", str));
                while (result.next()) {
                    if (counter == 3) {
                        counter = 0;
                        break;
                    }
                    Timestamp timestamp = result.getTimestamp("date_time");
                    count = result.getInt("count");
                    if (counter == 0 || counter == 2) {
                        if (count>0) {
                            Date testDate = simpleDateFormat.parse(timestamp.toString());
                            //             System.out.println(testDate + "\t" + count);
                            data.add(testDate);
                        }
                    }

                    counter++;
                }
                analyzeData(data);
                data.clear();
            }
            result.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
        loadBar.delete(0, loadBar.length());

        for (char ch : sb1.toString().toCharArray()) {
            this.column1.add(Integer.valueOf(String.valueOf(ch)));
        }
        for (char ch :sb2.toString().toCharArray()) {
            this.column2.add(Integer.valueOf(String.valueOf(ch)));
        }
        for (char ch :sb3.toString().toCharArray()) {
            this.column3.add(Integer.valueOf(String.valueOf(ch)));
        }
        this.columns.add(column1);
        this.columns.add(column2);
        this.columns.add(column3);
        return this.columns;
    }

    private Date roundTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void analyzeData(ArrayList<Date> data) {
        DateRangeValidator validator = new DateRangeValidator();
        validator.setDates(data.get(1), data.get(0));
        for (int i = 0; i < 3; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -i);
            Date testDate = roundTime(calendar.getTime());
            if (i == 0) {
                if (validator.isWithinRange(testDate)) {
                    sb1.append("2");
                } else sb1.append("0");
            }

            if (i == 1) {
                if (validator.isWithinRange(testDate)) {
                    sb2.append("2");
                } else sb2.append("0");
            }

            if (i == 2) {
                if (validator.isWithinRange(testDate)) {
                    sb3.append("2");
                } else sb3.append("0");
            }
        }
    }

    public void deleteDataFromLists(){
        sb1.delete(0,sb1.length());
        sb2.delete(0,sb2.length());
        sb3.delete(0,sb3.length());
        this.column1.clear();
        this.column2.clear();
        this.column3.clear();
        this.columns.clear();
    }
}