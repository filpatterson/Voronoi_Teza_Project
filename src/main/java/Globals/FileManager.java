package Globals;

import HalfPlaneIntersectionMethod.Site;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class FileManager {
    /**
     * read array list of sites from JSON formatted file
     * @return arraylist of sites
     */
    public static ArrayList<Site> readSitesFromFile(String filePath) {
        //  try reading file content
        Reader reader = null;
        try {
            reader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file for reading");
            e.printStackTrace();
        }

        //  take data from json as list of sites using Gson
        assert reader != null;
        ArrayList<Site> sites = new Gson().fromJson(reader, new TypeToken<ArrayList<Site>>() {}.getType());

        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing file");
            e.printStackTrace();
        }
        return sites;

    }

    /**
     * write sites to JSON formatted file
     * @param sites
     */
    public static void writeSitesToFile(ArrayList<Site> sites, String filePath) {
        //  try opening file
        FileWriter file;
        try {
            file = new FileWriter(filePath);
        } catch (IOException e) {
            System.err.println("Error in creating/reading file");
            e.printStackTrace();
            return;
        }

        //  create Gson object with "pretty pringing" and write arrayList into file
        new GsonBuilder().setPrettyPrinting().create().toJson(sites, file);

        //  close stream of work with file
        try {
            file.close();
        } catch (IOException e) {
            System.err.println("Error while closing file stream");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //  store all sites in ArrayList
        ArrayList<Site> sites = new ArrayList<>();

        //  set random generator and generate 100 random points for constructing Voronoi diagram
        Random rand = new Random();
        for (int i = 0; i < 50; i++)
            sites.add(new Site(rand.nextDouble() * Parameters.xLimit, rand.nextDouble() * Parameters.yLimit, Color.getColor("s" ,rand.nextInt(16777215))));

        writeSitesToFile(sites, "E:/output.json");
        sites.clear();
        sites = readSitesFromFile("E:/output.json");
        System.out.println(sites);
    }
}