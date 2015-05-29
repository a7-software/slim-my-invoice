package biz.a7software.slimmyinvoice.servlet;

import biz.a7software.slimmyinvoice.helper.DbHandler;
import biz.a7software.slimmyinvoice.helper.StringParams;
import biz.a7software.slimmyinvoice.helper.UploadHandler;
import biz.a7software.slimmyinvoice.login.LoginHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Upload class interfaces and handles GET and POST requests arriving from the web application.
 */
@WebServlet("/upload")
public class Upload extends HttpServlet {

    private static final long serialVersionUID = 4220745755155487595L;

    // Handles GET requests.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("upload - get");


        StringParams reqs = new StringParams(request.getParameterMap());

        //System.out.println("Hash = " + reqs.getParam("password"));
        if (false) {
            try {
                LoginHandler.getInstance().resetUserTable();
                DbHandler.getInstance().refreshDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        JSONObject resp = UploadHandler.getInstance(getServletContext()).handleGETRequest(reqs);

        System.out.println("GET RESP = " + resp);
        writeToResponse(response, resp);
    }

    // Handles POST requests.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("upload - post");

        JSONObject resp;

        if (LoginHandler.getInstance().getUser() == null) {
            resp = wrongJSON("No user logged in!");
        } else {
            checkMultipartForm(request);
            List<FileItem> items = null;
            try {
                items = parseRequest(request);
                resp = getHandler().readFile(items);
            } catch (FileUploadException e) {
                e.printStackTrace();
                resp = wrongJSON("Internal error: cannot parse request!");
            }
        }
        System.out.println("POST RESP = " + resp);
        writeToResponse(response, resp);
    }


    private JSONObject wrongJSON(String message) {
        JSONObject resp = new JSONObject();
        resp.put("result", "error");
        resp.put("message", message);
        return resp;
    }

    // Parses the POST request to obtain the list of files that have been uploaded.
    private List<FileItem> parseRequest(HttpServletRequest request) throws FileUploadException {
        ServletFileUpload upload = getRequestHandler();
        List<FileItem> itemsDuplicated = upload.parseRequest(request);
        return eliminateDuplicate(itemsDuplicated);
    }

    // Checks that the POST request is multipart.
    private void checkMultipartForm(HttpServletRequest request) {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }
    }

    // Gets the UploadHandler.
    private UploadHandler getHandler() {
        return UploadHandler.getInstance(getServletContext());
    }

    // Gets an upload handler.
    private ServletFileUpload getRequestHandler() {
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        File repository = getTempDir();
        factory.setRepository(repository);

        // Return a new file upload handler
        return new ServletFileUpload(factory);
    }

    // Gets a temp directory.
    private File getTempDir() {
        ServletContext servletContext = getServletContext();
        return (File) servletContext.getAttribute("javax.servlet.context.tempdir");
    }

    // Eliminate duplicates (don't know why files appears twice in POST requests)
    private List<FileItem> eliminateDuplicate(List<FileItem> itemsDuplicated) {
        List<FileItem> items = new ArrayList<FileItem>();
        for (int i = 0; i < itemsDuplicated.size(); i++) {
            FileItem tmp = itemsDuplicated.get(i);
            int j;
            for (j = 0; j < items.size(); j++) {
                String tmp_name = tmp.getName();
                String items_name = items.get(j).getName();
                if (tmp_name.equals(items_name)) {
                    break;
                }
            }
            if (j == items.size()) {
                items.add(tmp);
            }
        }
        return items;
    }

    // Writes the JSON string as response to the web application.
    private void writeToResponse(HttpServletResponse response, JSONObject resp)
            throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(resp.toString());
        response.getWriter().close();
    }
}