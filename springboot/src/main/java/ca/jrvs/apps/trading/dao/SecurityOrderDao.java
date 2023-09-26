package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SecurityOrderDao extends JdbcCrudDao<SecurityOrder> {


    private static final Logger logger = LoggerFactory.getLogger(SecurityOrderDao.class);

    private final String TABLE_NAME = "security_order";
    private final String ID_COLUMN = "id";
    private final String FK_COLUMN = "account_id";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleInsert;

    @Autowired
    public SecurityOrderDao(DataSource dataSource) {
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
    Class<SecurityOrder> getEntityClass() {
        return SecurityOrder.class;
    }

    @Override
    public int updateOne(SecurityOrder entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public <S extends SecurityOrder> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void delete(SecurityOrder entity) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void deleteByAccountId(Integer id) {
        List<SecurityOrder> orders = findAllByFk(id);
        deleteAll(orders);
    }

}
