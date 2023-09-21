package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class QuoteServiceIntTest {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceIntTest.class);

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private QuoteDao quoteDao;

    @Before
    public void setup() {
        quoteDao.deleteAll();
    }

    @Test
    public void buildQuote() {
        IexQuote iexQuote = quoteService.findIexQuoteByTicker("aapl");
        Quote quote = QuoteService.buildQuoteFromIexQuote(iexQuote);
        assertEquals(quote.getTicker(), iexQuote.getSymbol());
        assertEquals(quote.getLastPrice(), iexQuote.getLatestPrice());
    }

    @Test
    public void findIexQuoteByTicker() {
        IexQuote iexQuote = quoteService.findIexQuoteByTicker("sbux");
        assertEquals(iexQuote.getSymbol(), "SBUX");
    }

    @Test
    public void updateMarketData() {
        List<Quote> quotes = quoteService.saveQuotes(Arrays.asList("AAPL", "SBUX", "FB", "AMZN"));
        for (Quote quote : quotes) {
            quote.setLastPrice(0d);
            quoteDao.save(quote);
        }
        List<Quote> zeroPrice = quoteService.findAllQuotes();
        for (Quote quote : zeroPrice) {
            assertEquals(quote.getLastPrice(), 0, 0);
        }

        quoteService.updateMarketData();
        List<Quote> updatedData = quoteService.findAllQuotes();
        for (Quote quote : updatedData) {
            assertNotEquals(quote.getLastPrice(), 0, 0);
        }
    }

    @Test
    public void saveQuotes() {
        List<String> goodTickers = Arrays.asList("aapl", "sbux", "fb", "amzn");
        quoteService.saveQuotes(goodTickers);
        assertEquals(quoteDao.count(), 4);

        quoteDao.deleteAll();

        List<String> badTickers = Arrays.asList("aapl", "sbuz", "fb", "atz");
        try {
            List<Quote> quotes = quoteService.saveQuotes(badTickers);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        } finally {
            logger.debug("Number of quotes added " + quoteDao.count());
        }
    }

    @Test
    public void saveQuote() {
        Quote putOne = quoteService.saveQuote("fb");
        IexQuote check = quoteService.findIexQuoteByTicker("fb");
        assertEquals(putOne.getTicker(), check.getSymbol());
        assertEquals(putOne.getLastPrice(), check.getLatestPrice());

        //bad ticker
        try {
            quoteService.saveQuote("atz");
            fail();
        } catch (IllegalArgumentException e ) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void  findAllQuotes() {
        List<String> goodTickers = Arrays.asList("AAPL", "SBUX", "FB", "AMZN");
        quoteService.saveQuotes(goodTickers);
        HashSet<String> tickerSymbols = new HashSet<>(goodTickers);

        List<Quote> foundQuotes = quoteService.findAllQuotes();
        foundQuotes.forEach(quote -> assertTrue(tickerSymbols.contains(quote.getTicker())));

    }

}
