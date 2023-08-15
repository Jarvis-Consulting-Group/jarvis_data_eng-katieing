package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.util.DataTransferObject;

import java.util.ArrayList;

public class Order implements DataTransferObject{

    private long orderId;
    private String custFirstName;
    private String custLastName;
    private String custEmail;
    private String creationDate;
    private double totalDue;
    private String status;
    private String salesFirstName;
    private String salesLastName;
    private String salesEmail;
    private ArrayList<OrderLine> orderLine;

    public Order() {
        this.orderLine = new ArrayList<>();
    }



    public long getId() {
        return orderId;
    }

    public void setId(long orderId) {
        this.orderId = orderId;
    }

    public String getCustFirstName() {
        return custFirstName;
    }

    public void setCustFirstName(String custFirstName) {
        this.custFirstName = custFirstName;
    }

    public String getCustLastName() {
        return custLastName;
    }

    public void setCustLastName(String custLastName) {
        this.custLastName = custLastName;
    }

    public String getCustEmail() {
        return custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public double getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(double totalDue) {
        this.totalDue = totalDue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSalesFirstName() {
        return salesFirstName;
    }

    public void setSalesFirstName(String salesFirstName) {
        this.salesFirstName = salesFirstName;
    }

    public String getSalesLastName() {
        return salesLastName;
    }

    public void setSalesLastName(String salesLastName) {
        this.salesLastName = salesLastName;
    }

    public String getSalesEmail() {
        return salesEmail;
    }

    public void setSalesEmail(String salesEmail) {
        this.salesEmail = salesEmail;
    }

    public ArrayList<OrderLine> getOrderLine() {
        return orderLine;
    }

    public void addOrderLine(OrderLine orderLine) {
        this.orderLine.add(orderLine);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", custFirstName='" + custFirstName + '\'' +
                ", custLastName='" + custLastName + '\'' +
                ", custEmail='" + custEmail + '\'' +
                ", creationDate=" + creationDate +
                ", totalDue=" + totalDue +
                ", status='" + status + '\'' +
                ", salesFirstName='" + salesFirstName + '\'' +
                ", salesLastName='" + salesLastName + '\'' +
                ", salesEmail='" + salesEmail + '\'' +
                ", orderLine=" + orderLine +
                '}';
    }
}
