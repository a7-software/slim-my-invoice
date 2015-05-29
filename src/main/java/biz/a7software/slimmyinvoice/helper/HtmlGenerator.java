package biz.a7software.slimmyinvoice.helper;

import biz.a7software.slimmyinvoice.data.Invoice;
import biz.a7software.slimmyinvoice.data.Supplier;

/**
 * The HtmlGenerator class produces HTML code to be displayed in HTML pages.
 */
public class HtmlGenerator {

    private static volatile HtmlGenerator instance = null;

    private HtmlGenerator() {
    }

    public final static HtmlGenerator getInstance() {
        if (HtmlGenerator.instance == null) {
            synchronized (HtmlGenerator.class) {
                if (HtmlGenerator.instance == null) {
                    HtmlGenerator.instance = new HtmlGenerator();
                }
            }
        }
        return HtmlGenerator.instance;
    }

    // Produces HTML code to display a table of suppliers.
    public String suppliers2HTML(Supplier[] suppliers) {

        StringBuilder html = new StringBuilder(
                "<table><thead><tr>");

        html.append("<th width=\"25%\">" + Supplier.SUPPLIER_DISPLAY_HEADERS[0] + "</th>");
        html.append("<th width=\"15%\">" + Supplier.SUPPLIER_DISPLAY_HEADERS[1] + "</th>");
        html.append("<th width=\"50%\">" + Supplier.SUPPLIER_DISPLAY_HEADERS[2] + "</th>");
        html.append("</tr></thead><tbody>");

        for (int i = 0; i < suppliers.length; i++) {
            Supplier sup = suppliers[i];
            if (i % 2 == 0) {
                html.append("<tr>");
            } else {
                html.append("<tr class=\"alt\">");
            }
            html.append("<td width=\"25%\">" + sup.getName() + "</td>");
            html.append("<td width=\"15%\">" + FormatHandler.getInstance().formatOutputVat(sup.getVatNumber()) + "</td>");
            html.append("<td width=\"50%\">" + FormatHandler.getInstance().formatAddress(sup.getAddress()) + "</td>");
            html.append("</tr>");
        }
        html.append("</tbody></table>");
        return html.toString();
    }

    // Produces HTML code to display a table of invoices.
    public String invoices2HTML(Invoice[] invoices) {
        StringBuilder html = new StringBuilder(
                "<table><thead><tr>");

        html.append("<th width=\"5%\">" + Invoice.INVOICE_DISPLAY_HEADERS[0] + "</th>");
        html.append("<th width=\"8%\">" + Invoice.INVOICE_DISPLAY_HEADERS[1] + "</th>");
        html.append("<th width=\"8%\">" + Invoice.INVOICE_DISPLAY_HEADERS[2] + "</th>");
        html.append("<th width=\"30%\">" + Invoice.INVOICE_DISPLAY_HEADERS[3] + "</th>");
        html.append("<th width=\"8%\">" + Invoice.INVOICE_DISPLAY_HEADERS[4] + "</th>");
        html.append("<th width=\"5%\">" + Invoice.INVOICE_DISPLAY_HEADERS[5] + "</th>");
        html.append("<th width=\"8%\">" + Invoice.INVOICE_DISPLAY_HEADERS[6] + "</th>");
        html.append("<th width=\"8%\">" + Invoice.INVOICE_DISPLAY_HEADERS[7] + "</th>");
        html.append("</tr></thead><tbody>");

        for (int i = 0; i < invoices.length; i++) {
            Invoice inv = invoices[i];
            if (i % 2 == 0) {
                html.append("<tr>");
            } else {
                html.append("<tr class=\"alt\">");
            }
            html.append("<td width=\"5%\">" + inv.getId() + "</td>");
            html.append("<td width=\"8%\">" + inv.getDate() + "</td>");
            html.append("<td width=\"8%\">" + inv.getRef() + "</td>");
            html.append("<td width=\"30%\">" + inv.getSupplier().getName() + " (" + FormatHandler.getInstance().formatOutputVat(inv.getSupplier().getVatNumber()) + ")" + "</td>");
            html.append("<td width=\"8%\">€ " + inv.getSubtotal() + "</td>");
            html.append("<td width=\"5%\">" + inv.getVatRate() + " %</td>");
            html.append("<td width=\"8%\">€ " + inv.getVAT() + "</td>");
            html.append("<td width=\"8%\">€ " + inv.getTotal() + "</td>");
            html.append("</tr>");

        }
        html.append("</tbody></table>");
        return html.toString();
    }
}