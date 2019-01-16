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
import org.json.JSONArray;
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
public class UserLoginAsyncTask extends AsyncTask<String, Integer, String> {

    public interface OnCompleteUserLoginListener {
        public void onCompleteUserLogin(String statusMessage);
    }

    String TAG = "CorSetUpAsyncTask";
    int status = 0;
    String message = "";
    private Context context;
    private OnCompleteUserLoginListener onCompleteUserLoginListener;

    public UserLoginAsyncTask(Context context, OnCompleteUserLoginListener onCompleteUserLoginListener){
        this.context = context;
        this.onCompleteUserLoginListener = onCompleteUserLoginListener;
    }

    @Override
    protected String doInBackground(String... params) {

        String username = params[0];
        String password = params[1];
        String orgID = params[2];

        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        list.add(new BasicNameValuePair("username", username));
        list.add(new BasicNameValuePair("password", password));

        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 12000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        //int timeoutSocket = 5000;
        //HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        HttpResponse httpResponse = null;
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        Log.i("login url", Constants.URL + Constants.LOGIN_URL_REQUEST+orgID);
        HttpPost httpPost = new HttpPost(Constants.URL + Constants.LOGIN_URL_REQUEST+orgID);
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
            Log.i(TAG, "Response : ["+response+"]");

            JSONObject jsonObject = new JSONObject(response);
            JSONArray responseArray = jsonObject.getJSONArray("Response");
            for (int i = 0; i < responseArray.length(); i++) {

                status = responseArray.getJSONObject(i).getInt("loginStatus");

                if(status == 1){
                    String full_name = responseArray.getJSONObject(i).getString("userFullName");
                    String api_key = responseArray.getJSONObject(i).getString("apiKey");

                    context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_FULL_NAME, full_name).commit();
                    context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, api_key).commit();
                    context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_LOGIN_STS, 1).commit();

                    return "Success";
                } else{
                    context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_FULL_NAME, null).commit();
                    context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, null).commit();
                    context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_LOGIN_STS, 0).commit();
                    message = "Incorrect Password";
                    return message;
                }
            }


        } catch(ConnectTimeoutException e){

            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_FULL_NAME, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_LOGIN_STS, 0).commit();

            message = "Connection time out.\nPlease reset internet connection.";
            return message;
        }catch(UnknownHostException exception){

            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_FULL_NAME, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_LOGIN_STS, 0).commit();

            message = "Please reset internet connection.";
            return message;
        } catch(Exception e){

            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_FULL_NAME, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putString(Constants.SP_API_KEY, null).commit();
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_LOGIN_STS, 0).commit();

            message = "Login failed.";
            Log.e(TAG, "Exception", e);
            return message;
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(onCompleteUserLoginListener != null){
            onCompleteUserLoginListener.onCompleteUserLogin(result);
        }
    }
}
