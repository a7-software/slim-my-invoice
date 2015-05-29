package biz.a7software.slimmyinvoice.ocr;

import biz.a7software.slimmyinvoice.helper.Area;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The OcrEngine class is the interface with the Tesseract OCR engine.
 */
public class OcrEngine {

    public static final String ENG = "eng";
    public static final String FRA = "fra";

    public static final String DEFAULT_LANGUAGE = FRA;

    private static volatile OcrEngine engineInstance = null;

    private Tesseract instance;
    private Rectangle rect;


    private OcrEngine() {
        instance = Tesseract.getInstance();
        instance.setOcrEngineMode(TessAPI.TessOcrEngineMode.OEM_TESSERACT_ONLY);
        setLanguage(DEFAULT_LANGUAGE);
    }

    public final static OcrEngine getInstance() {
        if (OcrEngine.engineInstance == null) {
            synchronized (OcrEngine.class) {
                if (OcrEngine.engineInstance == null) {
                    OcrEngine.engineInstance = new OcrEngine();
                }
            }
        }
        return OcrEngine.engineInstance;
    }

    // Creates a rectangle Object from a Area object.
    public void chooseArea(Area area) {
        rect = new Rectangle(area.getX(), area.getY(), area.getW(), area.getH());
    }

    // Set the language of the OCR engine.
    public void setLanguage(String l) {
        if (l == null || l.isEmpty()) {
            return;
        }
        instance.setLanguage(l);
    }

    // Set the default language of the OCR engine.
    public void setDefaultLanguage() {
        instance.setLanguage(DEFAULT_LANGUAGE);
    }


    // Performs OCR on a given file according the a certain rectangle zone.
    public String result(File file) throws TesseractException {
        return instance.doOCR(file, rect);
    }

    // Performs OCR on a given BufferedImage according the a certain rectangle zone.
    public String result(BufferedImage img) throws TesseractException {
        return instance.doOCR(img, rect);
    }

    // Performs OCR on the entire provided BufferedImage.
    public String fullOcr(BufferedImage img) throws TesseractException {
        return instance.doOCR(img);
    }
}
