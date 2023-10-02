package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AccountDao extends JdbcCrudDao<Account>{

    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    private final String TABLE_NAME = "account";
    private final String ID_COLUMN = "id";
    private final String FK_COLUMN = "trader_id";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleInsert;

    @Autowired
    public AccountDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID_COLUMN);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public SimpleJdbcInsert getSimpleJdbcInsert() {
        return simpleInsert;
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
    Class<Account> getEntityClass() {
        return Account.class;
    }

    private Object[] makeUpdateValues(Account account) {
        return new Object[]{account.getTrader_id(), account.getAmount(), account.getId()};
    }

    @Override
    public int updateOne(Account account) {
        String update_sql = "UPDATE " + TABLE_NAME + " SET trader_id=?, " +
                "amount=? WHERE " + ID_COLUMN + "=?";
        return jdbcTemplate.update(update_sql, makeUpdateValues(account));
    }

    @Override
    public <S extends Account> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(Account entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void deleteByTraderId(Integer id) {
        List<Account> accounts = findAllByFk(id);
        deleteAll(accounts);
    }
}
