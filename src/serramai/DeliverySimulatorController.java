/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serramai;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

//jar imports
import jchodoriwsky.prog24178.assignments.Item;
import jchodoriwsky.prog24178.assignments.Main;
import jchodoriwsky.prog24178.assignments.Validator;
import jchodoriwsky.prog24178.assignments.Item.Vehicle;

/**
 * FXML Controller class A class that represents a program that delivers a set 
 * of 20 items through a set of three vehicles.
 *
 * @author Maiziel Serrao
 */
public class DeliverySimulatorController implements Initializable {

    //ArrayList of items and ArrayList of vehicles to be used in the program
    ArrayList<Item> items = makeItems();
    ArrayList<Vehicle> vehicles = makeVehicles();
    Vehicle vehicle1 = vehicles.get(0);
    Vehicle vehicle2 = vehicles.get(1);
    Vehicle vehicle3 = vehicles.get(2);

    //ArrayList of the items in each vehicle pending to be delivered
    ArrayList<Item> vehicle1Contents = vehicle1.getContents();
    ArrayList<Item> vehicle2Contents = vehicle2.getContents();
    ArrayList<Item> vehicle3Contents = vehicle3.getContents();

    //ArrayList of items delivered successfully by each vehicle
    ArrayList<Item> deliveredByVehicle1 = new ArrayList<Item>();
    ArrayList<Item> deliveredByVehicle2 = new ArrayList<Item>();
    ArrayList<Item> deliveredByVehicle3 = new ArrayList<Item>();

    //The current item of observation and its index
    Item currentItem = items.get(0);
    int currentItemIndex = 0;

    //ArrayList of all items delivered, regardless of which vehicle it came from
    ArrayList<Item> itemsDelivered = new ArrayList<Item>();
    double itemsDeliveredValue = 0;

    //declaration of all FXML tags
    @FXML
    private ImageView iv1, iv2, iv3, iv4;

    @FXML
    private Label lblv1a, lblv1b, lblv1c, lblv2a, lblv2b, lblv2c, lblv3a,
            lblv3b, lblv3c, lblItemTitle, lblItemValue, lblTitle, lblNext,
            lblError;

    @FXML
    private ToggleGroup grpVehicle;

    @FXML
    private RadioButton optA, optB, optC;

    @FXML
    private Button btnLoad, btnDeliver, btnQuit;

    @FXML
    private TextArea txtInfo;

    /**
     * A method that adds an item onto a vehicle of the selected radio button,
     * if that vehicle has not reached capacity.
     *
     * @param e - event that occurs when the user clicks on the "Load Items"
     * button
     */
    @FXML
    public void loadItem(ActionEvent e) {
        RadioButton selectedRadioButton
                = (RadioButton) grpVehicle.getSelectedToggle();

        if (selectedRadioButton == null) {
            lblError.setText("You have not selected a vehicle.");
        } else {
            lblError.setText("");
        }

        if (currentItemIndex < items.size()) {
            if (selectedRadioButton == optA) {
                if (!checkFull(vehicle1)) {
                    vehicle1Contents.add(currentItem);
                    currentItemIndex += 1;
                    if (currentItemIndex < items.size()) {
                        currentItem = items.get(currentItemIndex);
                    }
                } else {
                    lblError.setText(vehicle1.getName() + " can't carry anymore"
                            + " items!");
                }
            } else if (selectedRadioButton == optB) {
                if (!checkFull(vehicle2)) {
                    vehicle2Contents.add(currentItem);
                    currentItemIndex += 1;
                    if (currentItemIndex < items.size()) {
                        currentItem = items.get(currentItemIndex);
                    }
                } else {
                    lblError.setText(vehicle2.getName() + " can't carry anymore"
                            + " items!");
                }
            } else if (selectedRadioButton == optC) {
                if (!checkFull(vehicle3)) {
                    vehicle3Contents.add(currentItem);
                    currentItemIndex += 1;
                    if (currentItemIndex < items.size()) {
                        currentItem = items.get(currentItemIndex);
                    }
                } else {
                    lblError.setText(vehicle3.getName() + " can't carry anymore"
                            + " items!");
                }
            }

            updateInformation();
            changeColourWhenFull();
            deliverButtonAppear();
            loadLastItem();
        }
    }

