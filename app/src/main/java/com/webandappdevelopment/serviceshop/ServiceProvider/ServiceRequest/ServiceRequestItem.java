package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceRequest;

public class ServiceRequestItem {
    private int RequestId;
    private String CustomerName;
    private String Contact;
    private String ServiceRequired;
    private String Address;
    private String AppointmentDate;
    private String AppointmentMonth;
    private String Status;

    public ServiceRequestItem(int requestId, String customerName, String contact, String serviceRequired, String address, String appointmentDate, String appointmentMonth, String status) {
        RequestId = requestId;
        CustomerName = customerName;
        Contact = contact;
        ServiceRequired = serviceRequired;
        Address = address;
        AppointmentDate = appointmentDate;
        AppointmentMonth = appointmentMonth;
        Status = status;
    }

    public int getRequestId() {
        return RequestId;
    }
    public void setRequestId(int requestId) {
        RequestId = requestId;
    }

    public String getCustomerName() {
        return CustomerName;
    }
    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getContact() {
        return Contact;
    }
    public void setContact(String contact) {
        Contact = contact;
    }

    public String getServiceRequired() {
        return ServiceRequired;
    }
    public void setServiceRequired(String serviceRequired) {
        ServiceRequired = serviceRequired;
    }

    public String getAddress() {
        return Address;
    }
    public void setAddress(String address) {
        Address = address;
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

    public String getStatus() {
        return Status;
    }
    public void setStatus(String status) {
        Status = status;
    }
}
