package UI;

import Globals.FileManager;
import Globals.MapUtils;
import Globals.Utils;
import HalfPlaneIntersectionMethod.Site;
import HalfPlaneIntersectionMethod.VoronoiHalfPlaneIntersectionUI;
import HalfPlaneIntersectionMethod.Point;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

/**
 * User interface class that extends JDialog class. Made using intellij IDEA graphical designer, can be further changed
 * to the primitive or other enhanced tools.
 */
public class UserInterface extends JDialog {
    private JPanel contentPane;
    private JPanel siteControlPanel;
    private JPanel mapControlPanel;
    private JPanel mapPanel;
    private JPanel controlPanel;
    private JTextField xField;
    private JSlider redSlider;
    private JSlider greenSlider;
    private JSlider blueSlider;
    private JTextField nameCreateField;
    private JButton createEditSiteButton;
    private JButton createEditGeoSiteButton;
    private JTextField mapCenterLatitudeField;
    private JTextField mapCenterLongitudeField;
    private JButton changeMapCenterCoordsButton;
    private JLabel xLabel;
    private JLabel yLabel;
    private JTextField yField;
    private JLabel redLabel;
    private JLabel greenLabel;
    private JLabel blueLabel;
    private JLabel nameLabel;
    private JLabel mapCenterLatitudeLabel;
    private JLabel mapCenterLongitudeLabel;
    private JTextArea outputArea;
    private JButton mapSchemaButton;
    private JButton saveSitesToFileButton;
    private JLabel filePathLabel;
    private JTextField filePathField;
    private JTextField mapSchemaStatusField;
    private JButton readFileButton;
    private JButton editSiteButton;
    private JTextField editSiteNameField;
    private JButton removeSiteButton;
    private JTextField removeSiteNameField;
    private JLabel latitudeLabel;
    private JLabel longitudeLabel;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private JButton showMapCenterButton;
    private JButton showSiteButton;
    private JTextField showSiteField;
    private JLabel mapLabel;
    private JButton FindClosestPointButton;
    private JButton FindClosestPointGeoButton;

    //  flag for switching between map and diagram modes
    private boolean isMapRequired = true;

    //  flag of editing site and index of site for editing
    private boolean isEditModeEnabled = false;
    private int currentSiteEditIndex;

    //  voronoi diagram entity
    private VoronoiHalfPlaneIntersectionUI voronoiDiagram;

