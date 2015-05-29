package biz.a7software.slimmyinvoice.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Template class describes a template, i.e. a collection of zone.
 */
@DatabaseTable(tableName = "template")
public class Template {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Zone date_zone;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Zone invoice_id;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Zone subtotal;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Zone VATrate;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Zone VAT;
    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Zone total;


    // Constructors
    public Template() {
        date_zone = null;
        invoice_id = null;
        subtotal = null;
        VATrate = null;
        VAT = null;
        total = null;
    }

    // Getters and setters
    public Zone getDate() {
        return date_zone;
    }

    public void setDate(Zone date_zone) {
        this.date_zone = date_zone;
    }

    public Zone getId() {
        return invoice_id;
    }

    public void setId(Zone id) {
        this.invoice_id = id;
    }

    public Zone getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Zone subtotal) {
        this.subtotal = subtotal;
    }

    public Zone getVATrate() {
        return VATrate;
    }

    public void setVATrate(Zone VATrate) {
        this.VATrate = VATrate;
    }

    public Zone getVAT() {
        return VAT;
    }

    public void setVAT(Zone VAT) {
        this.VAT = VAT;
    }

    public Zone getTotal() {
        return total;
    }

    public void setTotal(Zone total) {
        this.total = total;
    }


    // For debugging purposes
    public void print() {
        if (date_zone != null) {
            System.out.println("Date zone (from top left corner):");
            date_zone.print();
        } else {
            System.out.println("No Date zone !");
        }

        if (invoice_id != null) {
            System.out.println("invoice_ref zone (from top left corner):");
            invoice_id.print();
        } else {
            System.out.println("No Id zone !");
        }
        if (VATrate != null) {
            System.out.println("VATrate zone (from top left corner):");
            VATrate.print();
        } else {
            System.out.println("No VATrate zone !");
        }

        if (VAT != null) {
            System.out.println("VAT zone (from top left corner):");
            VAT.print();
        } else {
            System.out.println("No VAT zone !");
        }

        if (total != null) {
            System.out.println("Total zone (from top left corner):");
            total.print();
        } else {
            System.out.println("No Total zone !");
        }
    }
}