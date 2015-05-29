package biz.a7software.slimmyinvoice.helper;


/**
 * The FileName class represents a filename and contains page number in case of multi-pages PDF files.
 */
public class FileName {

    private String fileName;
    private String name;
    private int pageNumber;

    public FileName(String fileName, String name) {
        this.fileName = fileName;
        this.name = name;
    }

    public FileName(String fileName, String name, int pageNumber) {
        this.fileName = fileName;
        this.name = name;
        this.pageNumber = pageNumber;
    }


    public boolean isEmpty() {
        return fileName == null || fileName.isEmpty();
    }

    public String get() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public String toString() {
        return fileName;
    }

}