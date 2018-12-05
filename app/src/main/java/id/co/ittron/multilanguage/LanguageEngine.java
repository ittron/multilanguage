package id.co.ittron.multilanguage;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class LanguageEngine {

    LanguageBuilder languageBuilder;

    String downloadURL;
    Boolean resultDownloadFile;
    Boolean isFinishDownload;

    ArrayList<AsyncTask> listDownloadFileLanguageAsyncTask;

    int totalDownloadedLanguage;

    public LanguageEngine() {};

    public LanguageEngine(LanguageBuilder languageBuilder) {
        this.languageBuilder = languageBuilder;
        resultDownloadFile = this.downloadFileLanguage();
    }

    public JSONObject getLanguage() {

        JSONObject languageJSONObject = null;
        String language;

        if(languageBuilder != null) {

            try {
                File mainLanguageFile = new File(languageBuilder.getContext().getFilesDir() + "/" + "language.dat");

                if(!mainLanguageFile.exists()) {


                    Log.w("LanguageLibraries", "Main language not found, automatically using first language on list");

                    JSONArray listLanguageJSON = this.getListAvailableLanguage();
                    this.setMainLanguage(listLanguageJSON.getString(0));
                }

                language = this.getMainLanguage();

                File languageFile = new File(languageBuilder.getContext().getFilesDir() + "/lang_" +language + ".json");

                FileInputStream inputStream = new FileInputStream(languageFile);
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                languageJSONObject = new JSONObject(sb.toString());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("LanguageLibraries", "Language Libraries is not initialized yet.");
        }

        return languageJSONObject;
    }

    public void setMainLanguage(String language) {

        Log.e("LanguageLibraries", "Set Main Language "+language);

        if(languageBuilder != null) {

            File languageFile = new File(languageBuilder.getContext().getFilesDir() + "/lang_" + language + ".json");

            if (!languageFile.exists()) {

                Log.e("LanguageLibraries", "Language Not Found, Have you added selected language to LanguageBuilder?");
            } else {
                FileOutputStream outputStream = null;

                File mainLanguage = new File(languageBuilder.getContext().getFilesDir() + "/" +"language.dat");
                try {
                    outputStream = new FileOutputStream(mainLanguage);
                    outputStream.write(language.getBytes());
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e("LanguageLibraries", "Language Libraries is not initialized yet.");
        }
    }

    public void resetAllLanguage() {
        JSONArray listAvailableLanguage = this.getListAvailableLanguage();
        for (int i=0;i<listAvailableLanguage.length();i++) {
            try {
                File languangeFile = new File(languageBuilder.getContext().getFilesDir()+"/lang_"+listAvailableLanguage.getString(i)+".json");
                if(languangeFile.exists()) {
                    languangeFile.delete();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        resultDownloadFile = this.downloadFileLanguage();
    }

    private String getMainLanguage() {
        String language = null;

        File mainLanguage = new File(languageBuilder.getContext().getFilesDir() + "/" +"language.dat");

        try {
            FileInputStream inputStream = new FileInputStream(mainLanguage);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            language = sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return language;
    }

    private void getDownloadUrlLanguage() {

        File downloadLanguageFile = new File(languageBuilder.getContext().getFilesDir() + "/" +"downloadLanguage.dat");
        try {
            FileInputStream inputStream = new FileInputStream(downloadLanguageFile);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            this.downloadURL = sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray getListAvailableLanguage() {
        JSONArray listLanguageJSON = null;

        FileInputStream inputStream = null;
        File listLanguageFile = new File(languageBuilder.getContext().getFilesDir() + "/" +"listLanguage.dat");
        try {
            inputStream = new FileInputStream(listLanguageFile);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            listLanguageJSON = new JSONArray(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listLanguageJSON;
    }


    private Boolean downloadFileLanguage() {

        this.getDownloadUrlLanguage();

        JSONArray listLanguageJSON;

        isFinishDownload = false;

        try {

            listLanguageJSON = this.getListAvailableLanguage();
            listDownloadFileLanguageAsyncTask = new ArrayList<>();

            totalDownloadedLanguage = listLanguageJSON.length();

            for(int i=0;i<listLanguageJSON.length();i++) {

                Log.e("LanguageLibraries","Download Language "+listLanguageJSON.getString(i));

                File languageFile = new File(languageBuilder.getContext().getFilesDir()+"/lang_"+listLanguageJSON.getString(i)+".json");
                if(!languageFile.exists()) {
                    final String languageDownload = listLanguageJSON.getString(i);

                    AsyncTask downloadFileLanguageAsyncTask = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {


                            StringBuilder sb = new StringBuilder();
                            try {
                                URL url = new URL(downloadURL+"/"+languageDownload);
                                BufferedReader in;
                                in = new BufferedReader(
                                        new InputStreamReader(
                                                url.openStream()));

                                String inputLine;
                                while ((inputLine = in.readLine()) != null)
                                    sb.append(inputLine);

                                in.close();

                                String responseString = sb.toString();

                                if(responseString.length()>0) {
                                    File languageDownloadFile = new File(languageBuilder.getContext().getFilesDir() + "/lang_" +languageDownload + ".json");

                                    FileOutputStream outputStream = new FileOutputStream(languageDownloadFile, false);

                                    outputStream.write(responseString.getBytes());

                                    outputStream.close();
                                } else {
                                    Log.e("Language Libraries", "No Language Found");
                                }

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }
                    };
                    listDownloadFileLanguageAsyncTask.add(downloadFileLanguageAsyncTask);

                }
            }

            for(int i=0; i<listDownloadFileLanguageAsyncTask.size();i++) {
                listDownloadFileLanguageAsyncTask.get(i).execute();
            }

            if(totalDownloadedLanguage==0) {
                isFinishDownload = true;
            }

            while(!isFinishDownload) {
                for(int i=0; i<listLanguageJSON.length();i++) {
                    File languageFile = new File(languageBuilder.getContext().getFilesDir() + "/lang_" +listLanguageJSON.getString(i)+".json");
                    if(languageFile.exists()) {
                        totalDownloadedLanguage -=1;
                    }
                }

                if(totalDownloadedLanguage <= 0) {
                    isFinishDownload = true;
                }

                Log.i("LanguageLibraries", "Total Download Language "+totalDownloadedLanguage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isFinishDownload;
    }
}
