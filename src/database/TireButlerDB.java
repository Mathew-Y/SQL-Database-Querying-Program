package database;

import java.io.File; // Import File class
import java.sql.*; // Import all classes in JDBC driver
import java.util.ArrayList; // Import ArrayList class
import java.io.PrintWriter; // Import PrintWriter class
import javafx.application.Application;  // Import Application class
import javafx.concurrent.Task; // Import Task class
import javafx.concurrent.WorkerStateEvent; // Import WorkerStateEvent class
import javafx.event.EventHandler; // Import EventHandler class
import javafx.geometry.HPos; // Import HPos class
import javafx.geometry.Insets; // Import Insets class
import javafx.geometry.Pos; // Import Pos class
import javafx.scene.*; // Import all javafx scene classes
import javafx.scene.control.Button; // Import Button class
import javafx.scene.control.Label; // Import Label class
import javafx.scene.control.PasswordField; // Import PasswordField class
import javafx.scene.control.TextField; // Import TextField class
import javafx.scene.layout.*; // Import all layout classes
import javafx.scene.paint.Color; // Import Color class
import javafx.stage.DirectoryChooser; // Import DirectoryChooser class
import javafx.stage.Modality; // Import Modality class
import javafx.stage.Stage; // Import Stage class
import javafx.scene.text.Font; // Import Font class
import java.time.*; // Import all java.time libraries

public class TireButlerDB extends Application
{ // Start of TireButlerDB class
    Stage window; // Creation of Stage class called window

    //Creation of all necessary buttons in the program
    Button closeButton1, closeButton2, addresses, appointmentVehicles, appointments, customHoursOperation, customTracks, emails,
            postalCodes, promoCodes, regions, services, settings, trucks, users, vehicles, regularSearch,
            dateSceneSearch, regularChangeDirectoryButton, dateSceneChangeDirectoryButton, financialSceneChangeDirectoryButton, searchDates, regularBackButton,
            dateSceneBackButton, financialReportBackButton, financialReportButton, dailyReportButton, financialSceneSearchDatesButton, dailySceneSearchButton, dailySceneDownloadDirectoryButton, dailySceneBackButton;

    String input; // String which will store the currently selected table
    BackgroundFill backgroundFill; // BackgroundFill will store the colour and other background details
    Scene loginScene, mainMenuScene, tableScene, dateTableScene, financialReportScene, dailyReportScene; // Creation of all necessary Scene variables in the program
    GridPane grid = new GridPane(); // Creation of main menu GridPane
    VBox layoutTableScene, layoutDateTableScene; // VBoxs which will store the layout of each scene

    // TextFields which will allow the user to type in information
    TextField serviceTextArea = new TextField();
    TextField dataBaseNameTextArea = new TextField();
    TextField regularTextArea = new TextField();
    TextField dateSceneTextArea = new TextField();
    TextField startDateTextArea = new TextField();
    TextField endDateTextArea = new TextField();
    TextField startDateFinancialTextArea = new TextField();
    TextField endDateFinancialTextArea = new TextField();

    Connection con; // Connection which will allow us to connect to our database

    // Creation of all necessary labels (Text) in the program
    Label loginMessage, loginValidityMessage, regularSearchMessage, dateSceneSearchMessage, dateSceneDateSearchMessage,
            financialSceneDateSearchMessage, regularDownloadValidityMessage, dateSceneRegularDownloadValidityMessage,
            dateSceneDateDownloadValidityMessage, financialSceneDownloadValidityMessage, dailySceneMessage, dailySceneDownloadValidityMessage;

    DirectoryChooser dirChooser = new DirectoryChooser(); // Creation of DirectoryChooser variable which will allow us to choose the directory to download to
    static String homePath = System.getProperty("user.home"); // String which will store the user's home path on their computer
    static File selectedDirectory = new File(homePath + "\\Downloads"); // File variable which will store the selected directory to download to, starts off in downloads folder

