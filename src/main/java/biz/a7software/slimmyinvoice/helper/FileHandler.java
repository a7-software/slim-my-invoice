package biz.a7software.slimmyinvoice.helper;

import org.apache.commons.fileupload.FileItem;

import java.io.File;

/**
 * The FileHandler class manages a File object and allow saving it.
 */
public class FileHandler {

    private File file;

    public FileHandler(File file) {
        this.file = file;
    }

    public void saveFile(FileItem item) throws Exception {
        item.write(this.file);
    }
}