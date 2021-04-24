package HalfPlaneIntersectionMethod;

import Globals.FileManager;
import Globals.MapUtils;
import Globals.Utils;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

public class CommandLineControl {
    private static boolean isExitCalled = false;

    /**
     * decompose received string command into tokens (words and numbers)
     * @param command command that must tokenized
     * @return array of tokens
     */
    public String[] commandToTokens(String command) {
        //  make all letters lowercase and remove repeating dots
        command = command.toLowerCase().replaceAll("\\.+", ".");

        //  split string by whitespaces (repeatable and non-repeatable)
        String[] words = command.split("\\s+");

        //  for all possible words and numbers
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("-p")) {
                break;
            }

            //  if this is a number, then remove repeating dots and remove commas
            if (words[i].matches(".*\\d.*")) {
                words[i] = words[i].replaceAll(",+", "");
                continue;
            }

            //  remove all characters from word that are not word-type ones
            words[i] = words[i].replaceAll("[^\\w]", "");
        }

        return words;
    }

    /**
     * check if all tokens of the command are valid
     * @param commandTokens array of tokens of the command
     * @return true if all tokens are valid, false if not
     */
    public boolean isCommandValid(String[] commandTokens) {
        //  array must have all valid String objects and they must not be empty
        for (String commandToken : commandTokens) {
            if (commandToken == null || commandToken.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * interprets command by tokens (IMPORTANT: tokens array must be checked to be valid)
     * @param commandTokens command tokens
     */
    public void interpretCommand(String[] commandTokens){
        switch (commandTokens[0]) {

            //  if user wants to add new site
            case "add":
                switch (commandTokens[1]) {

                    //  if user wants to add cartesian site
                    case "cartesian":
                        //  function takes arguments as: x, y, redIntensity, greenIntensity, blueIntensity, name
                        double x = Double.parseDouble(commandTokens[2]);
                        double y = Double.parseDouble(commandTokens[3]);
                        int red = Integer.parseInt(commandTokens[4]);
                        int green = Integer.parseInt(commandTokens[5]);
                        int blue = Integer.parseInt(commandTokens[6]);

                        Site newCartesianSite = new Site(x, y, new Color(red, green, blue), commandTokens[7]);

                        //  find geographical location of point basing on its cartesian coordinates on given map sector
                        newCartesianSite.toGeographical();
                        Utils.sitesStorage.add(newCartesianSite);
                        System.out.println("added cartesian point '" + newCartesianSite.getName() + "' to storage");
                        break;

                    //  if user wants to add geographical site
                    case "geographical":

                        //  function takes arguments as: latitude, longitude, redIntensity, greenIntensity, blueIntensity, name
                        Site newGeographicalSite = new Site(
                                Double.parseDouble(commandTokens[2]), Double.parseDouble(commandTokens[3]),
                                new Color(
                                        Integer.parseInt(commandTokens[4]), Integer.parseInt(commandTokens[5]), Integer.parseInt(commandTokens[6])
                                ),
                                commandTokens[7], true
                        );

                        //  register new point and try to find cartesian coordinates for point on given map sector
                        Utils.sitesStorage.add(newGeographicalSite);
                        System.out.println("added geographical point '" + newGeographicalSite.getName() +"' to storage");
                        if (newGeographicalSite.toCartesian()) {
                            System.out.println("successful point transformation to cartesian one on given map sector");
                        } else {
                            System.out.println("failed point transformation to cartesian, it is out of map sector");
                        }
                        break;
                }
                break;

            //  if user wants to change coordinates of existing site
            case "change":

                //  search for site with such a name as second command token
                Site siteToChange = null;
                for (Site site : Utils.sitesStorage) {
                    if (site.getName().equals(commandTokens[1])) {
                        siteToChange = site;
                        break;
                    }
                }

                //  if site with such name was found
                if (siteToChange != null) {
                    switch (commandTokens[2]) {

                        //  if user wants to change coordinates
                        case "coordinates":
                            switch (commandTokens[3]) {

                                //  if user wants to change cartesian coordinates
                                case "cartesian":

                                    //  take new coordinates as: x, y
                                    double oldCoordX = siteToChange.x;
                                    double oldCoordY = siteToChange.y;
                                    siteToChange.x = Double.parseDouble(commandTokens[4]);
                                    siteToChange.y = Double.parseDouble(commandTokens[5]);

                                    System.out.println("changed cartesian coordinates from x=" + oldCoordX + ", y=" + oldCoordY +
                                            " to x=" + siteToChange.x + ", y=" + siteToChange.y);

                                    siteToChange.toGeographical();
                                    break;

                                //  if user wants to change geographical coordinates
                                case "geographical":

                                    //  take new coordinates as: latitude, longitude
                                    double oldLon = siteToChange.longitude;
                                    double oldLat = siteToChange.latitude;
                                    siteToChange.latitude = Double.parseDouble(commandTokens[4]);
                                    siteToChange.longitude = Double.parseDouble(commandTokens[5]);

                                    System.out.println("changed geographical coordinates from long.=" + oldLon + ", lat.=" + oldLat +
                                            " to long.=" + siteToChange.x + ", lat.=" + siteToChange.y);

                                    //  check if point can be transformed to cartesian one on given map sector
                                    if (siteToChange.toCartesian()) {
                                        System.out.println("successful point transformation to cartesian one on given map sector");
                                    } else {
                                        System.out.println("failed point transformation to cartesian, it is out of map sector");
                                    }
                                    break;
                            }
                            break;

                        //  if user wants to change name of site
                        case "name":
                            String oldName = siteToChange.getName();
                            siteToChange.setName(commandTokens[4]);

                            System.out.println("changed name from '" + oldName + "' to '" + siteToChange.getName() + "'");
                            break;

                        //  if user wants to change color of site
                        case "color":

                            //  take new color as: redIntensity, greenIntensity, blueIntensity
                            Color oldColor = siteToChange.getColor();
                            siteToChange.setColor(
                                    new Color(Integer.parseInt(commandTokens[4]), Integer.parseInt(commandTokens[5]), Integer.parseInt(commandTokens[6]))
                            );
                            System.out.println("changed color from " + oldColor + " to " + siteToChange.getColor());
                            break;
                    }
                }
                break;

            case "show":
                if (commandTokens[1].equals("all")) {
                    for (Site site : Utils.sitesStorage) {
                        System.out.println(site);
                    }
                    break;
                }

                Site siteToShow = null;
                for (Site site : Utils.sitesStorage) {
                    if (site.getName().equals(commandTokens[1])) {
                        siteToShow = site;
                        break;
                    }
                }
                System.out.println("you requested - " + siteToShow);
                break;

            //  if user wants to remove site from the storage
            case "remove":
                Site siteToDelete = null;
                for (Site site : Utils.sitesStorage) {
                    if (site.getName().equals(commandTokens[1])) {
                        siteToDelete = site;
                        break;
                    }
                }
                Utils.sitesStorage.remove(siteToDelete);
                System.out.println("removed '" + commandTokens[1] + "' site from storage");
                break;

            //  if user requests changes in map
            case "map":

                //  take changes as: latitude, longitude, latitudeRadius, longitudeRadius
                double oldCenterLat = MapUtils.centerLatitude;
                double oldCenterLon = MapUtils.centerLongitude;
                double oldLatRad = MapUtils.latitudeRadius;
                double oldLonRad = MapUtils.longitudeRadius;

                MapUtils.setMapHandlerParameters(
                        Double.parseDouble(commandTokens[1]), Double.parseDouble(commandTokens[2]),
                        Double.parseDouble(commandTokens[3]), Double.parseDouble(commandTokens[4])
                );

                System.out.println("changed map center from lat.=" + oldCenterLat + ", long.=" + oldCenterLon + " to" +
                        " lat.=" + MapUtils.centerLatitude + ", long.=" + MapUtils.centerLongitude);
                System.out.println("radius changed from " + oldLatRad + " to " + MapUtils.latitudeRadius);

                //  remap all points to their cartesian representation basing on their geographical coordinates
                for (Site site : Utils.sitesStorage) {
                    site.toCartesian();
                }
                break;

            //  if user wants to draw voronoi diagram
            case "draw":
                VoronoiHalfPlaneIntersection voronoiDiagram;
                try {
                    voronoiDiagram = new VoronoiHalfPlaneIntersection();
                    voronoiDiagram.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            //  if user wants to read/write file storage
            case "file":
                switch (commandTokens[1]) {
                    //  if user wants to write to the file storage
                    case "export":
                        //  takes string after '-p' flag as path to file
                        if (FileManager.writeSitesToFile(commandTokens[3])) {
                            System.out.println("successfully written sites to the storage file");
                        } else {
                            System.out.println("error occurred during saving process, check error info");
                        }
                        break;

                    //  if user wants to read from file storage
                    case "import":
                        //  takes string after '-p' flag as path to file
                        if (FileManager.readSitesFromFile(commandTokens[3])) {
                            System.out.println("successfully imported all sites from the storage file");
                        } else {
                            System.out.println("error occurred during import process, check error info");
                        }
                        break;
                }
                break;

            //  if user wants to exit from the program
            case "exit":
                isExitCalled = true;
                break;
            default:
                System.out.println("there is no such command, try again");
        }
    }

    public static void main(String[] args) {
        CommandLineControl cmd = new CommandLineControl();

        //  coordinates of the Chisinau are considered as default values
        MapUtils.setMapHandlerParameters(47.024512, 28.832157, 0.1, 0.1);

        //  read input from user
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type commands. List of available commands at the moment:");

        System.out.println("\t1.  'add cartesian ?x ?y ?redValue ?greenValue ?blueValue ?name'");
        System.out.println("\t     add new site using cartesian coordinates and automatically finds geographical");
        System.out.println("\t     coordinates considering provided map area;");

        System.out.println("\t2.  'add geographical ?latitude ?longitude ?redValue ?greenValue ?blueValue ?name'");
        System.out.println("\t     add new site using geographical coordinates;");

        System.out.println("\t3.  'change ?name coordinates cartesian ?x ?y'");
        System.out.println("\t     change cartesian coordinates of the site and automatically find geographical ones ");
        System.out.println("\t     considering provided map area;");

        System.out.println("\t4.  'change ?name coordinates geographical ?latitude ?longitude'");
        System.out.println("\t     change geographical coordinates of the site;");

        System.out.println("\t5.  'change ?name name ?newName'");
        System.out.println("\t     change name of the site to the new one;");

        System.out.println("\t6.  'change ?name color ?redValue ?greenValue ?blueValue'");
        System.out.println("\t     change color of the site;");

        System.out.println("\t7.  'remove ?name'");
        System.out.println("\t     remove site from the storage;");

        System.out.println("\t8.  'map ?latitude ?longitude ?latitudeRadius ?longitudeRadius'");
        System.out.println("\t     change coordinates of the center of the map and set new radius of area to be ");
        System.out.println("\t     reviewed;");

        System.out.println("\t9.  'draw'");
        System.out.println("\t     show voronoi diagram");

        System.out.println("\t10. 'exit'");
        System.out.println("\t     exit from the program");

        System.out.println("\t11. 'help'");
        System.out.println("\t     show again all commands and their arguments");

        System.out.println();


        while(!isExitCalled) {
            System.out.print(">>>\t");
            String data = scanner.nextLine();
            String[] tokens = cmd.commandToTokens(data);
            if (cmd.isCommandValid(tokens)) {
                System.out.println(Arrays.toString(tokens));
                cmd.interpretCommand(tokens);
            }
            System.out.println();
        }
    }
}
