package com.application.volleyExtension;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by davide on 06/09/14.
 */
public class MultiPartRequest extends JsonRequest<JSONObject> {

    /* To hold the parameter name and the File to upload */
    private Map<String,File> fileUploads = new HashMap<String,File>();

    /* To hold the parameter name and the string content to upload */
    private Map<String,String> stringUploads = new HashMap<String,String>();

    public MultiPartRequest(int method, String url, String requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    public void addFileUpload(String param,File file) {
        fileUploads.put(param,file);
    }

    public void addStringUpload(String param,String content) {
        stringUploads.put(param,content);
    }

    public Map<String,File> getFileUploads() {
        return fileUploads;
    }

    public Map<String,String> getStringUploads() {
        return stringUploads;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
        return null;
    }

    protected static void setMultiPartBody(HttpEntityEnclosingRequestBase httpRequest,
                                           Request<?> request) throws AuthFailureError {

        // Return if Request is not MultiPartRequest
        if(request instanceof MultiPartRequest == false) {
            return;
        }

        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        //Iterate the fileUploads
        Map<String,File> fileUpload = ((MultiPartRequest)request).getFileUploads();
        for (Map.Entry<String, File> entry : fileUpload.entrySet()) {
            multipartEntity.addPart(((String)entry.getKey()), new FileBody((File)entry.getValue()));
        }

        //Iterate the stringUploads
        Map<String,String> stringUpload = ((MultiPartRequest)request).getStringUploads();
        for (Map.Entry<String, String> entry : stringUpload.entrySet()) {
            try {
                multipartEntity.addPart(((String)entry.getKey()), new StringBody((String)entry.getValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        httpRequest.setEntity(multipartEntity);
    }
}
