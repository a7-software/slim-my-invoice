package biz.a7software.slimmyinvoice.data;

import biz.a7software.slimmyinvoice.helper.FormatHandler;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Supplier class describes a supplier.
 */
@DatabaseTable(tableName = "supplier")
public class Supplier extends Company {

    public static final String[] SUPPLIER_DISPLAY_HEADERS = {"NAME", "VAT", "ADDRESS"};


    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Template template;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Fingerprint fingerprint;


    public Supplier() {
        this.vatNumber = Invoice.EMPTY;
        this.name = Invoice.EMPTY;
        this.address = new Address();
        this.template = new Template();
    }

    public Supplier(String name, Address address, String vatNumber, Template template, Fingerprint fingerprint) {
        this.vatNumber = vatNumber;
        this.name = name;
        this.address = address;
        this.template = template;
        this.fingerprint = fingerprint;
    }

    public void print() {
        System.out.println("Supplier: " + this.name);
    }


    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Fingerprint getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(Fingerprint fingerprint) {
        this.fingerprint = fingerprint;
    }

    public boolean isValid() {
        return FormatHandler.getInstance().isValidVAT(super.getVatNumber());
    }
}