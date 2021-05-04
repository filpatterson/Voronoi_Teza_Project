package UI;

import Globals.FileManager;
import Globals.MapUtils;
import Globals.Utils;
import HalfPlaneIntersectionMethod.Site;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Locale;

public class UserInterface extends JDialog {
    private JPanel contentPane;
    private JPanel siteControlPanel;
    private JPanel mapControlPanel;
    private JPanel mapPanel;
    private JPanel controlPanel;
    private JTextField xCoordField;
    private JSlider redSlider;
    private JSlider greenSlider;
    private JSlider blueSlider;
    private JTextField nameCreateField;
    private JButton createEditSiteButton;
    private JButton createGeoSiteButton;
    private JTextField mapCenterLatitudeField;
    private JTextField mapCenterLongitudeField;
    private JButton changeMapCenterCoordsButton;
    private JTextField changeNameField;
    private JButton changeRequestField;
    private JTextField removeNameField;
    private JButton removeRequestField;
    private JLabel xCoordLabel;
    private JLabel yCoordLabel;
    private JTextField yCoordField;
    private JLabel redLabel;
    private JLabel greenLabel;
    private JLabel blueLabel;
    private JLabel nameLabel;
    private JLabel createLabel;
    private JLabel createGeoLabel;
    private JLabel mapCenterLatitudeLabel;
    private JLabel mapCenterLongitudeLabel;
    private JLabel mapCenterChangeLabel;
    private JLabel changeNameLabel;
    private JLabel changeRequestLabel;
    private JLabel removeNameLabel;
    private JLabel removeRequestLabel;
    private JTextArea outputArea;
    private JButton mapSchemaButton;
    private JButton saveSitesToFileButton;
    private JLabel filePathLabel;
    private JTextField filePathField;
    private JTextField mapSchemaStatusField;
    private JButton readFileButton;
    private boolean isMapRequired = false;
    private boolean isEditModeEnabled = false;
    private int currentSiteEditIndex;

