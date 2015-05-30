package biz.a7software.slimmyinvoice.login;

import biz.a7software.slimmyinvoice.data.User;
import biz.a7software.slimmyinvoice.helper.DbHandler;
import biz.a7software.slimmyinvoice.helper.StringParams;
import org.json.JSONObject;

import javax.mail.MessagingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;

/**
 * The PasswordRequest class handles the - Forgot my password? -  procedure (secure password generation, hashing).
 */
public class PasswordRequest {
    private static volatile PasswordRequest instance = null;

    private PasswordRequest() {
    }

    public final static PasswordRequest getInstance() {
        if (PasswordRequest.instance == null) {
            synchronized (PasswordRequest.class) {
                if (PasswordRequest.instance == null) {
                    PasswordRequest.instance = new PasswordRequest();
                }
            }
        }
        return PasswordRequest.instance;
    }

    // Handles a password the - Forgot my password? -  procedure request.
    public JSONObject forgotPassword(StringParams reqs) {
        String username = reqs.getParam("username");

        String vat;
        username = username.replaceAll(" ", "");
        if (username.matches("[0-9]{10}")) {
            vat = username;
        } else {
            return wrongJson("The username must be composed of exactly 10 numbers!");
        }

        User dbUser = null;
        try {
            dbUser = LoginHandler.getInstance().retrieveUser(vat);
        } catch (SQLException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error when accessing the users database!");
        }
        if (dbUser == null) {
            return wrongJson("Unknown username! Please check it...");
        } else {
            User updated = dbUser;
            try {
                // Add temp password and timestamp
                String tempPassword = generatePassword();
                updated.setPassword(sha256(tempPassword));
                updated.setMustUpdate(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 2); // 2 days delay to update password
                LoginHandler.getInstance().updateUser(updated);
                DbHandler.getInstance().configureForUser(updated);
                LoginHandler.getInstance().setUser(updated);
                EmailHandler.getInstance().sendEmail(vat, tempPassword, updated.getName(), updated.getEmail());
            } catch (MessagingException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while sending email!");
            } catch (SQLException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while renewing password!");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while renewing password!");
            }
            return successJson(updated.getEmail());
        }
    }

    // Securely randomly generates a password.
    private String generatePassword() {
        final String AB = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%&*()_+=-|<>?{}[]~";
        SecureRandom rnd = new SecureRandom();

        int passWordLength = 10;
        StringBuilder sb = new StringBuilder(passWordLength);
        for (int i = 0; i < passWordLength; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    // Hashes string according the SHA-256 standards.
    private String sha256(String password) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    private JSONObject wrongJson(String message) {
        JSONObject resp = new JSONObject();
        resp.put("result", "error");
        resp.put("message", message);
        return resp;
    }

    private JSONObject successJson(String address) {
        JSONObject resp = new JSONObject();
        resp.put("result", "success");
        resp.put("address", address);
        return resp;
    }
}