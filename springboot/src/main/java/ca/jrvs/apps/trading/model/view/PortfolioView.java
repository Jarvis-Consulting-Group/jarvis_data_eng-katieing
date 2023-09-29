package ca.jrvs.apps.trading.model.view;

import java.util.HashSet;

public class PortfolioView {

    private Integer traderId;

    private HashSet<AccountView> portfolio;

    public Integer getTraderId() {
        return traderId;
    }

    public void setTraderId(Integer traderId) {
        this.traderId = traderId;
    }

    public HashSet<AccountView> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(HashSet<AccountView> portfolio) {
        this.portfolio = portfolio;
    }

    public void addAccount(AccountView accountView) {
        this.portfolio.add(accountView);
    }

}