    // Storing all attributes of each table to refer to
    final String[] ADDRESSESCOLUMNS = {"id", "streetNumber", "streetName", "city", "province", "country", "postalCode",
            "unit", "userId", "address", "latitude", "longitude"};
    final String[] APPOINTMENTVEHICLESCOLUMNS = {"id", "appointmentId", "vehicleId", "vehicleName", "services",
            "underground", "wheelLockLocation", "odometer", "licence", "vin", "subtotal", "isPrimary", "serviceCostList"};
    final String[] APPOINTMENTSCOLUMNS = {"id", "userId", "regionId", "date", "address", "postalCode", "latitude",
            "longitude", "startTime", "duration", "cost", "tax", "promoDiscount", "vipDiscount", "driverId", "track",
            "notes", "paid", "invoiceAddress", "oneTimeCharge", "promoCode", "referenceNumber", "orderNumber", "paidNote", "paymentAuth", "reminderSent"};
    final String[] CUSTOMHOURSOPERATIONCOLUMNS = {"id", "date", "regionId", "startTime", "endTime"};
    final String[] CUSTOMTRACKSCOLUMNS = {"id", "date", "tracks", "regionId"};
    final String[] EMAILSCOLUMNS = {"id", "emails", "title", "body", "status", "date"};
    final String[] POSTALCODESCOLUMNS = {"id", "postalCode", "regionId"};
    final String[] PROMOCODESCOLUMNS = {"id", "code", "value"};
    final String[] REGIONSCOLUMNS = {"id", "name", "color", "enabled"};
    final String[] SERVICESCOLUMNS = {"id", "code", "name", "duration", "cost", "cost2", "enabled", "description", "image", "mobileFeeEnabled"};
    final String[] SETTINGSCOLUMNS = {"field", "value"};
    final String[] TRUCKSCOLUMNS = {"id", "number", "driverId", "notes"};
    final String[] USERSCOLUMNS = {"id", "email", "password", "firstname", "lastname", "phone", "role", "moneristoken", "changed", "color", "isVip", "adminOnlyNotes", "notes1", "notes2", "notes3", "referral"};
    final String[] VEHICLESCOLUMNS = {"id", "vin", "year", "make", "model", "odometer", "licence", "torque", "pressure", "data1", "data2", "data3", "data4", "userId"};

    String[] currentTableColumns; // Storing the currently selected table's columns
    static String currentDate; // String which will store the current date

    public static void main(String[] args) throws Exception
    { // Start of main method
        launch(args); // Launch all arguments from start
    } // End of main method

