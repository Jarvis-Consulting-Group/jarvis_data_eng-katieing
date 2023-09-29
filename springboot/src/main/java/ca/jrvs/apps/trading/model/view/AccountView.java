package ca.jrvs.apps.trading.model.view;

import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;

import java.util.HashSet;

public class AccountView {

    private Account account;
    private HashSet<PositionView> positions;
    private double estValue;

    public static class PositionView {

        private Position position;
        private Quote quote;
        private double value;

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public Quote getQuote() {
            return quote;
        }

        public void setQuote(Quote quote) {
            this.quote = quote;
        }

        public double getValue() {
            return value;
        }

        public void setValue() {
            this.value = this.position.getPosition() * this.quote.getLastPrice();
        }
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public HashSet<PositionView> getPositions() {
        return positions;
    }

    public void setPositions(HashSet<PositionView> positions) {
        this.positions = positions;
    }

    public double getEstValue() {
        return estValue;
    }

    public void setEstValue(double estValue) {
        this.estValue = estValue;
    }

    public void addPosition(PositionView positionView) {
        if (this.positions == null) {
            this.positions = new HashSet<>();
            estValue = 0;
        }
        this.positions.add(positionView);
        estValue += positionView.value;
    }
}
