package org.tenok.coin;

import static org.junit.Assert.assertEquals;

import javax.security.auth.login.LoginException;

import com.slack.api.webhook.WebhookResponse;

import org.junit.Test;
import org.tenok.coin.data.entity.Orderable;
import org.tenok.coin.data.entity.impl.ActiveOrder;
import org.tenok.coin.data.entity.impl.CandleList;
import org.tenok.coin.data.entity.impl.candle_index.bollinger_band.BBObject;
import org.tenok.coin.data.entity.impl.candle_index.bollinger_band.BollingerBand;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MAObject;
import org.tenok.coin.data.entity.impl.candle_index.moving_average.MovingAverage;
import org.tenok.coin.data.impl.BybitDAO;
import org.tenok.coin.slack.SlackSender;
import org.tenok.coin.type.CoinEnum;
import org.tenok.coin.type.IntervalEnum;
import org.tenok.coin.type.SideEnum;
import org.tenok.coin.type.OrderTypeEnum;
import org.tenok.coin.type.TIFEnum;

public class DAOTest {
    @Test
    public void loginTest() {
        try {
            BybitDAO.getInstance().login("tenok2019");
        } catch (LoginException e) {
            e.printStackTrace();
            assert false;
        }
        assertEquals(true, BybitDAO.getInstance().isLoggedIn());
    }

    @Test
    public void sendMessage(){
        
        try {
            BybitDAO.getInstance().login("tenok2019");
        } catch (LoginException e1) {
            e1.printStackTrace();
        }
        WebhookResponse response = SlackSender.getInstance().sendTradingMessage(CoinEnum.BTCUSDT, SideEnum.OPEN_BUY, 1);
        assertEquals(200, response.getCode().intValue());
    }

    @Test
    public void orderTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");

        Orderable order = ActiveOrder.builder()
                                     .coinType(CoinEnum.LTCUSDT)
                                     .orderType(OrderTypeEnum.MARKET)
                                     .side(SideEnum.CLOSE_SELL)
                                     .qty(0.1)
                                     .tif(TIFEnum.GTC)
                                     .build();
                                     
        BybitDAO.getInstance().orderCoin(order);
        assertEquals(1, BybitDAO.getInstance().getOrderList().size());
    }

    @Test
    public void excpetionTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");
        
        WebhookResponse res = SlackSender.getInstance().sendException(new RuntimeException("something went wrong!"));
        assertEquals(200, res.getCode().intValue());
    }

    //  // @Test
    //  // public void BacktestCandleTest() {
    //     // BacktestDAO back = new BacktestDAO();
    //     //back.inputTest(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);
    //     // back.getCandleList(CoinEnum.BTCUSDT, IntervalEnum.FIFTEEN);  
    //     // CandleList candle = back.getCandleList(CoinEnum.BTCUSDT, IntervalEnum.DAY);
    //     // for (int i = 0; i<1000; i++){
    //     //     System.out.println(candle.get(i));
            
    //     // }
    //  // }

    @Test
    public void getInstrumentInfoTest() throws LoginException, InterruptedException {
        BybitDAO.getInstance().login("tenok2019");
        var inst = BybitDAO.getInstance().getInstrumentInfo(CoinEnum.BTCUSDT);

        assertEquals(CoinEnum.BTCUSDT, inst.getCoinType());
    }

    @Test
    public void indexingCandleTest() throws LoginException {
        BybitDAO.getInstance().login("tenok2019");
        CandleList candleList = BybitDAO.getInstance().getCandleList(CoinEnum.BTCUSDT, IntervalEnum.ONE);
        candleList.addIndex(MovingAverage.class);
        candleList.addIndex(BollingerBand.class);

        MAObject maObject = (MAObject) candleList.getIndexReversed(MovingAverage.class, 0);
        BBObject bbObject = (BBObject) candleList.getIndexReversed(BollingerBand.class, 0);
        assertEquals(maObject.getMa20(), bbObject.getMiddleBB(), 1);

        System.out.println(maObject.getMa20());
    }
}
