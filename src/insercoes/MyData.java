package insercoes;

import java.util.ArrayList;
import java.util.List;

public class MyData {
    private static String[] comidas = {
            "feijao",
            "arroz",
            "jiló",
            "farofa",
            "macarrao",
            "escondidin",
            "peixefrito",
            "carne",
            "frango",
            "mamão",
            "melão",
            "melancia",
            "banana",
            "abacate",
            "jaca",
            "acerola",
            "goiaba",
            "espinafre",
            "abacaxi",
            "maçã",
            "alface",
            "tomate",
            "berinjela",
            "carne seca",
            "miojo",
            "bolo",
            "chocolate",
            "panetone",
            "chocotone",
            "peru",
            "queijo",
            "pizza",
            "hamburger",
            "toranja",
            "alfajor",
            "vimatamina",
            "camarao",
            "açai",
            "carvao",
            "pao",
            "limonada"
    };
    private static String[] comidasProibidas = {
            "jiló",
            "abacate",
            "jaca",
            "berinjela",
    };
    private static List<String> comidasProibidasList = new ArrayList<>(List.of(comidasProibidas));

    public static String[] getComidas() {
        return comidas;
    }

    public static List<String> getComidasProibidasList() {
        return comidasProibidasList;
    }
}
