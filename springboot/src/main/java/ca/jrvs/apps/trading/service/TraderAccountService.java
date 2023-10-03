package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TraderAccountService {

    private TraderDao traderDao;
    private AccountDao accountDao;
    private PositionDao positionDao;
    private SecurityOrderDao securityOrderDao;

    @Autowired
    public TraderAccountService(TraderDao traderDao, AccountDao accountDao, PositionDao positionDao,
                                SecurityOrderDao securityOrderDao) {
        this.traderDao = traderDao;
        this.accountDao = accountDao;
        this.positionDao = positionDao;
        this.securityOrderDao = securityOrderDao;
    }

    /**
     * Create a new trader and initialize a new account with 0 amount.
     * -validate user input (all fields must be nonempty)
     * -create a trader
     * -create an account
     * -create, setup, and return new traderAccountView
     *
     * Assumption: to simplify the logic, each trader has only one account where traderId == accountId
     *
     * @param trader cannot be null. All fields cannot be null except for id
     * @return traderAccountView
     * @throws IllegalArgumentException if a trader has null fields or id is not null.
     */
    public TraderAccountView createTraderAndAccount(Trader trader) {
        //validate user input
        if (trader.getId() != null || trader.getFirst_name() == null || trader.getLast_name() == null ||
                trader.getCountry() == null || trader.getEmail() == null || trader.getDob() == null) {
            throw new IllegalArgumentException("Trader ID must be null. All other trader fields must be not null");
        }

        //create a trader and account
        traderDao.save(trader);
        Account traderAccount = new Account();
        traderAccount.setTrader_id(trader.getId());
        traderAccount.setAmount(0d);
        accountDao.save(traderAccount);

        //create and return traderAccountView
        TraderAccountView traderAccountView = new TraderAccountView();
        traderAccountView.setTrader(trader);
        traderAccountView.addAccount(traderAccount);
        return traderAccountView;
    }

    /**
     * A trader can be deleted iff it has no open position and 0 cash balance
     * -validate traderId
     * -get trader account by traderId and check account balance
     * -get positions by accountId and check positions
     * -delete all securityOrders, account, trader (in this order)
     *
     * @param traderId must not be null
     * @throws IllegalArgumentException if traderId is null or not found or unable to delete
     */
    public void deleteTraderById(Integer traderId) {
        if (traderId == null) {
            throw new IllegalArgumentException("Trader ID cannot be null");
        } else if (!traderDao.existsById(traderId)) {
            throw new IllegalArgumentException("Trader not found in the database");
        } else {
            List<Account> traderAccounts = accountDao.findAllByFk(traderId);
            if (traderAccounts.stream().mapToDouble(Account::getAmount).sum() != 0) {
                throw new IllegalArgumentException("Trader account(s) has remaining positive balance. " +
                        "Empty account(s) before deleting trader");
            } else {
                List<Position> positions = positionDao.findAllById(traderAccounts.stream()
                        .map(Account::getId).collect(Collectors.toList()));
                if (positions.stream().mapToDouble(Position::getPosition).sum() != 0) {
                    throw new IllegalArgumentException("Trader has open positions. " +
                            "Sell positions before deleting trader");
                } else {
                    for (Account account : traderAccounts) {
                        securityOrderDao.deleteByAccountId(account.getId());
                    }
                    accountDao.deleteByTraderId(traderId);
                    traderDao.deleteById(traderId);
                }
            }
        }
    }

    /**
     * Deposit a fund to an account by traderId
     * -validate user input
     * -account = accountDao.findByTraderId
     * -accountDao.updateAmountById
     *
     * @param traderId must not be null
     * @param fund must be greater than 0
     * @return updated account
     * @throws IllegalArgumentException if traderId is null or not found, or fund is <= 0
     */
    public Account deposit(Integer traderId, Double fund) {
        if (traderId == null || !traderDao.existsById(traderId)) {
            throw new IllegalArgumentException("Trader must be nonnull and must exist in database.");
        } else if (fund <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0");
        }
        Account account = accountDao.findAllByFk(traderId).get(0);
        account.setAmount(account.getAmount() + fund);
        return accountDao.save(account);
    }

    /**
     * Withdraw a fund to an account by traderId
     * -validate user input
     * -account = accountDao.findByTraderId
     * -accountDao.updateAmountById
     */
    public Account withdraw (Integer traderId, Double fund) {
        if (traderId == null || !traderDao.existsById(traderId)) {
            throw new IllegalArgumentException("Trader must be nonnull and must exist in database.");
        } else if (fund <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0");
        }

        List<Account> accounts = accountDao.findAllByFk(traderId);
        int i = 0;
        while (i < accounts.size() && accounts.get(i).getAmount() < fund) {
            i++;
        }
        if (i == accounts.size()) {
            throw new IllegalArgumentException("Insufficient funds in all accounts.");
        }
        Account chosenAccount = accounts.get(i);
        chosenAccount.setAmount(chosenAccount.getAmount() - fund);
        return accountDao.save(chosenAccount);
    }
}
