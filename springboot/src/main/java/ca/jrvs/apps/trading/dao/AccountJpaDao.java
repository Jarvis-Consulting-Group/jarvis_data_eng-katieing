package ca.jrvs.apps.trading.dao;


import ca.jrvs.apps.trading.model.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface AccountJpaDao extends JpaRepository<Account, Integer> {

    List<Account> getAccountByTraderId(Integer traderId);

}
