package biz.a7software.slimmyinvoice.helper;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * The AreaList class manages a list of Area objects.
 */
public class AreaList implements Iterable<Area> {

    private final List<Area> list;

    public AreaList(String areas) {
        list = new ArrayList<Area>();
        init(areas);
    }

    // Builds an Area list from formatted string.
    private void init(String areas) {
        if (areas == null || areas.isEmpty()) {
            return;
        }
        StringReader sr = new StringReader(areas);
        Properties ocrAreas = new Properties();
        try {
            ocrAreas.load(sr);
            for (Object key : ocrAreas.keySet()) {
                String name = key.toString();
                Area area = Area.create(name, ocrAreas.getProperty(name));
                if (area != null) {
                    list.add(area);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Should really never happen
        }
    }

    // Adds an Area to the list.
    public void add(Area area) {
        if (area != null) {
            list.add(area);
        }
    }

    // Gets last area of the list.
    public Area getLast() {
        if (list.size() >= 1) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Iterator<Area> iterator() {
        return list.iterator();
    }

    public int size() {
        return list.size();
    }
}