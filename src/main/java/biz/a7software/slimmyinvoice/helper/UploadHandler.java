package biz.a7software.slimmyinvoice.helper;

import biz.a7software.slimmyinvoice.data.*;
import biz.a7software.slimmyinvoice.login.LoginHandler;
import biz.a7software.slimmyinvoice.login.PasswordRequest;
import biz.a7software.slimmyinvoice.ocr.OcrHandler;
import boofcv.io.image.UtilImageIO;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * The UploadHandler class handles requests logic, handles errors and performs
 * basic requests (rotate invoice, next/previous page, enter a filed manually).
 */
public class UploadHandler {

    private static final double ROTATION_INCREMENT = 0.0025; //radians
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int SUPPLIERS = 10;
    private static final int INVOICES = 20;
    private static final String TESSDIR = "/tessdir/";
    private static ServletContext context;
    private static volatile UploadHandler instance = null;
    private FileName fileName;
    private List<FileName> fileNames;
    private AreaList areas;
    private ImageHandler image;
    private String language;
    private Boolean displaySaveSupplier = false;
    private Boolean displayUpdateSupplier = false;

    private UploadHandler(ServletContext context) {
        UploadHandler.context = context;
    }

    public final static UploadHandler getInstance(ServletContext context) {
        if (UploadHandler.instance == null) {
            synchronized (UploadHandler.class) {
                if (UploadHandler.instance == null) {
                    UploadHandler.instance = new UploadHandler(context);
                }
            }
        }
        return UploadHandler.instance;
    }

    public final static UploadHandler getInstance() {
        return UploadHandler.instance;
    }

    // Handles GET requests that do not require manipulating the invoice file.
    public JSONObject handleGETRequest(StringParams reqs) {
        String action = reqs.getParam("analysis");

        if (action.equals("login")) {
            try {
                clearHandler();
                return LoginHandler.getInstance().login(reqs);
            } catch (ServletException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error during login!");
            } catch (IOException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error during login!");
            }
        } else if (action.equals("register")) {
            return LoginHandler.getInstance().register(reqs);
        } else if (action.equals("changePassword")) {
            return LoginHandler.getInstance().updatePassword(reqs);
        } else if (action.equals("forgot_password")) {
            return PasswordRequest.getInstance().forgotPassword(reqs);
        } else if (action.equals("logout")) {
            clearHandler();
            LoginHandler.getInstance().setUser(null);
            return successUser(null);
        } else {
            if (LoginHandler.getInstance().getUser() == null) {
                return wrongJson("No user logged in!");
            } else if (action.equals("getSuppliers")) {
                return successJsonTable("Suppliers retrieved", SUPPLIERS);
            } else if (action.equals("getInvoices")) {
                return successJsonTable("Invoices retrieved", INVOICES);
            } else if (action.equals("getFullUser")) {
                return successUser(null);
            } else if (action.equals("reset_db")) {
                try {
                    DbHandler.getInstance().refreshDatabase();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while resetting the database!");
                }
                clearHandler();
                return successUser("DBResetOK");
            } else if (action.equals("getUser")) {
                if (image == null) {
                    return successJson("First upload an invoice.");
                } else {
                    return successJson("hide");
                }
            } else {
                try {
                    return checkFileAndAnalyse(reqs);
                } catch (Exception e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while processing request!");
                }
            }
        }
    }

