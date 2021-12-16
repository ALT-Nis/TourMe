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
            letters = new Node[27];
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
            if(c == ' ')
                position = 26;
            if(letters[position] == null)
                letters[position] = new Node();

            letters[position].addToList(s);
            letters[position].Insert(s, index + 1);
        }

        public List<String> Search(String inputText, int index){
//            Log.e("neki text", inputText + " " + String.valueOf(index));
            if(index == inputText.length())
                return cities;
            char c = inputText.charAt(index);
            int position = (int)(c - 'a');
            if(c == ' ')
                position = 26;
            if(letters[position] == null)
                return cities;
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

    List<String> allCities = Arrays.asList("Nis", "Beograd", "Novi Sad", "Subotica", "Bela Palanka");
    Trie trie;

    public Gradovi(){
        trie = new Trie();
        trie.Insert(allCities);
    }

    public List<String> Search(String inputText){
        return trie.Search(inputText.toLowerCase());
    }


}
