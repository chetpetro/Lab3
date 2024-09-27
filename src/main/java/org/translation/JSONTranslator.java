package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final List<String> countries;
    private final List<JSONObject> translations;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        this.countries = new ArrayList<>();
        this.translations = new ArrayList<>();
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject countryData = jsonArray.getJSONObject(i);
                String countryCode = countryData.getString("alpha3");

                countries.add(countryCode);
                translations.add(countryData);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        int countryIndex = countries.indexOf(country);
        if (countryIndex != -1) {
            JSONObject countryData = translations.get(countryIndex);
            List<String> languages = new ArrayList<>();
            for (String key : countryData.keySet()) {
                if (!"alpha2".equals(key) && !"alpha3".equals(key) && !"id".equals(key)) {
                    languages.add(key);
                }
            }
            return languages;
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countries);
    }

    @Override
    public String translate(String country, String language) {
        int countryIndex = countries.indexOf(country);
        if (countryIndex != -1) {
            JSONObject countryData = translations.get(countryIndex);
            if (countryData.has(language)) {
                return countryData.getString(language);
            }
        }
        return null;
    }
}
