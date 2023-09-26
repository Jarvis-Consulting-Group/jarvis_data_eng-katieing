package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.service.QuoteService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class PositionDaoIntTest {

    @Autowired
    private PositionDao positionDao;

    @Autowired
    private SecurityOrderDao securityOrderDao;

    @Autowired
    private AccountDao accountDao;

    private int accountId;

    @Autowired
    private TraderDao traderDao;

    @Autowired
    private QuoteService quoteService;

    @Before
    public void init() {
        quoteService.saveQuote("aapl");
        quoteService.saveQuote("fb");

        Trader savedTrader = new Trader();
        savedTrader.setFirst_name("katie");
        savedTrader.setLast_name("ing");
        savedTrader.setEmail("katieing7@gmail.com");
        savedTrader.setCountry("Canada");
        savedTrader.setDob(LocalDate.of(1994,12,11));

        traderDao.save(savedTrader);

        Account savedAccount = new Account();
        savedAccount.setTrader_id(savedTrader.getId());
        savedAccount.setAmount(10000.00);

        accountDao.save(savedAccount);
        accountId = savedAccount.getId();

        SecurityOrder savedOrder = new SecurityOrder();
        savedOrder.setAccount_id(accountId);
        savedOrder.setTicker("AAPL");
        savedOrder.setSize(3);
        savedOrder.setStatus("FILLED");

        securityOrderDao.save(savedOrder);

        SecurityOrder newOrder = new SecurityOrder();
        newOrder.setAccount_id(accountId);
        newOrder.setTicker("AAPL");
        newOrder.setSize(1);
        newOrder.setStatus("FILLED");

        securityOrderDao.save(newOrder);

        SecurityOrder invalidOrder = new SecurityOrder();
        invalidOrder.setAccount_id(accountId);
        invalidOrder.setTicker("AAPL");
        invalidOrder.setSize(1);
        invalidOrder.setStatus("CANCELLED");

        securityOrderDao.save(invalidOrder);
    }

    @Test
    public void existsById() {
        assertTrue(positionDao.existsById(accountId));
        assertFalse(positionDao.existsById(accountId+1));
    }

    @Test
    public void findByAccountIdAndTicker() {
        Optional<Position> position = positionDao.findByAccountIdAndTicker(accountId, "AAPL");
        assertTrue(position.isPresent());
        assertEquals(position.get().getPosition(), 4);

        try {
            positionDao.findByAccountIdAndTicker(accountId, "FB");
        } catch (IncorrectResultSizeDataAccessException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void findAllByAccountId() {
        SecurityOrder newOrder = new SecurityOrder();
        newOrder.setAccount_id(accountId);
        newOrder.setTicker("FB");
        newOrder.setSize(7);
        newOrder.setStatus("FILLED");

        securityOrderDao.save(newOrder);

        List<Position> positions = positionDao.findAllByAccountId(accountId);
        for (Position position : positions) {
            assertEquals(position.getAccount_id(), accountId);
            if (position.getTicker().equals("AAPL")) {
                assertEquals(position.getPosition(), 4);
            } else if (position.getTicker().equals("FB")) {
                assertEquals(position.getPosition(), 7);
            } else {
                fail();
            }
        }

        assertEquals(positionDao.count(), 2);
    }

    @After
    public void delete() {
        securityOrderDao.deleteAll();
        accountDao.deleteAll();
        traderDao.deleteAll();
    }



}
