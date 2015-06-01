package biz.a7software.slimmyinvoice.helper;


import biz.a7software.slimmyinvoice.data.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The DbHandler class manages suppliers and invoices tables.
 */
public class DbHandler {

    private static final String databaseBaseUrl = "jdbc:sqlite:" + Properties.databasePath + "database";
    private static volatile DbHandler instance = null;
    private ConnectionSource connectionSource = null;
    private String databaseUrl;
    private String user = "username";
    private String password = "password";

    private DbHandler() {
    }

    public final static DbHandler getInstance() {
        if (DbHandler.instance == null) {
            synchronized (DbHandler.class) {
                if (DbHandler.instance == null) {
                    DbHandler.instance = new DbHandler();
                    Logger.getLogger("com.j256.ormlite.dao.DaoManager").setLevel(Level.OFF);
                    Logger.getRootLogger().setLevel(Level.OFF);
                }
            }
        }
        return DbHandler.instance;
    }

    // Configures the database file for user
    public void configureForUser(User user) {
        databaseUrl = databaseBaseUrl + "_user" + user.getVatNumber() + ".db";
        this.user = user.getVatNumber();
        password = user.getPassword();
    }

    public String getUser() {
        return user;
    }

    // Resets the database
    public void refreshDatabase() throws SQLException {
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        createSuppliersTableIfNotExists();
        TableUtils.dropTable(connectionSource, Supplier.class, false);
        TableUtils.dropTable(connectionSource, Fingerprint.class, false);
        TableUtils.dropTable(connectionSource, Zone.class, false);
        TableUtils.dropTable(connectionSource, Template.class, false);
        createInvoiceTableIfNotExists();
        TableUtils.dropTable(connectionSource, Invoice.class, false);
    }

    /**
     * SUPPLIERS SECTION
     */
    // Creates the suppliers table if not exists.
    public void createSuppliersTableIfNotExists() throws SQLException {
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        TableUtils.createTableIfNotExists(connectionSource, Supplier.class);
        TableUtils.createTableIfNotExists(connectionSource, Address.class);
        TableUtils.createTableIfNotExists(connectionSource, Fingerprint.class);
        TableUtils.createTableIfNotExists(connectionSource, Zone.class);
        TableUtils.createTableIfNotExists(connectionSource, Template.class);
        connectionSource.close();
    }

    // Adds a supplier in the database.
    public void addSupplier(Supplier supplier) throws SQLException {
        createSuppliersTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        Dao<Supplier, String> accountDao = DaoManager.createDao(connectionSource, Supplier.class);
        accountDao.createIfNotExists(supplier);
        connectionSource.close();
    }

    // Updates a supplier in the database.
    public void updateSupplier(Supplier supplier) throws SQLException {
        createSuppliersTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        Dao<Template, Integer> templateDao = DaoManager.createDao(connectionSource, Template.class);
        templateDao.create(supplier.getTemplate());
        Dao<Supplier, String> accountDao = DaoManager.createDao(connectionSource, Supplier.class);
        accountDao.update(supplier);
        connectionSource.close();
    }

    // Searches for a supplier in the database based on his VAT number (=id).
    public Supplier retrieveSupplierFromVat(String vat) throws SQLException {
        createSuppliersTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        Dao<Supplier, String> accountDao = DaoManager.createDao(connectionSource, Supplier.class);
        Supplier supplier = accountDao.queryForId(vat);
        connectionSource.close();
        return supplier;
    }

    // Retrieves all suppliers from the database and returns them as a list.
    public List<Supplier> retrieveAllSuppliersList() throws SQLException {
        createSuppliersTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        Dao<Supplier, String> accountDao = DaoManager.createDao(connectionSource, Supplier.class);
        List<Supplier> supplierList = accountDao.queryForAll();
        connectionSource.close();
        return supplierList;
    }

    // Retrieves all suppliers from the database and returns them as a sorted array.
    public Supplier[] retrieveAllSuppliersSortedArray() throws SQLException {
        List<Supplier> suppliers = retrieveAllSuppliersList();
        Collections.sort(suppliers, new SuppliersComparator());
        return suppliers.toArray(new Supplier[suppliers.size()]);
    }

    // Checks whether or not a supplier is in the database.
    public boolean isSupplierInDB(Supplier supplier) throws SQLException {
        createSuppliersTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        Dao<Supplier, String> accountDao = DaoManager.createDao(connectionSource, Supplier.class);
        Supplier sup = accountDao.queryForId(supplier.getVatNumber());
        connectionSource.close();
        return sup != null;
    }

    /**
     * INVOICES SECTION
     */
    // Creates the invoices table if not exists.
    public void createInvoiceTableIfNotExists() throws SQLException {
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        TableUtils.createTableIfNotExists(connectionSource, Invoice.class);
        connectionSource.close();
    }

    // Adds an invoice in the database.
    public void addInvoice(Invoice invoice) throws SQLException {
        createInvoiceTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        Dao<Invoice, String> accountDao = DaoManager.createDao(connectionSource, Invoice.class);
        accountDao.createIfNotExists(invoice);
        connectionSource.close();
    }

    // Searches an invoice from the supplier with a certain id.
    public boolean findInvoice(Supplier supplier, String id) throws SQLException {
        createInvoiceTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        Dao<Invoice, String> accountDao = DaoManager.createDao(connectionSource, Invoice.class);
        List<Invoice> invoiceList = accountDao.queryBuilder().where()
                .eq(Invoice.REF_FIELD_NAME, id)
                .query();
        connectionSource.close();
        for (int i = 0; i < invoiceList.size(); i++) {
            if (invoiceList.get(i).getSupplier().getVatNumber().equals(supplier.getVatNumber())) {
                return true;
            }
        }
        return false;
    }

    // Retrieves all invoices from the database and returns them as a list.
    public List<Invoice> retrieveAllInvoicesList() throws SQLException {
        createInvoiceTableIfNotExists();
        connectionSource = new JdbcConnectionSource(databaseUrl, user, password) {
        };
        Dao<Invoice, String> accountDao = DaoManager.createDao(connectionSource, Invoice.class);
        List<Invoice> invoicesList = accountDao.queryForAll();
        connectionSource.close();
        return invoicesList;
    }

    // Retrieves all invoices from the database and returns them as a sorted array.
    public Invoice[] retrieveAllInvoicesSortedArray() throws SQLException {
        List<Invoice> invoices = retrieveAllInvoicesList();
        Collections.sort(invoices, new InvoicesComparator());
        return invoices.toArray(new Invoice[invoices.size()]);
    }
}

/**
 * The SuppliersComparator class compares Supplier objects according to alphabetical order on their names.
 */
class SuppliersComparator implements Comparator<Supplier> {
    @Override
    public int compare(Supplier supplier, Supplier comp) {
        String supplierName = supplier.getName();
        String compName = comp.getName();
        return supplierName.compareToIgnoreCase(compName);
    }
}

/**
 * The InvoicesComparator class compares Invoice objects according to numerical order on their id.
 */
class InvoicesComparator implements Comparator<Invoice> {
    @Override
    public int compare(Invoice invoice, Invoice comp) {
        Integer invoiceId = invoice.getId();
        Integer compId = comp.getId();
        return invoiceId.compareTo(compId);
    }
}