package Database;
import AlertMaker.AlertMaker;
import model.ModelTable;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseHelper {
    private static DatabaseHelper handler = null;
    private static String dbUrl = null;
    private static String dbUsername = null;
    private static String dbPassword = null;
    private static Connection conn;
    private static Statement stmt;

    static {
        createConnection();
    }


    public static DatabaseHelper getInstance() {
        if (handler == null) {
            handler = new DatabaseHelper();
        }
        return handler;
    }

    private static void createConnection() {
        try {
            File file=new File("src/main/resources/db.properties");
            FileInputStream fis = new FileInputStream(file.getCanonicalPath());
            Properties p = new Properties();
            p.load(fis);
            dbUrl = (String) p.get("db.url");
            dbUsername = (String) p.get("db.user");
            dbPassword = (String) p.get("db.password");
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("connection made");

        } catch (Exception e) {

            AlertMaker.showErrorMessage("Error connecting to database server", "");
        }
    }

    public static void closeConnection() {
        try {
            handler.getInstance().getConnection().close();
        } catch (SQLException e) {
            AlertMaker.showErrorMessage("Not able to close Db connection", "");
        }

    }

    public Connection getConnection() {
        return conn;
    }

    public ArrayList<String> getAllRooms() throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = null;
        try {
            String query = "SELECT * from bookingsystem.rooms";
            rs = execQuery(query);
            while (rs.next())
                list.add(rs.getString("roomname"));
        } catch (Exception e) {
            System.out.println("eerrr");// TODO: handle exception
        }
        return list;

    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery" + ex.getLocalizedMessage());
            return null;
        }

        return result;
    }

    public void deleteRoom(String name) {
        try {
            String query = "delete from  bookingsystem.rooms where roomname=?";
            PreparedStatement preparedStatement = handler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, name);
            int p = preparedStatement.executeUpdate();
            System.out.println("val p " + p);

        } catch (Exception e) {

            AlertMaker.showErrorMessage("", "Rooms table deleting room error");
        }
        deletefromMainTable(name);

    }

    private void deletefromMainTable(String name) {
        try {
            String query = "delete from  bookingsystem.main where roomname=?";
            PreparedStatement preparedStatement = handler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, name);
            int p = preparedStatement.executeUpdate();
            System.out.println("val p " + p);

        } catch (Exception e) {

            AlertMaker.showErrorMessage("", "Deleting room error");
        }
    }

    public void addRoom(String name) {
        try {
            String query = "Insert into bookingsystem.rooms values (?)";
            PreparedStatement preparedStatement = handler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, name);
            int p = preparedStatement.executeUpdate();
            System.out.println("val p " + p);
            AlertMaker.showInfoMessage("Added succesfully", "Room added succesfully", "");

        } catch (Exception e) {

            AlertMaker.showErrorMessage("", "Adding room error");
        }

    }

    public void addUser(String a, String b, String c) {

        try {
            String query = "Insert into bookingsystem.employees values (?,?,?,?)";
            PreparedStatement preparedStatement = handler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, a);
            preparedStatement.setString(2, b);
            preparedStatement.setString(3, "test123");
            preparedStatement.setString(4, c);
            int p = preparedStatement.executeUpdate();
            System.out.println("val p " + p);
            AlertMaker.showInfoMessage("Added succesfully", "User added succesfully", "");

        } catch (Exception e) {

            AlertMaker.showErrorMessage("", "Adding User error");
        }

    }

    public ArrayList<String> getAllDays() throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Monday");
        list.add("Tuesday");
        list.add("Wednesday");
        list.add("Thrusday");
        list.add("Friday");
        list.add("Saturday");
        list.add("Sunday");

        return list;

    }

    public ArrayList<String> getAllTimeSlots() throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = null;
        try {
            String query = "SELECT distinct timeslot from bookingsystem.main";
            rs = execQuery(query);
            while (rs.next()) {
                String str = "";
                int s = rs.getInt("timeslot") % 12;
                if (s < 8 && s > 0)
                    str = s + "PM" + " - " + (s + 1) + "PM";
                else {
                    if (s == 0)
                        str = " 12PM - 1PM";
                    else

                        str = s + "AM" + " - " + (s + 1) + "AM";
                }
                list.add(str);
            }
        } catch (Exception e) {
            System.out.println("eerrr");// TODO: handle exception
        }
        return list;

    }

    public void addTimeslot(String a, int i, String c) {
        System.out.println("in add time slot" + a + (i) + c);
        try {
            String query = "update  bookingsystem.main set isAvailable =1 Where roomname=? and timeslot=? and day=?";
            PreparedStatement preparedStatement = handler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, a);
            preparedStatement.setInt(2, i);
            preparedStatement.setString(3, c);
            int p = preparedStatement.executeUpdate();
            System.out.println("val p " + p);

        } catch (Exception e) {

            AlertMaker.showErrorMessage("Failed in deleting questions of this Category", "");
        }


    }

    public Boolean checkLoginDetails(String username, String password) {
        Boolean res = false;
        String sql = "SELECT * FROM bookingsystem.employees Where  username = ? and password = ?";

        try {
            PreparedStatement preparedStatement = handler.getInstance().getConnection().prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            res = resultSet.next();

        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        return res;
    }

    public ArrayList<ModelTable> intialTableData(String uname) throws SQLException {
        ArrayList<ModelTable> list = new ArrayList<>();

        ResultSet rs = null;
        try {
            rs = execQuery("select * from bookingsystem.booked where lower(name)='" + uname + "' order by book_date ;");

            while (rs.next()) {
                list.add(new ModelTable(rs.getString("Roomname"), rs.getDate("book_date"), rs.getString("timeslot"), rs.getString("Description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> getRooms(String day, int time, LocalDate date, String timeValue) {
        ArrayList<String> roomAvailable = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = execQuery("select roomname from bookingsystem.main where isAvailable = 1 and timeslot=" + time + " and lower(day)='" + day + "' and roomname not in(select Roomname from bookingsystem.booked where book_date = '" + date + "' and timeslot = '" + timeValue + "');");
            System.out.println(rs.toString());
            while (rs.next())
                roomAvailable.add(rs.getString(1));
            System.out.println(roomAvailable.toString());

        } catch (Exception e) {
            AlertMaker.showErrorMessage("Error occurred while booking", "Please try again later");
        }
        return roomAvailable;
    }


    public void addRoom(String room, Date date, String timeslot, String username, String desciption) {
        try {
            PreparedStatement preparedStmt = handler.getInstance().getConnection().prepareStatement("insert into bookingsystem.booked values(?,?,?,?,?)");
            preparedStmt.setString(1, room);
            preparedStmt.setDate(2, date);
            preparedStmt.setString(3, timeslot);
            preparedStmt.setString(4, username);
            preparedStmt.setString(5, desciption);
            preparedStmt.execute();
            addToReports("Room "+ room+" at time slot"+ timeslot + " on date :- "+ date+" is booked by "+ username);
        } catch (SQLException e) {
            AlertMaker.showErrorMessage("", "error while booking");
            System.out.println(e.getLocalizedMessage());
        }
    }

    public String getEmail(String username) {
        String emailId = null;
        try {

            String sql = "SELECT email FROM bookingsystem.employees Where  lower(username) = '" + username + "';";

            ResultSet resultSet = null;
            resultSet = execQuery(sql);
            while (resultSet.next())
                emailId = resultSet.getString("email");

        } catch (SQLException E) {
            System.out.println(E.getLocalizedMessage());
        }

        return emailId;
    }

    public int deleteRow(Date date, String roomname, String timeslot, String uname) {
        int p = 0;
        try {
            PreparedStatement preparedStmt = handler.getInstance().getConnection().prepareStatement("delete from bookingsystem.booked where book_date=? and roomname=? and timeslot=? and name =?");
            preparedStmt.setDate(1, date);
            preparedStmt.setString(2, roomname);
            preparedStmt.setString(3, timeslot);
            preparedStmt.setString(4, uname);
            p = preparedStmt.executeUpdate();
            addToReports("Room "+ roomname +" at time slot"+ timeslot + " on date :- "+ date+" is unbooked by "+ uname);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return p;
    }

    public String getfname(String username) {
        String emailId = null;
        try {

            String sql = "SELECT name FROM bookingsystem.employees Where  lower(username) = '" + username + "';";

            ResultSet resultSet = null;
            resultSet = execQuery(sql);
            while (resultSet.next())
                emailId = resultSet.getString("name");

        } catch (SQLException E) {
            System.out.println(E.getLocalizedMessage());
        }

        return emailId.substring(0, emailId.indexOf(" ")).trim();
    }
    public void addToReports(String message)
    {

        java.util.Date date= new java.util.Date();
        Timestamp t= new Timestamp(date.getTime());
        try
        {
            String query = "Insert into bookingsystem.reports values (?,?)";
            PreparedStatement preparedStatement = handler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setTimestamp(1, t);
            preparedStatement.setString(2,message);
            int p = preparedStatement.executeUpdate();
            System.out.println("val p " + p);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}