    public UserInterface() {
        //  coordinates of the Chisinau are considered as default values
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

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        //  button for creating new sites and editing existing ones if edit flag was called
        createEditSiteButton.addActionListener(e -> {
            try {
                //  get coordinates, color and name
                double xCoordinate = Double.parseDouble(xCoordField.getText());
                double yCoordinate = Double.parseDouble(yCoordField.getText());
                int redValue = (redSlider.getValue() / 100) * 255;
                int greenValue = (greenSlider.getValue() / 100) * 255;
                int blueValue = (blueSlider.getValue() / 100) * 255;
                String name = nameCreateField.getText();

                //  check if point is in the given sector
                if (xCoordinate < 0 || xCoordinate > Utils.xLimit) {
                    outputArea.setText("Point is out of X bounds");
                    return;
                } else if (yCoordinate < 0 || yCoordinate > Utils.yLimit) {
                    outputArea.setText("Point is out of X bounds");
                    return;
                }

                //  if there is no name, then inform user about that
                if (name == null || name.isEmpty()) {
                    outputArea.setText("there can't be site without name");
                    return;
                }

                //  clear fields from input, considering that all data was successfully taken
                xCoordField.setText("");
                yCoordField.setText("");
                nameCreateField.setText("");

                //  create new site using given data
                Site newSite = new Site(xCoordinate, yCoordinate, new Color(redValue, greenValue, blueValue), name);
                newSite.toGeographical();
                if (isEditModeEnabled) {
                    Utils.sitesStorage.set(currentSiteEditIndex, newSite);
                    isEditModeEnabled = false;
                    outputArea.setText("Site was successfully edited");
                    return;
                }

                Utils.sitesStorage.add(newSite);

                //  show info about new site
                outputArea.setText(newSite.toString());
            } catch (NumberFormatException nfe) {
                outputArea.setText("there are invalid coordinates inputted");
            }
        });

        //  button with the same purpose as create/edit one, but this one takes geographical coordinates
        createGeoSiteButton.addActionListener(e -> {
            try {
                //  get coordinates, color and name
                double longCoordinate = Double.parseDouble(xCoordField.getText());
                double latCoordinate = Double.parseDouble(yCoordField.getText());
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
                xCoordField.setText("");
                yCoordField.setText("");
                nameCreateField.setText("");

                //  create new site using given data
                Site newSite = new Site(
                        longCoordinate, latCoordinate, new Color(redValue, greenValue, blueValue), name, true
                );

                if (!newSite.toCartesian()) {
                    outputArea.setText("Point is out of given area bounds");
                    return;
                }

                if (isEditModeEnabled) {
                    Utils.sitesStorage.set(currentSiteEditIndex, newSite);
                    isEditModeEnabled = false;
                    outputArea.setText("Site was successfully edited");
                    return;
                }

                Utils.sitesStorage.add(newSite);

                //  show info about new site
                outputArea.setText(newSite.toString());
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

                ArrayList<Site> newSitesStorage = new ArrayList<>();

                for (Site site : Utils.sitesStorage) {
                    if (site.toCartesian()) {
                        newSitesStorage.add(site);
                    }
                }

                Utils.sitesStorage = newSitesStorage;
                if (isEditModeEnabled) {
                    isEditModeEnabled = false;
                }
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

            FileManager.readSitesFromFile(path);
            if (isEditModeEnabled) {
                isEditModeEnabled = false;
            }
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

            FileManager.writeSitesToFile(path);
            if (isEditModeEnabled) {
                isEditModeEnabled = false;
            }
        });

        //  removes site from storage
        removeRequestField.addActionListener(e -> {
            String siteNameToRemove = removeNameField.getText();
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
        });

        //  request edit of the existing site
        changeRequestField.addActionListener(e -> {
            String siteNameToEdit = changeNameField.getText();
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
                Utils.sitesStorage.remove(siteToEdit);
                outputArea.setText("Site with name '" + siteNameToEdit + "' is in the edit mode, change parameters");
                isEditModeEnabled = true;

                //  set x and y cartesian coordinates
                xCoordField.setText(String.valueOf(siteToEdit.x));
                yCoordField.setText(String.valueOf(siteToEdit.y));

                //  set color sliders
                redSlider.setValue(siteToEdit.getColor().getRed());
                greenSlider.setValue(siteToEdit.getColor().getGreen());
                blueSlider.setValue(siteToEdit.getColor().getBlue());

                //  set name
                nameCreateField.setText(siteToEdit.getName());
            }
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        controlPanel = new JPanel();
        controlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        controlPanel.setBackground(new Color(-2039584));
        panel1.add(controlPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(600, 400), new Dimension(600, 400), new Dimension(600, 400), 0, false));
        siteControlPanel = new JPanel();
        siteControlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 2, new Insets(5, 5, 5, 5), 0, 0));
        controlPanel.add(siteControlPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, 400), new Dimension(300, 400), new Dimension(300, 400), 0, false));
        xCoordLabel = new JLabel();
        xCoordLabel.setText("X/Long.:");
        siteControlPanel.add(xCoordLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xCoordField = new JTextField();
        xCoordField.setToolTipText("type x coordinate...");
        siteControlPanel.add(xCoordField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        yCoordLabel = new JLabel();
        yCoordLabel.setText("Y/Lat.:");
        siteControlPanel.add(yCoordLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yCoordField = new JTextField();
        yCoordField.setToolTipText("type y coordinate...");
        siteControlPanel.add(yCoordField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
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
        createLabel = new JLabel();
        createLabel.setText("Create");
        siteControlPanel.add(createLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameCreateField = new JTextField();
        nameCreateField.setToolTipText("type name of site...");
        siteControlPanel.add(nameCreateField, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        createEditSiteButton = new JButton();
        createEditSiteButton.setText("Create/edit site");
        siteControlPanel.add(createEditSiteButton, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        createGeoLabel = new JLabel();
        createGeoLabel.setText("New geo.:");
        siteControlPanel.add(createGeoLabel, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createGeoSiteButton = new JButton();
        createGeoSiteButton.setText("Create geo site");
        siteControlPanel.add(createGeoSiteButton, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, 24), null, 0, false));
        mapControlPanel = new JPanel();
        mapControlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(10, 2, new Insets(5, 5, 5, 5), 0, 0));
        controlPanel.add(mapControlPanel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(300, 400), new Dimension(300, 400), new Dimension(300, 400), 0, false));
        mapCenterLatitudeLabel = new JLabel();
        mapCenterLatitudeLabel.setText("Map lat.:");
        mapControlPanel.add(mapCenterLatitudeLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapCenterLatitudeField = new JTextField();
        mapCenterLatitudeField.setToolTipText("type map center latitude...");
        mapControlPanel.add(mapCenterLatitudeField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapCenterLongitudeLabel = new JLabel();
        mapCenterLongitudeLabel.setText("Map long.:");
        mapControlPanel.add(mapCenterLongitudeLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapCenterLongitudeField = new JTextField();
        mapCenterLongitudeField.setToolTipText("type map center longitude...");
        mapControlPanel.add(mapCenterLongitudeField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapCenterChangeLabel = new JLabel();
        mapCenterChangeLabel.setText("Change map:");
        mapControlPanel.add(mapCenterChangeLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeMapCenterCoordsButton = new JButton();
        changeMapCenterCoordsButton.setText("Change map center coords");
        mapControlPanel.add(changeMapCenterCoordsButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeNameLabel = new JLabel();
        changeNameLabel.setText("Site name:");
        mapControlPanel.add(changeNameLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeNameField = new JTextField();
        changeNameField.setToolTipText("type site to edit...");
        mapControlPanel.add(changeNameField, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        changeRequestLabel = new JLabel();
        changeRequestLabel.setText("Site to edit:");
        mapControlPanel.add(changeRequestLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        changeRequestField = new JButton();
        changeRequestField.setText("Request edit");
        mapControlPanel.add(changeRequestField, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeNameLabel = new JLabel();
        removeNameLabel.setText("Site to remove:");
        mapControlPanel.add(removeNameLabel, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeNameField = new JTextField();
        removeNameField.setToolTipText("type site to remove");
        mapControlPanel.add(removeNameField, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        removeRequestLabel = new JLabel();
        removeRequestLabel.setText("Site remove:");
        mapControlPanel.add(removeRequestLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeRequestField = new JButton();
        removeRequestField.setText("Remove site");
        mapControlPanel.add(removeRequestField, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveSitesToFileButton = new JButton();
        saveSitesToFileButton.setText("Save sites to file");
        mapControlPanel.add(saveSitesToFileButton, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filePathLabel = new JLabel();
        filePathLabel.setText("File path:");
        mapControlPanel.add(filePathLabel, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filePathField = new JTextField();
        mapControlPanel.add(filePathField, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mapSchemaStatusField = new JTextField();
        mapSchemaStatusField.setEditable(false);
        mapControlPanel.add(mapSchemaStatusField, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        readFileButton = new JButton();
        readFileButton.setText("Read file");
        mapControlPanel.add(readFileButton, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mapSchemaButton = new JButton();
        mapSchemaButton.setText("Map/Schema:");
        mapControlPanel.add(mapSchemaButton, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        Font outputAreaFont = this.$$$getFont$$$("Consolas", -1, 12, outputArea.getFont());
        if (outputAreaFont != null) outputArea.setFont(outputAreaFont);
        outputArea.setRows(5);
        outputArea.setWrapStyleWord(true);
        panel1.add(outputArea, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
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
