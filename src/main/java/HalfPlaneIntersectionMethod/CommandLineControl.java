package HalfPlaneIntersectionMethod;

import Globals.ConsoleColors;
import Globals.FileManager;
import Globals.MapUtils;
import Globals.Utils;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

public class CommandLineControl {
    //  flag for 'while' loop, true value of which will close program entirely
    private static boolean isExitCalled = false;

    private VoronoiHalfPlaneIntersection voronoi = null;

    //  string with all available commands
    private static final String commandsList = "\n\t1.  'add cartesian ?x ?y ?redValue ?greenValue ?blueValue ?name'" +
            "\n\t     add new site using cartesian coordinates and automatically finds geographical" +
            "\n\t     coordinates considering provided map area;" +

            "\n\t2.  'add geographical ?latitude ?longitude ?redValue ?greenValue ?blueValue ?name'" +
            "\n\t     add new site using geographical coordinates;" +

            "\n\t3.  'change ?name coordinates cartesian ?x ?y'" +
            "\n\t     change cartesian coordinates of the site and automatically find geographical ones " +
            "\n\t     considering provided map area;" +

            "\n\t4.  'change ?name coordinates geographical ?latitude ?longitude'" +
            "\n\t     change geographical coordinates of the site;" +

            "\n\t5.  'change ?name name ?newName'" +
            "\n\t     change name of the site to the new one;" +

            "\n\t6.  'change ?name color ?redValue ?greenValue ?blueValue'" +
            "\n\t     change color of the site;" +

            "\n\t7.  'change map center ?centerLatitude ?centerLongitude'" +
            "\n\t     change map center coordinates;" +

            "\n\t8.  'remove ?name'" +
            "\n\t     remove site from the storage;" +

            "\n\t9.  'calculate ?name'" +
            "\n\t     find site locus with given name " +

            "\n\t10. 'transform ?name to cartesian" +
            "\n\t     transform given point from geographical coordinates to cartesian" +

            "\n\t11. 'transform all to cartesian" +
            "\n\t     transform all points from geographical coordinates to cartesian" +

            "\n\t12. 'calculate all" +
            "\n\t     find loci for all sites covered by given map area" +

            "\n\t13. 'draw map'" +
            "\n\t     show voronoi diagram with map representation" +

            "\n\t14. 'draw schema'" +
            "\n\t     show voronoi diagram without map" +

            "\n\t14. 'file export -p ?pathToFile'" +
            "\n\t     write all sites data into file with given path " +

            "\n\t15. 'file import -p ?pathToFile'" +
            "\n\t     get sites info from file with given path " +

            "\n\t16. 'exit'" +
            "\n\t     exit from the program" +

            "\n\t17. 'help'" +
            "\n\t     show again all commands and their arguments";

