package com.application.datastorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.application.commons.Common;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * Created by davide on 22/05/14.
 */
public class HttpRequestData {
    String TAG = "HttpRequestData";
    static DefaultHttpClient httpclient;
    public HttpRequestData() {
        httpclient = new DefaultHttpClient();
    }

    public String asyncRequestData(final String method, URI url, final String JSONParams, final HttpEntity entity)
            throws IOException, ExecutionException, InterruptedException {
        AsyncTask<URL, Integer, String> asyncTask = new AsyncTask<URL, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(URL... urls) {
                if (urls != null) {
                    URI url;
                    try {
                        url = urls[0].toURI();
                        HttpParams httpParameters = new BasicHttpParams();
                        int timeoutConnection = 5000;
                        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                        int timeoutSocket = 5000;
                        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                        httpclient.setParams(httpParameters);
                        HttpResponse response = null;
                        if(method.equals("POST")) {
                            HttpPost httpPost = new HttpPost(url);
                            StringEntity se = new StringEntity(JSONParams);
                            httpPost.setEntity(se);
                            httpPost.setHeader("Accept", "application/json");
                            httpPost.setHeader("Content-type", "application/json");
                            response = httpclient.execute(httpPost);
                        } else if(method.equals("GET")) {
                            response = httpclient.execute(new HttpGet(url));
                        } else if(method.equals("GET-mimeType")) {
                            response = httpclient.execute(new HttpGet(url));
                        } else if(method.equals("DELETE")) {
                            response = httpclient.execute(new HttpDelete(url));
                        } else if(method.equals("PUT")) {
                            HttpPut httpPut = new HttpPut(url);
                            httpPut.setEntity(new StringEntity(JSONParams));
                            httpPut.setHeader("Content-type", "application/json");
                            response = httpclient.execute(httpPut);
                        } else if(method.equals("POST-mimeType")) {
                            HttpPost httpPost = new HttpPost(url);
                            httpPost.setEntity(entity);
//                            httpPost.setHeader("Content-type", "application/json");
                            response = httpclient.execute(httpPost);
                        }

                            if(response == null) {
                            Log.e(TAG, "no HTTP method available");
                            return null;
                        }

                        StatusLine statusLine = response.getStatusLine();
                        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                            if(method.equals("GET-mimeType")) {
                                Bitmap decodedPic = BitmapFactory.decodeStream(response.getEntity().getContent());
                                return "{'status': 'ok'}";
                            }

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            out.close();
                            return out.toString();
                        } else {
                            //Closes the connection.
                            if(response.getEntity() != null && response.getEntity().getContent() != null) {
                                response.getEntity().getContent().close();
                            }
                            throw new IOException(statusLine.getReasonPhrase());
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }

            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
                Log.d(TAG, "result data ASYNCTASK " + data);
                //TODO refactor it
//                customDir = co.getApplication().getApplicationContext()
//                    .getDir(Common.COFFEE_MACHINE_DIR, Context.MODE_PRIVATE); //Creating an internal dir;
//                Common.saveImageInStorage(decodedPic, customDir)

            }
        };

        return asyncTask.execute(url.toURL()).get(); //LA GET E' BLOCCANTE
    }

}
