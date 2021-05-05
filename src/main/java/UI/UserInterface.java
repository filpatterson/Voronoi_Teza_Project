package UI;

import Globals.FileManager;
import Globals.MapUtils;
import Globals.Utils;
import HalfPlaneIntersectionMethod.Site;
import HalfPlaneIntersectionMethod.VoronoiHalfPlaneIntersectionUI;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

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
    private boolean isMapRequired = true;
    private boolean isEditModeEnabled = false;
    private int currentSiteEditIndex;
    private VoronoiHalfPlaneIntersectionUI voronoiDiagram;

    public UserInterface() {
        //  coordinates of the Chisinau are considered as default values
        $$$setupUI$$$();
        MapUtils.setMapHandlerParameters(47.024512, 28.832157, 0.1);

        setContentPane(contentPane);
        setModal(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        mapSchemaStatusField.setText("Current mode: map");

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        try {
            voronoiDiagram = new VoronoiHalfPlaneIntersectionUI(isMapRequired);
            mapPanel.add(voronoiDiagram);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  button to create/edit site using fields for cartesian point estimation
        createEditSiteButton.addActionListener(e -> {
            try {
                //  get coordinates, color and name
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

                //  create new site using given data
                Site newSite = new Site(xCoordinate, yCoordinate, new Color(redValue, greenValue, blueValue), name);
                newSite.toGeographical();
                if (isEditModeEnabled) {
                    Utils.sitesStorage.set(currentSiteEditIndex, newSite);
                    isEditModeEnabled = false;
                    outputArea.setText("Site was successfully edited");
                    voronoiDiagram.repaint();
                    return;
                }

                Utils.sitesStorage.add(newSite);

                //  show info about new site
                outputArea.setText(newSite.toString());
                voronoiDiagram.repaint();
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

                //  create new site using given data
                Site newSite = new Site(
                        latCoordinate, longCoordinate, new Color(redValue, greenValue, blueValue), name, true
                );

                if (!newSite.toCartesian()) {
                    outputArea.setText("Point is out of given area bounds");
                    return;
                }

                if (isEditModeEnabled) {
                    Utils.sitesStorage.set(currentSiteEditIndex, newSite);
                    Utils.isModified = true;
                    isEditModeEnabled = false;
                    outputArea.setText("Site was successfully edited");
                    voronoiDiagram.repaint();
                    return;
                }

                Utils.sitesStorage.add(newSite);

                //  show info about new site
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

                if (isEditModeEnabled) {
                    isEditModeEnabled = false;
                }

                voronoiDiagram.repaint();
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

            if (!FileManager.readSitesFromFile(path)) {
                outputArea.setText("Could not read file from given path, check your input");
            }

            if (isEditModeEnabled) {
                isEditModeEnabled = false;
            }
            voronoiDiagram.repaint();

            outputArea.setText("Got all sites from file with path: " + path);
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

            if (!FileManager.writeSitesToFile(path)) {
                outputArea.setText("Could not write to given path, check your path input");
            }

            if (isEditModeEnabled) {
                isEditModeEnabled = false;
            }

            outputArea.setText("Sites were written to the file with path: " + path);
        });

        //  removes site from storage
        removeSiteButton.addActionListener(e -> {
            String siteNameToRemove = removeSiteNameField.getText();
            if (siteNameToRemove == null || siteNameToRemove.isEmpty()) {
                outputArea.setText("There is no site name specified");
                return;
            }

            Site siteToRemove = null;
            for (Site site : Utils.sitesStorage) {
                if (site.getName().equals(siteNameToRemove)) {
                    siteToRemove = site;
                }
            }

            if (siteToRemove == null) {
                outputArea.setText("There is no site with such name, check typed name");
            } else {
                Utils.sitesStorage.remove(siteToRemove);
                outputArea.setText("Site with name '" + siteNameToRemove + "' was removed");
                if (isEditModeEnabled) {
                    isEditModeEnabled = false;
                }
            }

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
            } else {
                outputArea.setText("Site with name '" + siteNameToEdit + "' is in the edit mode, change parameters");
                isEditModeEnabled = true;

                //  set x and y cartesian coordinates
                xField.setText(String.valueOf(siteToEdit.x));
                yField.setText(String.valueOf(siteToEdit.y));

                //  set color sliders
                redSlider.setValue(siteToEdit.getColor().getRed());
                greenSlider.setValue(siteToEdit.getColor().getGreen());
                blueSlider.setValue(siteToEdit.getColor().getBlue());

                //  set name
                nameCreateField.setText(siteToEdit.getName());

                //  set latitude/longitude
                longitudeField.setText(String.valueOf(siteToEdit.longitude));
                latitudeField.setText(String.valueOf(siteToEdit.latitude));
            }
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
                outputArea.setText(siteToShow.toString());
            }
        });

        showMapCenterButton.addActionListener(e -> {
            mapCenterLatitudeField.setText(String.valueOf(MapUtils.centerLatitude));
            mapCenterLongitudeField.setText(String.valueOf(MapUtils.centerLongitude));
        });
    }

    private void onOK() {
        // add your code here
        dispose();
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
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), 0, 0));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mapPanel = new JPanel();
        mapPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 0, 0));
        mapPanel.setBackground(new Color(-1));
        panel1.add(mapPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 4, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(600, 450), new Dimension(600, 450), new Dimension(600, 450), 0, false));
        mapLabel = new JLabel();
        mapLabel.setText("");
        mapPanel.add(mapLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        controlPanel = new JPanel();
        controlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        controlPanel.setBackground(new Color(-2039584));
        panel1.add(controlPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(650, 425), new Dimension(650, 425), new Dimension(650, 425), 0, false));
        siteControlPanel = new JPanel();
        siteControlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(9, 2, new Insets(5, 5, 5, 5), 0, 0));
        controlPanel.add(siteControlPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(325, 425), new Dimension(325, 425), new Dimension(325, 425), 0, false));
        xLabel = new JLabel();
        xLabel.setText("X:");
        siteControlPanel.add(xLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xField = new JTextField();
        xField.setToolTipText("site x cartesian coordinate");
        siteControlPanel.add(xField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        yLabel = new JLabel();
        yLabel.setText("Y:");
        siteControlPanel.add(yLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yField = new JTextField();
        yField.setToolTipText("site y cartesian coordinate");
        siteControlPanel.add(yField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        redLabel = new JLabel();
        redLabel.setText("Red:");
        siteControlPanel.add(redLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        redSlider = new JSlider();
        redSlider.setValue(50);
        siteControlPanel.add(redSlider, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        greenLabel = new JLabel();
        greenLabel.setText("Green:");
        siteControlPanel.add(greenLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        greenSlider = new JSlider();
        siteControlPanel.add(greenSlider, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        blueLabel = new JLabel();
        blueLabel.setText("Blue:");
        siteControlPanel.add(blueLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blueSlider = new JSlider();
        siteControlPanel.add(blueSlider, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        nameLabel = new JLabel();
        nameLabel.setText("Name:");
        siteControlPanel.add(nameLabel, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameCreateField = new JTextField();
        nameCreateField.setToolTipText("site name");
        siteControlPanel.add(nameCreateField, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        createEditSiteButton = new JButton();
        createEditSiteButton.setText("Create/edit");
        siteControlPanel.add(createEditSiteButton, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createEditGeoSiteButton = new JButton();
        createEditGeoSiteButton.setText("Create/edit geo");
        siteControlPanel.add(createEditGeoSiteButton, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        latitudeLabel = new JLabel();
        latitudeLabel.setText("Lat.:");
        siteControlPanel.add(latitudeLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        longitudeLabel = new JLabel();
        longitudeLabel.setText("Long.:");
        siteControlPanel.add(longitudeLabel, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        latitudeField = new JTextField();
        latitudeField.setToolTipText("latitude site coordinate");
        siteControlPanel.add(latitudeField, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        longitudeField = new JTextField();
        longitudeField.setToolTipText("longitude site coordinate");
        siteControlPanel.add(longitudeField, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapControlPanel = new JPanel();
        mapControlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(9, 2, new Insets(5, 5, 5, 5), 0, 0));
        controlPanel.add(mapControlPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(325, 425), new Dimension(325, 425), new Dimension(325, 425), 0, false));
        mapCenterLatitudeLabel = new JLabel();
        mapCenterLatitudeLabel.setText("Map lat.:");
        mapControlPanel.add(mapCenterLatitudeLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapCenterLatitudeField = new JTextField();
        mapCenterLatitudeField.setToolTipText("map center latitude coordinate");
        mapControlPanel.add(mapCenterLatitudeField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapCenterLongitudeLabel = new JLabel();
        mapCenterLongitudeLabel.setText("Map long.:");
        mapControlPanel.add(mapCenterLongitudeLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapCenterLongitudeField = new JTextField();
        mapCenterLongitudeField.setToolTipText("map center longitude coordinate");
        mapControlPanel.add(mapCenterLongitudeField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        changeMapCenterCoordsButton = new JButton();
        changeMapCenterCoordsButton.setText("Change map center coords");
        mapControlPanel.add(changeMapCenterCoordsButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editSiteButton = new JButton();
        editSiteButton.setText("Edit site:");
        mapControlPanel.add(editSiteButton, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editSiteNameField = new JTextField();
        editSiteNameField.setToolTipText("name of site to edit");
        mapControlPanel.add(editSiteNameField, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        removeSiteButton = new JButton();
        removeSiteButton.setText("Remove site:");
        mapControlPanel.add(removeSiteButton, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeSiteNameField = new JTextField();
        removeSiteNameField.setToolTipText("name of site to remove");
        mapControlPanel.add(removeSiteNameField, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        showMapCenterButton = new JButton();
        showMapCenterButton.setText("Show map center");
        mapControlPanel.add(showMapCenterButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showSiteButton = new JButton();
        showSiteButton.setText("Show site:");
        mapControlPanel.add(showSiteButton, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showSiteField = new JTextField();
        showSiteField.setToolTipText("name of site to show info");
        mapControlPanel.add(showSiteField, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapSchemaButton = new JButton();
        mapSchemaButton.setText("Map/Schema:");
        mapControlPanel.add(mapSchemaButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapSchemaStatusField = new JTextField();
        mapSchemaStatusField.setEditable(false);
        mapControlPanel.add(mapSchemaStatusField, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        filePathLabel = new JLabel();
        filePathLabel.setText("File path:");
        mapControlPanel.add(filePathLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filePathField = new JTextField();
        filePathField.setText("");
        filePathField.setToolTipText("path to JSON file with sites");
        mapControlPanel.add(filePathField, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        readFileButton = new JButton();
        readFileButton.setText("Read file");
        mapControlPanel.add(readFileButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveSitesToFileButton = new JButton();
        saveSitesToFileButton.setText("Save sites to file");
        mapControlPanel.add(saveSitesToFileButton, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        Font outputAreaFont = this.$$$getFont$$$("Consolas", -1, 12, outputArea.getFont());
        if (outputAreaFont != null) outputArea.setFont(outputAreaFont);
        outputArea.setRows(5);
        outputArea.setWrapStyleWord(true);
        panel1.add(outputArea, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(650, 25), new Dimension(650, 25), new Dimension(650, 25), 0, false));
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
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
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