    /**
     * A method that alters the GUI after the last item has been loaded.
     */
    public void loadLastItem() {
        if (checkFull(vehicle1) && checkFull(vehicle2) && checkFull(vehicle3)) {
            btnLoad.setVisible(false);
        } else {
            btnLoad.setVisible(true);
        }

        if (currentItemIndex == 20) {
            btnLoad.setVisible(false);
            btnQuit.setVisible(true);
            lblNext.setText("");
            lblItemTitle.setText("You have no more items.");
            lblItemValue.setText("");
        }
    }

    /**
     * A method that delivers items from selected vehicles. 
     *
     * @param e - event that occurs when the user clicks on the "Deliver Items"
     * button
     */
    @FXML
    public void deliverItems(ActionEvent e) {
        RadioButton selectedRadioButton
                = (RadioButton) grpVehicle.getSelectedToggle();
        boolean didItWork = true;
        if (currentItemIndex < 20) {
            didItWork = fullVehicleDelivery(selectedRadioButton);
            if (didItWork) {
                lblError.setText("");
            } else {
                lblError.setText("Please choose a vehicle that is full.");
            }
        } else if (currentItemIndex == 20) {
            didItWork = notFullVehicleDelivery(selectedRadioButton);
            if (didItWork) {
                lblError.setText("");
            } else {
                lblError.setText("Please choose a vehicle that has "
                        + "some content.");
            }
        }

        setBottomPane();
        updateInformation();
        loadLastItem();
        deliverButtonAppear();
    }
    
    /**
     * A method that delivers items from selected vehicles whose contents have
     * reached capacity. Prints an error message if the vehicle is not at full
     * capacity.
     * @param selectedRadioButton - radio button that represents the vehicle
     * that was selected
     * @return true if the delivery was successful
     */
    public boolean fullVehicleDelivery(RadioButton selectedRadioButton) {
        boolean didItWork = true;
        if (checkFull(vehicle1) && selectedRadioButton == optA) {
            deliveredByVehicle1.addAll(vehicle1.getContents());
            itemsDelivered.addAll(vehicle1.getContents());
            //adding all items delivered to top pane
            itemsDeliveredValue += getContentsValue(vehicle1);
            //calculating quantity of items delivered in top pane
            vehicle1Contents.clear();
            //changing the contents of the current vehicle
        } else if (checkFull(vehicle2) && selectedRadioButton == optB) {
            deliveredByVehicle2.addAll(vehicle2.getContents());
            itemsDelivered.addAll(vehicle2.getContents());
            //adding all items delivered to top pane
            itemsDeliveredValue += getContentsValue(vehicle2);
            //calculating quantity of items delivered in top pane
            vehicle2Contents.clear();
            //changing the contents of the current vehicle
        } else if (checkFull(vehicle3) && selectedRadioButton == optC) {
            deliveredByVehicle3.addAll(vehicle3.getContents());
            itemsDelivered.addAll(vehicle3.getContents());
            //adding all items delivered to top pane
            itemsDeliveredValue += getContentsValue(vehicle3);
            //calculating quantity of items delivered in top pane
            vehicle3Contents.clear();
            //changing the contents of the current vehicle
        } else {
            didItWork = false;
        }
        return didItWork;
    }
    
    /**
     * A method that delivers items from selected vehicles regardless of whether
     * those vehicles have reached capacity. 
     * @param selectedRadioButton - radio button that represents the vehicle
     * that was selected
     * @return true if the delivery was successful
     */
    public boolean notFullVehicleDelivery(RadioButton selectedRadioButton) {
        boolean didItWork = true;
        if (vehicle1.getContents().size()>0 && selectedRadioButton == optA) {
            deliveredByVehicle1.addAll(vehicle1.getContents());
            itemsDelivered.addAll(vehicle1.getContents());
            //adding all items delivered to top pane
            itemsDeliveredValue += getContentsValue(vehicle1);
            //calculating quantity of items delivered in top pane
            vehicle1Contents.clear();
            //changing the contents of the current vehicle
        } else if (vehicle2.getContents().size()>0 && 
                selectedRadioButton == optB) {
            deliveredByVehicle2.addAll(vehicle2.getContents());
            itemsDelivered.addAll(vehicle2.getContents());
            //adding all items delivered to top pane
            itemsDeliveredValue += getContentsValue(vehicle2);
            //calculating quantity of items delivered in top pane
            vehicle2Contents.clear();
            //changing the contents of the current vehicle
        } else if (vehicle3.getContents().size()>0 && 
                selectedRadioButton == optC) {
            deliveredByVehicle3.addAll(vehicle3.getContents());
            itemsDelivered.addAll(vehicle3.getContents());
            //adding all items delivered to top pane
            itemsDeliveredValue += getContentsValue(vehicle3);
            //calculating quantity of items delivered in top pane
            vehicle3Contents.clear();
            //changing the contents of the current vehicle
        } else {
            didItWork = false;
        }
        return didItWork;
    }

