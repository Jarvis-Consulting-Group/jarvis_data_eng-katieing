package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.AccountJpaDao;
import ca.jrvs.apps.trading.dao.TraderJpaDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class TraderAccountServiceIntTest {

    private TraderAccountView savedView;
    @Autowired
    private TraderAccountService traderAccountService;
    @Autowired
    private TraderJpaDao traderDao;
    @Autowired
    private AccountJpaDao accountDao;

    @Before
    public void setupTrader() {
        Trader trader = new Trader();
        trader.setFirst_name("katie");
        trader.setLast_name("ing");
        trader.setEmail("katieing7@gmail.com");
        trader.setDob(LocalDate.of(1994, 12, 11));
        trader.setCountry("canada");

        savedView = traderAccountService.createTraderAndAccount(trader);
    }

    @Test
    public void addBadTrader() {
        Trader emptyTrader = new Trader();
        try {
            traderAccountService.createTraderAndAccount(emptyTrader);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

        Trader noNameTrader = new Trader();
        noNameTrader.setCountry("usa");
        noNameTrader.setDob(LocalDate.of(1994,12,5));
        noNameTrader.setEmail("no_one@gmail.com");
        try {
            traderAccountService.createTraderAndAccount(noNameTrader);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void fundsAndDelete() {
        traderAccountService.deposit(savedView.getTraderId(), 100.37);
        double amount = accountDao.findById(savedView.getAccounts().get(0).getId()).get().getAmount();
        assertEquals(amount, 100.37, 0.001);

        try {
            traderAccountService.deleteTraderById(savedView.getTraderId());
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

        try {
            traderAccountService.deposit(savedView.getTraderId(), -3d);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

        Account secondAccount = new Account();
        secondAccount.setTraderId(savedView.getTraderId());
        secondAccount.setAmount(543.21);
        accountDao.save(secondAccount);

        traderAccountService.withdraw(savedView.getTraderId(), 400.01);
        List<Account> accounts = accountDao.getAccountByTraderId(savedView.getTraderId());
        double sum = accounts.stream().mapToDouble(Account::getAmount).sum();
        assertEquals(sum, 243.57,0.001);

        try {
            traderAccountService.withdraw(savedView.getTraderId(), 500d);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void deleteTrader() {
        traderAccountService.deleteTraderById(savedView.getTraderId());
        assertTrue(accountDao.getAccountByTraderId(savedView.getTraderId()).isEmpty());
        assertFalse(traderDao.existsById(savedView.getTraderId()));

        try {
            traderAccountService.deleteTraderById(5);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

}
