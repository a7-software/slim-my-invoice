package biz.a7software.slimmyinvoice.helper;

/**
 * The Properties class hosts user-dependant data.
 */
public abstract class Properties {

    // Path to the databases. Make sure this folder is readable and writable.
    public static final String databasePath = "/var/lib/slimmyinvoice/databases/";

    // Enter your address email here. Must be gmail otherwise you need to change the SMTP server.
    public static final String emailUsername = "";
    // Enter you email address password here.
    public static final String emailPassword = "";


    // Default gmail configuration.
    // SMTP Host server
    public static final String smtpHost = "smtp.gmail.com";
    // SMTP port
    public static final String smtpPort = "587";
}
