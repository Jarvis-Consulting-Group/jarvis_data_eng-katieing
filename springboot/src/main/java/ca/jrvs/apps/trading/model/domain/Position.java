package ca.jrvs.apps.trading.model.domain;

public class Position implements Entity<Integer>{

    private int account_id;
    private String ticker;
    private int position;

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public Integer getId() {
        return account_id;
    }

    @Override
    public void setId(Integer integer) {
        this.account_id = integer;
    }
}