    @Override
    public void start(Stage stage) throws Exception
    { // Start of start function
        currentDate = LocalDate.now().toString(); // Store today's date in currentDate variable
        dailySceneDownloadValidityMessage = new Label(""); // Initialize our dailySceneDownloadValidityMessage label, set to empty text
        financialSceneDownloadValidityMessage = new Label(""); // Initialize our financialSceneDownloadValidityMessage label, set to empty text
        serviceTextArea.setPromptText("Service"); // Set the prompt text of the service textfield
        dataBaseNameTextArea.setPromptText("Database"); // Set the prompt text of the database textfield
        startDateFinancialTextArea.setPromptText("Enter the start date (Ex: 2017-04-21)"); // Set the prompt text of the startDate textfield
        endDateFinancialTextArea.setPromptText("Enter the end date (Ex: 2017-04-21)"); // Set the prompt text of the endDate textfield
        financialSceneSearchDatesButton = new Button("Search Between Dates"); // Initialize the financialSceneSearchDatesButton button, set to "Search Between Dates"
        financialReportBackButton = new Button("Back"); // Initialize our back button to say "Back"
        financialSceneChangeDirectoryButton = new Button("Change Download Directory"); // Set our change download directory button to have the required text
        financialReportButton = new Button("Financial Report"); // Set the text for our financial report button to financial report
        dailySceneSearchButton = new Button("Search"); // Make our search button say search
        dailySceneDownloadDirectoryButton = new Button("Change Download Directory"); // Make our change download directory button say change download directory
        dailyReportButton = new Button("Daily Report"); // Make our daily report button say daily report
        dailySceneBackButton = new Button("Back"); // Make our back button say back
        closeButton1 = new Button("Close Program"); // Make our close program button say close program
        loginMessage = new Label("Login to MySQL localhost service"); // Set the label of our login screen to say Login to MySQL localhost service
        regularDownloadValidityMessage = new Label(""); // Set the download validity label to empty text
        dateSceneRegularDownloadValidityMessage = new Label(""); // Set the download validity label to empty text
        dateSceneDateDownloadValidityMessage = new Label(""); // Set the download validity label to empty text
        loginValidityMessage = new Label(""); // Set the login validity label to empty text
        GridPane loginGrid = new GridPane(); // Creation of our login GridPane
        loginGrid.setPadding(new Insets(10, 10, 10, 10)); // Set the padding of our login GridPane to be 10 from all sides
        loginGrid.setVgap(8); // Set the vertical gap of our loginGrid to 8
        loginGrid.setHgap(10); // Set the horizontal gap of our loginGrid to 10

        //Setting constraints of our loginGrid (Formatting)
        GridPane.setConstraints(loginMessage, 0, 0);
        Label serviceLabel = new Label("Service (Default: localhost:3306): ");
        GridPane.setConstraints(serviceLabel, 0, 4);
        GridPane.setConstraints(serviceTextArea, 1, 4);
        Label dataBaseLabel = new Label("Database name: ");
        GridPane.setConstraints(dataBaseLabel, 0, 5);
        GridPane.setConstraints(dataBaseNameTextArea, 1, 5);
        Label nameLabel = new Label("Username (Default: root): ");
        GridPane.setConstraints(nameLabel, 0, 6);
        TextField nameInput = new TextField();
        nameInput.setPromptText("Username");
        GridPane.setConstraints(nameInput, 1, 6);
        Label passLabel = new Label("Password: ");
        GridPane.setConstraints(passLabel, 0, 7);
        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Password");
        GridPane.setConstraints(passInput, 1, 7);
        Button loginButton = new Button("Log in");
        GridPane.setConstraints(loginButton, 1, 9);
        GridPane.setConstraints(loginValidityMessage, 1, 10);
        GridPane.setConstraints(closeButton1, 0, 17);

        // Setting all the children of our loginGrid
        loginGrid.getChildren().addAll(loginMessage, serviceLabel, serviceTextArea, dataBaseLabel, dataBaseNameTextArea, nameLabel, nameInput, passLabel, passInput, loginButton, loginValidityMessage, closeButton1);
        GridPane.setHalignment(loginMessage, HPos.RIGHT);

        loginGrid.setStyle("-fx-background-color:#CCE5FF; -fx-opacity:1;"); // Set the background colour
        loginScene = new Scene(loginGrid, 600, 600); // Creating a new scene. passing in our loginGrid

        loginButton.setOnAction(e ->
        { // Upon login button press, execute all of this code
            try
            { // Start of try block
                con = getConnection(serviceTextArea.getText(), dataBaseNameTextArea.getText(), nameInput.getText(), passInput.getText()); // Attempt to get connection using user entered parameters
                if (con == null)
                { // Start of if block
                    loginValidityMessage.setText(""); // Set the loginvaliditytext to be empty

                    Task<Void> sleeper = new Task<Void>()
                    { // Start of sleeper parameters
                        @Override
                        protected Void call() throws Exception
                        { // Start of call function
                            try
                            { // Start of try block
                                Thread.sleep(100); // Sleep the program for 100 ms
                            } // End of try block
                            catch (InterruptedException ignored)
                            { // Start of catch block
                            } // End of catch block
                            return null; // Return null back to caller of sleeper
                        } // End of call function
                    }; // End of sleeper parameters

                    sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>()
                    { // Start of sleeper's successful execution
                        @Override
                        public void handle(WorkerStateEvent workerStateEvent)
                        { // Start of handle function
                            loginValidityMessage.setText("Incorrect login or service not started."); // Tell the user that the login info was incorrect
                        } // End of handle function
                    }); // End of sleeper's successful execution
                    new Thread(sleeper).start(); // sleep the program
                } // End of if block
                else if(con.isValid(1))
                    window.setScene(mainMenuScene); // Set the scene to the main menu

            } // End of try block
            catch (Exception exception)
            { // Start of catch block
                exception.printStackTrace(); // Print error of exception
            } // End of catch block
        }); // End of login button commands to execute

        //Setting prompt text, fonts, and labels
        regularTextArea.setPromptText("Search the table");
        dateSceneTextArea.setPromptText("Search the table");
        startDateTextArea.setPromptText("Enter the start date (Ex: 2017-04-21)");
        endDateTextArea.setPromptText("Enter the end date (Ex: 2017-04-21)");
        regularSearchMessage = new Label("Enter Something to Search for In the Table");
        dateSceneSearchMessage = new Label("Enter Something to Search for In the Table");
        dateSceneDateSearchMessage = new Label("Date Searching");
        dailySceneMessage = new Label("Search for appointments taking place on: " + currentDate);
        financialSceneDateSearchMessage = new Label("Date Searching");
        dateSceneDateSearchMessage.setFont(new Font("Arial", 20));
        financialSceneDateSearchMessage.setFont(new Font("Arial", 20));
        window = stage;
        dirChooser.setInitialDirectory(new File(String.valueOf(selectedDirectory)));
        window.setTitle("TireButler Database Query Program");

        //Formatting grid
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(3);

        window.setOnCloseRequest(e ->
        { // Start of window being closed execution
            e.consume(); // Consume parameters
            close_program(); // Call close_program function
        }); // End of window being closed execution

        // Setting up introText label
        Label introText = new Label("Please choose a table you would like to query");
        introText.setFont(new Font("Arial", 13));

