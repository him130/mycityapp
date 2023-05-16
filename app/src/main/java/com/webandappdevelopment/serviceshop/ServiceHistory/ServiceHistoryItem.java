package com.webandappdevelopment.serviceshop.ServiceHistory;

public class ServiceHistoryItem {
    private int Id;
    private String Name;
    private String Address;
    private String Service;
    private String AppointmentDate;
    private String AppointmentMonth;
    private String Mobile;
    private String Category;
    private String Status;

    public ServiceHistoryItem(int id, String name, String address, String service, String appointmentDate, String appointmentMonth, String mobile, String category, String status) {
        Id = id;
        Name = name;
        Address = address;
        Service = service;
        AppointmentDate = appointmentDate;
        AppointmentMonth = appointmentMonth;
        Mobile = mobile;
        Category = category;
        Status = status;
    }

    public int getId() {
        return Id;
    }
    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }
    public void setAddress(String address) {
        Address = address;
    }

    public String getService() {
        return Service;
    }
    public void setService(String service) {
        Service = service;
    }

    public String getAppointmentDate() {
        return AppointmentDate;
    }
    public void setAppointmentDate(String appointmentDate) {
        AppointmentDate = appointmentDate;
    }

    public String getAppointmentMonth() {
        return AppointmentMonth;
    }
    public void setAppointmentMonth(String appointmentMonth) {
        AppointmentMonth = appointmentMonth;
    }

    public String getMobile() {
        return Mobile;
    }
    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getCategory() {
        return Category;
    }
    public void setCategory(String category) {
        Category = category;
    }

    public String getStatus() {
        return Status;
    }
    public void setStatus(String status) {
        Status = status;
    }
}
