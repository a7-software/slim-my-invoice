package biz.a7software.slimmyinvoice.helper;

import org.apache.commons.fileupload.FileItem;

/**
 * The HttpParam provides the interface for the getters for parameters from frontend requests.
 */
public interface HttpParam {

    FileItem getFile(String name);

    String getParam(String name);

    String[] getParams(String name);
}