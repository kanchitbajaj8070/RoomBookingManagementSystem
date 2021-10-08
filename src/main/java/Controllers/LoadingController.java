package Controllers;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import mail.Mail;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class LoadingController implements Initializable {
@FXML
    private ProgressBar Progressbar;
@FXML
private Label Txt;
private static boolean var=false;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sendMail();


    }
    public void sendMail()
    {
        Task sendmail= new Task() {
            @Override
            protected Object call() throws Exception {
                System.out.println("........smaial");
                            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");
                            String msg="The room "+SelectRoomController.room_selected_by_user+ " was succesfully booked_rooms for "+ simpleDateFormat.format(java.sql.Date.valueOf(DateTimeSelectController.full_date_slot_val))+" and the time slot is "+DashboardController.time_slot_map_inverse.get(DateTimeSelectController.time_slot_val)+".";
                            Mail.send("kanchitbajaj8070@gmail.com","hhtpzdbpwrtmxwsz",LoginController.email,"Room booking succesful on bookIT ",msg);
                            System.out.println("........smaial");
                            return true;
            }
        };
        sendmail.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Progressbar.progressProperty().unbind();
                closeStage();
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Success");
                a.setHeaderText(" Your room has been booked_rooms succesfully ");
                a.setTitle("Booking Confirmation");
                a.showAndWait();

            }
        });
        sendmail.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
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
//        Task  task1=new Task () {
//            @Override
//            protected void call() throws Exception {
//                System.out.println("........smaial");
//                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");
//                String msg="The room "+select_room_controller.room_selected_by_user+ " was succesfully booked_rooms for "+ simpleDateFormat.format(java.sql.Date.valueOf(DATE_TIME_SELECT.full_date_slot_val))+" and the time slot is "+dashboard.time_slot_map_inverse.get(DATE_TIME_SELECT.time_slot_val)+".";
//                mail.Mail.send("kanchitbajaj8070@gmail.com","hhtpzdbpwrtmxwsz",LoginController.email,"Room booking succesful on bookIT ",msg);
//                System.out.println("........smaial");
//            }
//        };
//        Runnable task= new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            System.out.println("........smaial");
//                            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");
//                            String msg="The room "+select_room_controller.room_selected_by_user+ " was succesfully booked_rooms for "+ simpleDateFormat.format(java.sql.Date.valueOf(DATE_TIME_SELECT.full_date_slot_val))+" and the time slot is "+dashboard.time_slot_map_inverse.get(DATE_TIME_SELECT.time_slot_val)+".";
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
//
//            }
//        };

        Thread t=new Thread(sendmail);
                t.start();


        //Proressbar.progressProperty().bind(task.run());

    }
    public void closeStage()
    {
        Stage stage = (Stage) Progressbar.getScene().getWindow();
        stage.close();
    }

}
