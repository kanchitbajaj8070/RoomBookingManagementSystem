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
            handler.getInstance().addRoom(room_selected_by_user, java.sql.Date.valueOf(DateTimeSelectController.full_date_slot_val), DashboardController.time_slot_map_inverse.get(DateTimeSelectController.time_slot_val), LoginController.uname, desc_list);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            AlertHandler.showErrorMessage(" Error", " Error making booking ", "Try Again");
            Stage stage = (Stage) description_label.getScene().getWindow();
            stage.close();
        }

        Controllers.DashboardController.obList.add(new ModelTable(room_selected_by_user, java.sql.Date.valueOf(DateTimeSelectController.full_date_slot_val), DashboardController.time_slot_map_inverse.get(DateTimeSelectController.time_slot_val), desc_list));
        Stage stage = (Stage) description_label.getScene().getWindow();
        AlertHandler.showInfoMessage("Info", "Succesful booking ", "");
        stage.close();
    }
}





