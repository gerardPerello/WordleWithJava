package Utils;

import org.json.simple.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class StatsTest {

    Stats stats;
    HashMap<String, Long> victories;

    public void initStats(){
        this.victories = new HashMap();
        this.victories.put("1",0L);
        this.victories.put("2",0L);
        this.victories.put("3",1L);
        this.victories.put("4",0L);
        this.victories.put("5",0L);
        this.victories.put("6",0L);
        stats = new Stats(4L, 25D, 2L, 1L, victories);

    }

    @Test
    public void addVictoriaTest() throws IOException {
        int contIntentos = 2;
        initStats();
        stats.addVictoria(contIntentos);
        JSONObject jsonObject = stats.StatsToJson();
        JSONObject stats = (JSONObject) jsonObject.get("Stats");
        HashMap victories = (HashMap) stats.get("Victories");
        assertEquals(victories.get(String.valueOf(contIntentos)), 1L);
    }

    @Test
    public void addDerrotaTest() throws IOException {
        initStats();
        stats.addDerrota();
        JSONObject jsonObject = stats.StatsToJson();
        JSONObject stats = (JSONObject) jsonObject.get("Stats");
        long jugades = ((long) stats.get("Jugades"));
        double exits = ((double) stats.get("Exits %"));
        long ratxaActual = ((long) stats.get("Ratxa Actual"));
        assertEquals(jugades, 5L);
        assertEquals(exits, 20D, 0.1);
        assertEquals(ratxaActual, 0L);
    }

    @Test
    public void statsToJsonTest() throws IOException {
        initStats();
        JSONObject jsonObject = stats.StatsToJson();
        JSONObject stats = (JSONObject) jsonObject.get("Stats");
        long jugades = ((long) stats.get("Jugades"));
        double exits = ((double) stats.get("Exits %"));
        long ratxaActual = ((long) stats.get("Ratxa Actual"));
        long ratxaMaxima = ((long) stats.get("Ratxa Maxima"));
        HashMap victories = (HashMap) stats.get("Victories");
        assertEquals(jugades, 4L);
        assertEquals(exits, 25D, 0.1);
        assertEquals(ratxaActual, 2L);
        assertEquals(ratxaMaxima, 1L);
        assertEquals(victories.get("3"), 1L);
    }
}