        // Setting up buttons for main menu
        closeButton2 = new Button("Close Program");
        addresses = new Button("Addresses");
        appointmentVehicles = new Button("Appointment Vehicles");
        appointments = new Button("Appointments");
        customHoursOperation = new Button("Custom Hours Operation");
        customTracks = new Button("Custom Tracks");
        emails = new Button("Emails");
        postalCodes = new Button("Postal Codes");
        promoCodes = new Button("Promo Codes");
        regions = new Button("Regions");
        services = new Button("Services");
        settings = new Button("Settings");
        trucks = new Button("Trucks");
        users = new Button("Users");
        vehicles = new Button("Vehicles");
        regularSearch = new Button("Search");
        dateSceneSearch = new Button("Search");
        regularChangeDirectoryButton = new Button("Change Download Directory");
        dateSceneChangeDirectoryButton = new Button("Change Download Directory");
        searchDates = new Button("Search Between Dates");
        regularBackButton = new Button("Back");
        dateSceneBackButton = new Button("Back");

        // Close button commands
        closeButton1.setOnAction(e -> close_program());
        closeButton2.setOnAction(e -> close_program());

        // Addresses button execution commands on press
        addresses.setOnAction(e ->
        {
            input = "addresses";
            currentTableColumns = ADDRESSESCOLUMNS;
            window.setScene(tableScene);
        });

        // appointment_vehicles button execution commands on press
        appointmentVehicles.setOnAction(e ->
        {
            input = "appointment_vehicles";
            currentTableColumns = APPOINTMENTVEHICLESCOLUMNS;
            window.setScene(tableScene);
        });

        // appointments button execution commands on press
        appointments.setOnAction(e ->
        {
            input = "appointments";
            currentTableColumns = APPOINTMENTSCOLUMNS;
            window.setScene(dateTableScene);
        });

        // custom hours operation button execution commands on press
        customHoursOperation.setOnAction(e ->
        {
            input = "custom_hours_operation";
            currentTableColumns = CUSTOMHOURSOPERATIONCOLUMNS;
            window.setScene(dateTableScene);
        });

        // custom tracks button execution commands on press
        customTracks.setOnAction(e ->
        {
            input = "custom_tracks";
            currentTableColumns = CUSTOMTRACKSCOLUMNS;
            window.setScene(dateTableScene);
        });

        // emails button execution commands on press
        emails.setOnAction(e ->
        {
            input = "emails";
            currentTableColumns = EMAILSCOLUMNS;
            window.setScene(tableScene);
        });

        // postalCodes button execution commands on press
        postalCodes.setOnAction(e ->
        {
            input = "postalcodes";
            currentTableColumns = POSTALCODESCOLUMNS;
            window.setScene(tableScene);
        });

        // promo codes button execution commands on press
        promoCodes.setOnAction(e ->
        {
            input = "promocodes";
            currentTableColumns = PROMOCODESCOLUMNS;
            window.setScene(tableScene);
        });

        // regions button execution commands on press
        regions.setOnAction(e ->
        {
            input = "regions";
            currentTableColumns = REGIONSCOLUMNS;
            window.setScene(tableScene);
        });

        // services button execution commands on press
        services.setOnAction(e ->
        {
            input = "services";
            currentTableColumns = SERVICESCOLUMNS;
            window.setScene(tableScene);
        });

        // settings button execution commands on press
        settings.setOnAction(e ->
        {
            input = "settings";
            currentTableColumns = SETTINGSCOLUMNS;
            window.setScene(tableScene);
        });

        // trucks button execution commands on press
        trucks.setOnAction(e ->
        {
            input = "trucks";
            currentTableColumns = TRUCKSCOLUMNS;
            window.setScene(tableScene);
        });

        // users button execution commands on press
        users.setOnAction(e ->
        {
            input = "users";
            currentTableColumns = USERSCOLUMNS;
            window.setScene(tableScene);
        });

        // vehicles button execution commands on press
        vehicles.setOnAction(e ->
        {
            input = "vehicles";
            currentTableColumns = VEHICLESCOLUMNS;
            window.setScene(tableScene);
        });

        // regular back button execution commands on press
        regularBackButton.setOnAction(e ->
        {
            window.setScene(mainMenuScene);
            regularDownloadValidityMessage.setText("");
        });

        // date scene back button execution commands on press
        dateSceneBackButton.setOnAction(e ->
        {
            window.setScene(mainMenuScene);
            dateSceneRegularDownloadValidityMessage.setText("");
            dateSceneDateDownloadValidityMessage.setText("");
        });

        // financial report back button execution commands on press
        financialReportBackButton.setOnAction(e ->
        {
            window.setScene(mainMenuScene);
            financialSceneDownloadValidityMessage.setText("");
        });

