package biz.a7software.slimmyinvoice.data;

/**
 * The Datum class describes a date and handles dates format.
 */
public class Datum { //@TODO : deal with that class or delete it!

    private Integer day;
    private Integer month;
    private Integer year;

    private String string;

    public Datum(String string) {
        this.string = string;
    }

    // Constructors
    public Datum() {
        this.day = 0;
        this.month = 0;
        this.year = 0;
    }

    public Datum(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }


    // Getters
    public int getDay() {
        return day;
    }

    // Setters
    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public String toString() {
        if (string != null) {
            return string;
        } else {
            return (String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + String.format("%04d", year));
        }
    }
}
