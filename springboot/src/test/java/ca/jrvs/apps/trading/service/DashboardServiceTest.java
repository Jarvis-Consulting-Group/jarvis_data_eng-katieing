package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.Trader;
import ca.jrvs.apps.trading.model.view.PortfolioView;
import ca.jrvs.apps.trading.model.view.TraderAccountView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardServiceTest {

    @Mock
    private TraderDao traderDao;
    @Mock
    private PositionDao positionDao;
    @Mock
    private AccountDao accountDao;
    @Mock
    private QuoteDao quoteDao;

    @InjectMocks
    private DashboardService dashboardService;

    private Trader trader;

    @Before
    public void initTrader() {

        trader = new Trader();
        trader.setId(1);
        trader.setFirst_name("katie");
        when(traderDao.findById(1)).thenReturn(Optional.of(trader));

        Account account1 = new Account();
        account1.setTraderId(1);
        account1.setAmount(100d);
        account1.setId(1);

        Account account2 = new Account();
        account2.setTraderId(1);
        account2.setAmount(999d);
        account2.setId(2);

        when(accountDao.findAllByFk(1)).thenReturn(Arrays.asList(account1, account2));

        Position apple1 = new Position();
        apple1.setTicker("AAPL");
        apple1.setPosition(3);
        apple1.setAccount_id(1);
        Position facebook1 = new Position();
        facebook1.setTicker("FB");
        facebook1.setPosition(9);
        facebook1.setAccount_id(1);
        when(positionDao.findAllByAccountId(1)).thenReturn(Arrays.asList(apple1, facebook1));

        Position apple2 = new Position();
        apple2.setTicker("AAPL");
        apple2.setPosition(6);
        apple2.setId(2);
        when(positionDao.findAllByAccountId(2)).thenReturn(Collections.singletonList(apple2));

        Quote aaplQuote = new Quote();
        aaplQuote.setTicker("AAPL");
        aaplQuote.setLastPrice(100d);
        when(quoteDao.findById("AAPL")).thenReturn(Optional.of(aaplQuote));

        Quote fbQuote = new Quote();
        fbQuote.setTicker("FB");
        fbQuote.setLastPrice(9d);
        when(quoteDao.findById("FB")).thenReturn(Optional.of(fbQuote));
    }

    @Test
    public void portfolioView() {
        PortfolioView portfolioView = dashboardService.getProfileViewByTraderId(1);

        assertEquals(1, (int) portfolioView.getTraderId());
    }

    @Test
    public void traderView() {
        TraderAccountView traderAccountView = dashboardService.getTraderAccount(1);

        assertEquals(traderAccountView.getTraderId(), 1);
    }
}
