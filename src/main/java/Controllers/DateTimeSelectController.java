package Controllers;

import AlertMaker.AlertHandler;
import Database.DatabaseHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DateTimeSelectController implements Initializable {
   public static int time_slot_val=-1;
   public static String date_slot_val;
   public static LocalDate full_date_slot_val;

    public DateTimeSelectController() {
    }
   public static ObservableList<String> room_list;

    @FXML
    private ProgressIndicator progreesIndicator;
    @FXML
    private AnchorPane PANE_2;

    @FXML
    private DatePicker DATE_PICKER;

    @FXML
    private Label DATE_LABEL;

    @FXML
    private Label TIME_LABEL;


    @FXML
    private Button available_room_button;

    @FXML
    void available_room_show(MouseEvent event) throws Exception {
        ExecutorService executorService= Executors.newFixedThreadPool(2);
        task t= new task();
     Platform.runLater(()->    progreesIndicator.setVisible(true));
        progreesIndicator.progressProperty().bind(t.progressProperty());
        executorService.execute(t);
        executorService.shutdown();
        t.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println(" failed ..");

                Platform.runLater(()-> {progreesIndicator.setVisible(false);});
            }
        });
        t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("succes");
                Platform.runLater(()->progreesIndicator.setVisible(false));
            }
        });
    }

    public class task extends Task<Void>
    {

        @Override
        protected Void call() throws Exception {
            button_functionality();
            return null;
        }

public DatabaseHelper handler=null;
    public void button_functionality() throws Exception {
        LocalDate date = DATE_PICKER.getValue();
        System.out.println(date);
        String time_selection = null;
        time_selection = TIME_SLOT_BOX.getValue();
        System.out.println(time_selection);
        if (time_selection == null && date_slot_val != null) {
            AlertHandler.showErrorMessage("Error- No Time Slot Selected", " Please select some timeslot ", "Select time slot in which you want to conduct the class ");
        } else if (date == null && time_selection != null) {
            AlertHandler.showErrorMessage("Error- No Date Selected", "Select some date from calendar", "Please select some date to conduct extra class");
        } else if (date != null && time_selection != null) {
            int map_time_slot = time_slot_map.get(time_selection);
            time_slot_val = map_time_slot;
            System.out.println(map_time_slot);
            full_date_slot_val = date;
            date_slot_val = date.getDayOfWeek().toString().toLowerCase().substring(0, 3);
            ArrayList<String> room_available = new ArrayList<>();
  /*          try {

                Class.forName("com.mysql.jdbc.Driver");
                DriverManager.setLoginTimeout(2);
                Connection con = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "oracle");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select room_name from USED_ROOMS where is_available = 1 and time_slot=" + time_slot_val + " and lower(day)='" + date_slot_val + "' and roomname not in(select Room_name from booked_rooms where DATE_OF_BOOKING = '" + full_date_slot_val + "' and time_slot = '" + time_selection + "');");

                System.out.println(rs);
                while (rs.next())
                    room_available.add(rs.getString(1));
                System.out.println(room_available);

            } catch (Exception e) {
                AlertHandler.showErrorMessage("Error", "Error occurred while booking", "Please try again later");
            }*/
  room_available=handler.getInstance().getRooms(date_slot_val,time_slot_val,full_date_slot_val,time_selection);
            room_list = FXCollections.observableArrayList(room_available);
            if (room_list.size() == 0) {
                AlertHandler.showErrorMessage("No Rooms Available", " No Rooms are available for your \n date and time combination", "Please select some other date or time slot");

            } else {
                Platform.runLater(() -> {
                    Stage primaryStage = new Stage();
                    Parent root = null;
                    try {
                        root = FXMLLoader.load(getClass().getResource("/fxmls/available_room.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    primaryStage.setTitle("Select Room ");
                    primaryStage.initModality(Modality.APPLICATION_MODAL);
                    primaryStage.setScene(new Scene(root));
                    primaryStage.show();
                    primaryStage.setResizable(false);

                    Stage stage = (Stage) DATE_LABEL.getScene().getWindow();
                    stage.close();
                });

            }
        }

         else {
            AlertHandler.showErrorMessage("Error - No Date And Time Selected", "No Date and Time selected", " Please select both date and time");
        }
    }
    }
    public void available_room_show_keyEvent(KeyEvent key_event) throws Exception
    {
        if (key_event.getCode() == KeyCode.ENTER) {
            System.out.println(key_event.getCode());
            ExecutorService executorService= Executors.newFixedThreadPool(2);
            task t= new task();
            Platform.runLater(()->    progreesIndicator.setVisible(true));
            progreesIndicator.progressProperty().bind(t.progressProperty());
            executorService.execute(t);
            executorService.shutdown();
            t.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    System.out.println(" failed ..");

                    Platform.runLater(()-> {progreesIndicator.setVisible(false);});
                }
            });
            t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    System.out.println("succes");
                    Platform.runLater(()->progreesIndicator.setVisible(false));
                }
            });
    }}
    @FXML
    private ComboBox<String> TIME_SLOT_BOX;
    static ObservableList<String> list = FXCollections.observableArrayList("8AM-9AM", "9AM -10AM", "10AM-11AM", "11AM-12PM", "12PM-1PM ", "1PM-2 PM ", "2PM-3PM", "3PM-4PM", "4PM-5PM", "5PM-6PM", "6PM-7PM");

    @Override

    public void initialize(URL location, ResourceBundle resources)
    {
        Platform.runLater(() -> {
            progreesIndicator.setVisible(false);
            TIME_SLOT_BOX.setItems(list);
            int c = 8;
            for (int i = 0; i < list.size(); i++) {
                time_slot_map.put(list.get(i), c);
                c = c + 1;
            }
        });
    }

    public static HashMap<String, Integer> time_slot_map = new HashMap<>();


}
