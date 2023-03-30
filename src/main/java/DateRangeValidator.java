import java.util.Date;

public class DateRangeValidator {
    private  Date startDate;
    private  Date endDate;

    public void setDates(Date startDate,Date endDate){
        this.startDate=startDate;
        this.endDate=endDate;
    }
    public boolean isWithinRange(Date testDate) {

        return testDate.getTime() >= startDate.getTime() &&
                testDate.getTime() <= endDate.getTime();
    }
}
