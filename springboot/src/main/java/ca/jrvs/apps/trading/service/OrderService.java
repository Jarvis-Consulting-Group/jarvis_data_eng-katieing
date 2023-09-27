package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.dto.MarketOrderDto;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private AccountDao accountDao;
    private SecurityOrderDao securityOrderDao;
    private QuoteDao quoteDao;
    private PositionDao positionDao;

    @Autowired
    public OrderService(AccountDao accountDao, SecurityOrderDao securityOrderDao, QuoteDao quoteDao,
                        PositionDao positionDao) {
        this.accountDao = accountDao;
        this.securityOrderDao = securityOrderDao;
        this.quoteDao = quoteDao;
        this.positionDao = positionDao;
    }

    /**
     * Execute a market order
     *
     * -validate the order (e.g. size and ticker)
     * -create a securityOrder
     * -Handle buy or sell order
     *  -buy: check account balance (calls helper method)
     *  -sell: check position for the ticker/symbol (calls helper method)
     *  -update securityOrder.status
     * -save and return security order
     *
     * @param orderDto market order
     * @return SecurityOrder from security_order table
     * @throws org.springframework.dao.DataAccessException if unable to get quote from DAO
     * @throws IllegalArgumentException for invalid input
     */
    public SecurityOrder executeMarketOrder(MarketOrderDto orderDto) {
        //why not update market data first?

        Quote quote = quoteDao.findById(orderDto.getTicker())
                .orElseThrow(() -> new DataRetrievalFailureException("Unable to get quote."));
        Account account = accountDao.findById(orderDto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account cannot be found."));

        SecurityOrder order = new SecurityOrder();
        order.setAccount_id(orderDto.getAccountId());
        order.setSize(orderDto.getSize());
        order.setTicker(orderDto.getTicker());

        if (orderDto.getSize() > 0) { //buying!
            order.setPrice(quote.getAskPrice());
            handleBuyMarketOrder(order, account);
        } else if (orderDto.getSize() < 0) {
            order.setPrice(quote.getBidPrice());
            handleSellMarketOrder(order, account);
        } else {
            throw new IllegalArgumentException("Size must be nonzero");
        }

        return securityOrderDao.save(order);
    }

    /**
     * Helper method that executes a buy order
     * @param securityOrder to be used in data databse
     * @param account account
     */
    protected void handleBuyMarketOrder(SecurityOrder securityOrder, Account account) {
        double orderAmount = securityOrder.getSize() * securityOrder.getPrice();
        if (account.getAmount() >= orderAmount) { //order is valid
            account.setAmount(account.getAmount() - orderAmount);
            accountDao.save(account);
            securityOrder.setStatus("FILLED");
        } else {
            securityOrder.setStatus("CANCELLED");
            securityOrder.setNotes("Insufficient funds. Order amount is " + orderAmount);
        }
    }

    /**
     * helper method that executes a sell order
     * @param securityOrder to be saved in data database
     * @param account account
     */
    protected void handleSellMarketOrder(SecurityOrder securityOrder, Account account) {
        double orderAmount = securityOrder.getSize() * securityOrder.getPrice();
        Optional<Position> position = positionDao.findByAccountIdAndTicker(account.getId(), securityOrder.getTicker());

        if (position.isPresent() && (position.get().getPosition() + securityOrder.getSize() > 0)) {
            account.setAmount(account.getAmount() - orderAmount);
            accountDao.save(account);
            securityOrder.setStatus("FILLED");
        } else {
            securityOrder.setStatus("CANCELLED");
            securityOrder.setNotes("Insufficient position.");
        }
    }



}
