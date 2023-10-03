package ca.jrvs.apps.trading.model.domain;

public class Account implements Entity<Integer>{

    private Integer id;
    private int trader_id;
    private double amount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTrader_id() {
        return trader_id;
    }

    public void setTrader_id(int trader_id) {
        this.trader_id = trader_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
    }
}
