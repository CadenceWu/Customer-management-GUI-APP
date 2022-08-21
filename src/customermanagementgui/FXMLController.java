package customermanagementgui;

import customermanagementgui.CreateAssDatabase;
import customermanagementgui.DBUtilAss;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import javax.swing.JOptionPane;
import javafx.scene.input.MouseEvent;

public class FXMLController {

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<Customer, String> colEmail;

    @FXML
    private TableColumn<Customer, Integer> colId;

    @FXML
    private TableColumn<Customer, String> colMobile;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private Button createDatabaseButton;

    @FXML
    private TableView<Customer> cusManageTable;

    @FXML
    private Button deleteButton;

    @FXML
    private Button displayButton;

    @FXML
    private TextField dsText;

    @FXML
    private TextField emailText;

    @FXML
    private TextField idText;

    @FXML
    private TextField mobileText;

    @FXML
    private TextField nameText;
    @FXML
    private TextField emailTextU;
    @FXML
    private TextField idTextU;
    @FXML
    private TextField mobileTextU;

    @FXML
    private TextField nameTextU;

    @FXML
    private Button searchButton;

    @FXML
    private Button updateButton;

    ObservableList<Customer> customers = FXCollections.observableArrayList();
    int index = -1;

    /*The method used when double click one of the update text boxes, 
      it clears all text boxes except ID text box */
    @FXML
    void doubleClick(MouseEvent event) {

        if (event.getClickCount() == 2) {
            nameTextU.clear();
            emailTextU.clear();
            mobileTextU.clear();
        }
    }