    public UserInterface() {
        //  initialize UI with all elements
        $$$setupUI$$$();

        //  set map center coordinates and radius of reviewable area
        MapUtils.setMapHandlerParameters(47.024512, 28.832157, 0.1);

        //  set main content pane where to show content
        setContentPane(contentPane);
        setModal(true);

        //  listener that checks if user has clicked on one of the presented sites on the map
        contentPane.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                for (Site site : Utils.sitesStorage) {
                    if (site.isClicked(x, y)) {
                        outputArea.setText("Was chosen site '" + site.getName() + "', info is shown in menu");

                        //  set all site parameters to be present on the edit menu from the right
                        xField.setText(String.valueOf(site.getX()));
                        yField.setText(String.valueOf(site.getY()));
                        redSlider.setValue((int) ((double) site.getColor().getRed() * ((double) 100 / 255)));
                        greenSlider.setValue((int) ((double) site.getColor().getGreen() * ((double) 100 / 255)));
                        blueSlider.setValue((int) ((double) site.getColor().getBlue() * ((double) 100 / 255)));
                        nameCreateField.setText(site.getName());
                        longitudeField.setText(String.valueOf(site.longitude));
                        latitudeField.setText(String.valueOf(site.latitude));
                        return;
                    }
                }

                //  if no site chosen, then show where user clicked and coordinates in both systems
                Point clickPoint = new Point(x, y);
                clickPoint.toGeographical();
                outputArea.setText("User clicked (" + x + ", " + y + ") point => geo(" + clickPoint.longitude + ", " +
                        clickPoint.latitude + ").");
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        //  at launch is chosen map representation mode
        mapSchemaStatusField.setText("Current mode: map");

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        //  create voronoi diagram entity and connect it with panel for showing it
        try {
            voronoiDiagram = new VoronoiHalfPlaneIntersectionUI(isMapRequired);
            mapPanel.add(voronoiDiagram);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  button to create/edit site using fields for cartesian point estimation
        createEditSiteButton.addActionListener(e -> {
            try {
                //  get coordinates, color and name from respective fields
                double xCoordinate = Double.parseDouble(xField.getText());
                double yCoordinate = Double.parseDouble(yField.getText());
                int redValue = (redSlider.getValue() * 255 / 100);
                int greenValue = (greenSlider.getValue() * 255 / 100);
                int blueValue = (blueSlider.getValue() * 255 / 100);
                String name = nameCreateField.getText();

                //  check if point is in the given sector
                if (xCoordinate < 0 || xCoordinate > Utils.xLimit) {
                    outputArea.setText("Point is out of X bounds");
                    return;
                } else if (yCoordinate < 0 || yCoordinate > Utils.yLimit) {
                    outputArea.setText("Point is out of Y bounds");
                    return;
                }

                //  if there is no name, then inform user about that
                if (name == null || name.isEmpty()) {
                    outputArea.setText("There can't be site without name");
                    return;
                }

                //  clear fields from input, considering that all data was successfully taken
                xField.setText("");
                yField.setText("");
                nameCreateField.setText("");
                latitudeField.setText("");
                longitudeField.setText("");

                //  set sliders values as default
                redSlider.setValue(50);
                greenSlider.setValue(50);
                blueSlider.setValue(50);

                //  create new site using given data and calculate geographical coordinates for that site
                Site newSite = new Site(xCoordinate, yCoordinate, new Color(redValue, greenValue, blueValue), name);
                newSite.toGeographical();

                //  if there is enabled edit mode, then apply changes to previously chosen site
                if (isEditModeEnabled) {
                    Utils.sitesStorage.set(currentSiteEditIndex, newSite);
                    isEditModeEnabled = false;
                    outputArea.setText("Site was successfully edited");
                    for (Site site : Utils.sitesStorage) {
                        site.findLocus();
                    }
                    voronoiDiagram.repaint();
                    return;
                }

                //  if there is not chosen edit mode, then create new site using given data
                Utils.sitesStorage.add(newSite);

                //  show info about new site
                outputArea.setText(newSite.toString());
                voronoiDiagram.repaint();

                //  show if there is an error in getting coordinates
            } catch (NumberFormatException nfe) {
                outputArea.setText("there are invalid coordinates inputted");
            }
        });

        //  button with the same purpose as create/edit one, but this one takes geographical coordinates
        createEditGeoSiteButton.addActionListener(e -> {
            try {
                //  get coordinates, color and name
                double longCoordinate = Double.parseDouble(longitudeField.getText());
                double latCoordinate = Double.parseDouble(latitudeField.getText());
                int redValue = (redSlider.getValue() / 100) * 255;
                int greenValue = (greenSlider.getValue() / 100) * 255;
                int blueValue = (blueSlider.getValue() / 100) * 255;
                String name = nameCreateField.getText();

                //  if there is no name, then inform user about that
                if (name == null || name.isEmpty()) {
                    outputArea.setText("There can't be site without name");
                    return;
                }

                //  clear fields from input, considering that all data was successfully taken
                xField.setText("");
                yField.setText("");
                nameCreateField.setText("");
                longitudeField.setText("");
                latitudeField.setText("");

                //  set slider values by default
                redSlider.setValue(50);
                greenSlider.setValue(50);
                blueSlider.setValue(50);

                //  create new site using given data and check if point can be presented on the given sector
                Site newSite = new Site(
                        latCoordinate, longCoordinate, new Color(redValue, greenValue, blueValue), name, true
                );
                if (!newSite.toCartesian()) {
                    outputArea.setText("Point is out of given area bounds");
                    return;
                }

                //  perform edit of the previously chosen site if such mode is enabled
                if (isEditModeEnabled) {
                    Utils.sitesStorage.set(currentSiteEditIndex, newSite);
                    Utils.isModified = true;
                    isEditModeEnabled = false;
                    outputArea.setText("Site was successfully edited");
                    for (Site site : Utils.sitesStorage) {
                        site.findLocus();
                    }
                    voronoiDiagram.repaint();
                    return;
                }

                Utils.sitesStorage.add(newSite);
                outputArea.setText(newSite.toString());
                voronoiDiagram.repaint();

            } catch (NumberFormatException nfe) {
                outputArea.setText("There are invalid coordinates inputted");
            }
        });

        //  button for changing map center coordinates
        changeMapCenterCoordsButton.addActionListener(e -> {
            try {
                //  get map center coordinates
                double latCoordinate = Double.parseDouble(mapCenterLatitudeField.getText());
                double longCoordinate = Double.parseDouble(mapCenterLongitudeField.getText());
                MapUtils.setCenterCoordinates(latCoordinate, longCoordinate);

                //  drop site edit mode if enabled
                if (isEditModeEnabled) {
                    isEditModeEnabled = false;
                }

                voronoiDiagram.repaint();
                mapCenterLatitudeField.setText("");
                mapCenterLongitudeField.setText("");

            } catch (NumberFormatException nfe) {
                outputArea.setText("there are invalid coordinates inputted");
            }
        });

        //  switch between map and schema representation mode
        mapSchemaButton.addActionListener(e -> {
            isMapRequired = !isMapRequired;
            if (isMapRequired) {
                mapSchemaStatusField.setText("Current mode: map");
            } else {
                mapSchemaStatusField.setText("Current mode: schema");
            }
            voronoiDiagram.isMapRequired = isMapRequired;
            voronoiDiagram.repaint();
        });

        //  read sites from given path
        readFileButton.addActionListener(e -> {
            String path = filePathField.getText();
            if (path == null || path.isEmpty()) {
                outputArea.setText("No path specified, type path to file");
                return;
            } else if (!path.contains("json") && !path.contains("JSON")) {
                outputArea.setText("There is no JSON file specified where to save data\nType JSON file");
                return;
            } else if (path.contains(" ")) {
                outputArea.setText("There can't be space in file path, check your input");
                return;
            }

            //  read sites from the given path and if something goes wrong function will give 'false'
            if (!FileManager.readSitesFromFile(path)) {
                outputArea.setText("Could not read file from given path, check your input");
            }

            if (isEditModeEnabled) {
                isEditModeEnabled = false;
            }
            voronoiDiagram.repaint();

            outputArea.setText("Got all sites from file with path: " + path);
            filePathField.setText("");
        });

        //  write sites to file with given path
        saveSitesToFileButton.addActionListener(e -> {
            String path = filePathField.getText();
            if (path == null || path.isEmpty()) {
                outputArea.setText("No path specified, type path to file");
                return;
            } else if (!path.contains("json") && !path.contains("JSON")) {
                outputArea.setText("There is no JSON file specified where to save data\nType JSON file");
                return;
            } else if (path.contains(" ")) {
                outputArea.setText("There can't be space in file path, check your input");
                return;
            }

            //  write sites to the file, function returns 'false' if something goes wrong
            if (!FileManager.writeSitesToFile(path)) {
                outputArea.setText("Could not write to given path, check your path input");
            }

            if (isEditModeEnabled) {
                isEditModeEnabled = false;
            }

            outputArea.setText("Sites were written to the file with path: " + path);
            filePathField.setText("");
        });

        //  removes site from storage
        removeSiteButton.addActionListener(e -> {
            //  get name of site to remove
            String siteNameToRemove = removeSiteNameField.getText();
            if (siteNameToRemove == null || siteNameToRemove.isEmpty()) {
                outputArea.setText("There is no site name specified");
                return;
            }

            //  search for requested site
            Site siteToRemove = null;
            for (Site site : Utils.sitesStorage) {
                if (site.getName().equals(siteNameToRemove)) {
                    siteToRemove = site;
                }
            }

            //  if site was not found then inform user, otherwise try removing
            if (siteToRemove == null) {
                outputArea.setText("There is no site with such name, check typed name");
            } else {
                Utils.sitesStorage.remove(siteToRemove);
                outputArea.setText("Site with name '" + siteNameToRemove + "' was removed");
                if (isEditModeEnabled) {
                    isEditModeEnabled = false;
                }
            }

            removeSiteNameField.setText("");
            voronoiDiagram.repaint();
        });

        //  request edit of the existing site
        editSiteButton.addActionListener(e -> {
            String siteNameToEdit = editSiteNameField.getText();
            if (siteNameToEdit == null || siteNameToEdit.isEmpty()) {
                outputArea.setText("There is no site name specified");
                return;
            }

            Site siteToEdit = null;
            for (int i = 0; i < Utils.sitesStorage.size(); i++) {
                if (Utils.sitesStorage.get(i).getName().equals(siteNameToEdit)) {
                    siteToEdit = Utils.sitesStorage.get(i);
                    currentSiteEditIndex = i;
                }
            }

            if (siteToEdit == null) {
                outputArea.setText("There is no site with such name, check typed name");

                //  if site was found then fill all respective UI fields and set 'site edit' flag
            } else {
                outputArea.setText("Site with name '" + siteNameToEdit + "' is in the edit mode, change parameters");
                isEditModeEnabled = true;

                //  set x and y cartesian coordinates
                xField.setText(String.valueOf(siteToEdit.x));
                yField.setText(String.valueOf(siteToEdit.y));

                //  set color sliders
                redSlider.setValue((int) ((double) siteToEdit.getColor().getRed() * ((double) 100 / 255)));
                greenSlider.setValue((int) ((double) siteToEdit.getColor().getGreen() * ((double) 100 / 255)));
                blueSlider.setValue((int) ((double) siteToEdit.getColor().getBlue() * ((double) 100 / 255)));

                //  set name
                nameCreateField.setText(siteToEdit.getName());

                //  set latitude/longitude
                longitudeField.setText(String.valueOf(siteToEdit.longitude));
                latitudeField.setText(String.valueOf(siteToEdit.latitude));
            }

            editSiteNameField.setText("");
        });

        //  show site functionality
        showSiteButton.addActionListener(e -> {
            String siteNameToShow = showSiteField.getText();
            if (siteNameToShow == null || siteNameToShow.isEmpty()) {
                outputArea.setText("There is no site name specified");
                return;
            }

            showSiteField.setText("");

            Site siteToShow = null;
            for (int i = 0; i < Utils.sitesStorage.size(); i++) {
                if (Utils.sitesStorage.get(i).getName().equals(siteNameToShow)) {
                    siteToShow = Utils.sitesStorage.get(i);
                }
            }

            if (siteToShow == null) {
                outputArea.setText("There is no site with such name, check typed name");
            } else {
                outputArea.setText("You requested: " + siteToShow);
            }

            showSiteField.setText("");
        });

        showMapCenterButton.addActionListener(e -> {
            mapCenterLatitudeField.setText(String.valueOf(MapUtils.centerLatitude));
            mapCenterLongitudeField.setText(String.valueOf(MapUtils.centerLongitude));
        });

        FindClosestPointButton.addActionListener(e -> {
            double xCoordinate = Double.parseDouble(xField.getText());
            double yCoordinate = Double.parseDouble(yField.getText());

            xField.setText("");
            yField.setText("");

            Point currentClientPosition = new Point(xCoordinate, yCoordinate);
            for (Site site : Utils.sitesStorage) {
                if (site.getLocus().contains(currentClientPosition)) {
                    outputArea.setText("Closest site to given coordinates: " + site.getName());
                    return;
                }
            }

            outputArea.setText("There is no locus containing this point, check coordinates again");
        });
        FindClosestPointGeoButton.addActionListener(e -> {
            double longCoordinate = Double.parseDouble(longitudeField.getText());
            double latCoordinate = Double.parseDouble(latitudeField.getText());

            longitudeField.setText("");
            latitudeField.setText("");

            Point currentClientPosition = new Point(longCoordinate, latCoordinate, false);
            if (currentClientPosition.toCartesian()) {
                for (Site site : Utils.sitesStorage) {
                    if (site.getLocus().contains(currentClientPosition)) {
                        outputArea.setText("Closest site to given coordinates: " + site.getName());
                        return;
                    }
                }
            } else {
                outputArea.setText("point is out of area bounds: lat.=" + currentClientPosition.latitude +
                        "; long.=" + currentClientPosition.longitude);
                return;
            }

            outputArea.setText("There is no locus containing this point, check coordinates again");
        });
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        UserInterface dialog = new UserInterface();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), 0, 0));
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mapPanel = new JPanel();
        mapPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 0, 0));
        mapPanel.setBackground(new Color(-1));
        panel1.add(mapPanel, new GridConstraints(0, 0, 4, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(600, 450), new Dimension(600, 450), new Dimension(600, 450), 0, false));
        mapLabel = new JLabel();
        mapLabel.setText("");
        mapPanel.add(mapLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        controlPanel.setBackground(new Color(-2039584));
        panel1.add(controlPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(650, 425), new Dimension(650, 425), new Dimension(650, 425), 0, false));
        siteControlPanel = new JPanel();
        siteControlPanel.setLayout(new GridLayoutManager(10, 2, new Insets(5, 5, 5, 5), 0, 0));
        controlPanel.add(siteControlPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(325, 425), new Dimension(325, 425), new Dimension(325, 425), 0, false));
        xLabel = new JLabel();
        xLabel.setText("X:");
        siteControlPanel.add(xLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xField = new JTextField();
        xField.setToolTipText("site x cartesian coordinate");
        siteControlPanel.add(xField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        yLabel = new JLabel();
        yLabel.setText("Y:");
        siteControlPanel.add(yLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yField = new JTextField();
        yField.setToolTipText("site y cartesian coordinate");
        siteControlPanel.add(yField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        redLabel = new JLabel();
        redLabel.setText("Red:");
        siteControlPanel.add(redLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        redSlider = new JSlider();
        redSlider.setValue(50);
        siteControlPanel.add(redSlider, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        greenLabel = new JLabel();
        greenLabel.setText("Green:");
        siteControlPanel.add(greenLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        greenSlider = new JSlider();
        siteControlPanel.add(greenSlider, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        blueLabel = new JLabel();
        blueLabel.setText("Blue:");
        siteControlPanel.add(blueLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blueSlider = new JSlider();
        siteControlPanel.add(blueSlider, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        nameLabel = new JLabel();
        nameLabel.setText("Name:");
        siteControlPanel.add(nameLabel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameCreateField = new JTextField();
        nameCreateField.setToolTipText("site name");
        siteControlPanel.add(nameCreateField, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        createEditSiteButton = new JButton();
        createEditSiteButton.setText("Create/edit");
        siteControlPanel.add(createEditSiteButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createEditGeoSiteButton = new JButton();
        createEditGeoSiteButton.setText("Create/edit geo");
        siteControlPanel.add(createEditGeoSiteButton, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        latitudeLabel = new JLabel();
        latitudeLabel.setText("Lat.:");
        siteControlPanel.add(latitudeLabel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        longitudeLabel = new JLabel();
        longitudeLabel.setText("Long.:");
        siteControlPanel.add(longitudeLabel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        latitudeField = new JTextField();
        latitudeField.setToolTipText("latitude site coordinate");
        siteControlPanel.add(latitudeField, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        longitudeField = new JTextField();
        longitudeField.setToolTipText("longitude site coordinate");
        siteControlPanel.add(longitudeField, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        FindClosestPointButton = new JButton();
        FindClosestPointButton.setText("Find site");
        siteControlPanel.add(FindClosestPointButton, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        FindClosestPointGeoButton = new JButton();
        FindClosestPointGeoButton.setText("Find site geo");
        siteControlPanel.add(FindClosestPointGeoButton, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapControlPanel = new JPanel();
        mapControlPanel.setLayout(new GridLayoutManager(9, 2, new Insets(5, 5, 5, 5), 0, 0));
        controlPanel.add(mapControlPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(325, 425), new Dimension(325, 425), new Dimension(325, 425), 0, false));
        mapCenterLatitudeLabel = new JLabel();
        mapCenterLatitudeLabel.setText("Map lat.:");
        mapControlPanel.add(mapCenterLatitudeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapCenterLatitudeField = new JTextField();
        mapCenterLatitudeField.setToolTipText("map center latitude coordinate");
        mapControlPanel.add(mapCenterLatitudeField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapCenterLongitudeLabel = new JLabel();
        mapCenterLongitudeLabel.setText("Map long.:");
        mapControlPanel.add(mapCenterLongitudeLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapCenterLongitudeField = new JTextField();
        mapCenterLongitudeField.setToolTipText("map center longitude coordinate");
        mapControlPanel.add(mapCenterLongitudeField, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        changeMapCenterCoordsButton = new JButton();
        changeMapCenterCoordsButton.setText("Change map center coords");
        mapControlPanel.add(changeMapCenterCoordsButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editSiteButton = new JButton();
        editSiteButton.setText("Edit site:");
        mapControlPanel.add(editSiteButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editSiteNameField = new JTextField();
        editSiteNameField.setToolTipText("name of site to edit");
        mapControlPanel.add(editSiteNameField, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        removeSiteButton = new JButton();
        removeSiteButton.setText("Remove site:");
        mapControlPanel.add(removeSiteButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeSiteNameField = new JTextField();
        removeSiteNameField.setToolTipText("name of site to remove");
        mapControlPanel.add(removeSiteNameField, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        showMapCenterButton = new JButton();
        showMapCenterButton.setText("Show map center");
        mapControlPanel.add(showMapCenterButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showSiteButton = new JButton();
        showSiteButton.setText("Show site:");
        mapControlPanel.add(showSiteButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showSiteField = new JTextField();
        showSiteField.setToolTipText("name of site to show info");
        mapControlPanel.add(showSiteField, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapSchemaButton = new JButton();
        mapSchemaButton.setText("Map/Schema:");
        mapControlPanel.add(mapSchemaButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapSchemaStatusField = new JTextField();
        mapSchemaStatusField.setEditable(false);
        mapControlPanel.add(mapSchemaStatusField, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        filePathLabel = new JLabel();
        filePathLabel.setText("File path:");
        mapControlPanel.add(filePathLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filePathField = new JTextField();
        filePathField.setText("");
        filePathField.setToolTipText("path to JSON file with sites");
        mapControlPanel.add(filePathField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        readFileButton = new JButton();
        readFileButton.setText("Read file");
        mapControlPanel.add(readFileButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveSitesToFileButton = new JButton();
        saveSitesToFileButton.setText("Save sites to file");
        mapControlPanel.add(saveSitesToFileButton, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        Font outputAreaFont = this.$$$getFont$$$("Consolas", -1, 12, outputArea.getFont());
        if (outputAreaFont != null) outputArea.setFont(outputAreaFont);
        outputArea.setRows(5);
        outputArea.setWrapStyleWord(true);
        panel1.add(outputArea, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(650, 25), new Dimension(650, 25), new Dimension(650, 25), 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
