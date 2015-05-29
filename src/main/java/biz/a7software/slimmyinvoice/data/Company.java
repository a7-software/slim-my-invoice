package biz.a7software.slimmyinvoice.data;

import com.j256.ormlite.field.DatabaseField;

/**
 * The Company class describes basic information about a company.
 */
public abstract class Company {

    @DatabaseField(id = true)
    protected String vatNumber;
    @DatabaseField
    protected String name;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    protected Address address;

    public Company() {
        this.vatNumber = Invoice.EMPTY;
        this.name = Invoice.EMPTY;
        this.address = new Address();
    }

    public Company(String vatNumber, String name, Address address) {
        this.vatNumber = vatNumber;
        this.name = name;
        this.address = address;
    }

    public String toString() {
        return name;
    }


    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
