package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class PositionDao extends JdbcCrudDao<Position> {

    private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);
    private final String TABLE_NAME = "position";
    private final String ID_COLUMN = "account_id"; //it isn't really, but nice to have saved
    private final String FK_COLUMN = "ticker"; //it isn't really, but nice to use for findAllByFk

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PositionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public SimpleJdbcInsert getSimpleJdbcInsert() {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdColumnName() {
        return ID_COLUMN;
    }

    @Override
    public String getFkColumnName() {
        return FK_COLUMN;
    }

    @Override
    Class<Position> getEntityClass() {
        return Position.class;
    }

    @Override
    public <S extends Position> S save(S entity) {
        throw new UnsupportedOperationException("Position view is read-only");
    }

    public Optional<Position> findByAccountIdAndTicker(int account_id, String ticker) {
        Optional<Position> entity = Optional.empty();
        String selectSql = "SELECT * FROM " + TABLE_NAME + " WHERE account_id = ? AND ticker = ?";

        try {
            entity = Optional.ofNullable(jdbcTemplate
                    .queryForObject(selectSql, BeanPropertyRowMapper.newInstance(Position.class), account_id, ticker));
        } catch (IncorrectResultSizeDataAccessException e) {
            logger.debug("Can't find position for trader id: " + account_id + " and ticker " + ticker, e);
        }
        return entity;
    }

    public List<Position> findAllByAccountId(int account_id) {
        List<Integer> accountId = Collections.singletonList(account_id);
        return findAllById(accountId);
    }

    @Override
    public int updateOne(Position entity) {
        throw new UnsupportedOperationException("Position view is read-only");
    }

    @Override
    public <S extends Position> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Position view is read-only");
    }

    @Override
    public void deleteById(Integer id) {
        throw new UnsupportedOperationException("Position view is read-only");
    }

    @Override
    public void delete(Position entity) {
        throw new UnsupportedOperationException("Position view is read-only");

    }

    @Override
    public void deleteAll(Iterable<? extends Position> entities) {
        throw new UnsupportedOperationException("Position view is read-only");

    }

}
