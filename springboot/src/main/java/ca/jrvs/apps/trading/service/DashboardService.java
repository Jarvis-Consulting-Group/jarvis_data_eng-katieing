package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.view.AccountView;
import ca.jrvs.apps.trading.model.view.PortfolioView;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class DashboardService {

    private TraderDao traderDao;
    private PositionDao positionDao;
    private AccountDao accountDao;
    private QuoteDao quoteDao;

    @Autowired
    public DashboardService(TraderDao traderDao, PositionDao positionDao, AccountDao accountDao, QuoteDao quoteDao) {
        this.traderDao = traderDao;
        this.positionDao = positionDao;
        this.accountDao = accountDao;
        this.quoteDao = quoteDao;
    }

    /**
     * Create and return a traderAccountView by traderId
     * -get trader account by id
     * -get trader info by id
     * -create and return traderAccountView
     *
     * @param traderId must not be null
     * @return traderAccountView
     * @throws IllegalArgumentException if traderId is null or not found
     */
    public TraderAccountView getTraderAccount(Integer traderId) {
        if (traderId == null) {
            throw new IllegalArgumentException("TraderId cannot be null");
        }

        TraderAccountView accountView = new TraderAccountView();
        accountView.setTrader(traderDao.findById(traderId)
                .orElseThrow(() -> new IllegalArgumentException("Trader cannot be found")));
        accountView.setAccounts(findAccountByTraderId(traderId));

        return accountView;
    }

    /**
     * Create and return portfolioView by traderId
     * -get account by traderId
     * -get positions by accountId
     * -create and return a portfolioView
     *
     * @param traderId must not be null
     * @return portfolioView
     * @throws IllegalArgumentException if traderId is null or not found
     */
    public PortfolioView getProfileViewByTraderId(Integer traderId) {
        if (traderId == null) {
            throw new IllegalArgumentException("TraderId cannot be null");
        }

        PortfolioView view = new PortfolioView();
        view.setTraderId(traderId);
        view.setPortfolio(new HashSet<>());

        List<Account> accounts = findAccountByTraderId(traderId);
        for (Account account : accounts) {
            AccountView accountView = new AccountView();
            accountView.setAccount(account);
            List<Position> positions = positionDao.findAllByAccountId(account.getId());
            for (Position position : positions) {
                Quote quote = quoteDao.findById(position.getTicker())
                        .orElseThrow(() -> new DataRetrievalFailureException("Quote for " +
                                position.getTicker() + " not found."));
                AccountView.PositionView positionView = new AccountView.PositionView();
                positionView.setPosition(position);
                positionView.setQuote(quote);
                positionView.setValue();
                accountView.addPosition(positionView);
            }
            view.addAccount(accountView);
        }

        return view;
    }

    /**
     * @throws IllegalArgumentException if traderId is not found
     */
    private List<Account> findAccountByTraderId(Integer traderId) {
        List<Account> accounts = accountDao.findAllByFk(traderId);
        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("Trader account(s) not found");
        } else {
            return accounts;
        }
    }
}
