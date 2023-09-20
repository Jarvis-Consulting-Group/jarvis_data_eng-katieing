package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.config.MarketDataConfig;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class MarketDataDao implements CrudRepository<IexQuote, String> {

    private static final String IEX_BATCH_PATH = "/stock/market/batch?symbols=%s&types=quote&token=";
    private final String IEX_BATCH_URL;

    private Logger logger = LoggerFactory.getLogger(MarketDataDao.class);
    private HttpClientConnectionManager httpClientConnectionManager;

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
            if (response.getStatusLine().getStatusCode() == 200) {
                return Optional.of(EntityUtils.toString(response.getEntity()));
            } else if (response.getStatusLine().getStatusCode() == 404) {
                return Optional.empty();
            } else {
                throw new DataRetrievalFailureException("Retrieval error: " + response.getStatusLine());
            }
        } catch (IOException e) {
            throw new DataRetrievalFailureException("HTTP Failed ", e);
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

        String symbols = StringUtils.join(tickers, ",");
        if (symbols.equals("")) {
            throw new IllegalArgumentException("No tickers provided");
        }
        Optional<String> body = executeHttpGet(String.format(IEX_BATCH_URL, symbols));

        List<IexQuote> quotes = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode allQuotes = objectMapper.readTree(body.get());

            for (String ticker : tickers) {
                ticker = ticker.toUpperCase();
                if (allQuotes.has(ticker)) {
                    IexQuote quote = objectMapper.treeToValue(allQuotes.get(ticker).get("quote"), IexQuote.class);
                    quotes.add(quote);
                } else {
                    throw new IllegalArgumentException("Invalid ticker: " + ticker);
                }
            }
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Invalid ticker(s): " + tickers, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse quote: ", e);
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
