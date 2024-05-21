package com.example.myapplication.Model;

public class Booking {
    private String bookingId;
    private String userId;
    private String passName;
    private String seat;
    private String train;
    private String bookingDate;
    private String bookingTime;

    public Booking() {
    }

    public Booking(String bookingId,String userId, String passName, String seat, String train, String bookingDate, String bookingTime, String bookingDetails) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.passName = passName;
        this.seat = seat;
        this.train = train;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
    }



    public Booking(String bookingId, String bookingDate, String bookingDetails) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

}

