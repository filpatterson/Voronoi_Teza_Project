package Globals;

import HalfPlaneIntersectionMethod.Site;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;

/**
 * Class for writing/reading JSON file
 */
public class FileManager {
    /**
     * read array list of sites from JSON formatted file
     * @return array list of sites
     */
    public static boolean readSitesFromFile(String filePath) {
        //  try reading file content
        Reader reader = null;
        try {
            reader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            System.err.println("Error opening file for reading");
            e.printStackTrace();
            return false;
        }

        //  take data from json as list of sites using Gson
        Utils.sitesStorage = new Gson().fromJson(reader, new TypeToken<ArrayList<Site>>() {}.getType());

        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing file");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * write sites to JSON formatted file
     */
    public static boolean writeSitesToFile(String filePath) {
        //  try opening file
        FileWriter file;
        try {
            file = new FileWriter(filePath);
        } catch (IOException e) {
            System.err.println("Error in creating/reading file");
            e.printStackTrace();
            return false;
        }

        //  create Gson object with "pretty printing" and write arrayList into file
        new GsonBuilder().setPrettyPrinting().create().toJson(Utils.sitesStorage, file);

        //  close stream of work with file
        try {
            file.close();
        } catch (IOException e) {
            System.err.println("Error while closing file stream");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
