package id.co.ittron.multilanguage;

import android.content.Context;
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

                FileInputStream inputStream = languageBuilder.getContext().openFileInput(language + ".json");
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

        if(languageBuilder != null) {

            File languageFile = new File(languageBuilder.getContext().getFilesDir() + "/" + language + ".json");
            if (!languageFile.exists()) {
                Log.e("Language Libraries", "Language Not Found, Have you added selected language to LanguageBuilder?");
            } else {
                FileOutputStream outputStream = null;
                try {
                    outputStream = languageBuilder.getContext().openFileOutput("language.dat", Context.MODE_PRIVATE);
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
                File languangeFile = new File(languageBuilder.getContext().getFilesDir()+"/"+listAvailableLanguage.getString(i)+".json");
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

        try {
            FileInputStream inputStream = languageBuilder.getContext().openFileInput("language.dat");
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
        try {
            FileInputStream inputStream = languageBuilder.getContext().openFileInput("downloadLanguage.dat");
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
        try {
            inputStream = languageBuilder.getContext().openFileInput("listLanguage.dat");
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

                File languageFile = new File(languageBuilder.getContext().getFilesDir()+"/"+listLanguageJSON.getString(i)+".json");
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
                                    FileOutputStream outputStream = languageBuilder.getContext().openFileOutput(languageDownload + ".json", Context.MODE_PRIVATE);

                                    outputStream.write(responseString.getBytes());

                                    outputStream.close();
                                } else {
                                    Log.e("Language Libraries", "No Language Found");
                                    totalDownloadedLanguage -=1;
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

                } else {
                    totalDownloadedLanguage -=1;
                }
            }

            for(int i=0; i<listDownloadFileLanguageAsyncTask.size();i++) {
                listDownloadFileLanguageAsyncTask.get(i).execute();
            }

            if(totalDownloadedLanguage==0) {
                isFinishDownload = true;
            }

            while(!isFinishDownload) {
                for(int i=0; i<listDownloadFileLanguageAsyncTask.size();i++) {
                    File languageFile = new File(languageBuilder.getContext().getFilesDir()+"/"+listLanguageJSON.getString(i)+".json");
                    if(languageFile.exists()) {

                        Log.i("LanguageLibraries", "Language "+listLanguageJSON.getString(i)+" exists");
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
