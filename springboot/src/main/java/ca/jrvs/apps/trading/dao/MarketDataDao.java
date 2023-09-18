package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class MarketDataDao implements CrudRepository<IexQuote, String> {

    private static final String IEX_BATCH_PATH = "/stock/market/batch?symbols=%s&types=quote&token=";
    private final String IEX_BATCH_URL;

    //private Logger logger = LoggerFactory.getLogger(MarketDataDao.class);
    private HttpClientConnectionManager httpClientConnectionManager;
    private JSONObject thisQuote;

    @Autowired
    public MarketDataDao(HttpClientConnectionManager httpClientConnectionManager, MarketDataConfig marketDataConfig) {
        this.httpClientConnectionManager = httpClientConnectionManager;
        IEX_BATCH_URL = marketDataConfig.getHost() + IEX_BATCH_PATH + marketDataConfig.getToken();
    }

    /**
     * Execute a get and return http entity/body as a string
     *
     * Tip: use EntityUtils.toString to process HTTP entity
     *
     * @param url resource URL
     * @return http response body or Optional empty for 404 response
     * @throws DataRetrievalFailureException if HTTP failed or status code is unexpected
     */
    private Optional<String> executeHttpGet(String url) {

        try (CloseableHttpClient client = getHttpClient()) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = client.execute(httpGet);
            return Optional.of(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            throw new DataRetrievalFailureException("HTTP Failed");
        }
    }

    /**
     * Borrow an HTTP client from the httpClientConnectionManager
     * @return a httpClient
     */
    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                //prevent connectionManager shutdown when calling httpClient.close()
                .setConnectionManagerShared(true)
                .build();
    }

    /**
     * Get an IexQuote (helper method which class findAllById)
     *
     * @param ticker must not be {@literal null}.
     * @throws IllegalArgumentException if a given ticker is invalid
     * @throws DataRetrievalFailureException if HTTP request failed
     * @return Optional IexQuote object
     */
    @Override
    public Optional<IexQuote> findById(String ticker) {
        Optional<IexQuote> iexQuote;
        List<IexQuote> quotes = findAllById(Collections.singletonList(ticker));

        if (quotes.size() == 0) {
            return Optional.empty();
        } else if (quotes.size() == 1) {
            iexQuote = Optional.of(quotes.get(0));
        } else {
            throw new DataRetrievalFailureException("Unexpected number of quotes");
        }
        return iexQuote;
    }

    /**
     * Get quotes from IEX
     * @param tickers is a list of tickers
     * @return a list of IexQuote objects
     * @throws IllegalArgumentException if any ticker is invalid or tickers is empty
     * @throws DataRetrievalFailureException if HTTP request failed
     */
    @Override
    public List<IexQuote> findAllById(Iterable<String> tickers) {

        int tickerCount = 0;
        StringBuilder builder = new StringBuilder();
        for (String ticker : tickers) {
            builder.append(ticker).append(',');
            tickerCount++;
        }

        if (tickerCount == 0) {
            throw new IllegalArgumentException("Tickers is empty");
        }

        Optional<String> body = executeHttpGet(String.format(IEX_BATCH_URL, builder));
        List<IexQuote> quotes = new ArrayList<>();

        //create iexquote for each block
        if (body.isPresent()) {
            String bodyText = body.get();
            JSONObject allQuotes = new JSONObject(bodyText);

            if (allQuotes.length() != tickerCount) {
                throw new IllegalArgumentException("Invalid ticker");
            }

            for (String quote : allQuotes.keySet()) {
                IexQuote newQuote = new IexQuote((JSONObject) ((JSONObject) allQuotes.get(quote)).get("quote"));
                quotes.add(newQuote);
            }
        }

        return quotes;
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public Iterable<IexQuote> findAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(IexQuote entity) {

    }

    @Override
    public void deleteAll(Iterable<? extends IexQuote> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends IexQuote> S save(S entity) {
        return null;
    }

    @Override
    public <S extends IexQuote> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }
}
