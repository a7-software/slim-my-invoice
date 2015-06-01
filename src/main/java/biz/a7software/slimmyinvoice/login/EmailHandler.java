package biz.a7software.slimmyinvoice.login;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * The EmailHandler class is responsible for sending an email with new password.
 */
public class EmailHandler {

    private static volatile EmailHandler instance = null;

    private EmailHandler() {
    }

    public final static EmailHandler getInstance() {
        if (EmailHandler.instance == null) {
            synchronized (EmailHandler.class) {
                if (EmailHandler.instance == null) {
                    EmailHandler.instance = new EmailHandler();
                }
            }
        }
        return EmailHandler.instance;
    }

    // Sends an email with new password.
    public static void sendEmail(String userUsername, String userPassword, String companyName, String mailAddress) throws MessagingException {


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", biz.a7software.slimmyinvoice.helper.Properties.smtpHost);
        props.put("mail.smtp.port", biz.a7software.slimmyinvoice.helper.Properties.smtpPort);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(biz.a7software.slimmyinvoice.helper.Properties.emailUsername, biz.a7software.slimmyinvoice.helper.Properties.emailPassword);
                    }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(biz.a7software.slimmyinvoice.helper.Properties.emailUsername));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(mailAddress));
        message.setSubject("Slim my invoice - Forgot your password");
        message.setText("Dear user from " + companyName + ","
                + "\n\n Here is your new password: "
                //+ "\n\n Login (VAT) = " + userUsername
                + "\n New temporary password: " + userPassword
                + "\n\n Your password is only valid for 2 days. After this period, you won't be able to log" +
                "in or change your password and you must again follow this procedure to get a new temporary password."
                + "\n\n To change your password, click on \"Change my password\" on the Sign in page.");

        Transport.send(message);
    }
}