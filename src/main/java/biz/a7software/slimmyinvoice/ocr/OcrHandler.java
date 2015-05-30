package biz.a7software.slimmyinvoice.ocr;

import biz.a7software.slimmyinvoice.data.*;
import biz.a7software.slimmyinvoice.helper.*;
import biz.a7software.slimmyinvoice.login.LoginHandler;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The OcrHandler class contains logic of OCR related operations.
 */
public class OcrHandler {

    private static volatile OcrHandler instance = null;

    private StringBuilder ocrResult;
    private Invoice invoice;

    private OcrHandler() {
        ocrResult = new StringBuilder();
        invoice = new Invoice();
        invoice.setUser(LoginHandler.getInstance().getUser());
    }

    public final static OcrHandler getInstance() {
        if (OcrHandler.instance == null) {
            synchronized (FormatHandler.class) {
                if (OcrHandler.instance == null) {
                    OcrHandler.instance = new OcrHandler();
                }
            }
        }
        return OcrHandler.instance;
    }

    // Clears data contained in this handler
    public void clear() {
        invoice = new Invoice();
        invoice.setUser(LoginHandler.getInstance().getUser());
    }

    // Performs OCR on the entire provided Image according to the given language.
    public String ocrSimple(String language, ImageHandler imageHandler) throws TesseractException {
        OcrEngine.getInstance().setLanguage(language);
        BufferedImage image = imageHandler.getImage();
        return OcrEngine.getInstance().fullOcr(image);
    }

    // Performs OCR on the provided Area on the given Image.
    public String ocrArea(Area area, ImageHandler imageHandler) throws TesseractException {
        BufferedImage image = imageHandler.getImage();
        OcrEngine.getInstance().chooseArea(area);
        return OcrEngine.getInstance().result(image);
    }

    // Performs an auto-analysis to find fields in the invoice contained in the provided image according to a certain language.
    public String ocrAutoAnalyse(String language, ImageHandler imageHandler) throws SQLException, TesseractException {

        OcrEngine.getInstance().setLanguage(language);

        BufferedImage image = imageHandler.getImage();
        String result = OcrEngine.getInstance().fullOcr(image);

        Fingerprint fingerprint = new Fingerprint(result);
        // Extract all suppliers from DB
        List<Supplier> supplierList = DbHandler.getInstance().retrieveAllSuppliersList();
        // Compare all suppliers fingerprints to the one we have
        Supplier supplier = SupplierMatcher.getInstance().findSupplier(supplierList, fingerprint);
        if (supplier != null) {
            invoice.setSupplier(supplier);
            if (supplier.getTemplate() != null) {
                this.ocrTemplateAnalyse(language, imageHandler);
                UploadHandler.getInstance().setDisplaySaveSupplier(false);
                UploadHandler.getInstance().setDisplayUpdateSupplier(false);
                return "Supplier found in the DB and invoice OCRed according to the supplier's template.";
            }
            UploadHandler.getInstance().setDisplaySaveSupplier(false);
            UploadHandler.getInstance().setDisplayUpdateSupplier(false);
            return "Supplier found in the DB but has no template.";
        } else { // no supplier found in the DB
            if (fingerprint.getVat() != null) {
                supplier = HtmlParser.getInstance().retrieveSupplierFromVATNum(fingerprint.getVat());
                invoice.setSupplier(supplier);
                if (supplier != null) {
                    ocrAutoDateAndAmountsAnalyse(supplier, result);// auto-analyse for date and amounts
                    UploadHandler.getInstance().setDisplaySaveSupplier(true);
                    UploadHandler.getInstance().setDisplayUpdateSupplier(false);
                    return "Supplier has been retrieved from online BCE database";
                }
                UploadHandler.getInstance().setDisplaySaveSupplier(false);
                UploadHandler.getInstance().setDisplayUpdateSupplier(false);
                return "A valid VAT number (" + fingerprint.getVat() + ") has been automatically retrieved " +
                        "but the online request from online BCE database gave no result.";
            }
            UploadHandler.getInstance().setDisplaySaveSupplier(false);
            UploadHandler.getInstance().setDisplayUpdateSupplier(false);
            return "No supplier found in the database and no VAT number has been automatically found via OCR.";
        }
    }

