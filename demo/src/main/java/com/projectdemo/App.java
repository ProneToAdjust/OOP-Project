package com.projectdemo;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import javafx.application.Platform;

public class App extends Application {

    private Controller controller;

    public App() {
		this.controller = new Controller();
	}

    @Override
    public void start(Stage primaryStage) throws Exception {
        App app = new App();
		Bank bank = DataStorage.loadDatabase();
        boolean showQuit = false;
		
		// continue looping forever
		do {
			// stay in login prompt until successful login
			app.loginMenu(bank);
			// stay in main menu until user quits
			showQuit = app.mainMenu(bank, primaryStage);
        } while (!showQuit);
}

private int selectAccountMenu() {
    //label for user accounts summary
    Label summaryLabel = new Label(controller.getSummary());
    int numOfAccounts = controller.getNumberOfAccounts();
    final int[] selectedAcc = {-1};
    //initialise choicebox to select accounts
    ChoiceBox<String> accountChoiceBox = new ChoiceBox<>();
    for (int i = 1; i <= numOfAccounts; i++) {
    //append choicebox with accounts user have
        accountChoiceBox.getItems().add(String.valueOf(i));
    }

    Button selectButton = new Button("Select");
    selectButton.setOnAction(event -> {
        try {
            //once selected, retrieve the index of the bank account selected
            //that index will be return from the function
            int selectedIndex = accountChoiceBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                selectedAcc[0] = selectedIndex;
                Scene scene = selectButton.getScene();
                if (scene != null) {
                    Stage stage = (Stage) scene.getWindow();
                    stage.close();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an account.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid choice. Please select an account number.");
            alert.showAndWait();
        }
    });

    //initialise the vbox for formatting
    //the scene and the stage for the javafx gui
    VBox vBox = new VBox(summaryLabel, new Label(String.format("Select an account number (1-%d):", numOfAccounts)), accountChoiceBox, selectButton);
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(10);
    Scene scene = new Scene(vBox, 300, 200);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.showAndWait();
    // add the following code to prevent stage from closing if no account selected
    stage.setOnCloseRequest(event -> {
        if (accountChoiceBox.getSelectionModel().isEmpty()) {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an account.");
            alert.showAndWait();
        }
    });
    return selectedAcc[0];
}

    private void loginMenu(Bank theBank) {
    
        // create UI components
        Label bankNameLabel = new Label("Welcome to " + theBank.getName());
        bankNameLabel.setFont(new Font("Segoe Script", 24));
        Label userIDLabel = new Label("User ID:");
        userIDLabel.setFont(new Font("Segoe Script", 18));
        Label pinLabel = new Label("Pin:");
        pinLabel.setFont(new Font("Segoe Script", 18));
        TextField userIDField = new TextField();
        PasswordField pinField = new PasswordField();
        Button loginButton = new Button("Login");
        Label errorLabel = new Label();
    
        // layout UI components
        VBox root = new VBox(10, bankNameLabel, userIDLabel, userIDField, pinLabel, pinField, loginButton, errorLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setBackground(new Background(new BackgroundFill(new RadialGradient(0, 0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#81c483")),
                    new Stop(1, Color.web("#fcc200"))),
            CornerRadii.EMPTY, Insets.EMPTY)));
    
        // create scene and stage
        Scene scene = new Scene(root, 300, 300);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Login");
    
        // set action for login button
        loginButton.setOnAction(event -> {
            final String userID = userIDField.getText();
            final String pin = pinField.getText();
    
            final Boolean loginResult = controller.loginUser(theBank, userID, pin);
            if (loginResult) {
                stage.close();
            } else {
                errorLabel.setText("Incorrect user ID/pin combination. Please try again.");
            }
        });

        stage.setOnCloseRequest(event -> {
            event.consume();
            stage.close();
        });
    
        // show stage
        stage.showAndWait();
    }

    private boolean mainMenu(Bank theBank, Stage primaryStage) {

        // initialise all the image views for the buttons
        Image imgView = new Image(getClass().getResourceAsStream("info.png"));
        ImageView viewView = new ImageView(imgView);
        viewView.setFitHeight(40);
        viewView.setPreserveRatio(true);

        Image imgDep = new Image(getClass().getResourceAsStream("deposit.png"));
        ImageView viewDep = new ImageView(imgDep);
        viewDep.setFitHeight(40);
        viewDep.setPreserveRatio(true);

        Image imgQuit = new Image(getClass().getResourceAsStream("quit.png"));
        ImageView viewQuit = new ImageView(imgQuit);
        viewQuit.setFitHeight(40);
        viewQuit.setPreserveRatio(true);

        Image imgSettings = new Image(getClass().getResourceAsStream("settings.png"));
        ImageView viewSettings = new ImageView(imgSettings);
        viewSettings.setFitHeight(40);
        viewSettings.setPreserveRatio(true);

        Image imgTransfer = new Image(getClass().getResourceAsStream("transfer.png"));
        ImageView viewTransfer = new ImageView(imgTransfer);
        viewTransfer.setFitHeight(40);
        viewTransfer.setPreserveRatio(true);

        Image imgWithdraw = new Image(getClass().getResourceAsStream("withdraw.png"));
        ImageView viewWithdraw = new ImageView(imgWithdraw);
        viewWithdraw.setFitHeight(40);
        viewWithdraw.setPreserveRatio(true);

        // init
        // initialising of the button and linking of the imageviews
        // creating of HBox and VBox to sort out the formatting
        AtomicBoolean isClosed = new AtomicBoolean(false);
        Label greetingLabel = new Label("Welcome, " + controller.getUserName());
        greetingLabel.setFont(new Font("Segoe Script", 26));
        Button showInfoButton = new Button("Show Account information");
        showInfoButton.setGraphic(viewView);
        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setGraphic(viewWithdraw);
        Button depositButton = new Button("Deposit");
        depositButton.setGraphic(viewDep);
        Button transferButton = new Button("Transfer");
        transferButton.setGraphic(viewTransfer);
        Button settingsButton = new Button("Settings");
        settingsButton.setGraphic(viewSettings);
        Button logoutButton = new Button("Log out");
        logoutButton.setGraphic(viewQuit);
        // top half of the gui
        HBox menuButtonBox1 = new HBox(10, showInfoButton, withdrawButton, depositButton);
        // bottom half of the gui
        HBox menuButtonBox2 = new HBox(10, transferButton, settingsButton, logoutButton);
        // putting them together
        VBox menuBox = new VBox(10, greetingLabel, menuButtonBox1, menuButtonBox2);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-background-color: #ACD1AD;"); 
        menuButtonBox1.setAlignment(Pos.CENTER);
        menuButtonBox2.setAlignment(Pos.CENTER);
    
        // Set the min and max sizes for the buttons
        showInfoButton.setMinSize(100, 20);
        showInfoButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        withdrawButton.setMinSize(100, 20);
        withdrawButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        depositButton.setMinSize(100, 20);
        depositButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        transferButton.setMinSize(100, 20);
        transferButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        settingsButton.setMinSize(100, 20);
        settingsButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        logoutButton.setMinSize(100, 20);
        logoutButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // user menu
        showInfoButton.setOnAction(event -> showAccountInformationMenu(primaryStage));
        withdrawButton.setOnAction(event -> withdrawFundsMenu(primaryStage));
        depositButton.setOnAction(event -> depositFundsMenu(primaryStage));
        transferButton.setOnAction(event -> transferFundsMenu(theBank));
        settingsButton.setOnAction(event -> settingsMenu());
        logoutButton.setOnAction(event -> {
            System.out.println("Successfully logged out");
            DataStorage.saveDatabase(theBank);
            isClosed.set(true);
            primaryStage.close();
            Platform.exit();
        });
    
        // create scene and window/stage
        Scene scene = new Scene(menuBox, 500, 380);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Main Menu");
        stage.showAndWait();
        //supposedly close the gui but it's not rly working??
        stage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.close();
        });

        if (isClosed.get()) {
            
            primaryStage.close();
        }

        return isClosed.get();
    }

