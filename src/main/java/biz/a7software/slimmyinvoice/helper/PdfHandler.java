package biz.a7software.slimmyinvoice.helper;

import org.apache.commons.fileupload.FileItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The PdfHandler class converts PDF files to PNG images (required for later manipulation and operations).
 */
public class PdfHandler {

    private static volatile PdfHandler instance = null;

    private PdfHandler() {
    }

    public final static PdfHandler getInstance() {
        if (PdfHandler.instance == null) {
            synchronized (PdfHandler.class) {
                if (PdfHandler.instance == null) {
                    PdfHandler.instance = new PdfHandler();
                }
            }
        }
        return PdfHandler.instance;
    }

    // Converts a PDF file to PNG image(s).
    public List<FileName> convertPDF(FileItem fileItem, String filePath, String dir) throws Exception {

        String fileName = fileItem.getName();
        File file = new File(filePath + "/" + fileName);

        List<FileName> filesNames = new ArrayList<FileName>();

        PDDocument document = null;

        fileItem.write(file);

        document = PDDocument.loadNonSeq(file, null);

        List<PDPage> pdPages = document.getDocumentCatalog().getAllPages();
        int page = 0;
        for (PDPage pdPage : pdPages) {
            ++page;
            BufferedImage bim = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 300);
            ImageIOUtil.writeImage(bim, filePath + "/" + fileItem.getName() + "-" + page + ".png", 300);
            filesNames.add(new FileName(dir + fileItem.getName() + "-" + page + ".png", fileItem.getName(), page - 1));
        }
        document.close();

        return filesNames;
    }
}