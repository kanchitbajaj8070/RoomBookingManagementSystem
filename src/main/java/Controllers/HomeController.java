package Controllers;


import AlertMaker.AlertHandler;
import Database.DatabaseHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeController implements Initializable {

    @FXML
    private Label Name_label;
    @FXML
    private ProgressIndicator progressofDetials;
    @FXML
    private Label uname_label;

    @FXML
    private Label type_label;

    @FXML
    private Label email_label;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ExecutorService service = Executors.newFixedThreadPool(1);
        loadIntialDetials task = new loadIntialDetials();
        progressofDetials.progressProperty().bind(task.progressProperty());
        service.execute(task);
        service.shutdown();

        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                AlertHandler.showErrorMessage("Error", "Failed Loading Details", "");
                Platform.runLater(() -> {
                    progressofDetials.progressProperty().unbind();
                    progressofDetials.setVisible(false);
                });
            }
        });
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("Succesful in loading detials");
                progressofDetials.progressProperty().unbind();
                Platform.runLater(() -> {
                    progressofDetials.progressProperty().unbind();
                    progressofDetials.setVisible(false);

                });
            }
        });
    }

    public void closeStage() {
        Stage curent = (Stage) uname_label.getScene().getWindow();
        curent.close();
    }


    static String ty = null;

    class loadIntialDetials extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            loadInit();
            return null;
        }

        public DatabaseHelper handler = null;

        public void loadInit() throws Exception {

            ResultSet rs = handler.getInstance().execQuery("select * from bookingsystem.employees where lower(username)='" + LoginController.uname + "';");
            try {
                if (rs.next()) {
                    Platform.runLater(() -> {

                        try {
                            Name_label.setText(rs.getString("name").toUpperCase());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        try {
                            uname_label.setText(rs.getString("username").toUpperCase());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        ty = "Teacher";
                        type_label.setText(ty.toUpperCase());
                        try {
                            email_label.setText(rs.getString("email").toUpperCase());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    });
                }
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }

        }
    }
}

