package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataRetrievalFailureException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.junit.Assert.fail;

public class MarketDataDaoIntTest {

    private MarketDataDao dao;
    private MarketDataDao badHostDao;
    private MarketDataDao badTokenDao;

    @Before
    public void init() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(50);

        MarketDataConfig marketDataConfig = new MarketDataConfig();
        marketDataConfig.setHost("https://cloud.iexapis.com/v1");
        marketDataConfig.setToken(System.getenv("TOKEN"));

        dao = new MarketDataDao(cm, marketDataConfig);

        MarketDataConfig badHost = new MarketDataConfig();
        badHost.setHost("https://cloud.lexapis.com/v1");
        badHost.setToken(System.getenv("TOKEN"));

        badHostDao = new MarketDataDao(cm, badHost);

        MarketDataConfig badToken = new MarketDataConfig();
        badToken.setHost("https://cloud.iexapis.com/v1");
        badToken.setToken("hi");

        badTokenDao = new MarketDataDao(cm, badToken);
    }

    @Test
    public void findIexQuotesByTickers() throws IOException {
        //happy path
        List<IexQuote> quoteList = dao.findAllById(Arrays.asList("AAPL","FB"));
        assertEquals(2, quoteList.size());
        assertEquals("AAPL", quoteList.get(0).getSymbol());

        //sad path
        try {
            dao.findAllById(Arrays.asList("AA5PL", "FB2"));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

        //another sad path
        try {
            badHostDao.findAllById(Arrays.asList("AAPL", "FB"));
            fail();
        } catch (DataRetrievalFailureException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

        //another sad path
        try {
            badTokenDao.findAllById(Arrays.asList("AAPL", "FB"));
            fail();
        } catch (DataRetrievalFailureException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void findByTicker() {
        //happy path
        String ticker = "AAPL";
        IexQuote iexQuote = dao.findById(ticker).get();
        assertEquals(ticker, iexQuote.getSymbol());

        //another happy path
        IexQuote lowercaseQuote = dao.findById("aapl").get();
        assertEquals(ticker.toUpperCase(), lowercaseQuote.getSymbol());

        //sad path
        try {
            dao.findById("FB2");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

        //another sad path
        try {
            dao.findById("A A P L");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }

}
