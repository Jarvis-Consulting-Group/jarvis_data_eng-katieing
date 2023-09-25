package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Entity;
import ca.jrvs.apps.trading.model.domain.Quote;
import com.sun.org.apache.xpath.internal.operations.Quo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class QuoteDao implements CrudRepository<Quote, String> {

    private static final String TABLE_NAME = "quote";
    private static final String ID_COLUMN_NAME = "ticker";

    private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class);
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;


    @Autowired
    public QuoteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME);
    }

    /**
     * hint: http://bit.ly/2sDz8hq DataAccessException family
     * @param quote must not be {@literal null}.
     * @return
     * @throws org.springframework.dao.DataAccessException for unexpected SQL result or SQL execution failure
     */
    @Override
    public Quote save(Quote quote) {
        if (existsById(quote.getTicker())) {
            int updatedRowNo = updateOne(quote);
            if (updatedRowNo != 1) {
                throw new DataRetrievalFailureException("Unable to update quote");
            }
        } else {
            addOne(quote);
        }
        return quote;
    }

    /**
     * helper method that saves one quote
     * @param quote
     */
    private void addOne(Quote quote) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(quote);
        int row = simpleJdbcInsert.execute(parameterSource);
        if (row != 1) {
            throw new IncorrectResultSizeDataAccessException("Failed to insert", 1, row);
        }
    }

    /**
     * helper method that updates one quote
     * @param quote
     * @return
     */
    private int updateOne(Quote quote) {
        String update_sql = "UPDATE " + TABLE_NAME + " SET last_price=?, bid_price=?, " +
                "bid_size=?, ask_price=?, ask_size=? WHERE " + ID_COLUMN_NAME + "=?";
        return jdbcTemplate.update(update_sql, makeUpdateValues(quote));
    }

    /**
     * helper method that makes sql update values objects
     * @param quote to be updated
     * @return UPDATE_SQL values
     */
    private Object[] makeUpdateValues(Quote quote) {
        return new Object[]{quote.getLastPrice(), quote.getBidPrice(), quote.getBidSize(), quote.getAskPrice(),
                quote.getAskSize(), quote.getTicker()};
    }

    /**
     * Updates quotes
     * @param entities must not be {@literal null}.
     * @throws IllegalArgumentException if a quote does not exist in the database
     * @return
     * @param <S>
     */
    @Override
    public <S extends Quote> Iterable<S> saveAll(Iterable<S> entities) {
        //does batch update/inserts make this more ACID(?) compliant?
        //never mind, I don't know how to do it because add vs update requires a check to the database
        //but so does save - a call for exists and then one to add/update
        //to see if it exists anyway. maybe after I implement findAllById()?
        entities.forEach(this::save);
        return entities;
    }

    /**
     * Find quote by ticker
     * @param ticker name
     * @return quote or Optional.empty if not found
     */
    @Override
    public Optional<Quote> findById(String ticker) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + " = ?";
        try {
            Quote quote = jdbcTemplate.queryForObject(query, BeanPropertyRowMapper.newInstance(Quote.class), ticker);
            return Optional.of(quote);
        } catch (EmptyResultDataAccessException e) {
            logger.debug("Can't find trader id: " + ticker, e);
            return Optional.empty();
        }

    }

    @Override
    public boolean existsById(String ticker) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + " = ?";
        int row = jdbcTemplate.queryForObject(query, Integer.class, ticker);
        return row == 1;
    }

    /**
     * return all quotes
     * @throws org.springframework.dao.DataAccessException if failed to update
     */
    @Override
    public List<Quote> findAll() {
        String query = "SELECT * FROM " + TABLE_NAME;
        return jdbcTemplate.query(query, BeanPropertyRowMapper.newInstance(Quote.class));
    }

    @Override
    public Iterable<Quote> findAllById(Iterable<String> strings) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public long count() {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        return jdbcTemplate.queryForObject(query, Long.class);
    }

    @Override
    public void deleteById(String ticker) {
        if (ticker.length() == 0) {
            throw new IllegalArgumentException("Id/ticker cannot be null");
        }
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + " = ?";
        jdbcTemplate.update(query, ticker);
    }

    @Override
    public void delete(Quote entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteAll(Iterable<? extends Quote> entities) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM " + TABLE_NAME;
        jdbcTemplate.update(query);
    }
}