    /**
     * A method that exits the program if clicked.
     *
     * @param e - the event that occurs when the user clicks the "Quit" button.
     */
    @FXML
    public void quit(ActionEvent e) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateInformation();
    }

    /**
     * A method that returns a double value representing the monetary value of
     * the vehicle's contents.
     *
     * @param vehicle - the Vehicle object whose contents value needs to be
     * checked
     * @return - double value representing the cost of the vehicle's contents
     */
    public double getContentsValue(Vehicle vehicle) {
        ArrayList<Item> items = vehicle.getContents();
        int total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getValue();
        }
        return total;
    }

    /**
     * A method that prints out all the vehicle's information labels in the GUI,
     * and calls on other methods that represent the information of the program.
     */
    public void updateInformation() {
        //vehicle information
        lblv1a.setText("A: " + vehicle1.getName());
        lblv2a.setText("B: " + vehicle2.getName());
        lblv3a.setText("C: " + vehicle3.getName());

        lblv1b.setText("Holding " + vehicle1.getContents().size()
                + " items / " + vehicle1.getCapacity() + " max");
        lblv2b.setText("Holding " + vehicle2.getContents().size()
                + " items / " + vehicle2.getCapacity() + " max");
        lblv3b.setText("Holding " + vehicle3.getContents().size()
                + " items / " + vehicle3.getCapacity() + " max");

        String vehicle1Value
                = String.format("%.2f", getContentsValue(vehicle1));
        lblv1c.setText("Contents value: $" + vehicle1Value);

        String vehicle2Value
                = String.format("%.2f", getContentsValue(vehicle2));
        lblv2c.setText("Contents value: $" + vehicle2Value);

        String vehicle3Value
                = String.format("%.2f", getContentsValue(vehicle3));
        lblv3c.setText("Contents value: $" + vehicle3Value);

        setItemTitleAndValue();
        setTitlePaneInfo();
        setItemImage();
        changeColourWhenFull();
        deliverButtonAppear();
    }

    /**
     * A method that returns true if a vehicle has reached it's capacity.
     *
     * @param vehicle - Vehicle object that needs to be checked if it has
     * reached its full capacity
     * @return boolean representing whether the vehicle is full
     */
    public boolean checkFull(Vehicle vehicle) {
        return vehicle.getCapacity() == vehicle.getContents().size();
    }

    /**
     * A method that displays information about the program in the top pane.
     * Information includes the number of items delivered as well as the total
     * cost of the items delivered.
     */
    public void setTitlePaneInfo() {
        //title pane information - fix itemsDeliveredValue
        String itemsDeliveredValueString
                = String.format("%.2f", itemsDeliveredValue);
        lblTitle.setText("Today, the fleet has delivered a total of "
                + itemsDelivered.size() + " items altogether worth $"
                + itemsDeliveredValueString + ".");
    }

    /**
     * A method that displays the information about the item to be delivered
     * next as well as its value.
     */
    public void setItemTitleAndValue() {
        lblItemTitle.setText(currentItem.getName());
        String itemValue
                = String.format("%.2f", (double) currentItem.getValue());
        lblItemValue.setText("Value: $" + itemValue);
    }

    /**
     * A method that changes the colour of the label representing the vehicle's
     * capacity to red when a vehicle has reached full capacity. If the vehicle
     * has not reached full capacity, the method returns the label's colour back
     * to black.
     */
    public void changeColourWhenFull() {
        //change colour of font in case any of the vehicels have reached cap
        if (checkFull(vehicle1)) {
            lblv1b.setTextFill(Color.RED);
        } else {
            lblv1b.setTextFill(Color.BLACK);
        }

        if (checkFull(vehicle2)) {
            lblv2b.setTextFill(Color.RED);
        } else {
            lblv2b.setTextFill(Color.BLACK);
        }

        if (checkFull(vehicle3)) {
            lblv3b.setTextFill(Color.RED);
        } else {
            lblv3b.setTextFill(Color.BLACK);
        }
    }

    /**
     * A method that displays information in the bottom pane of the GUI.
     * Information displayed includes items delivered according to vehicle in
     * descending order of cost.
     */
    public void setBottomPane() {
        String text = "";
        if (deliveredByVehicle1.isEmpty() && deliveredByVehicle2.isEmpty()
                && deliveredByVehicle3.isEmpty()) {
            text = "Nothing has been delivered as of yet...";
        }
        if (!deliveredByVehicle1.isEmpty()) {
            text += toString(vehicle1, 1) + " has delivered the following:"
                    + deliveredItemsInfo(deliveredByVehicle1) + "\n";
        }

        if (!deliveredByVehicle2.isEmpty()) {
            text += toString(vehicle2, 2) + " has delivered the following:"
                    + deliveredItemsInfo(deliveredByVehicle2) + "\n";
        }

        if (!deliveredByVehicle3.isEmpty()) {
            text += toString(vehicle3, 3) + " has delivered the following:"
                    + deliveredItemsInfo(deliveredByVehicle3);
        }
        txtInfo.setText(text);
    }

    /**
     * A method that sets the visibility of the "Make a Delivery" button to true
     * if a vehicle has reached full capacity. Otherwise, sets the button's
     * visibility to "false".
     */
    public void deliverButtonAppear() {
        if (checkFull(vehicle1) || checkFull(vehicle2) || checkFull(vehicle3)) {
            btnDeliver.setVisible(true);
        } else if (vehicle1.getContents().size() == 0
                && vehicle2.getContents().size() == 0
                && vehicle3.getContents().size() == 0 
                && currentItemIndex > 19) {
            btnDeliver.setVisible(false);
            lblError.setText("You have no more items to deliver.");
        } else if (currentItemIndex == 20) {
            btnDeliver.setVisible(true);
        } else {
            btnDeliver.setVisible(false);
        }
    }

    /**
     * A method that sets the next item to be delivered image to the next item's
     * image. If there are no more items to be delivered, the image is set to a
     * blank image.
     */
    public void setItemImage() {
        if (currentItemIndex < items.size()) {
            iv4.setImage(items.get(currentItemIndex).getImage());
        } else {
            String b = "assets/blank.jpg";
            Image bl = new Image(getClass().getResource(b).toExternalForm());
            iv4.setImage(bl);
        }
    }

    /**
     * A method that prints out a vehicle's order letter and name in a clean
     * fashion (example, "Vehicle X (VehicleName)"). The order letter of the
     * vehicle is dependent upon the integer value parameter.
     *
     * @param vehicle - Vehicle object whose name should be printed
     * @param rb - integer value representing the order letter of the vehicle
     * @return String representing the vehicle's order letter and the vehicle's
     * name
     */
    public String toString(Vehicle vehicle, int rb) {
        if (rb == 1) {
            return ("Vehicle A (" + vehicle.getName() + ")");
        } else if (rb == 2) {
            return ("Vehicle B (" + vehicle.getName() + ")");
        } else if (rb == 3) {
            return ("Vehicle C (" + vehicle.getName() + ")");
        } else {
            return ("Unknown vehicle");
        }
    }

    /**
     * A method that sorts and creates a String out all the items that have been
     * successfully delivered, written in order of descending cost.
     *
     * @param delivered - The ArrayList of items to be sorted and made into a
     * String
     * @return String that contains information of successfully delivered items
     * in descending order
     */
    public String deliveredItemsInfo(ArrayList<Item> delivered) {
        ArrayList<Item> deliveredCopy = delivered;
        Collections.sort(deliveredCopy);
        Collections.reverse(deliveredCopy);
        String deliveredItemsInfo = "";
        String a = " a ";
        String an = " an ";
        String properDecimals = "";
        for (int i = 0; i < deliveredCopy.size(); i++) {
            if ((i == deliveredCopy.size() - 1) && deliveredCopy.size() > 1) {
                deliveredItemsInfo += " and";
            }
            Character firstLetter = deliveredCopy.get(i).getName().charAt(0);
            if (firstLetter == 'A' || firstLetter == 'E' || firstLetter == 'I'
                    || firstLetter == 'O' || firstLetter == 'U') {
                deliveredItemsInfo += an;
            } else {
                deliveredItemsInfo += a;
            }
            properDecimals
                    = String.format("%.2f", deliveredCopy.get(i).getValue());
            deliveredItemsInfo += deliveredCopy.get(i).getName() + " worth $"
                    + properDecimals;

            if (i < deliveredCopy.size() - 1) {
                deliveredItemsInfo += ",";
            } else {
                deliveredItemsInfo += ".";
            }
        }
        return deliveredItemsInfo;
    }

    /**
     * A method that creates an ArrayList of 20 Item objects. All Item objects
     * are based off a CD album.
     *
     * @return ArrayList of 20 Item objects.
     */
    public ArrayList<Item> makeItems() {
        ArrayList<Item> allItems = new ArrayList<Item>();

        //Alessia Cara Item
        String alessiaCaraImg = "assets/alessiacara.png";
        Item alessiaCara
                = new Item​("Alessia Cara - The Pains of Growing Album", 9.00,
                        new Image(getClass().getResource(alessiaCaraImg).
                                toExternalForm()));
        allItems.add(alessiaCara);

        //Alicia Keys Item
        String aliciaKeysImg = "assets/aliciakeys.jpg";
        Item aliciaKeys
                = new Item​("Alicia Keys - The Diary of Alicia Keys Album",
                        11.00, new Image(getClass().getResource(aliciaKeysImg).
                                toExternalForm()));
        allItems.add(aliciaKeys);

        //Arcade Fire Item
        String arcadeFireImg = "assets/arcadefire.jpg";
        Item arcadeFire
                = new Item​("Arcade Fire - Neon Bible Album", 5.00,
                        new Image(getClass().getResource(arcadeFireImg).
                                toExternalForm()));
        allItems.add(arcadeFire);

        //Audioslave Item
        String audioslaveImg = "assets/audioslave.jpg";
        Item audioslave
                = new Item​("Audioslave - Audioslave Album", 8.00,
                        new Image(getClass().getResource(audioslaveImg).
                                toExternalForm()));
        allItems.add(audioslave);

        //Billy Talent Item
        String billyTalentImg = "assets/billytalent.jpg";
        Item billyTalent
                = new Item​("Billy Talent - Billy Talent Album", 7.00,
                        new Image(getClass().getResource(billyTalentImg).
                                toExternalForm()));
        allItems.add(billyTalent);

        //City and Colour Item
        String cityAndColourImg = "assets/cityandcolour.jpg";
        Item cityAndColour = new Item​("City and Colour - Little Hell Album",
                12.00, new Image(getClass().getResource(cityAndColourImg).
                        toExternalForm()));
        allItems.add(cityAndColour);

        //Heart Item
        String heartImg = "assets/heart.png";
        Item heart = new Item​("Heart - Heart Album", 15.00,
                new Image(getClass().getResource(heartImg).toExternalForm()));
        allItems.add(heart);

        //King Krule Item
        String kingKruleImg = "assets/kingkrule.jpg";
        Item kingKrule = new Item​("King Krule - 6 Feet Beneath the Moon Album",
                1.00, new Image(getClass().getResource(kingKruleImg).
                        toExternalForm()));
        allItems.add(kingKrule);

        //Lauryn Hill Item
        String laurynHillImg = "assets/laurynhill.jpg";
        Item laurynHill
                = new Item​("Lauryn Hill - The Miseducation of Lauryn Hill "
                        + "Album", 13.00,
                        new Image(getClass().getResource(laurynHillImg).
                                toExternalForm()));
        allItems.add(laurynHill);

        //Linkin' Park Item
        String linkinParkImg = "assets/linkinpark.jpg";
        Item linkinPark = new Item​("Linkin' Park - Meteora Album", 10.00,
                new Image(getClass().getResource(linkinParkImg).
                        toExternalForm()));
        allItems.add(linkinPark);

        //Local Natives Item
        String localNativesImg = "assets/localnatives.jpg";
        Item localNatives = new Item​("Local Natives - Hummingbird Album", 20.00,
                new Image(getClass().getResource(localNativesImg).
                        toExternalForm()));
        allItems.add(localNatives);

        //Queen Item
        String queenImg = "assets/queen.png";
        Item queen = new Item​("Queen - The Works Album", 20.00,
                new Image(getClass().getResource(queenImg).toExternalForm()));
        allItems.add(queen);

        //Rise Against Item
        String riseAgainstImg = "assets/riseagainst.jpg";
        Item riseAgainst = new Item​("Rise Against - Appeal to Reason Album",
                19.00, new Image(getClass().getResource(riseAgainstImg).
                        toExternalForm()));
        allItems.add(riseAgainst);

        //Sam Smith Item
        String samSmithImg = "assets/samsmith.png";
        Item samSmith = new Item​("Sam Smith - The Thrill of it All Album",
                25.00, new Image(getClass().getResource(samSmithImg).
                        toExternalForm()));
        allItems.add(samSmith);

        //Stevie Wonder Item
        String stevieWonderImg = "assets/steviewonder.jpg";
        Item stevieWonder = new Item​("Stevie Wonder - For Once In My Life "
                + "Album", 14.00,
                new Image(getClass().getResource(stevieWonderImg).
                        toExternalForm()));
        allItems.add(stevieWonder);

        //System of A Down Item
        String systemOfADownImg = "assets/systemofadown.jpg";
        Item systemOfADown = new Item​("System Of A Down - Toxicity Album",
                7.00, new Image(getClass().getResource(systemOfADownImg).
                        toExternalForm()));
        allItems.add(systemOfADown);

        //Tears for Fears Items
        String tearsForFearsImg = "assets/tearsforfears.jpg";
        Item tearsForFears = new Item​("Tears for Fears - Songs from the Big "
                + "Chair Album", 6.00,
                new Image(getClass().getResource(tearsForFearsImg).
                        toExternalForm()));
        allItems.add(tearsForFears);

        //The Killers Item
        String theKillersImg = "assets/thekillers.png";
        Item theKillers = new Item​("The Killers - Hot Fuss Album", 14.00,
                new Image(getClass().getResource(theKillersImg).
                        toExternalForm()));
        allItems.add(theKillers);

        //Tori Kelly Item
        String toriKellyImg = "assets/torikelly.jpg";
        Item toriKelly = new Item​("Tori Kelly - Unbreakable Smile Album",
                18.00, new Image(getClass().getResource(toriKellyImg).
                        toExternalForm()));
        allItems.add(toriKelly);

        //Whitney Houston Item
        String whitneyHoustonImg = "assets/whitneyhouston.jpg";
        Item whitneyHouston = new Item​("Whitney Houston - Whitney Album", 20.00,
                new Image(getClass().getResource(whitneyHoustonImg).
                        toExternalForm()));
        allItems.add(whitneyHouston);

        return allItems;
    }

    /**
     * A method that creates an ArrayList of three Vehicle objects. Vehicle
     * objects created include a Messenger Pigeon, Tony Hawk and a Soccer Mom
     * Van.
     *
     * @return ArrayList containing 3 Vehicle objects.
     */
    public ArrayList<Vehicle> makeVehicles() {
        ArrayList<Vehicle> allVehicles = new ArrayList<Vehicle>();

        String vehicle1Img = "assets/vehicle1.jpg";
        Item v1 = new Item();
        Item.Vehicle vehicle1 = v1.new Vehicle​("Messenger Pigeon", 1,
                new Image(getClass().getResource(vehicle1Img).
                        toExternalForm()));
        allVehicles.add(vehicle1);

        String vehicle2Img = "assets/vehicle2.jpg";
        Item v2 = new Item();
        Item.Vehicle vehicle2 = v2.new Vehicle​("Tony Hawk", 8,
                new Image(getClass().getResource(vehicle2Img).
                        toExternalForm()));
        allVehicles.add(vehicle2);

        String vehicle3Img = "assets/vehicle3.jpg";
        Item v3 = new Item();
        Item.Vehicle vehicle3 = v3.new Vehicle​("Soccer Mom Van", 3,
                new Image(getClass().getResource(vehicle3Img).
                        toExternalForm()));
        allVehicles.add(vehicle3);

        return (allVehicles);
    }
}
