package biz.a7software.slimmyinvoice.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Address class describes a mailing address.
 */
@DatabaseTable(tableName = "address")
public class Address {

    public static final String BELGIUM = "BELGIUM";

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String street;
    @DatabaseField
    private String number;
    @DatabaseField
    private String zip;
    @DatabaseField
    private String city;
    @DatabaseField
    private String country;

    public Address(String street, String number, String zip, String city, String country) {
        this.street = street.trim();
        this.number = number.trim();
        this.zip = zip.trim();
        this.city = city.trim();
        this.country = country.trim();
    }

    public Address() {
        this.street = Invoice.EMPTY;
        this.number = Invoice.EMPTY;
        this.zip = Invoice.EMPTY;
        this.city = Invoice.EMPTY;
        this.country = Invoice.EMPTY;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}