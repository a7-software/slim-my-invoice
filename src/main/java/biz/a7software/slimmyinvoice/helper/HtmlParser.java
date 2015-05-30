package biz.a7software.slimmyinvoice.helper;

import biz.a7software.slimmyinvoice.data.Address;
import biz.a7software.slimmyinvoice.data.Supplier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * The HtmlParser class performs HTML parsing to retrieve information about companies from the BCE online Database:
 * http://economie.fgov.be/fr/modules/onlineservice/bce/bce_public_search_odi.jsp
 */
public class HtmlParser {

    private static String NAME_FIELD = "nomination sociale";
    private static String ADDRESS_FIELD = "Adresse du si";
    private static String PRIVATE_ADDRESS_FIELD = "cause de la protection de la vie priv";


    private static volatile HtmlParser instance = null;

    private HtmlParser() {
    }

    public final static HtmlParser getInstance() {
        if (HtmlParser.instance == null) {
            synchronized (DbHandler.class) {
                if (HtmlParser.instance == null) {
                    HtmlParser.instance = new HtmlParser();
                }
            }
        }
        return HtmlParser.instance;
    }

    // Parse HTML response from a request based on the supplier's VAT number.
    public Supplier retrieveSupplierFromVATNum(String VAT) {

        Supplier supplier = null;

        String url = new String("http://kbopub.economie.fgov.be/kbopub/zoeknummerform.html?lang=fr&nummer=" + VAT + "&actionLu=Recherche");
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            Element table = doc.select("table").first();
            if (table == null) {
                return null;
            }

            Elements elements = table.select("tr");

            boolean nameFound = false;
            boolean addressFound = false;
            String name = null;
            String street = null;
            String number = null;
            String zip = null;
            String city = null;

            for (int i = 0; !(nameFound && addressFound) && i < elements.size(); i++) {
                // Company name
                if (elements.get(i).children().size() >= 2 && elements.get(i).child(0).toString().contains(NAME_FIELD)) {
                    String string = elements.get(i).child(1).toString();
                    String beforeName = "> ";
                    String afterName = "<br>";
                    int beforeNameIndex = string.indexOf(beforeName, 0) + beforeName.length();
                    int afterNameIndex = string.indexOf(afterName, 0);
                    name = string.substring(beforeNameIndex, afterNameIndex);
                    nameFound = true;
                }
                // Company address (usual case)
                if (elements.get(i).children().size() >= 2 && elements.get(i).child(0).toString().contains(ADDRESS_FIELD)) {
                    String string = elements.get(i).child(1).toString();

                    // Street
                    String beforeStreet = "> ";
                    String afterStreet = "&nbsp;";
                    int beforeStreetIndex = string.indexOf(beforeStreet, 0) + beforeStreet.length();
                    int afteStreetIndex = string.indexOf(afterStreet, 0);
                    street = string.substring(beforeStreetIndex, afteStreetIndex);

                    // Number
                    String afterNumber = " <a";
                    int afterNumberIndex = string.indexOf(afterNumber, afteStreetIndex);
                    number = string.substring(afteStreetIndex + afterStreet.length(), afterNumberIndex);

                    // Zip
                    String beforeZip = "</a><br>";
                    String afterZip = "&nbsp;";
                    int beforeZipIndex = string.indexOf(beforeZip, afterNumberIndex) + beforeZip.length();
                    int afterZipIndex = string.indexOf(afterZip, afterNumberIndex);
                    zip = string.substring(beforeZipIndex, afterZipIndex);

                    // City
                    String afterCity = " <span";
                    int afterCityIndex = string.indexOf(afterCity, afterZipIndex);
                    city = string.substring(afterZipIndex + afterZip.length(), afterCityIndex);

                    addressFound = true;
                }
                // Company address (private address case)
                else if (elements.get(i).children().size() >= 2 && elements.get(i).child(1).toString().contains(PRIVATE_ADDRESS_FIELD)) {
                    String string = elements.get(i).child(1).toString();

                    // Street
                    String beforeStreet = "active:<br> ";
                    String afterStreet = "&nbsp;";
                    int beforeStreetIndex = string.indexOf(beforeStreet, 0) + beforeStreet.length();
                    int afteStreetIndex = string.indexOf(afterStreet, 0);
                    street = string.substring(beforeStreetIndex, afteStreetIndex);

                    // Number
                    String afterNumber = " <br>";
                    int afterNumberIndex = string.indexOf(afterNumber, afteStreetIndex);
                    number = string.substring(afteStreetIndex + afterStreet.length(), afterNumberIndex);


                    // Zip
                    String afterZip = "&nbsp;";
                    int afterZipIndex = string.indexOf(afterZip, afterNumberIndex);
                    zip = string.substring(afterNumberIndex + afterNumber.length(), afterZipIndex);

                    // City
                    String afterCity = " </span>";
                    int afterCityIndex = string.indexOf(afterCity, afterZipIndex);
                    city = string.substring(afterZipIndex + afterZip.length(), afterCityIndex);

                    addressFound = true;
                }
            }

            if (nameFound || addressFound) {
                supplier = new Supplier();
                if (nameFound) {
                    supplier.setName(name);
                }
                if (addressFound) {
                    Address address = new Address(street, number, zip, city, Address.BELGIUM);
                    supplier.setAddress(address);
                }
                supplier.setVatNumber(VAT);
            }
        } catch (IOException e) {
            // URL has changed, the server is down or the computer is not connected to the Internet
            e.printStackTrace();
            return null;
        }
        return supplier;
    }
}