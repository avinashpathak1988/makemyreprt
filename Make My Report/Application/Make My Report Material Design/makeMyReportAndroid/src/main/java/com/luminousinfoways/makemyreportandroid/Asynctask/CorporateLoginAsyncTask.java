package com.luminousinfoways.makemyreportandroid.Asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.luminousinfoways.makemyreportandroid.util.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminousinfoways on 27/07/15.
 */
public class CorporateLoginAsyncTask extends AsyncTask<String, Integer, String> {

    public interface OnCompleteCorporateLoginListener {
        public void onCompleteCorporateLogin(String statusMessage);
    }

    String TAG = "CorSetUpAsyncTask";
    int status = 0;
    String message = "";
    private Context context;
    private OnCompleteCorporateLoginListener onCompleteCorporateLoginListener;

    public CorporateLoginAsyncTask(Context context, OnCompleteCorporateLoginListener onCompleteCorporateLoginListener){
        this.context = context;
        this.onCompleteCorporateLoginListener =onCompleteCorporateLoginListener;
    }

    @Override
    protected String doInBackground(String... params) {

        String corName = params[0];

        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        list.add(new BasicNameValuePair("corName", corName));

        HttpResponse httpResponse = null;

        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 12000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        //int timeoutSocket = 5000;
        //HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpPost httpPost = new HttpPost(Constants.URL + Constants.ORG_REQUEST_URL);
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            httpResponse = httpClient.execute(httpPost);
            InputStream inputStream = httpResponse.getEntity().getContent();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String response = "";
            while((line = bufferedReader.readLine()) != null){
                publishProgress(10);
                response = response + line;
            }
            Log.i(TAG, "Response : [" + response + "]");


				/*
				 * {
					    "Response": {
					        "message": "Success",
					        "url": "http://makemyreport.com/MobileApps/",
					        "cor": "DEMO",
					        "headerMsg": "DEMO",
					        "subheadMsg": "DEMO"
					    }
					}

				 * */

            JSONObject jsonObject = new JSONObject(response);
            JSONObject responseObj = jsonObject.getJSONObject("Response");
            message = responseObj.getString("message");
            String url = responseObj.getString("url");
            String orgId = responseObj.getString("cor");
            String orgName = responseObj.getString("headerMsg");
            String subheadMsg = responseObj.getString("subheadMsg");

            if(message.equalsIgnoreCase("success")){
                status = 1;
                context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_ID, orgId).commit();
                context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_NAME, orgName).commit();

                return "Success";
            } else{
                status = 0;
                context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_ID, null).commit();
                context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_NAME, null).commit();
                return "Incorrect Corporate Id.";
            }

        } catch(ConnectTimeoutException e){

            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_ID, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_NAME, null).commit();

            message = "Connection time out.\nPlease reset internet connection.";
            return message;
        } catch(UnknownHostException exception){

            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_ID, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_NAME, null).commit();

            message = "Please reset internet connection.";
            return message;
        } catch(Exception e){

            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_ID, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_ORG_NAME, null).commit();

            Log.e(TAG, "Exception", e);
            return null;
        }

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(onCompleteCorporateLoginListener != null){
            onCompleteCorporateLoginListener.onCompleteCorporateLogin(result);
        }
    }
}
