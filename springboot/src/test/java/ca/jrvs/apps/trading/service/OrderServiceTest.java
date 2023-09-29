package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dto.MarketOrderDto;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataRetrievalFailureException;
import org.yaml.snakeyaml.error.Mark;

import java.util.Optional;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    //capture parameter when calling securityOrderDao.save
    @Captor
    ArgumentCaptor<SecurityOrder> captorSecurityOrder;

    //mock all dependencies
    @Mock
    private AccountDao accountDao;
    @Mock
    private SecurityOrderDao securityOrderDao;
    @Mock
    private QuoteDao quoteDao;
    @Mock
    private PositionDao positionDao;

    //injecting mocked dependencies to the testing class via constructor
    @InjectMocks
    private OrderService orderService;

    private Account account;
    private Position position;

    @Before
    public void setupAccounts() {
        account = new Account();
        account.setId(1);
        account.setAmount(100d);
        when(accountDao.findById(1)).thenReturn(Optional.of(account));
        when(accountDao.save(any(Account.class))).thenReturn(account);

        Quote buyQuote = new Quote();
        buyQuote.setTicker("SBUX");
        buyQuote.setAskPrice(5d);
        when(quoteDao.findById("SBUX")).thenReturn(Optional.of(buyQuote));

        Quote sellQuote = new Quote();
        sellQuote.setTicker("ATZ");
        sellQuote.setBidPrice(8d);
        when(quoteDao.findById("ATZ")).thenReturn(Optional.of(sellQuote));

        when(quoteDao.findById("FB")).thenReturn(Optional.empty());

        position = new Position();
        position.setTicker("ATZ");
        position.setPosition(10);
        when(positionDao.findByAccountIdAndTicker(1, "ATZ")).thenReturn(Optional.of(position));
    }

    @Test
    public void badOrder() {
        MarketOrderDto marketOrderDto = new MarketOrderDto();
        marketOrderDto.setAccountId(1);
        marketOrderDto.setTicker("FB");
        marketOrderDto.setSize(1);

        try {
            orderService.executeMarketOrder(marketOrderDto);
        } catch (DataRetrievalFailureException e) {
            assertTrue(true);
        }
    }

    @Test
    public void executeValidBuy() {
        MarketOrderDto marketOrderDto = new MarketOrderDto();
        marketOrderDto.setAccountId(1);
        marketOrderDto.setTicker("SBUX");
        marketOrderDto.setSize(3);

        orderService.executeMarketOrder(marketOrderDto);

        verify(securityOrderDao).save(captorSecurityOrder.capture());

        assertEquals(captorSecurityOrder.getValue().getStatus(), "FILLED");
        assertEquals(account.getAmount(), 85d);
    }

    @Test
    public void invalidBuy() {
        MarketOrderDto marketOrderDto = new MarketOrderDto();
        marketOrderDto.setAccountId(1);
        marketOrderDto.setTicker("SBUX");
        marketOrderDto.setSize(21);

        orderService.executeMarketOrder(marketOrderDto);

        verify(securityOrderDao).save(captorSecurityOrder.capture());

        assertEquals(captorSecurityOrder.getValue().getStatus(), "CANCELLED");
        assertEquals(account.getAmount(), 100d);
    }

    @Test
    public void executeValidSell() {
        MarketOrderDto marketOrderDto = new MarketOrderDto();
        marketOrderDto.setAccountId(1);
        marketOrderDto.setTicker("ATZ");
        marketOrderDto.setSize(-3);

        orderService.executeMarketOrder(marketOrderDto);

        verify(securityOrderDao).save(captorSecurityOrder.capture());

        assertEquals(captorSecurityOrder.getValue().getStatus(), "FILLED");
        assertEquals(account.getAmount(), 124d);
    }

    @Test
    public void invalidSell() {
        MarketOrderDto marketOrderDto = new MarketOrderDto();
        marketOrderDto.setAccountId(1);
        marketOrderDto.setTicker("ATZ");
        marketOrderDto.setSize(-11);

        orderService.executeMarketOrder(marketOrderDto);

        verify(securityOrderDao).save(captorSecurityOrder.capture());

        assertEquals(captorSecurityOrder.getValue().getStatus(), "CANCELLED");
        assertEquals(position.getPosition(), 10);
        assertEquals(account.getAmount(), 100d);
    }

}