    private void ocrAutoDateAndAmountsAnalyse(Supplier supplier, String result) {

        String ocrWithoutSapce = result.replaceAll("\\s+", "").toLowerCase();
        Pattern pattern = Pattern.compile("(€|eur|euro|euros)?[0-9]+[,|\\.][0-9]{2}(€|eur|euro|euros)?");
        Matcher matcher = pattern.matcher(ocrWithoutSapce);

        //List<String> validVATs = new ArrayList<String>();
        while (matcher.find()) {
            //System.out.println("Amount ! " + matcher.group());
        }
    }

    // Performs OCR analysis with the given language in the provided image thanks to the template provided by the invoice in this class.
    public void ocrTemplateAnalyse(String language, ImageHandler imageHandler) throws TesseractException {
        if (language == null) {
            OcrEngine.getInstance().setDefaultLanguage();
        } else {
            OcrEngine.getInstance().setLanguage(language);
        }

        int imageHeight = imageHandler.getHeight();
        int imageWidth = imageHandler.getWidth();
        Template template = getInvoice().getSupplier().getTemplate();

        Zone date_zone = template.getDate();
        if (date_zone != null) {
            Area dateArea = date_zone.createArea(imageHeight, imageWidth);
            String dateStr = ocrArea(dateArea, imageHandler);
            invoice.setDate(dateStr);
        }

        Zone id_zone = template.getId();
        if (id_zone != null) {
            Area idArea = id_zone.createArea(imageHeight, imageWidth);
            String idStr = ocrArea(idArea, imageHandler);
            invoice.setRef(idStr);
        }

        Zone subtotal_zone = template.getSubtotal();
        if (subtotal_zone != null) {
            Area subtotalArea = subtotal_zone.createArea(imageHeight, imageWidth);
            String subtotalStr = ocrArea(subtotalArea, imageHandler);
            if (subtotalStr != null && !subtotalStr.replaceAll("\\s+", "").equals("")) {
                invoice.setSubtotal(FormatHandler.getInstance().extractAmount(subtotalStr));
            }
        }

        Zone vatRate_zone = template.getVATrate();
        if (vatRate_zone != null) {
            Area vatRateArea = vatRate_zone.createArea(imageHeight, imageWidth);
            String vatRateStr = ocrArea(vatRateArea, imageHandler);
            if (vatRateStr != null && !vatRateStr.replaceAll("\\s+", "").equals("")) {
                invoice.setVATrate(FormatHandler.getInstance().extractAmount(vatRateStr));
            }
        }

        Zone vat_zone = template.getVAT();
        if (vat_zone != null) {
            Area vatArea = vat_zone.createArea(imageHeight, imageWidth);
            String vatStr = ocrArea(vatArea, imageHandler);
            if (vatStr != null && !vatStr.replaceAll("\\s+", "").equals("")) {
                invoice.setVAT(FormatHandler.getInstance().extractAmount(vatStr));
            }
        }

        Zone total_zone = template.getTotal();
        if (total_zone != null) {
            Area totalArea = total_zone.createArea(imageHeight, imageWidth);
            String totalStr = ocrArea(totalArea, imageHandler);
            if (totalStr != null && !totalStr.replaceAll("\\s+", "").equals("")) {
                invoice.setTotal(FormatHandler.getInstance().extractAmount(totalStr));
            }
        }
    }

    // Performs OCR on the provided Area on the given Image and interprets the result as a VAT number.
    public String ocrVAT(Area last, ImageHandler image) throws TesseractException {
        String vatString = ocrArea(last, image);
        return FormatHandler.getInstance().formatInputVat(vatString);
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

}