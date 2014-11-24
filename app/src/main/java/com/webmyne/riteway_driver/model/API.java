package com.webmyne.riteway_driver.model;

import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;


public class API {




    public static Reader callWebservicePost(String SERVER_URL,String jsonString) {

        Reader reader = null;
        InputStream is=null;

        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SERVER_URL);
            // parse without any json body parameters
            if(jsonString.equalsIgnoreCase("")) {
                post.setHeader("Accept", "application/text");
                post.setHeader("Content-type", "application/text");
            } else { // with json body parameters
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
            }

            StringEntity e = new StringEntity(jsonString.toString(), HTTP.UTF_8);
            // parse without any json body parameters
            if(jsonString.equalsIgnoreCase("")) {
                e.setContentType("application/text");
            } else { // with json body parameters
                e.setContentType("application/json");
            }


            post.setEntity(e);

            HttpResponse response = client.execute(post);
//            Log.e("jsonString:",jsonString+"");
//            Log.e("response:",response+"");
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                reader = new InputStreamReader(is);
            } else {

//                Log.e("Error", +statusLine.getStatusCode()+""+statusLine.getReasonPhrase());
            }
        }catch (JsonSyntaxException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (JsonIOException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        }
        return reader;
    }

    public static Reader callWebservicePostWithNameValue(String SERVER_URL,List<NameValuePair> pair) {

        Reader reader = null;
        InputStream is=null;

        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SERVER_URL);
            post.setHeader("Accept", "text/json");
            post.setHeader("Content-type", "text/json");

            UrlEncodedFormEntity e = new UrlEncodedFormEntity(pair);
            e.setContentEncoding(HTTP.UTF_8);
            e.setContentType("application/json");
            post.setEntity(e);
            HttpResponse response = client.execute(post);

//            Log.e("response:",response+"");
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                reader = new InputStreamReader(is);
            } else {

//                Log.e("Error", +statusLine.getStatusCode()+""+statusLine.getReasonPhrase());
            }
        }catch (JsonSyntaxException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (JsonIOException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Error: ", e + "");
            e.printStackTrace();
        }
        return reader;
    }
}
