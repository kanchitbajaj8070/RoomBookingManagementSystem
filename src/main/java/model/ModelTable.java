package model;

import java.sql.Date;

public class ModelTable {
    public String Roomname;
    Date book_date;
    String timeslot;
    String name;

    public ModelTable(String roomname, Date book_date, String timeslot, String name) {
        this.Roomname = roomname;
        this.book_date = book_date;
        this.timeslot = timeslot;
        this.name = name;
    }



    public String getRoomname() {
        return Roomname;
    }

    public void setRoomname(String roomname) {
        Roomname = roomname;
    }

    public Date getBook_date() {
        return book_date;
    }

    public void setBook_date(Date book_date) {
        this.book_date = book_date;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