    private void showAccountInformationMenu(Stage primaryStage) {
        //label for user accounts summary
        Label summaryLabel = new Label(controller.getSummary());
        //create a new window
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Account Information");
    
        Label label = new Label("Do you want to show the account transaction history for the account?");
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
    
        yesButton.setOnAction(e -> {
            // transit to transaction history
            int accountChoice = selectAccountMenu();
            transactionHistoryMenu(accountChoice, primaryStage);
            dialog.close();
        });
    
        noButton.setOnAction(e -> {
            // exit the window
            System.out.println("\nExiting Account Information\n");
            dialog.close();
        });
        // layout of the UI
        VBox vbox = new VBox(20, summaryLabel, label, yesButton, noButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
    
        Scene dialogScene = new Scene(vbox, 400, 200);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void transactionHistoryMenu(int account, Stage primaryStage) {
        // print the transaction history
        ArrayList<String> transHistories = controller.getTransactionHistory(account);
    
        StringBuilder sb = new StringBuilder("Transaction history:\n");
        for (String transSummary : transHistories) {
            sb.append(transSummary).append("\n");
        }
    
        Label transactionHistoryLabel = new Label(sb.toString());
        transactionHistoryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        transactionHistoryLabel.setWrapText(true);
        transactionHistoryLabel.setTextAlignment(TextAlignment.LEFT);
    
        // Create a VBox to hold the label
        VBox vbox = new VBox(transactionHistoryLabel);
        vbox.setStyle("-fx-background-color: #ACD1AD;"); 
        
        vbox.setAlignment(Pos.CENTER);
    
        // Create a new scene and display it on the primary stage
        Scene transactionHistoryScene = new Scene(vbox, 400, 300);
        primaryStage.setScene(transactionHistoryScene);
        primaryStage.show();
    }

    private void withdrawFundsMenu(Stage primaryStage) {
        int selectedAcc;
        // initialise a final value that is retrievable in the ActionListener
        final double amount[] = {-1};
        double accountBal;
        double withdrawalLimit;
    
        //create labels
        Label withdrawFundsLabel = new Label("Withdraw funds");
        withdrawFundsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    
        Label summaryLabel = new Label(controller.getSummary());
        summaryLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    
        // get account to withdraw from
        selectedAcc = selectAccountMenu();
    
        accountBal = controller.getAccountBalance(selectedAcc);
        withdrawalLimit = controller.getWithdrawalLimit();
        
        // show acc balance
        Label accountBalLabel = new Label(String.format("Account Balance: $%.2f", accountBal));
        accountBalLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        // show withdrawal limit
        Label withdrawalLimitLabel = new Label(String.format("Withdrawal Limit: $%.2f", withdrawalLimit));
        withdrawalLimitLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    
        Label amountLabel = new Label("Enter the amount to withdraw");
        amountLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
    
        TextField amountField = new TextField();
        amountField.setMaxWidth(150);
    
        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        withdrawButton.setOnAction(e -> {
            try {
                amount[0] = Double.parseDouble(amountField.getText());
                if (amount[0] < 0) {
                    showAlert("Amount must be greater than zero.");
                } else if (amount[0] > accountBal) {
                    showAlert("Amount must not be greater than balance of $" + accountBal);
                } else if (amount[0] > withdrawalLimit) {
                    showAlert("Amount must not be greater than withdrawal limit of $" + withdrawalLimit);
                } else {
                    if (amount[0] != 0) {
                        controller.withdrawFunds(selectedAcc, amount[0], "Withdrawal");
                        // Pause the timeline for 5 seconds (5000 milliseconds)
                        PauseTransition delay = new PauseTransition(Duration.millis(5000));
                            delay.setOnFinished(event -> {
                                // Once the delay is complete, show an alert that the withdrawal is successful
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setHeaderText("Amount withdrawn successfully.");
                                alert.show();
                                primaryStage.close();
                            });

                            // Call the loadingPage method outside the PauseTransition block
                            loadingPage(primaryStage);

                            delay.play(); // Start the PauseTransition delay 
                    }
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid input. Please enter a valid number.");
            }
        });
    
        // create a VBox to hold the label and input fields
        VBox vbox = new VBox(withdrawFundsLabel, summaryLabel, accountBalLabel, withdrawalLimitLabel, amountLabel, amountField, withdrawButton);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #ACD1AD;"); 
    
        // create a new scene and display it on the primary stage
        Scene withdrawFundsScene = new Scene(vbox, 400, 300);
        primaryStage.setScene(withdrawFundsScene);
        primaryStage.show();
    }
    
    // Method to show an alert dialog with a given message
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void depositFundsMenu(Stage primaryStage) {
        int selectedAcc;
        AmountWrapper amountWrapper = new AmountWrapper();
    
        Label headerLabel = new Label("Deposit funds");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        headerLabel.setPadding(new Insets(20, 0, 10, 0));
    
        Label summaryLabel = new Label(controller.getSummary());
        summaryLabel.setPadding(new Insets(0, 0, 10, 0));
    
        // get account to deposit to
        selectedAcc = selectAccountMenu();
    
        // get amount to deposit
        Label amountLabel = new Label("Enter the amount to deposit: $");
        TextField amountTextField = new TextField();
        amountTextField.setPromptText("0.00");
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            try {
                amountWrapper.setAmount(Double.parseDouble(amountTextField.getText()));
                double amount = amountWrapper.getAmount();
                if (amount < 0) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setHeaderText("Invalid Amount");
                    alert.setContentText("Amount must be greater than zero.");
                    alert.showAndWait();
                } else {
                    controller.depositFunds(selectedAcc, amount, "Deposit");
                    // Pause the timeline for 5 seconds (5000 milliseconds)
                    PauseTransition delay = new PauseTransition(Duration.millis(5000));
                    delay.setOnFinished(event2 -> {
                        // Once the delay is complete, show an alert that the withdrawal is successful
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setHeaderText("Deposit Successful");
                        alert.setContentText(String.format("Amount $%.02f deposited successfully to account.", amount));
                        alert.show();
                        primaryStage.close();
                    });

                    // Call the loadingPage method outside the PauseTransition block
                    loadingPage(primaryStage);

                    delay.play(); // Start the PauseTransition delay 
                    
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setHeaderText("Invalid Amount");
                alert.setContentText("Please enter a valid number for the amount.");
                alert.showAndWait();
            }
        });
    
        // Create a VBox to hold the UI components
        VBox vbox = new VBox(headerLabel, summaryLabel, amountLabel, amountTextField, submitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ACD1AD;"); 
    
        // Create a new scene and display it on a new stage
        Scene depositFundsScene = new Scene(vbox, 400, 300);
        primaryStage.setScene(depositFundsScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.close();
        });
    }

    private void transferFundsMenu(Bank theBank) {
        int fromAcc;
        int toAcc;
        int typeTransfer;
        double transferAmt = -1;
        double acctBal;
        double transferLimit;
    
        System.out.println("Transfer funds");
        typeTransfer = getTypeTransfer();
    
        // get account to transfer from
        System.out.println("Account to transfer from:");
        fromAcc = selectAccountMenu();
        acctBal = controller.getAccountBalance(fromAcc);
    
        switch (typeTransfer) {
            case 1: //internal transfer
                // get account to transfer to
                System.out.println("Account to transfer to");
                toAcc = selectAccountMenu();
    
                transferLimit = controller.getTransferLimit();
                break;
            case 2: //external transfer
                // get account to transfer to
                ActionEvent event = new ActionEvent();
                toAcc = selectExtAccountMenu(theBank, event);
                transferLimit = controller.getExternalTransferLimit();
                break;
            default:
                throw new IllegalAccessError("int typeTransfer was invalid");
        }
    
        // get amount to transfer
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Transfer Amount");
        dialog.setHeaderText(controller.getSummary());
        dialog.setContentText(String.format("Enter the amount to transfer (max $%.02f): $", transferLimit));
    
        while (true) {
            Optional<String> result = dialog.showAndWait();
    
            if (result.isPresent()) {
                try {
                    transferAmt = Double.parseDouble(result.get());
                    if (transferAmt < 0)
                        System.out.println("Amount must be greater than zero.");
                    else if (transferAmt > acctBal)
                        System.out.printf("Amount must not be greater than balance " +
                                "of $%.2f.\n", acctBal);
                    else if (transferAmt > transferLimit)
                        System.out.printf("Amount must not be greater than transfer limit " +
                                "of $%.2f.\n", transferLimit);
                    else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid choice. Please input a number.");
                }
            } else {
                return;
            }
        }
    
        if (transferAmt != 0) {
            switch (typeTransfer) {
                case 1: //internal transfer
                    controller.transferFunds(fromAcc, toAcc, transferAmt);
                    break;
                case 2: //external transfer
                    controller.transferExtFunds(fromAcc, toAcc, transferAmt, theBank);
                    break;
                default:
                    throw new IllegalAccessError("int typeTransfer was invalid");
            }
        }
    }

    private int getTypeTransfer() {
        int typeTransfer = -1;
    
        // Create radio buttons for each transfer type
        RadioButton internalTransferRadio = new RadioButton("Internal");
        RadioButton externalTransferRadio = new RadioButton("External (to 3rd party accounts)");
        ToggleGroup transferTypeGroup = new ToggleGroup();
        internalTransferRadio.setToggleGroup(transferTypeGroup);
        externalTransferRadio.setToggleGroup(transferTypeGroup);
    
        // Create a VBox to hold the radio buttons
        VBox radioBox = new VBox();
        radioBox.setStyle("-fx-background-color: #ACD1AD;"); 
        radioBox.getChildren().addAll(internalTransferRadio, externalTransferRadio);
    
        // Create a dialog box to prompt the user for the transfer type
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Choose Transfer Type");
        dialog.getDialogPane().setContent(radioBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
        // Loop until the user selects a valid transfer type or cancels
        while (typeTransfer < 1 || typeTransfer > 2) {
            Optional<?> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Get the selected transfer type
                if (internalTransferRadio.isSelected()) {
                    typeTransfer = 1;
                } else if (externalTransferRadio.isSelected()) {
                    typeTransfer = 2;
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            } else {
                // User canceled the dialog
                break;
            }
        }
    
        return typeTransfer;
    }

    private int selectExtAccountMenu(Bank theBank, ActionEvent event) {
        
        String uuid = "";
        int selectedBankIndex;
        do {
            controller.checkAccUuidList(theBank);
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Account UUID");
            dialog.setHeaderText("Enter the account UUID that you want to transfer to:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                uuid = result.get();
                if (uuid.length() != 10) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Account UUID must have 10 characters. Please try again.");
                    alert.showAndWait();
                }
            }
        } while (uuid.length() != 10);
    
        if (controller.getSelectedBankIndex(theBank, uuid) == -1) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please try again!");
            alert.showAndWait();
            selectExtAccountMenu(theBank, event);
        }
    
        return selectedBankIndex = controller.getSelectedBankIndex(theBank, uuid);
        // do something with selectedBankIndex
    }

