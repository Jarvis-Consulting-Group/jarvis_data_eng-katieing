package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.service.QuoteService;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class SecurityOrderDaoIntTest {

    @Autowired
    private SecurityOrderDao securityOrderDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private TraderDao traderDao;

    @Autowired
    private QuoteService quoteService;

    private SecurityOrder savedOrder;

    @Before
    public void insertOne() {

        quoteService.saveQuote("aapl");

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

        savedOrder = new SecurityOrder();
        savedOrder.setAccount_id(savedAccount.getId());
        savedOrder.setTicker("AAPL");
        savedOrder.setSize(3);
        savedOrder.setStatus("FILLED");

        securityOrderDao.save(savedOrder);
    }

    @After
    public void deleteOne() {
        securityOrderDao.deleteAll();
        accountDao.deleteAll();
        traderDao.deleteAll();
    }

    @Test
    public void findAllById() {
        Optional<SecurityOrder> securityOrder = securityOrderDao.findById(savedOrder.getId());
        assertTrue(securityOrder.isPresent());

        List<SecurityOrder> securityOrders = Lists
                .newArrayList(securityOrderDao.findAllById(Arrays.asList(savedOrder.getId(), -1)));
        assertEquals(1, securityOrders.size());
        assertEquals(savedOrder.getSize(), securityOrders.get(0).getSize(), 0);
    }

    @Test
    public void addAndFindAll() {
        SecurityOrder newOrder = new SecurityOrder();
        newOrder.setAccount_id(savedOrder.getAccount_id());
        newOrder.setTicker("AAPL");
        newOrder.setSize(4);
        newOrder.setStatus("CANCELLED");

        securityOrderDao.save(newOrder);

        long count = securityOrderDao.count();
        assertEquals(count, 2);

        List<SecurityOrder> securityOrders = securityOrderDao.findAll();
        double sum = securityOrders.stream().mapToDouble(SecurityOrder::getSize).sum();
        assertEquals(sum, 7, 0);

        accountDao.deleteById(newOrder.getId());
    }

    @Test
    public void deleteByIds() {
        SecurityOrder newOrder = new SecurityOrder();
        newOrder.setAccount_id(savedOrder.getAccount_id());
        newOrder.setTicker("AAPL");
        newOrder.setSize(4);
        newOrder.setStatus("CANCELLED");
        securityOrderDao.save(newOrder);

        securityOrderDao.deleteAll(Arrays.asList(savedOrder, newOrder));
        assertFalse(securityOrderDao.existsById(savedOrder.getId()));
        assertFalse(securityOrderDao.existsById(newOrder.getId()));
    }


}
