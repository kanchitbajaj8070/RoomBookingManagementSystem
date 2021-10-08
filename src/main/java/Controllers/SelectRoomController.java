package Controllers;


import AlertMaker.AlertHandler;
import Database.DatabaseHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mail.Mail;
import model.ModelTable;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class SelectRoomController implements Initializable {
    @FXML
    private ProgressIndicator progressofBooking;
    public static String room_selected_by_user;
    @FXML
    private AnchorPane pane3;

    @FXML
    private ComboBox<String> select_room_button;

    @FXML
    private Label available_room_label;
    @FXML
    private Label description_label;

    @FXML

    private TextArea description_text_area;
    @FXML
    private Button select_room_ok_button;

    public int get_time() {
        return DateTimeSelectController.time_slot_val;
    }

    public String get_date() {
        return DateTimeSelectController.date_slot_val;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(get_time() + " " + get_date());
        Platform.runLater(() -> {
            progressofBooking.setVisible(false);
        });

        select_room_button.setItems(DateTimeSelectController.room_list);

    }

    public void on_enter_pressed(KeyEvent key_event) throws Exception {
        if (key_event.getCode() == KeyCode.ENTER) {
            System.out.println(key_event.getCode());
            room_and_description_selected();
        }

    }
public DatabaseHelper handler=null;
    public void room_and_description_selected() throws Exception {
        room_selected_by_user = select_room_button.getValue();
        if (room_selected_by_user == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Select Room");
            alert.setHeaderText("Select some room");
            alert.setContentText("Please select room from available rooms");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
            return;
        }
//            ObservableList<CharSequence>
        String desc_list = description_text_area.getText();
        if (desc_list.length() > 150) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Description Length Exceeded");
            alert.setHeaderText("Type the description of booking less than 50 characters");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    System.out.println("Pressed OK.");
                }
            });
            return;
        }

        System.out.println(room_selected_by_user);
        System.out.println(desc_list);
        System.out.println(DateTimeSelectController.full_date_slot_val);
        System.out.println(DateTimeSelectController.time_slot_val);
        try {
/*
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(3);
            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "oracle");
            PreparedStatement preparedStmt = con.prepareStatement("insert into booked_rooms values(?,?,?,?,?)");
            preparedStmt.setString(3, room_selected_by_user);
            preparedStmt.setDate(1, java.sql.Date.valueOf(DateTimeSelectController.full_date_slot_val));
            preparedStmt.setString(2, extra_class_bookingsystem.Controllers.dashboard.time_slot_map_inverse.get(DateTimeSelectController.time_slot_val));
            preparedStmt.setString(4, LoginController.uname);

            preparedStmt.execute();*/
            handler.getInstance().addRoom(room_selected_by_user,java.sql.Date.valueOf(DateTimeSelectController.full_date_slot_val),DashboardController.time_slot_map_inverse.get(DateTimeSelectController.time_slot_val),LoginController.uname,desc_list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            AlertHandler.showErrorMessage(" Error", " Error making booking ", "Try Again");
            Stage stage = (Stage) description_label.getScene().getWindow();
            stage.close();
        }

       Controllers.DashboardController.obList.add(new ModelTable(room_selected_by_user, java.sql.Date.valueOf(DateTimeSelectController.full_date_slot_val), DashboardController.time_slot_map_inverse.get(DateTimeSelectController.time_slot_val), desc_list));
        Stage stage = (Stage) description_label.getScene().getWindow();
        AlertHandler.showInfoMessage("Info","Succesful booking ","");
        stage.close();
//        Runnable task= new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            System.out.println("........smaial");
//                            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");
//                            String msg="The room "+select_room_controller.room_selected_by_user+ " was succesfully booked_rooms for "+ simpleDateFormat.format(java.sql.Date.valueOf(DateTimeSelectController.full_date_slot_val))+" and the time slot is "+dashboard.time_slot_map_inverse.get(DateTimeSelectController.time_slot_val)+".";
//                            mail.Mail.send("kanchitbajaj8070@gmail.com","hhtpzdbpwrtmxwsz",LoginController.email,"Room booking succesful on bookIT ",msg);
//                            System.out.println("........smaial");
//                        }
//                    });
//                }
//                catch ( Exception e)
//                {
//                    System.out.println(e.getStackTrace());
//                }
//
//
//            }
//        };
//        new Thread(task).start();
//
//        //Progressbar.progressProperty().bind(task.run());
//        //Stage stage = (Stage) Progressbar.getScene().getWindow();
/*
      sendMailToUser taskMail= new sendMailToUser();
      Platform.runLater(()->{progressofBooking.setVisible(true);});
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                if(taskMail.isRunning())
                    event.consume();
                else
                {
                    Stage curr=(Stage) select_room_button.getScene().getWindow();
                    curr.close();
                }
            }
        });
      progressofBooking.progressProperty().bind(taskMail.progressProperty());
        ExecutorService executorService= Executors.newFixedThreadPool(1);
        executorService.execute(taskMail);
        executorService.shutdown();
        taskMail.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("succesfull mailing");
                Platform.runLater(()->select_room_ok_button.setDisable(false));
               Platform.runLater(()->{ AlertHandler.showInfoMessage("Booking succesful","Booking succesfull","");
                   stage.close();
               });

            }
        });
        taskMail.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("failed  to mail");
                Platform.runLater(()->{progressofBooking.setVisible(false);});
                progressofBooking.progressProperty().unbind();
                Platform.runLater(()->select_room_ok_button.setDisable(false));
                AlertHandler.showErrorMessage("error","Booking not succesfull","Error connecting with our servers");
                stage.close();

            }
        });*/
    }


    public class sendMailToUser extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            sendMail();
            return null;
        }

        public void sendMail() {
            Platform.runLater(()->select_room_ok_button.setDisable(true));
            System.out.println("........smaial");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String msg = "The room " + SelectRoomController.room_selected_by_user + " was succesfully booked_rooms for " + simpleDateFormat.format(java.sql.Date.valueOf(DateTimeSelectController.full_date_slot_val)) + " and the time slot is " + DashboardController.time_slot_map_inverse.get(DateTimeSelectController.time_slot_val) + ".";
            Mail.send("kanchitbajaj8070@gmail.com", "hhtpzdbpwrtmxwsz", LoginController.email, "Room booking succesful on bookIT ", msg);
            System.out.println("........smaial");
        }

    }         /*  sendMailToUser.setOnSucceeded(new EventHandler<WorkerStateEvent>()
    {
        @Override
        public void handle (WorkerStateEvent event){
        progressofBooking.progressProperty().unbind();
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Success");
        a.setHeaderText(" Your room has been booked_rooms succesfully ");
        a.setTitle("Booking Confirmation");
        a.showAndWait();

    }
    });
            sendmail.setOnFailed(new EventHandler<WorkerStateEvent>(){
        @Override
        public void handle (WorkerStateEvent event){
        Progressbar.progressProperty().unbind();
        closeStage();
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText("Failed");
        a.setHeaderText(" The room booking has failed \n Please try again  ");
        a.setTitle("Booking Confirmation");
        a.showAndWait();
    }
    });
Progressbar.progressProperty().bind(sendmail.progressProperty());
        return null;*/

}