        // Creation of financial VBox
        VBox financialVBox = create_layout();
        financialVBox.getChildren().addAll(financialSceneDateSearchMessage, startDateFinancialTextArea, endDateFinancialTextArea, financialSceneDownloadValidityMessage, financialSceneSearchDatesButton, financialSceneChangeDirectoryButton, financialReportBackButton);

        // Create new financial report scene
        financialReportScene = new Scene(financialVBox, 600, 600);

        // Execution upon pressing financial report button
        financialReportButton.setOnAction(e ->
        {
            window.setScene(financialReportScene);
        });

        // Execution upon pressing dailySceneSearchButton
        dailySceneSearchButton.setOnAction(e ->
        {
            try
            { // Start of try block
                dailySceneDownloadValidityMessage.setText("Loading...");
                StringBuilder command = new StringBuilder();
                assert con != null;

                currentDate = "2020-04-06";

                //Execute this code in SQL
                command.append("SELECT regions.id AS regionId, regions.name, appointments.id AS \"Appointment ID\", appointments.date, appointments.startTime, appointments.duration, users.firstname, users.phone, users.email, appointments.address, appointment_vehicles.services, appointment_vehicles.vehicleName, vehicles.licence, vehicles.torque, vehicles.pressure FROM regions JOIN appointments ON regions.id = appointments.regionId JOIN users ON appointments.userId = users.id JOIN appointment_vehicles ON appointments.id = appointment_vehicles.appointmentId JOIN vehicles ON appointment_vehicles.vehicleId = vehicles.Id WHERE date = \"").append(currentDate).append("\" ORDER BY regions.id;");

                PreparedStatement statement = con.prepareStatement(String.valueOf(command)); // Creates a preparedStatement in order to execute this string in SQL

                createCSV(statement); // Create a CSV file from this statement

                dailySceneDownloadValidityMessage.setText("Results downloaded in: " + selectedDirectory); // Tell the user where the file has downloaded
            } // End of try block
            catch (Exception throwable)
            { // Start of catch block
                throwable.printStackTrace(); // Print out error
            } // End of catch block
        });

        // Execution upon changing download directory button press
        dailySceneDownloadDirectoryButton.setOnAction(e ->
        {
            selectedDirectory = dirChooser.showDialog(window); // Show the user their file explorer
            if(selectedDirectory == null)
                selectedDirectory = new File((homePath + "\\Downloads\\DBresults.csv")); // Set the file in the location of their downloads folder
        });

        // Execution upon daily scene back button press
        dailySceneBackButton.setOnAction(e ->
        {
            window.setScene(mainMenuScene);
            dailySceneDownloadValidityMessage.setText("");
        });

        // Creation of VBox for the daily report scene
        VBox dailyReportVBox = create_layout();
        dailyReportVBox.getChildren().addAll(dailySceneMessage, dailySceneDownloadValidityMessage, dailySceneSearchButton, dailySceneDownloadDirectoryButton, dailySceneBackButton);

        dailyReportScene = new Scene(dailyReportVBox, 600, 600); // Creation of new Scene for the daily report menu

        // Execution upon daily report button press
        dailyReportButton.setOnAction(e ->
        {
            window.setScene(dailyReportScene); // Set the scene to the daily report scene
        });

        // Execution upon pressing search dates button
        searchDates.setOnAction(e ->
        {
            try
            { // Start of try block
                // Setting up label text and textfield prompt text
                dateSceneDateDownloadValidityMessage.setText("Loading...");
                startDateTextArea.setPromptText("Enter Start Date (Ex: 2015-01-25)");
                endDateTextArea.setPromptText("Enter End Date (Ex: 2015-02-25)");
                String startDate = startDateTextArea.getText();
                String endDate = endDateTextArea.getText();
                assert con != null;

                // StringBuilder which will allow us to construct our SQL query from the user's input
                StringBuilder command = new StringBuilder("SELECT * FROM " + input + " WHERE date BETWEEN \"" + startDate + "\" AND \"" + endDate + "\";");

                PreparedStatement statement = con.prepareStatement(command.toString()); // Create a preparedstatement to run our query

                createCSV(statement); // Create CSV file based on the statement provided

                dateSceneDateDownloadValidityMessage.setText("Results downloaded in: " + selectedDirectory); // Tell the user where the file has downloaded
            } // End of try block
            catch (Exception throwable)
            { // Start of catch block
                throwable.printStackTrace(); // Print out error
            } // End of try block
        });

