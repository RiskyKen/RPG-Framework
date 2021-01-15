package moe.plushie.rpg_framework.shop.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import moe.plushie.rpg_framework.core.common.lib.LibModInfo;

public class NameGenerator {
    
    private static final String ASSETS_LOCATION = "assets/" + LibModInfo.ID + "/";
    
    private final String[] nouns;
    private final String[] adjectives;
    
    public NameGenerator() {
        nouns = readLines("nouns-english.csv");
        adjectives = readLines("adjectives-english.csv");
    }
    
    public String generateName() {
        return getRandom(adjectives) + " " + getRandom(nouns);
    }
    
    public static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    private String[] readLines(String filename) {
        List<String> lines = new ArrayList<String>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ASSETS_LOCATION + filename)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream); BufferedReader  bufferedReader = new BufferedReader(inputStreamReader)) {
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        line = line.split(",")[0];
                        line = line.substring(0, 1).toUpperCase() + line.substring(1);
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toArray(new String[lines.size()]);
    }
}
