package com.example.masterprojectcsa;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.ResultSet;
import javafx.scene.layout.AnchorPane;


public class Controller {

    private final Database db = new Database();
    //private final List<Map<String, Object>> pizzaList = new ArrayList<>();
    private final List<Map<String, Object>> MatchList = new ArrayList<>();

    private boolean isCreating = false; // Flag to track creation state

    private int matchIdVal;



    @FXML
    private AnchorPane root;
    @FXML
    private Button FirstButton;
    @FXML
    private Button doublePrevious;
    @FXML
    private Button previous;
    @FXML
    private Button next;
    @FXML
    private Button doubleNext;
    @FXML
    private Button LastButton;

    private int currentMatchIndex = 0;


    @FXML
    private MenuItem connect;

    @FXML
    private TextField teamNumberField;

    @FXML
    private TextField autonomousPointsField;

    @FXML
    private TextField ampField;

    @FXML
    private TextField cyclesField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private CheckBox autonomousStatusCheckBox;

    @FXML
    private TextField matchNumberField;

    @FXML
    private Button createButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    public void initialize() {
        // Add a mouse click event handler to the root pane
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            root.requestFocus();  // This will take focus away from text fields
        });

        setButtonVisibility(false);
        setFieldAccessibility(false);
    }


    @FXML
    private void handleExitAction() {
        // Handle exit action here
    }

    public void clearFields() {
        teamNumberField.clear();
        autonomousPointsField.clear();
        ampField.clear();
        cyclesField.clear();
        descriptionField.clear();
        autonomousStatusCheckBox.setSelected(false);
        matchNumberField.clear();
    }

    private void setFieldAccessibility(boolean enabled) {
        // Enable or disable all fields based on the boolean value 'enabled'
        teamNumberField.setDisable(!enabled);
        autonomousPointsField.setDisable(!enabled);
        ampField.setDisable(!enabled);
        cyclesField.setDisable(!enabled);
        descriptionField.setDisable(!enabled);
        autonomousStatusCheckBox.setDisable(!enabled);
        matchNumberField.setDisable(!enabled);
    }

    private void setButtonVisibility(boolean visible) {
        // Set visibility of CRUD buttons
        createButton.setVisible(visible);
        updateButton.setVisible(visible);
        deleteButton.setVisible(visible);

        // Set visibility of navigation buttons
        FirstButton.setVisible(visible);
        doublePrevious.setVisible(visible);
        previous.setVisible(visible);
        next.setVisible(visible);
        doubleNext.setVisible(visible);
        LastButton.setVisible(visible);
    }


    public void loadMatchData() {
        try {
            ResultSet rs = db.Read();
            MatchList.clear();
            while (rs.next()) {
                Map<String, Object> MatchData = new HashMap<>();
                MatchData.put("id", rs.getInt("id"));
                MatchData.put("team", rs.getInt("team"));
                MatchData.put("auton_points", rs.getInt("auton_points"));
                MatchData.put("amp", rs.getInt("amp"));
                MatchData.put("cycles", rs.getInt("cycles"));
                MatchData.put("description", rs.getString("description"));
                MatchData.put("auton_status", rs.getInt("auton_status") == 1); // Store as Boolean
                MatchData.put("match_number", rs.getInt("match_number"));
                MatchList.add(MatchData);
            }

            if (!MatchList.isEmpty()) {
                displayMatchData();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public enum NavigationAction {
        FIRST, PREVIOUS, NEXT, DOUBLE_PREVIOUS, DOUBLE_NEXT, LAST
    }

    @FXML
    protected void navigateRecords(NavigationAction action, int offset) {
        switch (action) {
            case FIRST:
                currentMatchIndex = 0;
                break;
            case LAST:
                currentMatchIndex = MatchList.size() - 1;
                break;
            case PREVIOUS:
                currentMatchIndex = Math.max(0, currentMatchIndex - 1);
                break;
            case NEXT:
                currentMatchIndex = Math.min(MatchList.size() - 1, currentMatchIndex + 1);
                break;
            case DOUBLE_PREVIOUS:
                currentMatchIndex = Math.max(0, currentMatchIndex - 2);
                break;
            case DOUBLE_NEXT:
                currentMatchIndex = Math.min(MatchList.size() - 1, currentMatchIndex + 2);
                break;
        }
        displayMatchData();
    }



    public void displayMatchData() {
        if (currentMatchIndex>=0 && currentMatchIndex < MatchList.size()) {
            Map<String, Object> currentMatch = MatchList.get(currentMatchIndex);
            matchIdVal = (Integer) currentMatch.get("id");
            teamNumberField.setText(String.valueOf(currentMatch.get("team")));
            autonomousPointsField.setText(String.valueOf(currentMatch.get("auton_points")));
            ampField.setText(String.valueOf(currentMatch.get("amp")));
            cyclesField.setText(String.valueOf(currentMatch.get("cycles")));
            descriptionField.setText(String.valueOf(currentMatch.get("description")));
            autonomousStatusCheckBox.setSelected((Boolean) currentMatch.get("auton_status"));
            matchNumberField.setText(String.valueOf(currentMatch.get("match_number")));
        } else {
            clearFields();
        }
    }


    @FXML
    protected void onConnectClick() {
        if (db.isConnected()) {
            // Disconnect from the database
            db.Disconnect();
            connect.setText("Connect"); // Update the connection button text
            clearFields(); // Clear all input fields
            currentMatchIndex = 0; // Reset the current index
            setButtonVisibility(false); // Hide all action buttons
            setFieldAccessibility(false); // Disable all input fields
            isCreating = false; // Ensure the application is not in creation mode

        } else {
            // Connect to the database
            db.Connect();
            connect.setText("Disconnect"); // Update the connection button text

            loadMatchData(); // Load or refresh the match data from the database
            setButtonVisibility(true); // Show all action buttons
            setFieldAccessibility(false); // Enable all input fields
        }
    }

    @FXML
    private void onCreateButtonClicked() {
        if (isCreating) {
            // User is currently creating a new match, process the form data
            try {
                int teamNumber = Integer.parseInt(teamNumberField.getText());
                int autonomousPoints = Integer.parseInt(autonomousPointsField.getText());
                int amp = Integer.parseInt(ampField.getText());
                int cycles = Integer.parseInt(cyclesField.getText());
                int matchNumber = Integer.parseInt(matchNumberField.getText());
                String description = descriptionField.getText();
                int autonomousStatus = autonomousStatusCheckBox.isSelected() ? 1 : 0;

                // Call to database method to create a new match entry
                db.Create(teamNumber, autonomousPoints, amp, cycles, description, autonomousStatus, matchNumber);

                clearFields(); // Clear the fields after submission
                createButton.setText("Create"); // Reset button text to "Create"
                isCreating = false; // Reset the flag
                loadMatchData(); // Reload the match data from database
                currentMatchIndex = MatchList.size() - 1; // Update current index to new match
                displayMatchData(); // Display newly created match

                // Show a confirmation message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Creation Successful");
                alert.setHeaderText(null);
                alert.setContentText("New match data has been successfully created.");
                alert.showAndWait();
            } catch (NumberFormatException e) {
                // Show error message if the input format is invalid
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Input Format Error");
                errorAlert.setContentText("Please enter valid numbers in the fields.");
                errorAlert.showAndWait();
            }
        } else {
            // User is not currently creating a match, prepare UI for match data entry
            clearFields(); // Clear any existing data in the fields

            createButton.setText("Submit"); // Change button text to "Submit" to indicate action
            isCreating = true; // Set the flag to indicate creation mode is active
        }
    }



    @FXML
    private void onUpdateButtonClicked() {
        if (isCreating) {
            // User is currently updating a match, process the form data
            try {
                int teamNumber = Integer.parseInt(teamNumberField.getText());
                int autonomousPoints = Integer.parseInt(autonomousPointsField.getText());
                int amp = Integer.parseInt(ampField.getText());
                int cycles = Integer.parseInt(cyclesField.getText());
                int matchNumber = Integer.parseInt(matchNumberField.getText());
                String description = descriptionField.getText();
                int autonomousStatus = autonomousStatusCheckBox.isSelected() ? 1 : 0;

                // Call to database method to update the match entry
                db.Update(matchIdVal, teamNumber, autonomousPoints, amp, cycles, description, autonomousStatus, matchNumber);

                loadMatchData(); // Reload the match data from database
                displayMatchData(); // Display updated match data

                // Show a pop-up message indicating the data has been updated
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("The match has been updated!");
                alert.showAndWait();

                updateButton.setText("Update"); // Reset button text to "Update"
                isCreating = false; // Reset the flag
                setFieldAccessibility(false); // Disable all input fields
            } catch (NumberFormatException e) {
                // Handle parsing errors here if input is invalid
                System.out.println("Invalid input format.");
            }
        } else {
            updateButton.setText("Submit"); // Change button text to "Submit" to indicate action
            isCreating = true; // Set the flag to indicate update mode is active
            setFieldAccessibility(true); // Enable all input fields
        }
    }



    @FXML
    protected void onDeleteButtonClicked() {
        // Create a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Delete Match");
        alert.setContentText(String.format("Are you sure you want to delete the match with ID: %d?", matchIdVal));

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If the user clicked OK, delete the match from the database

            db.Delete(matchIdVal);

            loadMatchData();

            // Check if currentMatchIndex is still a valid index
            if (currentMatchIndex >= MatchList.size()) {
                currentMatchIndex --;

            }

            // Display the current match data
            displayMatchData();
        }
    }



    public MenuItem getConnect() {
        return connect;
    }

    public void setConnect(MenuItem connect) {
        this.connect = connect;
    }

    public TextField getTeamNumberField() {
        return teamNumberField;
    }

    public void setTeamNumberField(TextField teamNumberField) {
        this.teamNumberField = teamNumberField;
    }

    public TextField getAutonomousPointsField() {
        return autonomousPointsField;
    }

    public void setAutonomousPointsField(TextField autonomousPointsField) {
        this.autonomousPointsField = autonomousPointsField;
    }

    public TextField getAmpField() {
        return ampField;
    }

    public void setAmpField(TextField ampField) {
        this.ampField = ampField;
    }

    public TextField getCyclesField() {
        return cyclesField;
    }

    public void setCyclesField(TextField cyclesField) {
        this.cyclesField = cyclesField;
    }

    public TextArea getDescriptionField() {
        return descriptionField;
    }

    public void setDescriptionField(TextArea descriptionField) {
        this.descriptionField = descriptionField;
    }

    public CheckBox getAutonomousStatusCheckBox() {
        return autonomousStatusCheckBox;
    }

    public void setAutonomousStatusCheckBox(CheckBox autonomousStatusCheckBox) {
        this.autonomousStatusCheckBox = autonomousStatusCheckBox;
    }

    public TextField getMatchNumberField() {
        return matchNumberField;
    }

    public void setMatchNumberField(TextField matchNumberField) {
        this.matchNumberField = matchNumberField;
    }

    public Button getCreateButton() {
        return createButton;
    }

    public void setCreateButton(Button createButton) {
        this.createButton = createButton;
    }

    public Button getUpdateButton() {
        return updateButton;
    }

    public void setUpdateButton(Button updateButton) {
        this.updateButton = updateButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }

    @FXML
    private void handleFirst() {
        navigateRecords(NavigationAction.FIRST, 0);
    }

    @FXML
    private void handleLast() {
        navigateRecords(NavigationAction.LAST, 0);
    }

    @FXML
    private void handleNext() {
        navigateRecords(NavigationAction.NEXT, 0);
    }

    @FXML
    private void handlePrevious() {
        navigateRecords(NavigationAction.PREVIOUS, 0);
    }

    @FXML
    private void handleDoubleNext() {
        navigateRecords(NavigationAction.DOUBLE_NEXT, 0);
    }

    @FXML
    private void handleDoublePrevious() {
        navigateRecords(NavigationAction.DOUBLE_PREVIOUS, 0);
    }

    @FXML
    private void handleMouseClicked(MouseEvent event) {
        root.requestFocus();
    }


}