        // Execution upon financial scene search dates button press
        financialSceneSearchDatesButton.setOnAction(e ->
        {
            try { // Start of try block
                // Setting up labels and textfield inputs
                financialSceneDownloadValidityMessage.setText("Loading...");
                StringBuilder command = new StringBuilder();
                String startDate = startDateFinancialTextArea.getText();
                String endDate = endDateFinancialTextArea.getText();
                assert con != null;

                if(startDate.equals("") || endDate.equals(""))
                    //Execute if dates are left empty
                    command.append("SELECT appointments.id, appointments.date, users.firstname, users.lastname, users.phone, users.email, appointments.address, appointments.invoiceAddress, appointment_vehicles.vehicleName, vehicles.vin, vehicles.licence, vehicles.odometer, appointment_vehicles.wheelLockLocation, vehicles.torque, vehicles.pressure, appointment_vehicles.services, appointments.cost, appointments.paymentAuth, appointments.postalCode, appointments.promoCode, appointments.notes FROM appointments JOIN users ON appointments.userId = users.id JOIN appointment_vehicles ON appointments.id = appointment_vehicles.appointmentId JOIN vehicles ON appointment_vehicles.vehicleId = vehicles.id;");
                else
                    //Implement the user's selected dates into our SQL query
                    command.append("SELECT appointments.id, appointments.date, users.firstname, users.lastname, users.phone, users.email, appointments.address, appointments.invoiceAddress, appointment_vehicles.vehicleName, vehicles.vin, vehicles.licence, vehicles.odometer, appointment_vehicles.wheelLockLocation, vehicles.torque, vehicles.pressure, appointment_vehicles.services, appointments.cost, appointments.paymentAuth, appointments.postalCode, appointments.promoCode, appointments.notes FROM appointments JOIN users ON appointments.userId = users.id JOIN appointment_vehicles ON appointments.id = appointment_vehicles.appointmentId JOIN vehicles ON appointment_vehicles.vehicleId = vehicles.id WHERE date BETWEEN \"").append(startDate).append("\" AND \"").append(endDate).append("\";");

                PreparedStatement statement = con.prepareStatement(String.valueOf(command)); // Create statement based on our command builder

                createCSV(statement); // Create CSV using our statement

                financialSceneDownloadValidityMessage.setText("Results downloaded in: " + selectedDirectory); // Tell the user where the file has downloaded
            } // End of try block
            catch (Exception throwable)
            { // Start of catch block
                throwable.printStackTrace(); // Print out error
            } // End of catch block
        });

        // Execution upon financial scene change directory button press
        financialSceneChangeDirectoryButton.setOnAction(e ->
        {
            selectedDirectory = dirChooser.showDialog(window); // Bring up the file explorer for the user
                if(selectedDirectory == null)
                    selectedDirectory = new File((homePath + "\\Downloads\\DBresults.csv")); // Set the default directory to the downloads folder
        });

        // Execution upon regular search button press
        regularSearch.setOnAction(e ->
        {
            try
            { // Start of try block
                // Set up labels and textfield inputs
                regularDownloadValidityMessage.setText("Loading...");
                String query = regularTextArea.getText();
                assert con != null;

                // Create our SQL query in command
                StringBuilder command = new StringBuilder("SELECT * FROM " + input + " WHERE " + currentTableColumns[0] + " LIKE '%" + query + "%'");

                for(int i = 1; i < currentTableColumns.length; i++)
                    command.append(" OR ").append(currentTableColumns[i]).append(" LIKE '%").append(query).append("%'"); // Run through all the columns in our table and search for given input

                command.append(";"); // Add a semi colon at the end of our query

                PreparedStatement statement = con.prepareStatement(command.toString()); // Create a preparedstatement with our command StringBuilder

                createCSV(statement); // Create a CSV file with our given statement

                regularDownloadValidityMessage.setText("Results downloaded in: " + selectedDirectory); // Tell the user where the file was downloaded
            } // End of try block
            catch (Exception throwable)
            { // Start of catch block
                throwable.printStackTrace();
            } // End of catch block
        });

