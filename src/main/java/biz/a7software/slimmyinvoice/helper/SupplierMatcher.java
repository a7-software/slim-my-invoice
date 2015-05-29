package biz.a7software.slimmyinvoice.helper;

import biz.a7software.slimmyinvoice.data.Fingerprint;
import biz.a7software.slimmyinvoice.data.Supplier;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The SupplierMatcher class is responsible for finding a supplier from a list of suppliers that best matches
 * a given fingerprint according to a calculated score.
 */
public class SupplierMatcher {

    private static final double LINE_SIMILARITY_THRESHOLD = 0.5;
    private static final double BEST_SCORE_THRESHOLD = 0.3;

    private static final double LINE_NUMBER_WEIGHT = 0.10;
    private static final double SUPPLIER_NAME_PRESENT_WEIGHT = 0.30;
    private static final double IDENTICAL_LINES_WEIGHT = 0.60;

    private static volatile SupplierMatcher instance = null;

    private SupplierMatcher() {
    }

    public final static SupplierMatcher getInstance() {
        if (SupplierMatcher.instance == null) {
            synchronized (SupplierMatcher.class) {
                if (SupplierMatcher.instance == null) {
                    SupplierMatcher.instance = new SupplierMatcher();
                }
            }
        }
        return SupplierMatcher.instance;
    }

    // Finds a supplier from a list of suppliers that best matches a given fingerprint according to a calculated score.
    public Supplier findSupplier(List<Supplier> supplierList, Fingerprint fingerprint) {

        String vat = fingerprint.getVat();

        if (vat != null) {
            try {
                Supplier db = DbHandler.getInstance().retrieveSupplierFromVat(vat);
                if (db != null) {
                    return db;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        double score;
        double bestScore = 0.0;
        int supplierIndex = -1;
        Supplier currentSupplier;
        Fingerprint dbFingerprint;

        for (int i = 0; i < supplierList.size(); i++) {
            score = 0.0;
            currentSupplier = supplierList.get(i);
            dbFingerprint = currentSupplier.getFingerprint();

            /**
             * Criterion 1: Number of lines.
             */
            if (dbFingerprint.getNumberOfLines() == fingerprint.getNumberOfLines()) {
                score += LINE_NUMBER_WEIGHT;
            } else {
                score += LINE_NUMBER_WEIGHT * (1.0 / (double) (Math.abs(dbFingerprint.getNumberOfLines() - fingerprint.getNumberOfLines())));
            }

            /**
             * Criterion 2: Similar lines.
             */
            double nbSimilarLines = 0;
            double nbLines = fingerprint.getNumberOfLines();
            for (int j = 0; j < dbFingerprint.getNumberOfLines(); j++) {
                String dbLine = dbFingerprint.getLines()[j];
                for (int k = 0; k < nbLines; k++) {
                    String line = fingerprint.getLines()[k];
                    if (((double) LevenshteinDistance.computeLevenshteinDistance(line, dbLine) / (double) Math.max(line.length(), dbLine.length())) < LINE_SIMILARITY_THRESHOLD) {
                        nbSimilarLines++;
                    }
                }
            }
            if (nbSimilarLines >= nbLines) {
                score += IDENTICAL_LINES_WEIGHT;
            } else {
                score += IDENTICAL_LINES_WEIGHT * (nbSimilarLines / nbLines);
            }

            /**
             * Criterion 3: Presence of the supplier's name.
             */
            if (currentSupplier.getName() != null) {
                List<String> keywords = new ArrayList<String>();
                String lcResult = fingerprint.getBrutOcr().toLowerCase();
                String[] names = currentSupplier.getName().toLowerCase().split("\\s+");
                for (int j = 0; j < names.length; j++) {
                    keywords.add(names[j]);
                }
                int matches = 0;
                for (int k = 0; k < keywords.size(); k++) {
                    if (lcResult.contains(keywords.get(k))) {
                        matches++;
                    }
                }
                if (keywords.size() > 0) {
                    score += SUPPLIER_NAME_PRESENT_WEIGHT * (matches / (double) keywords.size());
                }
            }

            if (score > bestScore) {
                bestScore = score;
                supplierIndex = i;
            }
        }

        if (supplierIndex != -1 && bestScore > BEST_SCORE_THRESHOLD) {
            return supplierList.get(supplierIndex);
        } else {
            return null;
        }
    }
}