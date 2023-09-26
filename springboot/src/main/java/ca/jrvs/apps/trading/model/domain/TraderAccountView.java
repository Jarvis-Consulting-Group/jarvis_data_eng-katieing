package ca.jrvs.apps.trading.model.domain;

import java.util.ArrayList;
import java.util.List;

public class TraderAccountView {

    private Trader trader;
    private List<Account> accounts;

    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    public int getTraderId() {
        return trader.getId();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Account account) {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        accounts.add(account);
    }
}
