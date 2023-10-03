package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
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
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class TraderDaoIntTest {

    @Autowired
    private TraderDao traderDao;

    private Trader savedTrader;

    @Before
    public void insertOne() {
        savedTrader = new Trader();
        savedTrader.setFirst_name("katie");
        savedTrader.setLast_name("ing");
        savedTrader.setEmail("katieing7@gmail.com");
        savedTrader.setCountry("Canada");
        savedTrader.setDob(LocalDate.of(1994,12,11));

        traderDao.save(savedTrader);
    }

    @After
    public void deleteOne() {
        traderDao.deleteAll();
    }

    @Test
    public void findAllById() {
        Optional<Trader> trader = traderDao.findById(savedTrader.getId());
        assertTrue(trader.isPresent());

        List<Trader> traders = Lists
                .newArrayList(traderDao.findAllById(Arrays.asList(savedTrader.getId(), -1)));
        assertEquals(1, traders.size());
        assertEquals(savedTrader.getCountry(), traders.get(0).getCountry());
    }

    @Test
    public void addAndFindAll() {
        Trader newTrader = new Trader();
        newTrader.setFirst_name("taylor");
        newTrader.setLast_name("swift");
        newTrader.setDob(LocalDate.of(1989, 12, 13));
        newTrader.setCountry("usa");
        newTrader.setEmail("tswift@gmail.com");

        traderDao.save(newTrader);

        long count = traderDao.count();
        assertEquals(count, 2);

        List<Trader> traders = traderDao.findAll();
        for (Trader trader : traders) {
            if (trader.getFirst_name().equals("katie")) {
                assertTrue(true);
            } else if (trader.getFirst_name().equals("taylor")) {
                assertTrue(true);
            } else {
                fail();
            }
        }

        traderDao.deleteById(newTrader.getId());
    }

}
