package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.ResourceNotFoundException;
import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class QuoteService {

    private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);

    private QuoteDao quoteDao;
    private MarketDataDao marketDataDao;

    @Autowired
    public QuoteService(QuoteDao quoteDao, MarketDataDao marketDataDao) {
        this.quoteDao = quoteDao;
        this.marketDataDao = marketDataDao;
    }

    /**
     * Update quote table against IEX source
     * -get all quotes from the db
     * -for each ticker get iexQuote
     * convert iexQuote to quote entity
     * persist quote to db
     *
     * @throws ResourceNotFoundException if ticker is not found from IEX
     * @throws org.springframework.dao.DataAccessException if unable to retrieve data
     * @throws IllegalArgumentException for invalid input
     */
    public List<Quote> updateMarketData() {
        List<String> tickers = findAllQuotes().stream().map(Quote::getTicker).collect(Collectors.toList());
        try {
            return saveQuotes(tickers);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException();
        }
    }

    /**
     * Helper method. Map an IexQuote to a Quote entity
     * Note: `iexQuote.getLatestPrice == null` if the stock market is closed
     * Make sure to set a default value for number field(s).
     * @param iexQuote
     * @return Quote
     */
    protected static Quote buildQuoteFromIexQuote(IexQuote iexQuote) {

        Quote quote = new Quote();
        quote.setTicker(iexQuote.getSymbol());
        quote.setLastPrice(iexQuote.getLatestPrice());
        quote.setAskPrice(iexQuote.getIexAskPrice()==null ? 0 : iexQuote.getIexAskPrice());
        quote.setAskSize(iexQuote.getIexAskSize()==null ? 0 : iexQuote.getIexAskSize());
        quote.setBidPrice(iexQuote.getIexBidPrice()==null ? 0 : iexQuote.getIexBidPrice());
        quote.setBidSize(iexQuote.getIexBidSize()==null ? 0 : iexQuote.getIexBidSize());
        return quote;

    }

    /**
     * Validate (against IEX) and save given tickers to quote table
     *
     * -get iexQuote(s)
     * -convert each iexQuote to quote entity
     * -persist the quote to db
     *
     * @param tickers a list of tickers
     * @throws IllegalArgumentException if ticker is not found from IEX
     * @return list of quotes
     */
    public List<Quote> saveQuotes(List<String> tickers) {
        List<Quote> quotes = new ArrayList<>();
        tickers.forEach(ticker -> quotes.add(saveQuote(ticker)));
        return quotes;
    }

    /**
     * Helper method
     * @param ticker
     * @return
     * @throws IllegalArgumentException if ticker is invalid
     */
    public Quote saveQuote(String ticker) {
        return saveQuote(buildQuoteFromIexQuote(findIexQuoteByTicker(ticker)));
    }


    /**
     * Find an IexQuote
     *
     * @param ticker id
     * @return IexQuote object
     * @throws IllegalArgumentException if ticker is invalid
     */
    public IexQuote findIexQuoteByTicker(String ticker) {
        return marketDataDao.findById(ticker)
                .orElseThrow(() -> new IllegalArgumentException(ticker + " is invalid"));
    }

    /**
     * Update a given quote to quote table without validation
     * @param quote
     * @return
     */
    public Quote saveQuote(Quote quote) {
        return quoteDao.save(quote);
    }

    /**
     * Find all quotes from the quote table
     * @return a list of quotes
     */
    public List<Quote> findAllQuotes() {
        return quoteDao.findAll();
    }
}
