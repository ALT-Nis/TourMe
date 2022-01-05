package com.example.tourme.Model;

import java.util.ArrayList;
import java.util.List;

public class StaticVars {
    public static String lastSearch = "";

    public static List<Integer> listOfFragments = new ArrayList<>();

    public static String getLastSearch() { return lastSearch; }

    public static void setLastSearch(String lastSearch) { StaticVars.lastSearch = lastSearch; }

    public static List<Integer> getListOfFragments() { return listOfFragments; }

    public static void setListOfFragments(List<Integer> listOfFragments) { StaticVars.listOfFragments = listOfFragments; }
}
