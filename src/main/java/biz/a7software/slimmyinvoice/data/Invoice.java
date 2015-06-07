package biz.a7software.slimmyinvoice.data;

import biz.a7software.slimmyinvoice.helper.FormatHandler;
import biz.a7software.slimmyinvoice.login.LoginHandler;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Invoice class describes an invoice.
 * It tells whether an invoice is valid or not (i.e. all fields are valid and coherent with each other).
 * It can partially auto-complete an invoice based on relationship between some of the fields.
 */
@DatabaseTable(tableName = "invoice")
public class Invoice {

    public static final String EMPTY = " ";
    public static final String[] INVOICE_DISPLAY_HEADERS = {"No", "DATE", "REF", "SUPPLIER", "SUBTOTAL", "VAT RATE", "VAT", "TOTAL"};
    public static final String REF_FIELD_NAME = "reference";

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private String date;
    @DatabaseField(columnName = REF_FIELD_NAME, canBeNull = false)
    private String ref;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private Supplier supplier;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private User user;
    @DatabaseField(canBeNull = false)
    private double subtotal;
    @DatabaseField(canBeNull = false)
    private double VATrate;
    @DatabaseField(canBeNull = false)
    private double VAT;
    @DatabaseField(canBeNull = false)
    private double total;


    public Invoice() {
        this.date = EMPTY;
        this.ref = EMPTY;
        this.supplier = new Supplier();
        this.user = LoginHandler.getInstance().getUser();
        this.subtotal = 0.0;
        this.VATrate = 0.0;
        this.VAT = 0.0;
        this.total = 0.0;
    }


    public Boolean isValid() {
        return isDateValid(date) && isSupplierValid(supplier) && !ref.equals(EMPTY) && isAmountValid();
    }

    public Boolean isAmountValid() {
        if (subtotal == 0.0 && VATrate == 0.0 && VAT == 0.0 && total == 0.0) {
            return false;
        }
        return compareDoubleIgnoreRounding(subtotal + VAT, total) && compareDoubleIgnoreRounding(subtotal * (VATrate / 100), VAT);
    }

    public Boolean compareDoubleIgnoreRounding(Double d1, Double d2) {
        double d1Rounded = FormatHandler.getInstance().round(d1, 2);
        double d2Rounded = FormatHandler.getInstance().round(d2, 2);
        return d1Rounded == d2Rounded || d1Rounded == d2Rounded + 0.01 || d1Rounded == d2Rounded - 0.01;
    }

    private boolean isDateValid(String date) {
        return date != EMPTY;
    }

    private boolean isSupplierValid(Supplier supplier) {
        if (supplier == null) {
            return false;
        }
        return supplier.isValid();
    }

    // Auto completion when 2 out of the 4 amount fields are non nul.
    public void autoComplete2OutOf4() {
        Double VATrate = this.VATrate / 100;

        if (subtotal == 0.0 && VATrate == 0.0 && VAT != 0.0 && total != 0.0) {
            if ((VAT >= 0.0 && total >= 0.0) || (VAT <= 0.0 && total <= 0.0)) {
                subtotal = total - VAT;
                this.VATrate = 100 * (VAT / subtotal);
            }
        } else if (subtotal == 0.0 && VATrate > 0.0 && VAT == 0.0 && total != 0.0) {
            subtotal = total / VATrate;
            VAT = total - subtotal;
        } else if (subtotal != 0.0 && VATrate == 0.0 && VAT == 0.0 && total != 0.0) {
            if ((subtotal >= 0.0 && total >= 0.0) || (subtotal <= 0.0 && total <= 0.0)) {
                VAT = total - subtotal;
                this.VATrate = 100 * (VAT / subtotal);
            }
        } else if (subtotal == 0.0 && VATrate > 0.0 && VAT != 0.0 && total == 0.0) {
            subtotal = VAT / VATrate;
            total = subtotal + VAT;
        } else if (subtotal != 0.0 && VATrate > 0.0 && VAT == 0.0 && total == 0.0) {
            VAT = subtotal * VATrate;
            total = subtotal + VAT;
        } else if (subtotal != 0.0 && VATrate == 0.0 && VAT != 0.0 && total == 0.0) {
            if ((subtotal >= 0.0 && VAT >= 0.0) || (subtotal <= 0.0 && VAT <= 0.0)) {
                total = subtotal + VAT;
                this.VATrate = 100 * (VAT / subtotal);
            }
        }
    }

