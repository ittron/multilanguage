package id.co.ittron.multilanguage;

import org.json.JSONException;
import org.json.JSONObject;

public class Language {

    static LanguageEngine languageEngine = new LanguageEngine();

    public static LanguageBuilder init(LanguageBuilder languageBuilder) {
        return languageBuilder;
    }

    static void Build(LanguageBuilder languageBuilder) {
        languageEngine = new LanguageEngine(languageBuilder);
    }

    public static void setMainLanguage(String language) {
        languageEngine.setMainLanguage(language);
    }

    public static String getLanguage(String language) {
        JSONObject languageJSONObject = languageEngine.getLanguage();
        String resultLanguage = null;

        if(languageJSONObject!=null) {
            try {
                if(languageJSONObject.has(language)) {
                    resultLanguage = languageJSONObject.getString(language);
                } else {
                    resultLanguage = language;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            resultLanguage = language;
        }
        return resultLanguage;
    }

    public static void resetAllLanguage() {
        languageEngine.resetAllLanguage();
    }
}
