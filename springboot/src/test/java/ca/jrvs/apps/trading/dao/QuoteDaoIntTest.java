package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteDaoIntTest {

    @Autowired
    private QuoteDao quoteDao;

    private Quote savedQuote;

    @Before
    public void insertSome() {
        savedQuote = new Quote();
        savedQuote.setAskPrice(10d);
        savedQuote.setAskSize(10);
        savedQuote.setBidPrice(10.2d);
        savedQuote.setBidSize(10);
        savedQuote.setId("aapl");
        savedQuote.setLastPrice(10.1d);
        quoteDao.save(savedQuote);

        savedQuote.setTicker("amzn");
        savedQuote.setBidSize(2);
        savedQuote.setAskSize(12);
        quoteDao.save(savedQuote);

        savedQuote.setTicker("atz");
        savedQuote.setBidSize(29);
        savedQuote.setAskSize(19);
        quoteDao.save(savedQuote);

        savedQuote.setTicker("aapl");
        savedQuote.setBidSize(10);
        savedQuote.setAskSize(10);
    }


    @Test
    public void saveOne() {
        savedQuote.setAskSize(11);
        quoteDao.save(savedQuote);
        Optional<Quote> updated = quoteDao.findById(savedQuote.getId());
        assertEquals(updated.get().getAskSize(), savedQuote.getAskSize());
    }

    @Test
    public void checkExistsById() {
        assertTrue(quoteDao.existsById("aapl"));
        assertFalse(quoteDao.existsById("fb"));
    }

    @Test
    public void findByTicker() {
        Optional<Quote> foundQuote = quoteDao.findById("aapl");
        assertTrue(foundQuote.isPresent());
        assertEquals(savedQuote.getId(), foundQuote.get().getId());
        assertEquals(savedQuote.getAskPrice(), foundQuote.get().getAskPrice());

        Optional<Quote> nonexistentQuote = quoteDao.findById("apl");
        assertFalse(nonexistentQuote.isPresent());

        Optional<Quote> addedQuote = quoteDao.findById("amzn");
        assertTrue(addedQuote.isPresent());
        assertEquals(addedQuote.get().getId(), "amzn");
    }

    @Test
    public void findAll() {
        List<String> tickers = new ArrayList<>();
        quoteDao.findAll().forEach(quote -> tickers.add(quote.getTicker()));
        assertEquals(tickers.size(), 3);
    }

    @Test
    public void count() {
        assertEquals(quoteDao.count(), 3);
    }

    @Test
    public void saveAll() {
        Quote newOne = new Quote();
        newOne.setAskPrice(20d);
        newOne.setAskSize(5);
        newOne.setBidPrice(15.2d);
        newOne.setBidSize(16);
        newOne.setId("veqt");
        newOne.setLastPrice(25.1d);
        quoteDao.save(newOne);

        Iterable<Quote> quotes = quoteDao.findAll();
        HashMap<String, Integer> bidSizes = new HashMap<>();
        for (Quote quote : quotes) {
            bidSizes.put(quote.getId(), quote.getBidSize());
            quote.setBidSize(quote.getBidSize()+2);
        }
        quoteDao.saveAll(quotes);
        Iterable<Quote> updatedBidSizes = quoteDao.findAll();
        for (Quote quote : updatedBidSizes) {
            assertEquals((int) quote.getBidSize() , bidSizes.get(quote.getId())+2);
        }
    }

    @Test
    public void deleteById() {
        quoteDao.deleteById("amzn");
        assertFalse(quoteDao.existsById("amzn"));
        assertTrue(quoteDao.existsById("aapl"));
    }

    @Test
    public void deleteAll() {
        quoteDao.deleteAll();
        assertEquals(quoteDao.count(), 0);
    }

    @After
    public void deleteOne() {
        quoteDao.deleteAll();
    }

}