        // Execution upon date scene search button press
        dateSceneSearch.setOnAction(e ->
        {
            try
            { // Start of try block
                // Setting up label text and textfield inputs
                dateSceneRegularDownloadValidityMessage.setText("Loading...");
                ArrayList<String> results;
                String query = dateSceneTextArea.getText();
                assert con != null;

                // Construction of SQL query through command
                StringBuilder command = new StringBuilder("SELECT * FROM " + input + " WHERE " + currentTableColumns[0] + " LIKE '%" + query + "%'");

                for(int i = 1; i < currentTableColumns.length; i++)
                    command.append(" OR ").append(currentTableColumns[i]).append(" LIKE '%").append(query).append("%'"); // Run through all the columns in the table and search for the given input

                command.append(";"); // Add semi colon to the end of our query

                PreparedStatement statement = con.prepareStatement(command.toString()); // Create a preparedstatement with the command we have built

                createCSV(statement); // Create a CSV file with the statement given

                dateSceneRegularDownloadValidityMessage.setText("Results downloaded in: " + selectedDirectory); // Tell the user where the file has downloaded
            } // End of try block
            catch (Exception throwable)
            { // Start of catch block
                throwable.printStackTrace(); // Print out error
            } // End of catch block
        });

        // Execution upon regular change directory button press
        regularChangeDirectoryButton.setOnAction(e ->
        {
            selectedDirectory = dirChooser.showDialog(window); // Show the user their file explorer
        });

        // Execution upon date scene change directory button press
        dateSceneChangeDirectoryButton.setOnAction(e ->
        {
            selectedDirectory = dirChooser.showDialog(window); // Show the user their file explorer
            if(selectedDirectory == null)
                selectedDirectory = new File((homePath + "\\Downloads\\DBresults.csv")); // Set the default path of the directory to the downloads folder
        });

        // Set up the table scene layout
        layoutTableScene = create_layout();
        layoutTableScene.getChildren().addAll(regularSearchMessage, regularTextArea, regularDownloadValidityMessage, regularSearch, regularChangeDirectoryButton, regularBackButton);
        tableScene = new Scene(layoutTableScene, 600, 600);

        // Set up the date table scene layout
        layoutDateTableScene = create_layout();
        layoutDateTableScene.getChildren().addAll(dateSceneSearchMessage, dateSceneTextArea, dateSceneRegularDownloadValidityMessage, dateSceneSearch, dateSceneChangeDirectoryButton, dateSceneDateSearchMessage, startDateTextArea, endDateTextArea, dateSceneDateDownloadValidityMessage, searchDates, dateSceneBackButton);
        dateTableScene = new Scene(layoutDateTableScene, 600, 600);

        // Set all constraints of the main menu layout
        GridPane.setConstraints(introText, 1, 2);
        GridPane.setConstraints(addresses, 0, 5);
        GridPane.setConstraints(appointmentVehicles, 0, 6);
        GridPane.setConstraints(appointments, 0, 7);
        GridPane.setConstraints(customHoursOperation, 0, 8);
        GridPane.setConstraints(customTracks, 0, 9);
        GridPane.setConstraints(emails, 0, 10);
        GridPane.setConstraints(postalCodes, 0, 11);
        GridPane.setConstraints(promoCodes, 3, 5);
        GridPane.setConstraints(regions, 3, 6);
        GridPane.setConstraints(services, 3, 7);
        GridPane.setConstraints(settings, 3, 8);
        GridPane.setConstraints(trucks, 3, 9);
        GridPane.setConstraints(users, 3, 10);
        GridPane.setConstraints(vehicles, 3, 11);
        GridPane.setConstraints(financialReportButton, 1, 16);
        GridPane.setConstraints(dailyReportButton, 1, 19);
        GridPane.setConstraints(closeButton2, 0, 25);

        // Set all children of the main menu scene
        this.grid.getChildren().addAll(introText, addresses, appointmentVehicles, appointments,
                customHoursOperation, customTracks, emails, postalCodes, promoCodes, regions, services, settings,
                trucks, users, vehicles, financialReportButton, dailyReportButton, closeButton2);

        grid.setStyle("-fx-background-color:#CCE5FF; -fx-opacity:1;"); // Set the background colour

        mainMenuScene = new Scene(this.grid, 600, 600); // Set the layout, width and height

