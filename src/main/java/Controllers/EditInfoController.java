package Controllers;

import AlertMaker.AlertHandler;
import Database.DatabaseHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditInfoController {

    @FXML
    private TextField txtnewName;

    @FXML
    private Button update_name_button;

    @FXML
    private TextField txtnewEmail;
public DatabaseHelper handler=null;
    @FXML
    private Button update_email_button;
    public void handle_update_name()
    {  taskChangeName task= new taskChangeName();
        ExecutorService executorService= Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("succesful name updation");
            }
        });
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("failed name updation");
                AlertHandler.showErrorMessage("Error"," Cant update name","");}
        });
    }

    public class taskChangeName extends Task<Void> {
        @Override
        protected Void call() throws Exception {
           handleUupdateNname();
           return null;
        }


        public void handleUupdateNname() throws Exception {

            if (txtnewName.getLength() == 0) {
                AlertHandler.showErrorMessage(" Error-Empty name field", " New name cant be empty\n Type some value", null);

            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Update Name ");
                    alert.setHeaderText("Your name will be updated ");
                    alert.setContentText(" Are you sure you want to proceed ?");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            try {
                                String sql = "update bookingsystem.employees set name= ? where username =? ";

                                PreparedStatement preparedStatement = handler.getInstance().getConnection().prepareStatement(sql);
                                preparedStatement.setString(1, txtnewName.getText());
                                preparedStatement.setString(2, LoginController.uname);
                                int p = preparedStatement.executeUpdate();
                                System.out.println(p);
                                if (p == 1) {
                                    String names = txtnewName.getText();
                                    System.out.println(names);
                                    int t1 = names.indexOf(' ');
                                    if (t1 != -1)
                                        LoginController.fname = names.substring(0, t1);
                                    else
                                        LoginController.fname = names;
                                    DashboardController b= LoginController.refernceLoader.getController();
                                    b.refresh_userLabel();
                                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                    alert1.setTitle("Name Update successfull ");
                                    alert1.setHeaderText("Your name has been updated succesfully ");
                                    alert1.showAndWait();
                                    closeStage();


                                } else {
                                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                                    alert1.setTitle("Name update unsuccessful ");
                                    alert1.setHeaderText("Your name cant be updated  ");
                                    alert1.showAndWait();
                                }
                                txtnewName.setText(null);

                            } catch (SQLException ex) {
                                // AlertHandler.showErrorMessage("Error","Error occurred while booking","Please try again later");
                                System.err.println(ex.getMessage());
                            }
                        }
                    });
                });
            }
        }
    }
        public void handle_enter_key_action(KeyEvent key_event) throws Exception {
            {
                if (key_event.getCode() == KeyCode.ENTER) {
                    //System.out.println(key_event.getCode());
                    handle_update_name();

                }
            }
        }
    public void closeStage()
    {
     //Stage curent= (Stage) update_name_button.getScene().getWindow();
       // curent.close();
    }
    }





