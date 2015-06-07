package biz.a7software.slimmyinvoice.helper;

import biz.a7software.slimmyinvoice.data.Zone;

/**
 * The Area class represents a rectangle area in absolute coordinates.
 */
public class Area {

    private static final String REGEX_COORDINATE = "^(\\d+,){3}\\d+(:(\\w|[;/\\-\\.])*)?$";

    private int x, y, w, h;

    private Area(String value) {
        String[] values = value.split(":");
        String[] point = values[0].split(",");
        x = Math.max(0, Integer.parseInt(point[0]));
        y = Math.max(0, Integer.parseInt(point[1]));
        w = Math.max(1, Integer.parseInt(point[2]));
        h = Math.max(1, Integer.parseInt(point[3]));
    }

    public Area(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }


    public static Area create(String value) {
        if (!isRegular(value)) {
            return null;
        }
        return new Area(value);
    }

    private static boolean isRegular(String value) {
        return value != null && value.matches(REGEX_COORDINATE);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }


    // Converts an Area to a Zone.
    public Zone getZone(ImageHandler image) {
        int height = image.getHeight();
        int width = image.getWidth();
        return new Zone(x, y, w, h, height, width);
    }

    // For debugging purposes.
    public void print() {
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        System.out.println("h = " + h);
        System.out.println("w = " + w);

    }
}