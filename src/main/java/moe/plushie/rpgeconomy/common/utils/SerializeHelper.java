package moe.plushie.rpgeconomy.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import moe.plushie.rpgeconomy.RpgEconomy;

public final class SerializeHelper {

    private SerializeHelper() {
    }

    public static String readFile(File file, Charset encoding) {
        BufferedReader inputStream = null;
        String text = null;
        try {
            inputStream = new BufferedReader(new FileReader(file));
            byte[] data = IOUtils.toByteArray(inputStream, encoding);
            text = new String(data, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return text;
    }
    
    public static JsonElement readJsonFile(File file) {
        return readJsonFile(file, Charsets.UTF_8);
    }

    public static JsonElement readJsonFile(File file, Charset encoding) {
        String jsonString = readFile(file, encoding);
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(jsonString);
        } catch (Exception e) {
            RpgEconomy.getLogger().error("Error parsing json.");
            RpgEconomy.getLogger().error(e.getLocalizedMessage());
            return null;
        }
    }
}
