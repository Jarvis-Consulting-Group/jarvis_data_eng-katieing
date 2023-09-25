package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Trader;
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
public class AccountDaoIntTest {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private TraderDao traderDao;

    private Account savedAccount;

    @Before
    public void insertOne() {
        Trader savedTrader = new Trader();
        savedTrader.setFirst_name("katie");
        savedTrader.setLast_name("ing");
        savedTrader.setEmail("katieing7@gmail.com");
        savedTrader.setCountry("Canada");
        savedTrader.setDob(LocalDate.of(1994,12,11));

        traderDao.save(savedTrader);

        savedAccount = new Account();
        savedAccount.setTrader_id(savedTrader.getId());
        savedAccount.setAmount(1000.00);

        accountDao.save(savedAccount);
    }

    @After
    public void deleteOne() {
        accountDao.deleteAll();
        traderDao.deleteAll();
    }

    @Test
    public void findAllById() {
        Optional<Account> account = accountDao.findById(savedAccount.getId());
        assertTrue(account.isPresent());

        List<Account> accounts = Lists
                .newArrayList(accountDao.findAllById(Arrays.asList(savedAccount.getId(), -1)));
        assertEquals(1, accounts.size());
        assertEquals(savedAccount.getAmount(), accounts.get(0).getAmount(), 0);
    }

    @Test
    public void addAndFindAll() {
        Account newAccount = new Account();
        newAccount.setTrader_id(savedAccount.getTrader_id());
        newAccount.setAmount(5.00);

        accountDao.save(newAccount);

        long count = accountDao.count();
        assertEquals(count, 2);

        List<Account> accounts = accountDao.findAll();
        double sum = accounts.stream().mapToDouble(Account::getAmount).sum();
        assertEquals(sum, 1005, 0);

        accountDao.deleteById(newAccount.getId());
    }

}
