package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class MarketDataDaoIntTest {

    private MarketDataDao dao;

    @Before
    public void init() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(50);

        MarketDataConfig marketDataConfig = new MarketDataConfig();
        marketDataConfig.setHost("https://cloud.iexapis.com/v1");
        marketDataConfig.setToken("");

        dao = new MarketDataDao(cm, marketDataConfig);
    }

    @Test
    public void findIexQuotesByTickers() throws IOException {
        //happy path
        List<IexQuote> quoteList = dao.findAllById(Arrays.asList("AAPL","FB"));
        assertEquals(2, quoteList.size());
        assertEquals("AAPL", quoteList.get(0).getSymbol());
    }

}