    /**
     * decompose received string command into tokens (words and numbers)
     * @param command command that must tokenized
     * @return array of tokens
     */
    public String[] commandToTokens(String command) {
        if (command == null || command.isEmpty()) {
            System.out.println(ConsoleColors.RED +
                    "\tno command received, do not try to break the program :)" +
                    ConsoleColors.RESET);
            return null;
        }

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
        if (commandTokens == null || commandTokens.length == 0) {
            System.out.println(ConsoleColors.RED +
                    "\tcommand has no tokens, cancel" +
                    ConsoleColors.RESET);
            return false;
        }

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
                //  if there are too small amount of arguments or too big -> cancel operation
                if (commandTokens.length < 8) {
                    System.out.println(ConsoleColors.RED +
                            "\tthere are not enough arguments; type 'help' if you want more info" +
                            ConsoleColors.RESET);
                    return;
                } else if (commandTokens.length > 8) {
                    System.out.println(ConsoleColors.RED +
                            "\tthere are too many arguments; type 'help' if you want more info" +
                            ConsoleColors.RESET);
                    return;
                }

                switch (commandTokens[1]) {

                    //  if user wants to add cartesian site
                    case "cartesian":
                        //  function takes arguments as: x, y, redIntensity, greenIntensity, blueIntensity, name
                        double x = Double.parseDouble(commandTokens[2]);
                        double y = Double.parseDouble(commandTokens[3]);
                        int red = Integer.parseInt(commandTokens[4]);
                        int green = Integer.parseInt(commandTokens[5]);
                        int blue = Integer.parseInt(commandTokens[6]);

                        if (red < 0 || green < 0 || blue < 0) {
                            System.out.println(ConsoleColors.RED +
                                    "\tone of RGB values is less than zero, operation cancel" +
                                    ConsoleColors.RESET);
                            return;
                        } else if (red > 255 || green > 255 || blue > 255) {
                            System.out.println(ConsoleColors.RED +
                                    "\tone of RGB values is more than max color value, operation cancel" +
                                    ConsoleColors.RESET);
                            return;
                        }
                        String name = commandTokens[7];

                        Site newCartesianSite = new Site(x, y, new Color(red, green, blue), name);

                        //  find geographical location of point basing on its cartesian coordinates on given map sector
                        newCartesianSite.toGeographical();
                        Utils.sitesStorage.add(newCartesianSite);
                        System.out.println("added cartesian point '" + newCartesianSite.getName() + "' to storage");
                        return;

                    //  if user wants to add geographical site
                    case "geographical":
                        double latitude = Double.parseDouble(commandTokens[2]);
                        double longitude = Double.parseDouble(commandTokens[3]);
                        red = Integer.parseInt(commandTokens[4]);
                        green = Integer.parseInt(commandTokens[5]);
                        blue = Integer.parseInt(commandTokens[6]);
                        name = commandTokens[7];

                        Site newGeographicalSite = new Site(latitude, longitude, new Color(red, green, blue), name, true);

                        //  register new point and try to find cartesian coordinates for point on given map sector
                        Utils.sitesStorage.add(newGeographicalSite);
                        System.out.println("added geographical point '" + newGeographicalSite.getName() +"' to storage");
                        if (newGeographicalSite.toCartesian()) {
                            System.out.println("successful point transformation to cartesian one on given map sector");
                        } else {
                            System.out.println(ConsoleColors.YELLOW +
                                    "failed point transformation to cartesian, it is out of map sector" +
                                    ConsoleColors.RESET);
                        }
                        return;
                    default:
                        System.out.println(ConsoleColors.RED +
                                "\tneither cartesian nor geographical point chosen, operation cancelled" +
                                ConsoleColors.RESET);
                        return;
                }

            //  if user wants to change coordinates of existing site
            case "change":
                //  there can't be less than 4 arguments for 'change' command
                if (commandTokens.length < 4) {
                    System.out.println(ConsoleColors.RED +
                            "\tnot enough arguments even to define command, operation cancel" +
                            ConsoleColors.RESET);
                    return;
                }

                //  request for changing center coordinates of the map
                if (commandTokens[1].equals("map")) {
                    if (commandTokens[2].equals("center")) {
                        double centerLatitude = Double.parseDouble(commandTokens[3]);
                        double centerLongitude = Double.parseDouble(commandTokens[4]);
                        MapUtils.setCenterCoordinates(centerLatitude, centerLongitude);
                        System.out.println("\tchanged center of map");
                    } else {
                        System.out.println(ConsoleColors.RED +
                                "\tincorrect change type was requested" +
                                ConsoleColors.RESET);
                    }
                    return;
                }

                //  search for site with such a name as second command token
                Site siteToChange = null;
                for (Site site : Utils.sitesStorage) {
                    if (site.getName().equals(commandTokens[1])) {
                        siteToChange = site;
                        break;
                    }
                }

                if (siteToChange == null) {
                    System.out.println(ConsoleColors.YELLOW +
                            "\tthere is no site with such name, check name or request all sites to find name" +
                            ConsoleColors.RESET);
                    return;
                }

                //  if site with such name was found
                switch (commandTokens[2]) {

                    //  if user wants to change coordinates
                    case "coordinates":
                        if (commandTokens.length < 6) {
                            System.out.println(ConsoleColors.RED +
                                    "\tthere are not enough arguments, operation cancelled" +
                                    ConsoleColors.RESET);
                            return;
                        } else if (commandTokens.length > 6) {
                            System.out.println(ConsoleColors.RED +
                                    "\ttoo big amount of arguments, operation cancelled" +
                                    ConsoleColors.RESET);
                            return;
                        }

                        switch (commandTokens[3]) {
                            //  if user wants to change cartesian coordinates
                            case "cartesian":
                                //  take new coordinates as: x, y
                                double oldCoordX = siteToChange.x;
                                double oldCoordY = siteToChange.y;
                                siteToChange.x = Double.parseDouble(commandTokens[4]);
                                siteToChange.y = Double.parseDouble(commandTokens[5]);

                                System.out.println("changed cartesian coordinates from " +
                                        "x=" + oldCoordX + ", y=" + oldCoordY +
                                        " to x=" + siteToChange.x + ", y=" + siteToChange.y);

                                siteToChange.toGeographical();
                                return;

                            //  if user wants to change geographical coordinates
                            case "geographical":
                                //  take new coordinates as: latitude, longitude
                                double oldLon = siteToChange.longitude;
                                double oldLat = siteToChange.latitude;
                                siteToChange.latitude = Double.parseDouble(commandTokens[4]);
                                siteToChange.longitude = Double.parseDouble(commandTokens[5]);

                                System.out.println("changed geographical coordinates from " +
                                        "long.=" + oldLon + ", lat.=" + oldLat +
                                        " to long.=" + siteToChange.x + ", lat.=" + siteToChange.y);

                                //  check if point can be transformed to cartesian one on given map sector
                                if (siteToChange.toCartesian()) {
                                    System.out.println("\tsuccessful point transformation to cartesian one on given map sector");
                                } else {
                                    System.out.println(ConsoleColors.YELLOW +
                                            "\tfailed point transformation to cartesian, it is out of map sector" +
                                            ConsoleColors.RESET);
                                }
                                return;

                            default:
                                System.out.println(ConsoleColors.RED +
                                        "\tneither cartesian nor geographical coordinates where chosen, operation cancelled" +
                                        ConsoleColors.RESET);
                                return;
                        }

                    //  if user wants to change name of site
                    case "name":
                        if (commandTokens.length > 4) {
                            System.out.println(ConsoleColors.RED +
                                    "\tthere are too many arguments, operation cancelled" +
                                    ConsoleColors.RESET);
                            return;
                        }
                        String oldName = siteToChange.getName();
                        siteToChange.setName(commandTokens[3]);

                        System.out.println("changed name from '" + oldName + "' to '" + siteToChange.getName() + "'");
                        return;

                    //  if user wants to change color of site
                    case "color":
                        if (commandTokens.length < 7) {
                            System.out.println(ConsoleColors.RED +
                                    "\tthere are not enough arguments, operation cancelled" +
                                    ConsoleColors.RESET);
                            return;
                        } else if (commandTokens.length > 7) {
                            System.out.println(ConsoleColors.RED +
                                    "\tthere are too many arguments, operation cancelled" +
                                    ConsoleColors.RESET);
                            return;
                        }

                        //  take new color as: redIntensity, greenIntensity, blueIntensity
                        Color oldColor = siteToChange.getColor();
                        int red = Integer.parseInt(commandTokens[4]);
                        int green = Integer.parseInt(commandTokens[5]);
                        int blue = Integer.parseInt(commandTokens[6]);
                        siteToChange.setColor(new Color(red, green, blue));

                        System.out.println("changed color from " + oldColor + " to " + siteToChange.getColor());
                        return;
                }
                return;

            case "show":
                if (commandTokens.length < 2) {
                    System.out.println(ConsoleColors.RED +
                            "\tnot enough arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                } else if (commandTokens.length > 2) {
                    System.out.println(ConsoleColors.RED +
                            "\ttoo many arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                }

                if (commandTokens[1].equals("all")) {
                    for (Site site : Utils.sitesStorage) {
                        System.out.println(site);
                    }
                    return;
                }

                Site siteToShow = null;
                for (Site site : Utils.sitesStorage) {
                    if (site.getName().equals(commandTokens[1])) {
                        siteToShow = site;
                        break;
                    }
                }

                if (siteToShow == null) {
                    System.out.println(ConsoleColors.YELLOW +
                            "\tno site with such name was found" +
                            ConsoleColors.RESET);
                    return;
                }

                System.out.println("you requested - " + siteToShow);
                return;

            //  if user wants to remove site from the storage
            case "remove":
                if (commandTokens.length < 2) {
                    System.out.println(ConsoleColors.RED +
                            "\tnot enough arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                } else if (commandTokens.length > 2) {
                    System.out.println(ConsoleColors.RED +
                            "\ttoo many arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                }

                Site siteToDelete = null;
                for (Site site : Utils.sitesStorage) {
                    if (site.getName().equals(commandTokens[1])) {
                        siteToDelete = site;
                        break;
                    }
                }

                if (siteToDelete == null) {
                    System.out.println(ConsoleColors.YELLOW +
                            "\tno site with such name, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                }

                Utils.sitesStorage.remove(siteToDelete);
                System.out.println("removed '" + commandTokens[1] + "' site from storage");
                return;

            case "calculate":
                if (commandTokens.length < 2) {
                    System.out.println(ConsoleColors.RED +
                            "\tnot enough arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                } else if (commandTokens[1].equals("all")) {
                    for (Site site : Utils.sitesStorage) {
                        site.findLocus();
                    }
                    System.out.println("\tloci for all sites was found");
                    return;
                } else {
                    Site siteToCalculate = null;
                    for (Site site : Utils.sitesStorage) {
                        if (site.getName().equals(commandTokens[1])) {
                            siteToCalculate = site;
                            break;
                        }
                    }

                    if (siteToCalculate != null) {
                        siteToCalculate.findLocus();
                        System.out.println("\tlocus for '" + siteToCalculate.getName() + "' was found");
                    } else {
                        System.out.println(ConsoleColors.RED +
                                "\tnot enough arguments, operation cancelled" +
                                ConsoleColors.RESET);
                    }
                    return;
                }

            //  if user wants to draw voronoi diagram
            case "draw":
                if (commandTokens.length < 2) {
                    System.out.println(ConsoleColors.RED +
                            "\tnot enough arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                } else if (commandTokens.length > 2) {
                    System.out.println(ConsoleColors.RED +
                            "\ttoo many arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                }

                if (Utils.sitesStorage.size() == 0) {
                    System.out.println(ConsoleColors.RED +
                            "\tthere are no sites to draw map for, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                }

                try {
                    if (voronoi == null) {
                        if (commandTokens[1].equals("map")) {
                            voronoi = new VoronoiHalfPlaneIntersection(true);
                            voronoi.setVisible(true);
                        } else if (commandTokens[1].equals("schema")) {
                            voronoi = new VoronoiHalfPlaneIntersection(false);
                            voronoi.setVisible(true);
                        } else {
                            System.out.println(ConsoleColors.RED +
                                    "\tnot chosen draw type (map or schema), operation cancelled" +
                                    ConsoleColors.RESET);
                            return;
                        }
                    } else {
                        if (commandTokens[1].equals("map")) {
                            if (!voronoi.isMapRequired) {
                                voronoi.isMapRequired = true;
                            }
                            voronoi.repaint();
                        } else if (commandTokens[1].equals("schema")) {
                            if (voronoi.isMapRequired) {
                                voronoi.isMapRequired = false;
                            }
                            voronoi.repaint();
                        } else {
                            System.out.println(ConsoleColors.RED +
                                    "\tnot chosen draw type (map or schema), operation cancelled" +
                                    ConsoleColors.RESET);
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;

            //  if user wants to read/write file storage
            case "file":
                if (commandTokens.length < 4) {
                    System.out.println(ConsoleColors.RED +
                            "\tnot enough arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                } else if (commandTokens.length > 4) {
                    System.out.println(ConsoleColors.RED +
                            "\ttoo many arguments, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                } else if (!commandTokens[2].equals("-p")) {
                    System.out.println(ConsoleColors.RED +
                            "\tno path flag detected, operation cancelled" +
                            ConsoleColors.RESET);
                    return;
                }

                switch (commandTokens[1]) {
                    //  if user wants to write to the file storage
                    case "export":
                        //  takes string after '-p' flag as path to file
                        if (FileManager.writeSitesToFile(commandTokens[3])) {
                            System.out.println("successfully written sites to the storage file");
                        } else {
                            System.out.println(ConsoleColors.RED +
                                    "error occurred during saving process, check error info" +
                                    ConsoleColors.RESET);
                        }
                        return;

                    //  if user wants to read from file storage
                    case "import":
                        //  takes string after '-p' flag as path to file
                        if (FileManager.readSitesFromFile(commandTokens[3])) {
                            System.out.println("successfully imported all sites from the storage file");
                        } else {
                            System.out.println(ConsoleColors.RED +
                                    "error occurred during import process, check error info" +
                                    ConsoleColors.RESET);
                        }
                        return;
                }
                return;

            //  if user wants to exit from the program
            case "exit":
                isExitCalled = true;
                return;

            //  show all commands available
            case "help":
                System.out.println("\tavailable commands at the moment:");
                System.out.println(commandsList);
                return;

            //  get cartesian coordinates of site or all sites from geographical coordinates
            case "transform":
                if(commandTokens[1].equals("all")) {
                    for (Site site : Utils.sitesStorage) {
                        site.toCartesian();
                    }
                    System.out.println("\tsuccessfully transformed geographical coordinates to cartesian for all sites");
                } else {
                    Site siteToTransform = null;
                    for (Site site : Utils.sitesStorage) {
                        if (site.getName().equals(commandTokens[1])) {
                            siteToTransform = site;
                            break;
                        }
                    }

                    if (siteToTransform == null) {
                        System.out.println(ConsoleColors.RED +
                                "no site with such name was found" +
                                ConsoleColors.RESET);
                    } else {
                        siteToTransform.toCartesian();
                        System.out.println("\tsuccessfully transformed geographical coordinates to cartesian");
                    }
                }
                return;

            default:
                System.out.println(ConsoleColors.RED +
                        "there is no such command, try again" +
                        ConsoleColors.RESET);
        }
    }

    public static void main(String[] args) {
        //  initialize command line control
        CommandLineControl cmd = new CommandLineControl();

        //  coordinates of the Chisinau are considered as default values
        MapUtils.setMapHandlerParameters(47.024512, 28.832157, 0.1);

        //  read input from user
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type commands. List of available commands at the moment:");
        System.out.println(commandsList);

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
