/**package Jsonner;

import ComUtilsExceptions.ProtocolException;
import Utils.JsonnerToFile;
import Utils.Stats;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class JsonnerToFileTest {

    Stats stats;
    HashMap<String, Long> victories;
    JsonnerToFile jsonner;
    int idSeason;
    public void initJsonner() throws IOException {

        this.victories = new HashMap();
        this.victories.put("1",0L);
        this.victories.put("2",0L);
        this.victories.put("3",1L);
        this.victories.put("4",0L);
        this.victories.put("5",0L);
        this.victories.put("6",0L);
        stats = new Stats(4L, 25D, 2L, 1L, victories);

        jsonner = new JsonnerToFile();
    }


    @Test(expected = Test.None.class)//no exception expected
    public void checkIdSeasonTest() throws ProtocolException, IOException {
        initJsonner();
        idSeason = jsonner.CreateNewClient("TESTCLIENT");
        jsonner.checkIdSeason(idSeason, "TESTCLIENT");//We use a value that we know that will be in the DB
    }

    @Test
    public void statsPlayerTest() throws IOException {
        initJsonner();
        idSeason = jsonner.CreateNewClient("TESTCLIENT");
        jsonner.updateStats(idSeason, stats);
        Stats statsPlayer = jsonner.getStatsPlayer(idSeason);
        assertEquals(statsPlayer.StatsToJson().toJSONString(), stats.StatsToJson().toJSONString());
    }


    /**
    @Test(expected = Test.None.class)//no exception expected
    public void test() throws IOException, ProtocolException {
        initJsonner();
        int idSeason;
        String ruta = "/Server/db/TESTING.json";
        jsonner.setRuta(ruta);
        idSeason = jsonner.CreateNewClient("TEST");
        jsonner.getAllClientsFromFile();
        jsonner.checkIdSeason(idSeason ,"TEST");
    }
}
 */
