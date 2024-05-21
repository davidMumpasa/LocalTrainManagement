package com.example.myapplication.Model;

public class Ticket {
    private String bookingId;
    private String passName;
    private String seat;
    private String train;
    private String bookingDate;
    private String bookingTime;

    public Ticket(String bookingId, String passName, String seat, String train, String bookingDate, String bookingTime) {
        this.bookingId = bookingId;
        this.passName = passName;
        this.seat = seat;
        this.train = train;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
    }

    public Ticket() {
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPassName() {
        return passName;
    }

    public void setPassName(String passName) {
        this.passName = passName;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }
}