    // Auto completion when 3 out of the 4 amount fields are non nul.
    public void autoComplete3OutOf4() {
        Double VATrate = this.VATrate / 100;

        if (subtotal != 0.0 && VATrate > 0.0 && VAT != 0.0) {
            if (compareDoubleIgnoreRounding(subtotal * VATrate, VAT)) {
                total = subtotal + VAT;
            }
        }
        if (subtotal != 0.0 && VATrate > 0.0 && total != 0.0) {
            if (compareDoubleIgnoreRounding(subtotal * (1 + VATrate), total)) {
                VAT = total - subtotal;
            }
        }
        if (subtotal != 0.0 && VAT != 0.0 && total != 0.0) {
            if (compareDoubleIgnoreRounding(subtotal + VAT, total) && (VAT / subtotal) >= 0) {
                this.VATrate = 100 * (VAT / subtotal);
            }
        }
        if (VATrate > 0.0 && VAT != 0.0 && total != 0.0) {
            if (compareDoubleIgnoreRounding(VAT * (1 + VATrate), total * VATrate)) {
                subtotal = total - VAT;
            }
        }
    }

    // Auto completion for the manual analysis.
    public void autoCompleteManualAnalysis() {
        autoComplete2OutOf4();
        roundAmountFields();
    }

    // Auto completion for the auto analysis.
    public void autoCompleteAutoAnalysis() {
        autoComplete3OutOf4();
        autoComplete2OutOf4();
        roundAmountFields();
    }

    // Round amount fields to the second decimal, VAT rate to the unit.
    public void roundAmountFields() {
        subtotal = FormatHandler.getInstance().round(subtotal, 2);
        VATrate = FormatHandler.getInstance().round(VATrate, 0);
        VAT = FormatHandler.getInstance().round(VAT, 2);
        total = FormatHandler.getInstance().round(total, 2);
    }

    // Indicates the UI which fields to red because they are not consistent or missing.
    public String redFields() {
        String redFields = "";
        if (date == EMPTY) {
            redFields += "date:";
        }
        if (ref == EMPTY) {
            redFields += "ref:";
        }
        if (!isAmountValid()) {
            if (subtotal == 0.0) {
                redFields += "sub:";
            }
            if (VATrate <= 0.0) {
                redFields += "rate:";
            }
            if (VAT == 0.0) {
                redFields += "vat:";
            }
            if (total == 0.0) {
                redFields += "total:";
            }
        }

        int neg = 0;
        if (subtotal < 0) {
            neg ++;
        }
        if (VAT < 0) {
            neg ++;
        }
        if (total < 0) {
            neg ++;
        }

        if (!(neg == 3 || (neg == 2 && VAT == 0) || neg == 0)) {
            redFields += "vat:total:rate:sub";
        }

        Double VATrate = this.VATrate / 100;
        if (compareDoubleIgnoreRounding(subtotal * (1 + VATrate), total) && VATrate >= 0) {
            if (!compareDoubleIgnoreRounding(total, subtotal + VAT)) {
                redFields += "vat:";
            }
        } else if (compareDoubleIgnoreRounding(subtotal * VATrate, VAT) && VATrate >= 0) {
            if (!compareDoubleIgnoreRounding(total, subtotal + VAT)) {
                redFields += "total:";
            }
        } else if (compareDoubleIgnoreRounding(subtotal + VAT, total)) {
            if (!compareDoubleIgnoreRounding(subtotal * VATrate, VAT) || VATrate <= 0) {
                redFields += "rate:";
            }
        } else if (compareDoubleIgnoreRounding(VAT * (1 + VATrate), total * VATrate) && VATrate >= 0) {
            if (!compareDoubleIgnoreRounding(total, subtotal + VAT)) {
                redFields += "sub:";
            }
        } else {
            redFields += "vat:total:rate:sub";
        }
        return redFields;
    }


    // For debugging purposes
    public void print() {
        System.out.println("-------------------------------");
        System.out.println("Invoice reference: " + ref);
        System.out.println("Date: " + date);
        System.out.println("From (supplier): " + supplier.getName());
        System.out.println("To (user): " + LoginHandler.getInstance().getUser().getName());
        System.out.println("Subtotal amount: " + subtotal);
        System.out.println("VAT rate: " + VATrate);
        System.out.println("TVA amount: " + VAT);
        System.out.println("TOTAL amount: " + total);
        System.out.println("-------------------------------");
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if (date.equals(EMPTY)) {
            this.date = EMPTY;
        } else {
            this.date = date.trim();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        if (ref.equals(EMPTY)) {
            this.ref = EMPTY;
        } else {
            this.ref = ref.trim();
        }
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getVatRate() {
        return VATrate;
    }

    public Double getVAT() {
        return VAT;
    }

    public void setVAT(Double VAT) {
        this.VAT = VAT;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setVATrate(Double VATrate) {
        this.VATrate = VATrate;
    }

}