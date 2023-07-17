package Utils;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Stats {

    /**
     * VARIABLES
     */
    private long jugades;
    private double exits;
    private long ratxa_actual;
    private long ratxa_maxima;
    private Map<String, Long> victories;
    /**
     * FI DE VARIABLES
     */

    /**
     * Constructor buit de la classe stats, es fa servir per a crear les estadístiques d'un client nou.
     */
    public Stats(){
        this.jugades = 0;
        this.exits = 0;
        this.ratxa_actual = 0;
        this.ratxa_maxima = 0;
        this.victories = new HashMap();
        this.victories.put("1",0L);
        this.victories.put("2",0L);
        this.victories.put("3",0L);
        this.victories.put("4",0L);
        this.victories.put("5",0L);
        this.victories.put("6",0L);
    }

    /**
     * Constructor amb parameters de la classe stats.
     * @param jugades Nombre de partides jugades.
     * @param exits Percentatge de victories.
     * @param ratxa_actual Ratxa actual de partides guanyades.
     * @param ratxa_maxima Ratxa màxima de partides guanyades.
     * @param victories HashSet de victories en 1...6 intents.
     */
    public Stats(long jugades, double exits, long ratxa_actual, long ratxa_maxima, HashMap<String, Long> victories){
        this.jugades = jugades;
        this.exits = exits;
        this.ratxa_actual = ratxa_actual;
        this.ratxa_maxima = ratxa_maxima;
        this.victories = victories;
    }

    /**
     * Mètode que es crida quan es vol afegir una nova victoria al client.
     * @param contIntentos Ronda (1....6) que ha guanyat la partida el client.
     */
    public void addVictoria(int contIntentos)
    {
        jugades++;
        ratxa_actual++;
        if(ratxa_actual > ratxa_maxima){
            ratxa_maxima = ratxa_actual;
        }
        long novesVictories = victories.get(String.valueOf(contIntentos)) + 1;
        victories.put(Integer.toString(contIntentos), novesVictories);
        Long sumValues = victories.values().stream().mapToLong(i->i).sum();
        exits = (((float)sumValues/(float)jugades)*100);
    }

    /**
     * Mètode que es crida quan es vol afegir una derrota a un client.
     */
    public void addDerrota(){
        jugades++;
        ratxa_actual = 0;
        Long sumValues = victories.values().stream().mapToLong(i->i).sum();
        exits = (((float)sumValues/(float)jugades)*100);
    }


    /**
     * Mètode que es fa servir per retornar un JSON de les stats.
     * @return JSONOBJECT de les estadistiques segons es demana.
     * @throws IOException
     */
    public JSONObject StatsToJson() throws IOException{

        JSONObject stats = new JSONObject();
        JSONObject jsonStats = new JSONObject();
        jsonStats.put("Jugades",jugades);
        jsonStats.put("Exits %",exits);
        jsonStats.put("Ratxa Actual",ratxa_actual);
        jsonStats.put("Ratxa Maxima",ratxa_maxima);
        jsonStats.put("Victories",victories);
        stats.put("Stats", jsonStats);
        return stats;
    }


}
