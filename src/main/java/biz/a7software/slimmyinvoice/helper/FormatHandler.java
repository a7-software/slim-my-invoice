package biz.a7software.slimmyinvoice.helper;


import biz.a7software.slimmyinvoice.data.Address;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * The FormatHandler class handles formats for display (amounts, VAT number, etc.) or for validation purposes (VAT number, email, etc.).
 */
public class FormatHandler {

    private static volatile FormatHandler instance = null;

    private FormatHandler() {
    }

    public final static FormatHandler getInstance() {
        if (FormatHandler.instance == null) {
            synchronized (FormatHandler.class) {
                if (FormatHandler.instance == null) {
                    FormatHandler.instance = new FormatHandler();
                }
            }
        }
        return FormatHandler.instance;
    }

    // Extracts a double value from a string according to most conventional
    // ways of writing numbers or null if the string cannot be interpreted.
    public static Double extractAmount(String input) {

        input = input.trim();
        boolean negative = false;
        if (input.startsWith("-")) {
            negative = true;
        }

        input = input.replaceAll(",", ".");
        input = input.replaceAll("[^0-9|^\\.]", "");

        if (input.matches("[0-9]+\\.?")) {
            return signDouble(negative, new Double(input));
        } else if (input.matches("[0-9]+\\.[0-9]{1,2}")) {
            return signDouble(negative, new Double(input));
        } else if (input.matches("\\.[0-9]{1,2}")) {
            return signDouble(negative, new Double(input));
        } else {
            if (input.matches("([0-9]|\\.)*\\.[0-9]")) {
                return signDouble(negative, new Double(input.substring(0, input.length() - 2).replaceAll("\\.", "") + input.substring(input.length() - 2)));
            }
            if (input.matches("([0-9]|\\.)*\\.[0-9]{2}")) {
                return signDouble(negative, new Double(input.substring(0, input.length() - 3).replaceAll("\\.", "") + input.substring(input.length() - 3)));
            }
            if (input.matches("([0-9]|\\.)*")) {
                return signDouble(negative, new Double(input.replaceAll("\\.", "")));
            }
            return null;
        }
    }

    private static Double signDouble(boolean negative, Double d) {
        if (negative) {
            return new Double(-d);
        }
        return d;
    }

    // Rounds a double value according to a certain place value.
    public static double round(Double value, Integer places) {
        if (places < 0) {
            throw new IllegalArgumentException("Round places must be positive!");
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // Check whether the provided string is a valid Belgian VAT number.
    public boolean isValidVAT(String vat) {

        String firstEightNum = vat.substring(0, 8);
        String lastTwoNum = vat.substring(8);

        Long firstEightNumVal = new Long(firstEightNum);
        Long lastTwoNumVal = new Long(lastTwoNum);

        return (lastTwoNumVal == (97 - (firstEightNumVal % 97)));
    }

    // Interprets old Belgian VAT numbers according to the new regulation.
    public String formatInputVat(String vat) {
        vat = vat.replaceAll("[^0-9]", "");
        if (vat.length() == 9) {
            vat = "0" + vat;
            return vat;
        } else if (vat.length() == 10) {
            return vat;
        } else {
            return null;
        }
    }

    // Check whether the provided string is a valid email address according to RFC 822 (http://www.ietf.org/rfc/rfc822.txt)
    public boolean isValidEmail(String email) {
        boolean isValid = false;
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e) {
            // Nothing special has to be done, method will return false
        }
        return isValid;
    }

    // Cannot override toString from Address class since it conflicts with the serialization required for the database.
    public String formatAddress(Address address) {
        if (address == null) {
            return "No address";
        }
        return address.getStreet() + ", " + address.getNumber() + " - " + address.getZip() + " "
                + address.getCity() + " (" + address.getCountry() + ")";
    }

    // Formats Belgian VAT numbers according to the new regulation.
    public String formatOutputVat(String vat) {
        if (vat.length() == 10) {
            return "BE " + vat.substring(0, 4) + " " + vat.substring(4, 7) + " " + vat.substring(7);
        } else {
            return vat;
        }
    }

    // Formats amounts to be displayed and adds currency.
    public String formatOutputAmount(Double amount) {
        return String.format("%.2f%n", amount);
    }

    // Formats VAT rates to be displayed.
    public String formatOutputVATRate(Double vatRate) {
        return String.format("%.2f", vatRate) + " %";
    }
}