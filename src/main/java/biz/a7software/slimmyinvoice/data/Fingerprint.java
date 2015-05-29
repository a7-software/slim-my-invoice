package biz.a7software.slimmyinvoice.data;

import biz.a7software.slimmyinvoice.helper.DbHandler;
import biz.a7software.slimmyinvoice.helper.FormatHandler;
import biz.a7software.slimmyinvoice.helper.HtmlParser;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Fingerprint class describes a signature of an invoice.
 * It also extracts the VAT number of the supplier from the invoice.
 */
@DatabaseTable(tableName = "fingerprint")
public class Fingerprint {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private int numberOfLines;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String[] lines;
    @DatabaseField
    private String brutOcr;
    @DatabaseField
    private String vat;

    public Fingerprint() {
        // required for ORMLite
    }

    public Fingerprint(String ocrResult) {
        brutOcr = ocrResult;
        lines = ocrResult.split("\n");
        numberOfLines = lines.length;
        vat = extractVatNumberFromPlainText(ocrResult);
    }

    private String extractVatNumberFromPlainText(String ocrResult) {

        String ocrWithoutSapce = ocrResult.replaceAll("\\s+", "").toLowerCase();
        Pattern pattern = Pattern.compile("(be)?[0|1]?[0-9]{3}\\p{Punct}?[0-9]{3}\\p{Punct}?[0-9]{3}");
        Matcher matcher = pattern.matcher(ocrWithoutSapce);

        List<String> validVATs = new ArrayList<String>();
        while (matcher.find()) {
            String vat = FormatHandler.getInstance().formatInputVat(matcher.group());
            if (FormatHandler.getInstance().isValidVAT(vat)) {
                validVATs.add(vat);
            }
        }
        // Remove the VAT number of the user (that must also appear on the invoice)
        validVATs.remove(DbHandler.getInstance().getUser());

        if (validVATs.size() == 0) {
            return null;
        } else { // validVATs.size() > 1
            // Remove wrong VAT numbers
            return filterList(validVATs, ocrResult);
        }
    }

    //TODO : test it one time
    private String filterList(List<String> validVATs, String ocrResult) {

        double bestScore = 0.0;
        int bestIndex = 0;
        int listSize = validVATs.size();
        for (int i = 0; i < listSize; i++) {
            Supplier supplier = HtmlParser.getInstance().retrieveSupplierFromVATNum(validVATs.get(i));
            if (supplier != null) {
                List<String> keywords = new ArrayList<String>();
                if (supplier.getName() != null) {
                    String[] names = supplier.getName().toLowerCase().split("\\s+");
                    for (int j = 0; j < names.length; j++) {
                        keywords.add(names[j]);
                    }
                }
                Address address = supplier.getAddress();
                if (address != null) {
                    if (address.getStreet() != null)
                        keywords.add(address.getStreet());
                    if (address.getNumber() != null)
                        keywords.add(address.getNumber());
                    if (address.getCity() != null)
                        keywords.add(address.getCity());
                    if (address.getZip() != null)
                        keywords.add(address.getZip());
                }

                ocrResult = ocrResult.toLowerCase();
                int matches = 0;
                for (int k = 0; k < keywords.size(); k++) {
                    if (ocrResult.contains(keywords.get(k).toLowerCase())) {
                        matches++;
                    }
                }
                if (keywords.size() > 0) {
                    double score = matches / (double) keywords.size();
                    if (score > bestScore) {
                        bestScore = score;
                        bestIndex = i;
                    }
                }
            }
        }
        return validVATs.get(bestIndex);
    }


    public int getNumberOfLines() {
        return numberOfLines;
    }

    public String[] getLines() {
        return lines;
    }

    public String getBrutOcr() {
        return brutOcr;
    }

    public String getVat() {
        return vat;
    }
}