package biz.a7software.slimmyinvoice.data;

import biz.a7software.slimmyinvoice.helper.Area;
import biz.a7software.slimmyinvoice.helper.FormatHandler;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Zone class represents a rectangle area in relative coordinates.
 */
@DatabaseTable(tableName = "zone")
public class Zone {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private Double xProp;
    @DatabaseField
    private Double yProp;
    @DatabaseField
    private Double wProp;
    @DatabaseField
    private Double hProp;

    public Zone() {
        // required for ORMLite
    }

    public Zone(int x, int y, int w, int h, int height, int width) {
        xProp = new Double((double) x / (double) width);
        yProp = new Double((double) y / (double) height);
        wProp = new Double((double) w / (double) width);
        hProp = new Double((double) h / (double) height);
    }

    // For debugging purposes
    public void print() {
        System.out.println("x between " + new Double(FormatHandler.round(xProp, 2)).toString()
                + " and " + FormatHandler.round(xProp + wProp, 2));
        System.out.println("y between " + FormatHandler.round(yProp, 2)
                + " and " + FormatHandler.round(yProp + hProp, 2));
    }

    // Converts an Zone to an Area.
    public Area createArea(int height, int width) {
        return new Area((int) Math.round(xProp * width), (int) Math.round(yProp * height),
                (int) Math.round(wProp * width), (int) Math.round(hProp * height));
    }
}