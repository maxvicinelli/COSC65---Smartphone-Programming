package edu.dartmouth.cs.myruns1.utils;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;


public class ActivityDetectionDictionary extends Dictionary<String, Integer> {

    private ArrayList<String> keys;
    private ArrayList<Integer> values;


    public ActivityDetectionDictionary() {
        values = addValues();
        keys = addKeys();
    }

    private ArrayList<Integer> addValues() {
        ArrayList<Integer> vals = new ArrayList<>();
        for (int i=0; i<8; i++) {
            vals.add(0);
        }
        return vals;
    }

    private ArrayList<String> addKeys() {
        ArrayList<String> keys = new ArrayList<>();
        keys.add("In Vehicle");
        keys.add("On Bicycle");
        keys.add("On Foot");
        keys.add("Running");
        keys.add("Still");
        keys.add("Tilting");
        keys.add("Walking");
        keys.add("Unknown");
        return keys;
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public Enumeration<String> keys() {
        return null;
    }

    @Override
    public Enumeration<Integer> elements() {
        return null;
    }

    @Override
    public Integer get(Object key) {
        for (int i=0; i<keys.size(); i++) {
            if (keys.get(i).equals(key)){
                return values.get(i);
            }
        }
        return null;
    }

    public void incrementVal(String key) {
        for (int i=0; i<keys.size(); i++) {
            if (keys.get(i).equals(key)) {
                int curr = values.get(i);
                values.set(i, curr+1);
            }
        }
    }

    public String getMax(){
        int largest = 0;
        int index = 0;
        for (int i=0; i<values.size(); i++) {
            if (values.get(i) > largest) {
                largest = values.get(i);
                index = i;
            }
        }
        return keys.get(index);
    }

    @Override
    public Integer put(String key, Integer value) {
        for (int i=0; i<keys.size(); i++) {
            if (keys.get(i).equals(key)) {
                values.set(i, value);
            }
        }
        return null;
    }

    @Override
    public Integer remove(Object key) {
        return null;
    }
}
