package com.example.tourme.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Gradovi {
    public class Node{
        Node[] letters;
        List<String> cities;

        public Node(){
            letters = new Node[32];
            cities = new ArrayList<>();
        }

        public void addToList(String city){
            cities.add(city);
        }

        public void Insert(String s, int index){
            if(index == s.length())
                return;
            char c = s.charAt(index);

            int position = (int)(c - 'a');
            switch(c){
                case ' ': position = 26; break;
                case 'š': position = 27; break;
                case 'đ': position = 28; break;
                case 'č': position = 29; break;
                case 'ć': position = 30; break;
                case 'ž': position = 31; break;
            }

            if(letters[position] == null)
                letters[position] = new Node();

            letters[position].addToList(s);
            letters[position].Insert(s, index + 1);
        }

        public List<String> Search(String inputText, int index){
            if(index == inputText.length())
                return cities;
            char c = inputText.charAt(index);
            int position = (int)(c - 'a');
            switch(c){
                case ' ': position = 26; break;
                case 'š': position = 27; break;
                case 'đ': position = 28; break;
                case 'č': position = 29; break;
                case 'ć': position = 30; break;
                case 'ž': position = 31; break;
            }
            if(letters[position] == null)
                letters[position] = new Node();
            return letters[position].Search(inputText, index + 1);

        }


    }

    public class Trie{
        public Node node;
        public Trie(){
            node = new Node();
        }

        public void Insert(List<String> cities){
            for(String f : cities){
                node.Insert(f.toLowerCase(), 0);
            }
        }

        public List<String> Search(String inputText){
            return node.Search(inputText, 0);
        }
    }

    List<String> allCities = Arrays.asList("niš", "beograd", "novi sad", "subotica", "bela palanka", "leskovac");
    List<String> allCitiesC = Arrays.asList("Niš", "Beograd", "Novi Sad", "Subotica", "Bela Palanka", "Leskovac");

    Trie trie;

    public static String lastSearch = "";

    public List<String> getAllCities() { return allCities; }

    public List<String> getAllCitiesC() { return allCitiesC; }

    public static String getLastSearch() { return lastSearch; }

    public static void setLastSearch(String lastSearch) { Gradovi.lastSearch = lastSearch; }

    public Gradovi(){
        trie = new Trie();
        trie.Insert(allCities);
    }

    public List<String> Search(String inputText){
        return trie.Search(inputText.toLowerCase());
    }


}
