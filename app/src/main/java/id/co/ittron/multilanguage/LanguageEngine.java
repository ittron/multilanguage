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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LanguageEngine {

    LanguageBuilder languageBuilder;

    String downloadURL;
    Boolean resultDownloadFile;
    Boolean isFinishDownload;

    ArrayList<DownloadFileLanguageAsyncTask> listDownloadFileLanguageAsyncTask;
    DownloadFileLanguageAsyncTask downloadFileLanguageAsyncTask;

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
                    downloadFileLanguageAsyncTask = new DownloadFileLanguageAsyncTask(listLanguageJSON.getString(i));
                    downloadFileLanguageAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                    listDownloadFileLanguageAsyncTask.add(downloadFileLanguageAsyncTask);
                } else {
                    totalDownloadedLanguage -=1;
                }
            }

            if(totalDownloadedLanguage==0) {
                isFinishDownload = true;
            }

            while(!isFinishDownload) {

                if(totalDownloadedLanguage == 0) {
                    isFinishDownload = true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isFinishDownload;
    }


    class DownloadFileLanguageAsyncTask extends AsyncTask<String, Void, String> {

        String languageDownload;
        public DownloadFileLanguageAsyncTask(String languageDownload) {
            this.languageDownload = languageDownload;
        }

        @Override
        protected String doInBackground(String... params) {

            Log.i("LanguageLibraries", "Downloading File Language "+languageDownload);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(downloadURL+"/"+languageDownload)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = null;

            try {

                response = client.newCall(request).execute();
                InputStream in = response.body().byteStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String result, line = reader.readLine();
                result = line;
                while((line = reader.readLine()) != null) {
                    result += line;
                }

                if(result.length()>0) {
                    FileOutputStream outputStream = languageBuilder.getContext().openFileOutput(languageDownload + ".json", Context.MODE_PRIVATE);
                    outputStream.write(result.getBytes());
                    outputStream.close();
                } else {
                    Log.e("Language Libraries", "No Language Found");
                }
                response.body().close();

                totalDownloadedLanguage -=1;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Executed";
        }
    }
}