    /*The method used when selecting the item from the table view, the relevant information 
     will be shown on its corresponding text boxs.*/
    @FXML
    void getSelected(MouseEvent event) {
        index = cusManageTable.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }
        idTextU.setText(colId.getCellData(index).toString());
        nameTextU.setText(colName.getCellData(index).toString());
        emailTextU.setText(colEmail.getCellData(index).toString());
        mobileTextU.setText(colMobile.getCellData(index).toString());

    }

    //The method allows a user to add data they've entered into the database
    @FXML
    void addAction(ActionEvent event) {
        Customer cus = new Customer();
        try {
            if (!cusManageTable.getItems().isEmpty()) {

                if (!idText.getText().isEmpty()) {
                    int enterId = Integer.valueOf(idText.getText());
                    cus.setId(enterId);
                    String enterName = nameText.getText();
                    cus.setName(enterName);
                    String enterEmail = emailText.getText();
                    cus.setEmail(enterEmail);
                    String enterMobile = mobileText.getText();
                    cus.setMobile(enterMobile);
                    String insertSQL = String.format("INSERT INTO customer (id,name,email,mobile) VALUES "
                            + "('" + cus.getId() + "','" + cus.getName() + "','" + cus.getEmail() + "','" + cus.getMobile() + "');");
                    int count = DBUtilAss.executeUpdate(insertSQL);
                    if (count == 0) {
                        JOptionPane.showMessageDialog(null, "Failed to add a new customer.\n (It could be the reason of duplication)");

                    } else {
                        JOptionPane.showMessageDialog(null, "Successfully Added a new customer");
                        customers = getAllCustomers();
                        idText.clear();
                        nameText.clear();
                        emailText.clear();
                        mobileText.clear();
                        displayCustomer();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "ID cannot be empty");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Click the button to create the database first");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please enter an interger in the 'id' text box or an appropriate value");
        }
    }

    ///The method used when a user wants to create the database/table for the first time or they wants to re-create it. 
    @FXML
    void createDatabaseAction(ActionEvent event
    ) {
        CreateAssDatabase.createSmtbizDB();
        customers = getAllCustomers();
        displayCustomer();
        JOptionPane.showMessageDialog(null, "Create a database 'smtbiz'");
    }

    // The method used when a user wants to delete a customer from the database according to the ID they've entered
    @FXML
    void deleteAction(ActionEvent event
    ) {
        Customer cus = new Customer();

        try {
            if (!cusManageTable.getItems().isEmpty()) {

                if (!dsText.getText().isEmpty()) {

                    int deleteId = Integer.valueOf(dsText.getText());
                    cus.setId(deleteId);

                    String deleteSQL = "DELETE FROM customer WHERE ID='" + cus.getId() + "';";
                    int count = DBUtilAss.executeUpdate(deleteSQL);

                    if (count == 0) {
                        JOptionPane.showMessageDialog(null, "Customer Not Found");

                    } else {
                        JOptionPane.showMessageDialog(null, "Deleted the customer from the list");
                        customers = getAllCustomers();
                        displayCustomer();
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Enter the ID");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Click the button to create the database first");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please enter an interger or an appropriate value");
        }
        dsText.clear();

    }

    // The method used when a user wants to search a customer from the database according to the ID they've entered
    @FXML
    void searchAction(ActionEvent event
    ) {
        Customer cus = new Customer();
        Customer c = null;
        try {

            if (!cusManageTable.getItems().isEmpty()) {

                if (!dsText.getText().isEmpty()) {
                    int deleteId = Integer.valueOf(dsText.getText());
                    cus.setId(deleteId);
                    String query = "SELECT * FROM customer WHERE ID=" + cus.getId() + ";";

                    ResultSet rs = DBUtilAss.executeQuery(query);

                    if (rs.next()) {
                        c = new Customer();
                        c.setId(rs.getInt("ID"));
                        c.setName(rs.getString("Name"));
                        c.setEmail(rs.getString("Email"));
                        c.setMobile(rs.getString("Mobile"));
                        JOptionPane.showMessageDialog(null, "Found the customer");
                        JOptionPane.showMessageDialog(null, "ID: " + c.getId() + "\nName: "
                                + c.getName() + "\nEmail: " + c.getEmail() + "\nMobile: " + c.getMobile());

                    } else {
                        JOptionPane.showMessageDialog(null, "Customer Not Found");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Enter the ID");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Click the button to create the database first");
            }
        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Please enter an interger or an appropriate value");
        }
        dsText.clear();

    }

    //The method that allows a user to update the exist customer information. Id cannot be changed.
    @FXML
    void updateAction(ActionEvent event
    ) {
        Customer cus = new Customer();

        try {
            if (!cusManageTable.getItems().isEmpty()) {

                if (!idTextU.getText().isEmpty()) {
                    int updateId = Integer.valueOf(idTextU.getText());
                    cus.setId(updateId);
                    String updateName = nameTextU.getText();
                    cus.setName(updateName);
                    String updateEmail = emailTextU.getText();
                    cus.setEmail(updateEmail);
                    String updateMobile = mobileTextU.getText();
                    cus.setMobile(updateMobile);

                    String updateSQL = "UPDATE customer set name='" + cus.getName() + "',email='" + cus.getEmail() + ""
                            + "',mobile='" + cus.getMobile() + "'  WHERE ID='" + cus.getId() + "';";
                    int count = DBUtilAss.executeUpdate(updateSQL);

                    if (count == 0) {
                        JOptionPane.showMessageDialog(null, "Customer Not Found");

                    } else {
                        JOptionPane.showMessageDialog(null, "Update the information");
                        customers = getAllCustomers();
                        displayCustomer();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Select the data or enter the ID you would like to update");
                }

            } else {
                JOptionPane.showMessageDialog(null, "Click the button to create the database first");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Please enter an interger or an appropriate value");
        }
        idTextU.clear();
        nameTextU.clear();
        emailTextU.clear();
        mobileTextU.clear();
    }

    //The method that will get all customers information
    public static ObservableList<Customer> getAllCustomers() {
        String query = "SELECT * FROM customer;";
        ObservableList<Customer> customers = FXCollections.observableArrayList();

        try {
            ResultSet rs = DBUtilAss.executeQuery(query);

            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("ID"));
                c.setName(rs.getString("Name"));
                c.setEmail(rs.getString("Email"));
                c.setMobile(rs.getString("Mobile"));
                customers.add(c);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException on executeQuery: " + ex.getMessage());
        }

        return customers;
    }

    //The method to show all customers in the table view
    public void displayCustomer() {
        colId.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<Customer, String>("email"));
        colMobile.setCellValueFactory(new PropertyValueFactory<Customer, String>("mobile"));
        cusManageTable.setItems(customers);
    }
}
