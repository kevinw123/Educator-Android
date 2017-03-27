package com.group25.proj2;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostImageToImaggaAsync extends AsyncTask<String, String, String> {
    private static final String TAG = "PostImagetoImagga";
    final String imageUri;
    private static String contentId;
    public PostImageToImaggaAsync(String imageUri){
        this.imageUri = imageUri;
    }

    @Override
    protected void onPreExecute() {
    }

    /*
     * Run the uploading image to server and image tagging in backgrgound to prevent
     * too much work on Main UI thread.
     */
    @Override
    protected String doInBackground(String... params) {
        try {
            String response = postImageToImagga(imageUri);
            try {
                JSONArray person = (new JSONObject(response)).getJSONArray("uploaded");
                String json = (new JSONObject(person.get(0).toString())).getString("id");
                contentId = json;
                System.out.println(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String result = getImaggaTags();
            return result;

        } catch (Exception e) {

        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
    }

    /*
     * Call tagging endpoint of the imagga API to get tags for image
     */
    public String getImaggaTags(){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.imagga.com/v1/tagging").newBuilder();
        urlBuilder.addQueryParameter("content", contentId);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Basic YWNjX2ZlMjg4ZWI0YzUyMDA5MjpjYWM2YTRjNTZhYWE1ZTc0YzllYWE5YWJlNWYzZDFjNw==")
                .build();
        try{
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e){
            System.out.println("exception");
        }

        return "";
    }

    /*
     * Uploads picture taken from camera to Imagga Server
     * ContentId received to tag it using Imagga tagging endpoint
     */
    public String postImageToImagga(String filepath) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary =  "*****"+Long.toString(System.currentTimeMillis())+"*****";
        String lineEnd = "\r\n";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        String filefield = "image";

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        File file = new File(filepath);
        FileInputStream fileInputStream = new FileInputStream(file);

        URL url = new URL("https://api.imagga.com/v1/content");
        connection = (HttpURLConnection) url.openConnection();

        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
        connection.setRequestProperty("Authorization", "Basic YWNjX2ZlMjg4ZWI0YzUyMDA5MjpjYWM2YTRjNTZhYWE1ZTc0YzllYWE5YWJlNWYzZDFjNw==");

        outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] +"\"" + lineEnd);
        outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
        outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
        outputStream.writeBytes(lineEnd);

        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];

        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while(bytesRead > 0) {
            outputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        outputStream.writeBytes(lineEnd);
        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        inputStream = connection.getInputStream();

        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            inputStream.close();
            connection.disconnect();
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            return response.toString();
        } else {
            throw new Exception("Non ok response returned");
        }

    }
}
