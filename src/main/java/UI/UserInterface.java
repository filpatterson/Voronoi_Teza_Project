package UI;

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
    private boolean isMapRequired = false;

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

                //  if there is no name, then inform user about that
                if (name == null || name.isEmpty()) {
                    outputArea.setText(getConsoleOutput("there can't be site without name"));
                    return;
                }

                //  clear fields from input, considering that all data was successfully taken
                xCoordField.setText("");
                yCoordField.setText("");
                nameCreateField.setText("");

                //  create new site using given data
                Site newSite = new Site(xCoordinate, yCoordinate, new Color(redValue, greenValue, blueValue), name);
                newSite.toGeographical();
                Utils.sitesStorage.add(newSite);

                //  show info about new site
                outputArea.setText(newSite.toString());
            } catch (NumberFormatException nfe) {
                outputArea.setText(getConsoleOutput("there are invalid coordinates inputted"));
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
                    outputArea.setText(getConsoleOutput("there can't be site without name"));
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
                    outputArea.setText(getConsoleOutput("point is out of given area bounds"));
                    return;
                }
                Utils.sitesStorage.add(newSite);

                //  show info about new site
                outputArea.setText(newSite.toString());
            } catch (NumberFormatException nfe) {
                outputArea.setText(getConsoleOutput("there are invalid coordinates inputted"));
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
            } catch (NumberFormatException nfe) {
                outputArea.setText("there are invalid coordinates inputted");
            }
        });

        //  switch representation mode
        mapSchemaButton.addActionListener(e -> {
            isMapRequired = !isMapRequired;
            if (isMapRequired) {
                outputArea.setText("Switched diagram mode to Map");
            } else {
                outputArea.setText("Switched diagram mode to Schema");
            }
        });
    }

    private String getConsoleOutput(String textToShow) {
        if (isMapRequired) {
            return "\tDiagram mode: Map\n" + textToShow;
        } else {
            return "\tDiagram mode: Schema\n" + textToShow;
        }
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
        mapControlPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, new Insets(5, 5, 5, 5), 0, 0));
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