    private void settingsMenu() {
        String[] choices = {"Transfer limit", "External transfer limit", "Withdrawal limit", "Change Pin No."};
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices[0], choices);
        dialog.setTitle("Settings");
        dialog.setHeaderText(null);
        dialog.setContentText("Select an option:");
    
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String choice = result.get();
            switch (choice) {
                case "Transfer limit":
                    changeLimitMenu("transfer");
                    break;
                case "External transfer limit":
                    changeLimitMenu("external transfer");
                    break;
                case "Withdrawal limit":
                    changeLimitMenu("withdrawal");
                    break;
                case "Change Pin No.":
                    changePinNoMenu();
                    break;
                default:
                    throw new IllegalAccessError("String choice was invalid");
            }
        }
    }

    private void changeLimitMenu(String limitType) {
        final double amount[] = {-1};
        Label titleLabel = new Label("Enter new " + limitType + " limit: $");
        Label errorLabel = new Label("Amount must be greater than zero.");
        TextField amountTextField = new TextField();
        amountTextField.setPromptText("Enter amount");
    
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            try {
                amount[0] = Double.parseDouble(amountTextField.getText());
                if (amount[0] <= 0) {
                    errorLabel.setVisible(true);
                } else {
                    errorLabel.setVisible(false);
                    switch (limitType) {
                        case "transfer":
                            controller.changeTransferLimit(amount[0]);
                            break;
                        case "external transfer":
                            controller.changeExternalTransferLimit(amount[0]);
                            break;
                        case "withdrawal":
                            controller.changeWithdrawalLimit(amount[0]);
                            break;
                        default:
                            throw new IllegalAccessError("String limitType was invalid");
                    }
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText("Limit updated successfully");
                    alert.setContentText(String.format("New " + limitType + " limit has been set to $%.2f", amount[0]));
                    alert.showAndWait();
                    Stage stage = (Stage) okButton.getScene().getWindow();
                    stage.close();
                }
            } catch (NumberFormatException ex) {
                errorLabel.setVisible(true);
            }
        });
    
        VBox vbox = new VBox(10, titleLabel, amountTextField, errorLabel, okButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        errorLabel.setVisible(false);
    
        Scene scene = new Scene(vbox);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Change Limit");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void changePinNoMenu() {
        TextInputDialog currentPinDialog = new TextInputDialog();
        currentPinDialog.setTitle("Change Pin No.");
        currentPinDialog.setHeaderText(null);
        currentPinDialog.setContentText("Please enter the current pin: ");
        Optional<String> currentPinResult = currentPinDialog.showAndWait();

        if (currentPinResult.isPresent()) {
            String currentPin = currentPinResult.get();

            TextInputDialog newPinDialog = new TextInputDialog();
            newPinDialog.setTitle("Change Pin No.");
            newPinDialog.setHeaderText(null);
            newPinDialog.setContentText("Please enter the new pin: ");
            Optional<String> newPinResult = newPinDialog.showAndWait();

            if (newPinResult.isPresent()) {
                String newPin = newPinResult.get();

                TextInputDialog rePinDialog = new TextInputDialog();
                rePinDialog.setTitle("Change Pin No.");
                rePinDialog.setHeaderText(null);
                rePinDialog.setContentText("Please re-enter the new pin: ");
                Optional<String> rePinResult = rePinDialog.showAndWait();

                if (rePinResult.isPresent()) {
                    String rePin = rePinResult.get();

                    if (!newPin.equals(rePin)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Change Pin No.");
                        alert.setHeaderText(null);
                        alert.setContentText("New pin does not match.");
                        alert.showAndWait();
                    } else if (newPin.isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Change Pin No.");
                        alert.setHeaderText(null);
                        alert.setContentText("New pin cannot be empty.");
                        alert.showAndWait();
                    } else if (controller.validatePin(currentPin)) {
                        controller.changePin(newPin);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Change Pin No.");
                        alert.setHeaderText(null);
                        alert.setContentText("Pin has been changed.");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Change Pin No.");
                        alert.setHeaderText(null);
                        alert.setContentText("Invalid pin. Please try again.");
                        alert.showAndWait();
                    }

                    settingsMenu();
                }
            }
        }
    }

    private void loadingPage(Stage primaryStage) {
        // Create a label with the loading message
        Label loadingLabel = new Label("Loading...");
        Media sound = new Media(getClass().getResource("moneycountingsound.mp3").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
        Image gif = new Image(getClass().getResourceAsStream("moneycounting.gif"));
        ImageView gifView = new ImageView(gif);
        // Create a StackPane to center the label
        StackPane root = new StackPane();
        root.getChildren().add(loadingLabel);
        root.getChildren().add(gifView);

        // Create the loading scene
        Scene loadingScene = new Scene(root, 300, 200);

        // Set the loading scene as the primary stage scene
        primaryStage.setScene(loadingScene);
        primaryStage.setTitle("Loading...");

        // Show the primary stage
        primaryStage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}