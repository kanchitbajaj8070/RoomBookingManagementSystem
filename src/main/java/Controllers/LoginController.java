package Controllers;

import AlertMaker.AlertHandler;
import Database.DatabaseHelper;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
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
import javafx.stage.Stage;
import mail.Mail;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController implements Initializable {
    public DatabaseHelper handler=null;
    static String uname;
    static String password;
    static String fname;
    public static String email;
    @FXML
    private JFXTextField txtUsername;
    @FXML
    private Label progressLabel;
    @FXML
    private JFXTextField passwordDuplicate;
 public static Stage referenceStage=null;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private ProgressIndicator progressDashboard;
    @FXML
    private ProgressBar progress;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private Button btnSignin;

    @FXML
    private Label btnForgot;

    @FXML
    void handleButtonAction(MouseEvent event) throws Exception {

        sign_in_action();

    }
public static FXMLLoader refernceLoader;
    public void update_email() {
        email=handler.getInstance().getEmail(LoginController.uname);
        System.out.println("email is"+email);
        LoginController.fname=handler.getInstance().getfname(LoginController.uname);
    }

    public void actionHandleCheckBox(ActionEvent actionEvent) {
        if(showPasswordCheckBox.isSelected()) {
            passwordDuplicate.setText(txtPassword.getText());
            txtPassword.setVisible(false);
        }
        else
        {
            txtPassword.setVisible(true);

        }
        System.out.println(passwordDuplicate.getText());
        System.out.println(txtPassword.getText());
    }


public void sign_in_action()
{    btnSignin.setDisable(true);
    signInTask s= new signInTask();
    Platform.runLater(() -> {
        btnSignin.setText("Signing In...");
        progressDashboard.setVisible(true);
        progressLabel.setVisible(true);
    });
    ExecutorService executorService= Executors.newFixedThreadPool(4);
    executorService.execute(s);
    executorService.shutdown();
    progressDashboard.progressProperty().bind(s.progressProperty());
    s.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
        @Override
        public void handle(WorkerStateEvent event) {
            btnSignin.setDisable(false);
            btnSignin.setText("Sign In");
            Boolean res= s.getValue();
            System.out.println("val is "+res +"!!!");
            Platform.runLater(()->{ progressLabel.setText("opening dashboard!!!!!!!");});

            if(res==true) {
                Stage current = (Stage) txtUsername.getScene().getWindow();
                current.close();

                try {
                    Stage primaryStage = new Stage();
                    referenceStage=primaryStage;
                    refernceLoader = new FXMLLoader(getClass().getResource("/fxmls/dashboard.fxml"));
                    primaryStage.setTitle("bookIT-extra class booking system");
                    Parent root =refernceLoader.load();
                    primaryStage.setScene(new Scene(root));
                    primaryStage.setResizable(false);

                    primaryStage.show();
                    primaryStage.setOnCloseRequest(e ->
                    {
                        e.consume();
                        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                        a.setContentText("Are you sure you want to proceed");
                        a.setHeaderText(" You are about to close the bookIT application ");
                        a.setTitle("Close Application Confirmation");
                        a.showAndWait().ifPresent(rs -> {
                            if (rs == ButtonType.OK) {
                                primaryStage.close();
                                System.exit(1);
                            }
                        });
                    });

                } catch (Exception e) {
                    AlertHandler.showErrorMessage("Error","Error occured while connecting with our server","Check Internet \n And try again");
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("failed");
            }
        }
    });
    s.setOnFailed(new EventHandler<WorkerStateEvent>() {
        @Override
        public void handle(WorkerStateEvent event) {
            btnSignin.setDisable(false);
            btnSignin.setText("Sign In");
            progressDashboard.setVisible(false);
            progressLabel.setVisible(false);
            progress.setVisible(false);
            AlertHandler.showErrorMessage("Error","Error occured while connecting with our server","Check Internet \n And try again");
        }
    });
}
    public class signInTask extends Task<Boolean> {
        private Boolean result = false;

        @Override
        protected Boolean call() throws Exception {
            result= signin_action();
            return result;
        }

        public Boolean signin_action() throws Exception {

            uname = txtUsername.getText();
            password = txtPassword.getText();

            Platform.runLater(() -> {
                btnForgot.setDisable(true);
                progressLabel.setText("Checking User Detials ..");
            });
            System.out.println(uname + "  " + password);
          /*  String sql = "SELECT * FROM FACULTY Where  username = ? and password = ?";
            String sql_fname = "SELECT name FROM faculty Where  username = ?";


            try {
                Class.forName("com.mysql.jdbc.Driver");
                DriverManager.setLoginTimeout(8);
                Connection con = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "oracle");

                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, uname);
                preparedStatement.setString(2, password);
                PreparedStatement stmt_fname = con.prepareStatement(sql_fname);
                stmt_fname.setString(1, uname);
                ResultSet resultSet = preparedStatement.executeQuery();
                ResultSet result_fname = stmt_fname.executeQuery();
                if (result_fname.next() == true) {
                    String names = result_fname.getString("name");
                    // System.out.println(names);
                    int t1 = names.indexOf(' ');
                    if (t1 != -1)
                        fname = names.substring(0, t1);
                    else
                        fname = names;
                    // System.out.println(fname);
                }*/
            Boolean res = handler.getInstance().checkLoginDetails(uname, password);
            result = res;
            if (res == true) {
                update_email();
                Platform.runLater(() -> {
                    progressLabel.setText("uploading email of user ......");
                });
            }

            if (res == false) {
                AlertHandler.showErrorMessage("Wrong Credentials", "Please enter correct Login detials", " You have entered wrong username or password");
                Platform.runLater(() -> {
                    btnSignin.setDisable(false);
                    btnForgot.setDisable(false);
                    progressLabel.setVisible(false);
                    progressDashboard.setVisible(false);
                    progress.setVisible(false);
                });
            }
                return result;

     /*

*/


        }
    }
    public void forgot_password_function(MouseEvent event) throws Exception
    {
        if(txtUsername.getText()==null) {
            AlertHandler.showErrorMessage("Enter Username", "Username can not be left empty", "");
            return;

        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Forgot Password ");
        alert.setHeaderText("We will send a new password to your registered mail \nand you will not be able to use your old password ");
        alert.setContentText(" Are you sure you want to proceed?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
                try {

                    String sql = "SELECT email_id FROM faculty Where  username = ?";
                    Class.forName("com.mysql.jdbc.Driver");
                    DriverManager.setLoginTimeout(2);
                    Connection con = DriverManager.getConnection(
                            "jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "oracle");
                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setString(1, txtUsername.getText());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next() == false) {
                        AlertHandler.showErrorMessage("Wrong Username", "Enter correct username in the username\n field of Login window", "");
                        return;
                    } else {
                        do {
                            email = resultSet.getString("email_id");
                            System.out.println(email);

                        } while (resultSet.next());
                    }
                }
                    catch (SQLException ex) {
                        System.err.println(ex.getMessage());
                        AlertHandler.showErrorMessage("No Internet Connectivity","You are not connected to the internet","Please connect to the internet for proceeding further");
                    } catch (ClassNotFoundException e) {
                    AlertHandler.showErrorMessage("No Internet Connectivity","You are not connected to the internet","Please connect to the internet for proceeding further");
                }
                try {
                    update_password();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Mail.send("kanchitbajaj8070@gmail.com","hhtpzdbpwrtmxwsz",email,"New password for bookIT","your new password is "+new_random_pass+" .Kindly change it at the earliest from change password feature in app");
            }});

    }
    String new_random_pass;
public void update_password() throws Exception {
    try {
        String sql = "update authentication set password= ? where username =? ";
        Class.forName("com.mysql.jdbc.Driver");
        DriverManager.setLoginTimeout(2);
        Connection con = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "oracle");

      new_random_pass = getAlphaNumericString(8);
        //System.out.println(new_random_pass);
               PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, new_random_pass);
        preparedStatement.setString(2, txtUsername.getText());
        preparedStatement.executeUpdate();
        LoginController.password=new_random_pass;

    } catch (SQLException ex) {
        System.err.println(ex.getMessage());
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
}
    public void handle_enter_key_action(KeyEvent key_event) throws Exception {
        {
            if (key_event.getCode() == KeyCode.ENTER) {
                //System.out.println(key_event.getCode());
                sign_in_action();

            }
        }
    }
    static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";


        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {


            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtPassword.setText(null);
        txtUsername.setText(null);
        btnForgot.setVisible(false);
        passwordDuplicate.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        txtPassword.textProperty().bindBidirectional(passwordDuplicate.textProperty());
progressDashboard.setVisible(false);
progress.setVisible(false);
progressLabel.setVisible(false);
    }
}