    // Handles GET requests that do require manipulating the invoice.
    public JSONObject checkFileAndAnalyse(HttpParam reqs) {

        String analysis = reqs.getParam("analysis");

        language = reqs.getParam("language");
        Invoice invoice = OcrHandler.getInstance().getInvoice();

        if (fileName == null || fileName.isEmpty()) {
            return wrongJson("Missing file!");
        }

        // Analysis (no switch on STRINGS before Java 7...)
        if ("next_page".equals(analysis)) {
            if (fileNames == null || fileNames.isEmpty()) {
                return wrongJson("This invoice only contains one page!");
            } else {
                if (fileName.getPageNumber() < fileNames.size() - 1) {
                    fileName = fileNames.get(fileName.getPageNumber() + 1);
                    try {
                        image = new ImageHandler(oriImgFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return wrongJson("Unexpected error while loading the image!");
                    }
                    return successJson("next page loaded");
                } else {
                    return wrongJson("No next page available!");
                }
            }
        } else if ("previous_page".equals(analysis)) {
            if (fileNames == null || fileNames.isEmpty()) {
                return wrongJson("This invoice only contains one page!");
            } else {
                if (fileName.getPageNumber() > 0) {
                    fileName = fileNames.get(fileName.getPageNumber() - 1);
                    try {
                        image = new ImageHandler(oriImgFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return wrongJson("Unexpected error while loading the image!");
                    }
                    return successJson("previous page loaded");
                } else {
                    return wrongJson("No previous page available!");
                }
            }
        } else if ("rotate_left".equals(analysis)) {
            return rotate(LEFT);
        } else if ("rotate_right".equals(analysis)) {
            return rotate(RIGHT);
        } else if ("encode_invoice".equals(analysis)) {
            if (!invoice.isValid()) {
                return wrongJson("The invoice is not valid and cannot be added in the database!");
            }
            String action;
            try {
                if (DbHandler.getInstance().findInvoice(invoice.getSupplier(), invoice.getRef())) {
                    return wrongJson("An invoice with this reference already exists in the database for this supplier!");
                }
                DbHandler.getInstance().addInvoice(invoice);
                action = "Invoice ";
                // While encoding an invoice, if supplier is unknown to the database, it is added
                if (!DbHandler.getInstance().isSupplierInDB(invoice.getSupplier())) {
                    addSupplier(invoice.getSupplier());
                    action += "and supplier ";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while adding invoice in the database");
            } catch (TesseractException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while performing OCR!");
            }
            fileName = null;
            displaySaveSupplier = false;
            displayUpdateSupplier = false;
            image = null;
            OcrHandler.getInstance().setInvoice(new Invoice());
            action += "encoded";
            return successJson(action);
        } else if ("get_supplier_from_vat_manual".equals(analysis) || "get_supplier_from_vat_ocr".equals(analysis)) {
            String vat = null;
            if ("get_supplier_from_vat_manual".equals(analysis)) {
                vat = reqs.getParam("vat");
                vat = FormatHandler.getInstance().formatInputVat(vat);
                if (vat == null) {
                    invoice.setSupplier(null);
                    return wrongJson("Badly formatted VAT number (you must enter 10 numbers)!");
                }
            } else if ("get_supplier_from_vat_ocr".equals(analysis)) {
                areas = new AreaList(reqs.getParam("ocr_areas"));
                if (areas.getLast() == null) {
                    return wrongJson("Please provide a selection zone!");
                }
                try {
                    vat = OcrHandler.getInstance().ocrVAT(areas.getLast(), image);
                } catch (TesseractException e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while performing OCR!");
                }
                if (vat == null) {
                    return wrongJson("Cannot find a valid VAT number from the OCR result of the zone you selected!");
                }
            }
            Supplier supplier = null;
            if (!FormatHandler.getInstance().isValidVAT(vat)) {
                invoice.setSupplier(null);
                return wrongJson("VAT number is not valid!");
            }
            if (vat.equals(LoginHandler.getInstance().getUser().getVatNumber())) {
                return wrongJson("The VAT number your entered is your own VAT! You should be mistaken.");
            }
            // First check in local DB.
            try {
                supplier = DbHandler.getInstance().retrieveSupplierFromVat(vat);
            } catch (SQLException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while getting supplier!");
            }
            if (supplier != null) { // The supplier is in the local DB
                invoice.setSupplier(supplier);
                try {
                    OcrHandler.getInstance().ocrTemplateAnalyse(reqs.getParam("language"), image);
                } catch (TesseractException e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while performing OCR on the retrieved supplier!");
                }
                displaySaveSupplier = false;
                displayUpdateSupplier = false;
                return successJson("Supplier retrieved from the database and invoice scanned according to the supplier's template present in te database");
            } else { // If the supplier is not in the local DB we can retrieve its info from the BCE.
                supplier = HtmlParser.getInstance().retrieveSupplierFromVATNum(vat);
                if (supplier != null) {
                    invoice.setSupplier(supplier);
                    displaySaveSupplier = true;
                    displayUpdateSupplier = false;
                    return successJson("Supplier retrieved from the BCE online database.");
                } else {
                    invoice.setSupplier(null);
                    return wrongJson("Supplier cannot be retrieved neither from local database nor from BCE online database!");
                }
            }
        } else if ("add_supplier".equals(analysis)) {
            Supplier supplier = invoice.getSupplier();
            if (supplier.getName().equals(Invoice.EMPTY)) {
                return wrongJson("No supplier to add!");
            } else {
                try {
                    if (DbHandler.getInstance().isSupplierInDB(supplier)) {
                        return wrongJson("The supplier is already in the database! (either identical name and/or identical VAT number is already in the database)");
                    } else {
                        addSupplier(supplier);
                        displaySaveSupplier = false;
                        displayUpdateSupplier = false;
                        return successJson("Added supplier " + supplier.getName() + " (VAT = " + supplier.getVatNumber() + ") in the database");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while adding supplier " + supplier.getName() + " (VAT = " + supplier.getVatNumber() + ") in the database");
                } catch (TesseractException e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while adding supplier " + supplier.getName() + " (VAT = " + supplier.getVatNumber() + ") in the database");
                }
            }
        } else if ("update_supplier".equals(analysis)) {
            Supplier supplier = invoice.getSupplier();
            if (supplier.getName().equals(Invoice.EMPTY)) {
                return wrongJson("No supplier to update!");
            } else {
                try {
                    if (DbHandler.getInstance().isSupplierInDB(supplier)) {
                        DbHandler.getInstance().updateSupplier(supplier);
                        displaySaveSupplier = false;
                        displayUpdateSupplier = false;
                        return successJson("Supplier " + supplier.getName() + " (VAT = " + supplier.getVatNumber() + ") has been updated!");
                    } else {
                        addSupplier(supplier);
                        displaySaveSupplier = false;
                        displayUpdateSupplier = false;
                        return successJson("Supplier " + supplier.getName() + " (VAT = " + supplier.getVatNumber() + ") was not in the database and have been added");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while updating supplier " + supplier.getName() + " (VAT = " + supplier.getVatNumber() + ") in the database");
                } catch (TesseractException e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while updating supplier " + supplier.getName() + " (VAT = " + supplier.getVatNumber() + ") in the database");
                }
            }
        } else if ("auto".equals(analysis)) {
            try {
                String result = OcrHandler.getInstance().ocrAutoAnalyse(language, image);
                invoice.autoCompleteAutoAnalysis();
                return successJson(result);
            } catch (SQLException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while retrieving suppliers from the database to perform auto analysis!");
            } catch (TesseractException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while performing OCR on the invoice!");
            }
        } else {
            if (invoice.getSupplier().getName().equals(Invoice.EMPTY)) {
                return wrongJson("Please enter the supplier before analysing the invoice data!");
            }
            if ("manual".equals(analysis)) {
                try {
                    manualAnalysis(reqs);
                } catch (Exception e) {
                    e.printStackTrace();
                    return wrongJson(e.getMessage());
                }
                return successJson("Manual entry for \"" + reqs.getParam("field") + "\" registered");
            } else {
                areas = new AreaList(reqs.getParam("ocr_areas"));
                if (areas.getLast() == null) {
                    return wrongJson("Please provide a selection zone!");
                } else {
                    try {
                        singleZoneAnalysis(analysis);
                    } catch (TesseractException e) {
                        e.printStackTrace();
                        return wrongJson("Unexpected error while retrieving performing OCR!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return wrongJson(e.getMessage());
                    }
                    if (displaySaveSupplier == false) {
                        displayUpdateSupplier = true;
                    }
                    return successJson(analysis + " zone OCR'ed");
                }
            }
        }
    }

    // Handles requests for handling manual entries.
    private void manualAnalysis(HttpParam reqs) throws Exception {

        Invoice invoice = OcrHandler.getInstance().getInvoice();
        String field = reqs.getParam("field");

        if (field.equals("date")) {
            String date = reqs.getParam("date").trim().equals("") ? Invoice.EMPTY : reqs.getParam("date").trim();
            invoice.setDate(date);
            return;
        } else if (field.equals("ref")) {
            String id = reqs.getParam("id").trim().equals("") ? Invoice.EMPTY : reqs.getParam("id").trim();
            invoice.setRef(id);
            return;
        } else {
            Double value = 0.0;
            if (field.equals("subtotal")) {
                value = FormatHandler.getInstance().extractAmount(reqs.getParam("subtotal"));
                checkValue(value);
                invoice.setSubtotal(value);
            } else if (field.equals("vatRate")) {
                value = FormatHandler.getInstance().extractAmount(reqs.getParam("vatRate"));
                checkValue(value);
                invoice.setVATrate(value);
            } else if (field.equals("vat")) {
                value = FormatHandler.getInstance().extractAmount(reqs.getParam("vat"));
                checkValue(value);
                invoice.setVAT(value);
            } else if (field.equals("total")) {
                value = FormatHandler.getInstance().extractAmount(reqs.getParam("total"));
                checkValue(value);
                invoice.setTotal(value);
            }
            if ((double) value != 0.0) {
                invoice.autoCompleteManualAnalysis();
            }
        }
    }

    private void checkValue(Double value) throws Exception {
        if (value == null) {
            throw new Exception("The entered value cannot be converted to a number! Please consider xxx.dd syntax (dd = decimal part - 0, 1 or 2 numbers only) for numbers.");
        }
    }

    // Adds a supplier in the Database
    private void addSupplier(Supplier supplier) throws SQLException, TesseractException {
        String ocrResult = OcrHandler.getInstance().ocrSimple(language, image);
        supplier.setFingerprint(new Fingerprint(ocrResult));
        DbHandler.getInstance().addSupplier(supplier);
    }

    // Handles request for OCRing a zone.
    private void singleZoneAnalysis(String analysis) throws Exception {

        Zone zone = areas.getLast().getZone(image);
        Invoice invoice = OcrHandler.getInstance().getInvoice();

        String result = OcrHandler.getInstance().ocrArea(areas.getLast(), image);
        if (result != null && !result.equals("")) {
            result = result.trim();
            if ("date".equals(analysis)) {
                invoice.setDate(result);
                invoice.getSupplier().getTemplate().setDate(zone);
            } else if ("ref".equals(analysis)) {
                invoice.setRef(result);
                invoice.getSupplier().getTemplate().setId(zone);
            } else {
                Double value = FormatHandler.getInstance().extractAmount(result);
                if (value == null) {
                    throw new Exception("The analysed value cannot be converted to a number! Please consider xxx.xx syntax for numbers.");
                } else {
                    if (analysis.equals("subtotal")) {
                        invoice.setSubtotal(value);
                        invoice.getSupplier().getTemplate().setSubtotal(zone);
                    } else if (analysis.equals("vatRate")) {
                        invoice.setVATrate(value);
                        invoice.getSupplier().getTemplate().setVATrate(zone);
                    } else if (analysis.equals("vat")) {
                        invoice.setVAT(value);
                        invoice.getSupplier().getTemplate().setVAT(zone);
                    } else if (analysis.equals("total")) {
                        invoice.setTotal(value);
                        invoice.getSupplier().getTemplate().setTotal(zone);
                    }
                }
                if ((double) value != 0.0) {
                    invoice.autoCompleteManualAnalysis();
                }
            }
        }
    }

    private JSONObject wrongJson(String message) {
        JSONObject resp = new JSONObject();
        resp.put("result", "error");
        resp.put("message", message);

        resp = addJSONData(resp);

        return resp;
    }

    private JSONObject successJson(String message) {
        JSONObject resp = new JSONObject();
        resp.put("result", "success");
        resp.put("message", message);

        resp = addJSONData(resp);

        return resp;
    }

    private JSONObject addJSONData(JSONObject resp) {
        return addDisplayData(addInvoiceData(addSupplierData(addImageData(addUserData(resp)))));
    }

    private JSONObject addInvoiceData(JSONObject resp) {
        Invoice result = OcrHandler.getInstance().getInvoice();
        resp = result.getDate() != Invoice.EMPTY ? resp.put("date", result.getDate().trim()) : resp.put("date", " ");
        resp = result.getRef() != Invoice.EMPTY ? resp.put("id", result.getRef().trim()) : resp.put("id", " ");
        resp.put("subtotal", FormatHandler.getInstance().formatOutputAmount(result.getSubtotal()));
        resp.put("vatRate", FormatHandler.getInstance().formatOutputVATRate(result.getVatRate()));
        resp.put("vat", FormatHandler.getInstance().formatOutputAmount(result.getVAT()));
        resp.put("total", FormatHandler.getInstance().formatOutputAmount(result.getTotal()));
        return resp;
    }

    private JSONObject addSupplierData(JSONObject resp) {
        Supplier supplier = OcrHandler.getInstance().getInvoice().getSupplier();
        if (supplier != null) {
            resp.put("supplier", supplier.getName());
            Address address = supplier.getAddress();
            if (address != null) {
                resp.put("street", address.getStreet());
                resp.put("number", address.getNumber());
                resp.put("city", address.getCity());
                resp.put("zip", address.getZip());
                resp.put("country", address.getCountry());
            }
            resp.put("vatNb", FormatHandler.getInstance().formatOutputVat(supplier.getVatNumber()));
            resp = addZonesChecked(resp);
        }
        return resp;
    }

    private JSONObject addUserData(JSONObject resp) {
        if (LoginHandler.getInstance().getUser() == null) {
            resp.put("user", JSONObject.NULL);
        } else {
            resp.put("user", LoginHandler.getInstance().getUser());
        }
        return resp;
    }

    private JSONObject addImageData(JSONObject resp) {
        if (fileName == null) {
            resp.put("imgPath", JSONObject.NULL);
            resp.put("imgName", JSONObject.NULL);
        } else {
            resp.put("imgPath", context.getContextPath() + fileName);
            resp.put("imgName", fileName.getName());
        }
        return resp;
    }

    private JSONObject addDisplayData(JSONObject resp) {
        resp.put("displaySaveInvoice", OcrHandler.getInstance().getInvoice().isValid());
        resp.put("displaySaveSupplier", displaySaveSupplier);
        resp.put("displayUpdateSupplier", displayUpdateSupplier);
        if (fileNames == null || fileNames.isEmpty()) {
            resp.put("nextPrevButtons", false);
        } else {
            resp.put("nextPrevButtons", true);
            resp.put("grayPrevButton", fileName.getPageNumber() == 0);
            resp.put("grayNextButton", fileName.getPageNumber() == fileNames.size() - 1);
        }
        if (!OcrHandler.getInstance().getInvoice().isValid()) {
            String redFields = OcrHandler.getInstance().getInvoice().redFields();
            if (redFields.contains("date")) {
                resp.put("redDate", true);
            }
            if (redFields.contains("ref")) {
                resp.put("redRef", true);
            }
            if (redFields.contains("sub")) {
                resp.put("redSub", true);
            }
            if (redFields.contains("rate")) {
                resp.put("redRate", true);
            }
            if (redFields.contains("vat")) {
                resp.put("redVat", true);
            }
            if (redFields.contains("total")) {
                resp.put("redTotal", true);
            }
        }

        return resp;
    }

    // Provides information about zones that are present in the template (to be displayed to the user)
    private JSONObject addZonesChecked(JSONObject resp) {

        if (OcrHandler.getInstance().getInvoice().getSupplier() != null
                && OcrHandler.getInstance().getInvoice().getSupplier().getTemplate() != null) {
            Template temp = OcrHandler.getInstance().getInvoice().getSupplier().getTemplate();
            String zones = "";
            if (temp.getDate() != null) {
                zones = zones + "date:";
            }
            if (temp.getId() != null) {
                zones = zones + "id:";
            }
            if (temp.getSubtotal() != null) {
                zones = zones + "sub:";
            }
            if (temp.getVATrate() != null) {
                zones = zones + "rate:";
            }
            if (temp.getVAT() != null) {
                zones = zones + "vat:";
            }
            if (temp.getTotal() != null) {
                zones = zones + "total:";
            }
            resp.put("zones", zones);
        }
        return resp;
    }

    private JSONObject successJsonTable(String action, int table) {
        JSONObject resp = new JSONObject();
        resp.put("result", "success");
        resp.put("message", action);
        if (LoginHandler.getInstance().getUser() == null) {
            resp.put("user", JSONObject.NULL);
        } else {
            resp.put("user", LoginHandler.getInstance().getUser());
        }
        try {
            if (table == SUPPLIERS) {
                resp.put("table", HtmlGenerator.getInstance().suppliers2HTML(DbHandler.getInstance().retrieveAllSuppliersSortedArray()));
            } else if (table == INVOICES) {
                resp.put("table", HtmlGenerator.getInstance().invoices2HTML(DbHandler.getInstance().retrieveAllInvoicesSortedArray()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error while retrieving data from the database!");
        }

        return resp;
    }

    private JSONObject successUser(String message) {
        JSONObject resp = new JSONObject();
        resp.put("result", "success");
        resp.put("message", message);
        if (LoginHandler.getInstance().getUser() == null) {
            resp.put("user", JSONObject.NULL);
        } else {
            resp.put("user", LoginHandler.getInstance().getUser());
            resp.put("name", LoginHandler.getInstance().getUser().getName());
            resp.put("vat", FormatHandler.getInstance().formatOutputVat(LoginHandler.getInstance().getUser().getVatNumber()));
            resp.put("address", FormatHandler.getInstance().formatAddress(LoginHandler.getInstance().getUser().getAddress()));
        }
        return resp;
    }

    // Handles files uploading.
    public JSONObject readFile(List<FileItem> items) {
        clearHandler();
        Iterator<FileItem> iter = items.iterator();
        // Not useful since the user can only select on file but could be useful for future multiple selection
        while (iter.hasNext()) {
            FileItem item = iter.next();
            if (item.getName().equals("")) {
                return wrongJson("No file was selected!");
            }

            String extension = FilenameUtils.getExtension(item.getName());

            if (!(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                    || extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("bmp"))) {
                return wrongJson("File extension not supported!");
            }
            if (extension.equals("pdf")) {
                try {
                    fileNames = PdfHandler.getInstance().convertPDF(item, context.getRealPath(TESSDIR), TESSDIR);
                } catch (Exception e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while converting PDF file to image (for later OCR analysis)!");
                }
                if (!fileNames.isEmpty()) {
                    fileName = fileNames.get(0);
                    try {
                        image = new ImageHandler(oriImgFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return wrongJson("Unexpected error while loading the image!");
                    }
                }
            } else {
                fileName = new FileName(TESSDIR + item.getName(), item.getName());
                try {
                    save(item);
                } catch (Exception e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while writing the uploaded file to a temporary file!");
                }
                try {
                    image = new ImageHandler(oriImgFile());
                } catch (IOException e) {
                    e.printStackTrace();
                    return wrongJson("Unexpected error while loading the image!");
                }
            }
        }

        try {
            String analyseAction = OcrHandler.getInstance().ocrAutoAnalyse(language, image);
            OcrHandler.getInstance().getInvoice().autoCompleteAutoAnalysis();
            return successJson("Invoice uploaded. " + analyseAction);
        } catch (SQLException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error while retrieving suppliers from the database to perform auto analysis!");
        } catch (TesseractException e) {
            e.printStackTrace();
            fileName = null;
            return wrongJson("Unexpected error while performing OCR during auto-analysis! It seems that your file format is not supported.");
        }
    }

    // Writes a file to disk.
    private void save(FileItem file) throws Exception {
        FileHandler fileHandler = new FileHandler(oriImgFile());
        fileHandler.saveFile(file);
    }


    private File oriImgFile() {
        return new File(context.getRealPath(fileName.get()));
    }

    // Rotates an image.
    private JSONObject rotate(int direction) {
        String filePath = context.getRealPath(fileName.get());
        BufferedImage input = UtilImageIO.loadImage(filePath);
        if (input == null) {
            return wrongJson("Cannot rotate invoice");
        }
        AffineTransform transform = new AffineTransform();
        if (direction == LEFT) {
            transform.rotate(-ROTATION_INCREMENT, input.getWidth() / 2, input.getHeight() / 2);
        } else if (direction == RIGHT) {
            transform.rotate(ROTATION_INCREMENT, input.getWidth() / 2, input.getHeight() / 2);
        }
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        input = op.filter(input, null);
        File output = new File(filePath);
        try {
            ImageIO.write(input, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error while writing the image after rotation!");
        }
        try {
            image = new ImageHandler(oriImgFile());
        } catch (IOException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error while loading the image!");
        }
        return successJson("Invoice rotated");
    }

    // Clears data in this handler.
    private void clearHandler() {
        fileNames = null;
        fileName = null;
        image = null;
        displaySaveSupplier = false;
        displayUpdateSupplier = false;
        OcrHandler.getInstance().setInvoice(new Invoice());
    }

    public void setDisplayUpdateSupplier(Boolean displayUpdateSupplier) {
        this.displayUpdateSupplier = displayUpdateSupplier;
    }

    public void setDisplaySaveSupplier(Boolean displaySaveSupplier) {
        this.displaySaveSupplier = displaySaveSupplier;
    }
}