        window.setScene(loginScene); // Set the starting scene to the loginScene
        window.show(); // Show the Stage (window)
    } // End of start function

    public static Connection getConnection(String service, String databaseName, String username, String password) throws Exception
    { // Start of getConnection function
        try
        { // Start of try block
            String driver = "com.mysql.cj.jdbc.Driver"; // Driver used for java sql connector
            String url = "jdbc:mysql://" + service + "/" + databaseName; // Create url based off of the input the user has given
            Class.forName(driver); // Retreives class caller

            return DriverManager.getConnection(url, username, password); // Attempt to get connection based on the input given
        } // End of try block
        catch(Exception ignored)
        { // Start of catch block
        } // End of catch block
        return null; // Return null
    } // End of getConnection function

    public static void createCSV(PreparedStatement statement) throws SQLException
    { // Start of createCSV function
        ResultSet results = statement.executeQuery(); // Execute the query and save all results in the results variable
        StringBuilder sb = new StringBuilder(); // Create a new StringBuilder called sb
        ResultSetMetaData rsmd = results.getMetaData(); // get all metadate of results and store it in rsmd
        String valueWithDoubleQuotes = ""; // String which will keep our data in double quotes

        int columnsNumber = rsmd.getColumnCount(); // Set columnsNumber equal to the number of columns in our given table

        for (int i = 1; i <= columnsNumber; i++)
            sb.append(rsmd.getColumnName(i)).append(","); // Start off by printing all column names in csv file

        while(results.next())
        { // Start of while loop
            sb.append("\r\n"); // Append a new line in CSV file

            for (int i = 1; i <= columnsNumber; i++)
            { // Start of for loop
                String columnValue = results.getString(i); // Extract the next result, store it under columnValue

                if(columnValue == null)
                    columnValue = " "; // put a space if the column value is empty

                valueWithDoubleQuotes = columnValue.replace(columnValue, "\"" + columnValue + "\""); // Keep any double quotes in the column value if there is

                sb.append(valueWithDoubleQuotes).append(","); // Separate all column values with commas to separate in the CSV file
            } // End of for loop
        } // End of while loop

        try
        { // Start of try block
            PrintWriter pw = new PrintWriter(new File(selectedDirectory.getAbsolutePath() + "\\DBresults.csv")); // Print a new file into our selected directory
            pw.write(sb.toString()); // Write the string we have previously built with all the values in CSV format
            pw.close(); // Close our PrintWriter variable
        } // End of try block

        catch(Exception e)
        { // Start of catch block
            System.out.println(e); // Print our error
        } // End of catch block
    }

    public static class confirmClose
    { // Start of confirmClose class

        static boolean response; // Variable that will store the user's response to wanting to close the program, it is static to be able to refer to the display function

        public static boolean display(String title, String message)
        { // Start of display function, returns a boolean which is response
            Stage window = new Stage(); // Creation of a new Stage called window

            window.initModality(Modality.APPLICATION_MODAL); // Stops all inputs on the main program when a new window pops up (Close program window)
            window.setTitle(title); // Set the window title to whatever we pass in when using display in Main
            window.setMinWidth(250); // Set the minimum width to 250 px

            Label label = new Label(message); // Creation of a new Label called label, it will store whatever message we pass in when using display in Main

            Button yesButton = new Button("Yes"); // Creation of a Button called button with the text "Yes"
            Button noButton = new Button("No"); // Creation of a Button called button with the text "No"

            // Do this when user presses the yes button (lambda expression)
            yesButton.setOnAction(e ->
            { // Start of instructions upon yesButton press
                response = true; // Set response to true
                window.close(); // Close the alert box window
            }); // End of instructions upon yesButton press

            // Do this when user presses the no button (lambda expression)
            noButton.setOnAction(e ->
            { // Start of instructions upon noButton press
                response = false; // Set response to false
                window.close(); // Close the alert box window
            }); // End of instructions upon noButton press

            VBox layout = new VBox(10); // Create a new VBox called layout with 10 px spacing
            layout.getChildren().addAll(label, yesButton, noButton); // Add the label, yesButton, and noButton we created to the layout
            layout.setAlignment(Pos.CENTER); // Center everything in the layout

            Scene scene = new Scene(layout); // Create a new Scene called scene which will contain the layout we created
            window.setScene(scene); // Set the scene to scene
            window.showAndWait(); // Block all input until this alert box is dealt with

            return response; // Return user's response

        } // End of display function
    } // End of confirmClose class

    private void close_program()
    { // Start of close_program function
        boolean response = confirmClose.display("Title", "Are you sure you want to exit?"); // Create a Boolean called response which will call the display function and pass in the title and message
        if(response) // Check if the user pressed yes
            window.close(); // Do this if true
    } // End of close_program function

    private VBox create_layout()
    { // Start of create_layout function
        VBox layout = new VBox(30); // Create new VBox called layout with 40 px spacing
        layout.setPadding(new Insets(20, 20, 20, 20)); // Set the layout padding to 20px from each side
        layout.setAlignment(Pos.CENTER); // Center all children in the layout VBox
        this.backgroundFill = new BackgroundFill(Color.rgb(204, 229, 255), CornerRadii.EMPTY, Insets.EMPTY); // Set background_fill to a new BackgroundFill with the background set to aquamarine
        Background background = new Background(this.backgroundFill); // Create a new background and pass in the background_fill we created
        layout.setBackground(background); // Set the background of our layout to the background we created
        return layout; // return the layout
    } // End of create_layout function
} // End of TireButlerDB class