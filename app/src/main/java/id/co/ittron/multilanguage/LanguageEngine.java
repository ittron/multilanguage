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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LanguageEngine {

    LanguageBuilder languageBuilder;

    String downloadURL;
    Boolean resultDownloadFile;
    Boolean isFinishDownload;

    ArrayList<AsyncTask> listDownloadFileLanguageAsyncTask;

    int totalDownloadedLanguage;
    int timeOut = 1000;

    DownloadFileLanguageAsyncTask downloadFileLanguageAsyncTask;

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

    public void setTimeOutWaitingDownload(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setMainLanguage(String language) {
        try {
            Thread.sleep(timeOut);

            Log.d("LanguageLibraries", "Set Main Language "+language);

            if(languageBuilder != null) {

                File languageFile = new File(languageBuilder.getContext().getFilesDir() + "/lang_" + language + ".json");

                if (!languageFile.exists()) {

                    Log.d("LanguageLibraries", "Language Not Found, Have you added selected language to LanguageBuilder?");
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
                Log.d("LanguageLibraries", "Language Libraries is not initialized yet.");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
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
                    downloadFileLanguageAsyncTask = new DownloadFileLanguageAsyncTask(listLanguageJSON.getString(i));
                    downloadFileLanguageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    listDownloadFileLanguageAsyncTask.add(downloadFileLanguageAsyncTask);

                }
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
                    File languageDownloadFile = new File(languageBuilder.getContext().getFilesDir() + "/lang_" +languageDownload + ".json");

                    FileOutputStream outputStream = new FileOutputStream(languageDownloadFile, false);
                    outputStream.write(result.getBytes());
                    outputStream.close();
                } else {
                    Log.e("Language Libraries", "No Language Found");
                }
                response.body().close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Executed";
        }
    }

}
