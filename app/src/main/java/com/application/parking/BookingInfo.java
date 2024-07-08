package com.application.parking;

public class BookingInfo {
    String DateFrom;
    String DateTo;
    String Email;
    float Price;
    String KeyOfParking;
    String SlotNum;
    String KeyOfBooking;
    String Phone;
    String Name;

    public BookingInfo() {
    }

    public BookingInfo(String dateFrom, String dateTo, String email, float price, String keyOfParking, String slotNum, String keyOfBooking, String phone, String name) {
        DateFrom = dateFrom;
        DateTo = dateTo;
        Email = email;
        Price = price;
        KeyOfParking = keyOfParking;
        SlotNum = slotNum;
        KeyOfBooking = keyOfBooking;
        Phone = phone;
        Name = name;
    }

    public void setDateFrom(String dateFrom) {
        DateFrom = dateFrom;
    }

    public void setDateTo(String dateTo) {
        DateTo = dateTo;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public void setKeyOfParking(String keyOfParking) {
        KeyOfParking = keyOfParking;
    }

    public void setSlotNum(String slotNum) {
        SlotNum = slotNum;
    }

    public void setKeyOfBooking(String keyOfBooking) {
        KeyOfBooking = keyOfBooking;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDateFrom() {
        return DateFrom;
    }

    public String getDateTo() {
        return DateTo;
    }

    public String getEmail() {
        return Email;
    }

    public float getPrice() {
        return Price;
    }

    public String getKeyOfParking() {
        return KeyOfParking;
    }

    public String getSlotNum() {
        return SlotNum;
    }

    public String getKeyOfBooking() {
        return KeyOfBooking;
    }

    public String getPhone() {
        return Phone;
    }

    public String getName() {
        return Name;
    }
}
