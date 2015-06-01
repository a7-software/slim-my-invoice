package biz.a7software.slimmyinvoice.login;

import biz.a7software.slimmyinvoice.data.Address;
import biz.a7software.slimmyinvoice.data.Supplier;
import biz.a7software.slimmyinvoice.data.User;
import biz.a7software.slimmyinvoice.helper.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The LoginHandler class is the interface with the users database and handles login
 * related operations (login check, registration, updating password).
 */
public class LoginHandler {

    private static final String USER = "admin";
    private static final String PASS = "ys$:E8@4gvz}/qh";
    private static volatile LoginHandler instance = null;
    private ConnectionSource connectionSource = null;
    private String databaseUrl = "jdbc:sqlite:" + Properties.databasePath + "users.db";
    private User user = null;

    private LoginHandler() {
    }

    public final static LoginHandler getInstance() {
        if (LoginHandler.instance == null) {
            synchronized (LoginHandler.class) {
                if (LoginHandler.instance == null) {
                    LoginHandler.instance = new LoginHandler();
                    Logger.getLogger("com.j256.ormlite.dao.DaoManager").setLevel(Level.OFF);
                    Logger.getRootLogger().setLevel(Level.OFF);
                }
            }
        }
        return LoginHandler.instance;
    }

    // Creates the user table in the database if not exists.
    public void createUserTableIfNotExists() throws SQLException {
        connectionSource = new JdbcConnectionSource(databaseUrl, USER, PASS) {
        };
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Address.class);
        connectionSource.close();
    }

    // Resets the user table in the database.
    public void resetUserTable() throws SQLException {
        connectionSource = new JdbcConnectionSource(databaseUrl, USER, PASS) {
        };
        TableUtils.dropTable(connectionSource, User.class, false);
        connectionSource.close();
    }

    // Adds a user in the database.
    public void addUser(User user) throws SQLException {
        createUserTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, USER, PASS) {
        };
        Dao<User, String> accountDao = DaoManager.createDao(connectionSource, User.class);
        accountDao.createIfNotExists(user);
        connectionSource.close();
    }

    // Updates a user in the database.
    public void updateUser(User user) throws SQLException {
        createUserTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, USER, PASS) {
        };
        Dao<User, String> accountDao = DaoManager.createDao(connectionSource, User.class);
        accountDao.update(user);
        connectionSource.close();
    }

    // Retrieves a user in the DB from his VAT number.
    public User retrieveUser(String vat) throws SQLException {
        createUserTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, USER, PASS) {
        };
        Dao<User, String> accountDao = DaoManager.createDao(connectionSource, User.class);
        User user = accountDao.queryForId(vat);
        connectionSource.close();
        return user;
    }

    // Handles a login request.
    public JSONObject login(StringParams reqs) throws ServletException, IOException {

        String username = reqs.getParam("username");
        String vat;
        username = username.replaceAll(" ", "");
        if (username.matches("[0-9]{10}")) {
            vat = username;
        } else {
            return wrongJson("The username must be composed of exactly 10 numbers!");
        }


        User dbUser;
        try {
            dbUser = retrieveUser(vat);
        } catch (SQLException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error when accessing the users database!");
        }
        if (dbUser == null) {
            return wrongJson("Unknown username! Please check it or consider registering if this is the first time you want to log in!");
        } else {
            if (dbUser.getMustUpdate() != null) {
                if (dbUser.getMustUpdate() < System.currentTimeMillis()) {
                    return wrongJson("Temporary password has expired. Please click on \"Forgot my password?\" to receive a new temporary password");
                }
            }
            String password = reqs.getParam("password");
            if (dbUser.getPassword().equals(password)) {
                DbHandler.getInstance().configureForUser(dbUser);
                this.user = dbUser;
                return successJson();
            } else {
                return wrongJson("Wrong password!");
            }
        }
    }

    // Handles a register request.
    public JSONObject register(StringParams reqs) {

        String username = reqs.getParam("username");
        if (username.length() != 10) {
            return wrongJson("The username must be composed of exactly 10 numbers!");
        }

        // Useless check for now since we only accept number entries (useful if the webapp is changed)
        String vat;
        username = username.replaceAll(" ", "");
        if (username.matches("[0-9]{10}")) {
            vat = username;
        } else {
            return wrongJson("The username must be composed of exactly 10 numbers!");
        }

        if (!FormatHandler.getInstance().isValidVAT(vat)) {
            return wrongJson("The VAT number provided as username is not valid!");
        }

        User user;
        try {
            user = retrieveUser(vat);
        } catch (SQLException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error when accessing the users database!");
        }
        if (user != null) {
            return wrongJson("Already registered! Please consider login instead of registering.");
        }

        String pwdLengthCheck = reqs.getParam("passwordMinLenReached");
        if (pwdLengthCheck.equals("false")) {
            return wrongJson("The password must be composed of at least 8 characters!");
        }

        String pwdsMatchCheck = reqs.getParam("passwordMatched");
        if (pwdsMatchCheck.equals("false")) {
            return wrongJson("The passwords do not match!");
        }

        String email = reqs.getParam("email");
        if (email == null || email.equals("")) {
            return wrongJson("Please provide an email address");
        }

        if (!FormatHandler.getInstance().isValidEmail(email)) {
            return wrongJson("The email address you have entered is not valid!");
        }

        String verifyEmail = reqs.getParam("verify_email");
        if (!verifyEmail.equals(email)) {
            return wrongJson("The email addresses do not match!");
        }

        Supplier supplier = HtmlParser.getInstance().retrieveSupplierFromVATNum(vat);
        if (supplier == null) {
            return wrongJson("Cannot retrieve data from the BCE online database to get. Please retry later! If the problem persists, contact the support.");
        }

        String password = reqs.getParam("password");
        user = new User(supplier.getVatNumber(), password, supplier.getName(), supplier.getAddress(), email);
        try {
            addUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error while adding new user in the database!");

        }
        DbHandler.getInstance().configureForUser(user);
        this.user = user;
        return successJson();
    }

    // Handles a password update request.
    public JSONObject updatePassword(StringParams reqs) {

        String username = reqs.getParam("username");
        if (username.length() != 10) {
            return wrongJson("The username must be composed of exactly 10 numbers!");
        }

        // Useless check for now since we only accept number entries (useful if the webapp is changed)
        String vat;
        username = username.replaceAll(" ", "");
        if (username.matches("[0-9]{10}")) {
            vat = username;
        } else {
            return wrongJson("The username must be composed of exactly 10 numbers!");
        }

        if (!FormatHandler.getInstance().isValidVAT(vat)) {
            return wrongJson("The VAT number provided as username is not valid!");
        }

        User user;
        try {
            user = retrieveUser(vat);
        } catch (SQLException e) {
            e.printStackTrace();
            return wrongJson("Unexpected error when accessing the users database!");
        }
        if (user == null) {
            return wrongJson("Unknown username! Please check it or consider registering if this is the first time you want to login!");
        } else {
            if (user.getMustUpdate() != null) {
                if (user.getMustUpdate() < System.currentTimeMillis()) {
                    return wrongJson("Temporary password has expired. Please follow the \"Forgot my password?\" (accessible from Login page) procedure to receive a new temporary password");
                }
            }

            String oldPwd = reqs.getParam("oldPwd");
            if (!user.getPassword().equals(oldPwd)) {
                return wrongJson("Old password is wrong");
            }

            String pwdLengthCheck = reqs.getParam("passwordMinLenReached");
            if (pwdLengthCheck.equals("false")) {
                return wrongJson("The new password must be composed of at least 8 characters!");
            }

            String pwdsMatchCheck = reqs.getParam("passwordMatched");
            if (pwdsMatchCheck.equals("false")) {
                return wrongJson("The new passwords do not match!");
            }

            user.setPassword(reqs.getParam("newPwd"));
            user.setMustUpdate(Long.MAX_VALUE);
            try {
                updateUser(user);
            } catch (SQLException e) {
                e.printStackTrace();
                return wrongJson("Unexpected error while adding new password in the database!");
            }
            return successJson();
        }
    }

    private JSONObject wrongJson(String message) {
        JSONObject resp = new JSONObject();
        resp.put("result", "error");
        resp.put("message", message);
        return resp;
    }

    private JSONObject successJson() {
        JSONObject resp = new JSONObject();
        resp.put("result", "success");
        return resp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}