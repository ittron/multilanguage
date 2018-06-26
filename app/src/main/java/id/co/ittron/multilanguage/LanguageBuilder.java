package id.co.ittron.multilanguage;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LanguageBuilder {

    Context context;
    String downloadURL;
    String[] listLanguage;

    public LanguageBuilder(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;

        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput("downloadLanguage.dat", Context.MODE_PRIVATE);
            outputStream.write(this.downloadURL.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAvailableLanguage(String[] listLanguage) {
        this.listLanguage = listLanguage;

        FileOutputStream outputStream = null;
        JSONArray listLanguageJSON = null;
        try {
            listLanguageJSON = new JSONArray(listLanguage);
            outputStream = context.openFileOutput("listLanguage.dat", Context.MODE_PRIVATE);
            outputStream.write(listLanguageJSON.toString().getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void build() {
        Language.Build(this);
    }


}
