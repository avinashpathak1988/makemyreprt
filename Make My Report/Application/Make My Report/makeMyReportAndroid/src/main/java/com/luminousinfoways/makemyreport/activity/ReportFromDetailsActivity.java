package com.luminousinfoways.makemyreport.activity;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.List;

        import org.apache.http.client.ClientProtocolException;
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
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Parcel;
        import android.text.TextUtils;
        import android.util.JsonToken;
        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.animation.Animation;
        import android.view.animation.Animation.AnimationListener;
        import android.view.animation.AnimationUtils;
        import android.view.animation.TranslateAnimation;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.actionbarsherlock.app.SherlockFragmentActivity;
        import com.luminousinfoways.makemyreport.R;
        import com.luminousinfoways.makemyreport.pojo.FieldDetails;
        import com.luminousinfoways.makemyreport.pojo.FieldOptions;
        import com.luminousinfoways.makemyreport.pojo.FiledValidation;
        import com.luminousinfoways.makemyreport.pojo.FormDetails;
        import com.luminousinfoways.makemyreport.pojo.FormField;
        import com.luminousinfoways.makemyreport.pojo.Options;
        import com.luminousinfoways.makemyreport.pojo.ReportForm;
        import com.luminousinfoways.makemyreport.util.CAlertDialog;
        import com.luminousinfoways.makemyreport.util.Constants;
        import com.luminousinfoways.makemyreport.util.DynamicForm;
        import com.luminousinfoways.makemyreport.util.FileChooser;
        import com.luminousinfoways.makemyreport.util.MMRSingleTone;
        import com.luminousinfoways.makemyreport.util.MultipartUtility;
        import com.luminousinfoways.makemyreport.util.Util;

        import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ReportFromDetailsActivity extends SherlockFragmentActivity
        implements AnimationListener, OnClickListener {

    SmoothProgressBar mPocketBar;
    LinearLayout lProLayout;

    private int FILE_REQ = 1652;
    private static String title;
    private static String no_of_times_report_submited;
    private boolean isFormsuccessfullySubmitted = false;
    private String userResponseId = "";

    Animation animation;
    RelativeLayout layout;
    boolean moveUp = false;
    boolean moveDown = false;
    LinearLayout layoutTransparent;

    //GPSTracker gps;
    double lattitude = 0.0f;
    double longitude = 0.0f;
    private String address;
    private String postalCode;

    TextView btnNext;
    TextView btnReset;

    //TextView tvaddress;

    public static ReportForm mReportForm = null;
    private static final String BUNDLE_KEY_REPORT_FORM = "formDetails";

    private final static String TAG = ReportFromDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_layout);

        Log.i(TAG, "onCreate()");

        btnNext = (TextView) findViewById(R.id.btnNext);
        btnReset = (TextView) findViewById(R.id.btnReset);

        btnNext.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        layoutTransparent = (LinearLayout) findViewById(R.id.layoutTransparent);

        lProLayout = (LinearLayout) findViewById(R.id.layoutProgress);
        mPocketBar = (SmoothProgressBar) findViewById(R.id.google_now);

        //tvaddress = (TextView) findViewById(R.id.address);

        if(getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString("report_title");
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            tvTitle.setText(title);
        }

        if(getIntent().getExtras() != null) {
            no_of_times_report_submited = getIntent().getExtras().getString("no_of_times_report_submited");
            TextView tvNo_of_times_report_submited = (TextView) findViewById(R.id.no_of_times_report_submited);
            tvNo_of_times_report_submited.setText(no_of_times_report_submited);
        }

        String form_id = null;
        String fb_assign_user_id = null;

        if(getIntent().getExtras() != null) {
            form_id = getIntent().getExtras().getString("fb_form_id");
            fb_assign_user_id = getIntent().getExtras().getString("fb_assign_user_id");
        }
        if(getIntent().getExtras() != null &&
                getIntent().getExtras().getBoolean("isSubmitFailed", false) == true){
            mReportForm = MMRSingleTone.getInstance().mReportForm;
            DynamicForm.generateForm(mReportForm, null, ReportFromDetailsActivity.this);
            DynamicForm.saveFieldInputedValues(mReportForm);
        }

//        if ((savedInstanceState != null)
//                  && (savedInstanceState.getSerializable(BUNDLE_KEY_REPORT_FORM) != null)) {
//                   mReportForm = (ReportForm) savedInstanceState
//                           .getSerializable(BUNDLE_KEY_REPORT_FORM);
//        } else{

        if(mReportForm == null) {
            if (Util.isConnected(this)) {
                if(form_id != null && fb_assign_user_id != null) {
                    ReportListFromDetailsAsyncTask asyncTask = new ReportListFromDetailsAsyncTask();
                    asyncTask.execute(form_id, fb_assign_user_id);
                }
            } else {
                /*layout = (RelativeLayout) findViewById(R.id.layoutBg);
                animation = AnimationUtils.loadAnimation(ReportFromDetailsActivity.this, R.anim.move_up);
                animation.setAnimationListener(ReportFromDetailsActivity.this);
                moveUp = true;
                moveDown = false;
                layout.setDrawingCacheEnabled(true);
                layout.startAnimation(animation);*/

                customAlert("No internet connection.", 0, false, null);
            }
        }

        //MMRSingleTone.getInstance().context = this;
        //startService(new Intent(getBaseContext(), GPSTracker.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mReportForm != null){
            DynamicForm.generateForm(mReportForm, null, ReportFromDetailsActivity.this);
            DynamicForm.saveFieldInputedValues(mReportForm);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DynamicForm.saveFieldInputedValues(mReportForm);
    }

    /*public void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ReportFromDetailsActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ReportFromDetailsActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getBaseContext(), GPSTracker.class));
        super.onDestroy();
    }

    public void availableLocation(String _address, String _postalCode, double lat, double lon){
        lattitude = lat;
        longitude = lon;
        address = _address;
        postalCode = _postalCode;

        //tvaddress.setVisibility(View.VISIBLE);
        //tvaddress.setText(address+"\n"+postalCode);

        if(mReportForm != null)
            DynamicForm.generateForm(mReportForm, null, ReportFromDetailsActivity.this);
    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                Util.hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch(viewId){
            case R.id.btnReset:
                if(mReportForm != null) {
                    DynamicForm.generateForm(mReportForm, null, ReportFromDetailsActivity.this);
                    DynamicForm.saveFieldInputedValues(mReportForm);
                }
                break;
            case R.id.btnNext:
                if(!Util.isConnected(ReportFromDetailsActivity.this)){
                    Toast.makeText(ReportFromDetailsActivity.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mReportForm != null && mReportForm.getFormFieldList() != null
                        && mReportForm.getFormFieldList().size() > 0) {

                    DynamicForm.saveFieldInputedValues(mReportForm);

                    /*List<FormField> _fieldDetailsList = mReportForm.getFormFieldList();
                    for (int count = 0; count < _fieldDetailsList.size(); count++) {
                        FormField formField = _fieldDetailsList.get(count);
                        FieldDetails fieldDetails = formField.getFieldDetails();
                        if (fieldDetails.getFieldType() == DynamicForm.FieldType.TEXTBOX ||
                                fieldDetails.getFieldType() == DynamicForm.FieldType.TEXTAREA ||
                                fieldDetails.getFieldType() == DynamicForm.FieldType.FILE ||
                                fieldDetails.getFieldType() == DynamicForm.FieldType.SNAP ||
                                fieldDetails.getFieldType() == DynamicForm.FieldType.DATE) {
                            String val = null;

                            for (int k = 0; k < formField.getViewList().size(); k++) {
                                View view = formField.getViewList().get(k);
                                if (view instanceof EditText) {
                                    val = ((EditText) view).getText().toString().trim();
                                } else if (view instanceof TextView) {
                                    val = ((TextView) view).getText().toString().trim();
                                } else {
                                    val = "Empty Val";
                                }
                            }

                            if (((val == null) || (val.length() <= 0)) &&
                                    (fieldDetails.getFieldValidation().getField_is_compulsory().equalsIgnoreCase("y"))) {
                                Toast.makeText(ReportFromDetailsActivity.this,
                                        fieldDetails.getLabelName() + " is mandatory.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!validateTypeField(fieldDetails, val).equalsIgnoreCase("valid")) {
                                Toast.makeText(ReportFromDetailsActivity.this,
                                        validateTypeField(fieldDetails, val), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (fieldDetails.getFieldType() == DynamicForm.FieldType.FILE) {

                                String[] filesCanUpload = {"bmp", "doc", "docx", "gif", "html",
                                        "jpeg", "jpg", "pdf", "png", "xls", "xlsx", "rar", "zip", "txt"};

                                boolean is_contain = false;

                                for (int i = 0; i < filesCanUpload.length; i++) {
                                    if (val.contains(filesCanUpload[i])) {
                                        is_contain = true;
                                        break;
                                    }
                                }

                                if (!is_contain) {
                                    Toast.makeText(ReportFromDetailsActivity.this,
                                            "Please check the file you are uploading.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                        } else if (fieldDetails.getFieldType() == DynamicForm.FieldType.SELECT ||
                                fieldDetails.getFieldType() == DynamicForm.FieldType.CHECKBOX ||
                                fieldDetails.getFieldType() == DynamicForm.FieldType.RADIO) {

                            String val = "";
                            if ((fieldDetails.getFieldType() == DynamicForm.FieldType.SELECT)) {
                                boolean isSelectedValue = false;
                                for (int k = 0; k < formField.getViewList().size(); k++) {
                                    View view = formField.getViewList().get(k);
                                    if (view instanceof Spinner) {

                                        String selectedOptionVal = (String) ((Spinner) view).getSelectedItem();
                                        for (int cnt = 0;
                                             cnt > fieldDetails.getFieldOptions().getOptions().size();
                                             cnt++) {
                                            String optoinName = fieldDetails.getFieldOptions().getOptions().get(cnt).getOptionName();
                                            if (optoinName.equalsIgnoreCase(selectedOptionVal)) {
                                                fieldDetails.getFieldOptions().getOptions().get(cnt).setSelected(true);
                                                isSelectedValue = true;
                                            }
                                        }
                                    }
                                }

                                if (fieldDetails.getFieldValidation().getField_is_compulsory().equalsIgnoreCase("y")
                                        && !isSelectedValue) {
                                    Toast.makeText(ReportFromDetailsActivity.this,
                                            fieldDetails.getLabelName() + " is mandatory.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } else if (fieldDetails.getFieldType() == DynamicForm.FieldType.CHECKBOX) {
                                boolean isSelectedValue = false;
                                for (int k = 0; k < formField.getViewList().size(); k++) {
                                    View view = formField.getViewList().get(k);
                                    if (view instanceof CheckBox) {
                                        CheckBox chkBox = (CheckBox) view;
                                        for (int cnt = 0; cnt < fieldDetails.getFieldOptions().getOptions().size(); cnt++) {
                                            Options option = fieldDetails.getFieldOptions().getOptions().get(cnt);
                                            if (chkBox.getText().toString().trim().equalsIgnoreCase(option.getOptionName())
                                                    && chkBox.isChecked()) {
                                                option.setSelected(true);
                                                isSelectedValue = true;
                                            }
                                        }
                                    }
                                }

                                if (fieldDetails.getFieldValidation().getField_is_compulsory().equalsIgnoreCase("y")
                                        && !isSelectedValue) {
                                    Toast.makeText(ReportFromDetailsActivity.this,
                                            fieldDetails.getLabelName() + " is mandatory.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } else if (fieldDetails.getFieldType() == DynamicForm.FieldType.RADIO) {
                                boolean isSelectedValue = false;
                                for (int k = 0; k < formField.getViewList().size(); k++) {
                                    View view = formField.getViewList().get(k);
                                    if (view instanceof RadioButton) {
                                        RadioButton radioBtn = ((RadioButton) view);
                                        for (int cnt = 0; cnt < fieldDetails.getFieldOptions().getOptions().size(); cnt++) {
                                            Options option = fieldDetails.getFieldOptions().getOptions().get(cnt);
                                            if (radioBtn.getText().toString().trim().equalsIgnoreCase(option.getOptionName())
                                                    && radioBtn.isChecked()) {
                                                option.setSelected(true);
                                                isSelectedValue = true;
                                            }
                                        }
                                    }
                                }

                                if (fieldDetails.getFieldValidation().getField_is_compulsory().equalsIgnoreCase("y")
                                        && !isSelectedValue) {
                                    Toast.makeText(ReportFromDetailsActivity.this,
                                            fieldDetails.getLabelName() + " is mandatory.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                    }*/

                    boolean isAllFieldValid = true;
                    List<FieldDetails> formFieldList = new ArrayList<FieldDetails>();
                    for(int i = 0; i < mReportForm.getFormFieldList().size(); i++) {

                        FieldDetails formField = mReportForm.getFormFieldList().get(i).getFieldDetails();

                        String validationMessage = validateTypeField(formField,
                                formField.getFieldValue());

                        if (validationMessage.equalsIgnoreCase("valid") == false) {
                            //customAlert(validationMessage, 0, false, null);
                            //TODO generate form with error message

                            isAllFieldValid = false;
                            formField.setIsInvalid(true);
                            formField.setErrorMsg(validationMessage);
                        }
                        formFieldList.add(formField);
                    }

                    if(isAllFieldValid == false){
                        DynamicForm.generateForm(mReportForm, formFieldList, ReportFromDetailsActivity.this);
                        return;
                    }

                    MMRSingleTone.getInstance().mReportForm = null;
                    MMRSingleTone.getInstance().mReportForm = mReportForm;
                    Intent intent = new Intent(ReportFromDetailsActivity.this, CameraCaptureActivity.class);
                    intent.putExtra("no_of_times_report_submited", no_of_times_report_submited);
                    intent.putExtra("report_title", title);
                    intent.putExtra("userResponseId", userResponseId);
                    startActivity(intent);
                    finish();

                    //startSubmitReportAsync();
                }
                break;
            default:
                break;
        }
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState()");
        outState.putSerializable(BUNDLE_KEY_REPORT_FORM, mReportForm);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState()");
        mReportForm = (ReportForm) savedInstanceState
                .getSerializable(BUNDLE_KEY_REPORT_FORM);
    }*/

    private class ReportListFromDetailsAsyncTask extends AsyncTask<String, Void, ReportForm>{

        String TAG = "ReportListFromDetailsAsyncTask";
        String message = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lProLayout.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);
            btnReset.setEnabled(false);
            mPocketBar.progressiveStop();
            mPocketBar.progressiveStart();
        }

        @Override
        protected ReportForm doInBackground(String... params) {

            String orgID = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
            if(orgID == null)
                return null;
            String apikey = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
            if(apikey == null)
                return null;
            String form_id = params[0];
            String fbassignuserid = params[1];

            ReportForm reportForm = null;

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            //int timeoutSocket = 5000;
            //HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(Constants.URL + Constants.REPORT_FORM_DETAILS_URL_REQUEST+orgID);
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
            parameters.add(new BasicNameValuePair("fb_form_id", form_id));
            parameters.add(new BasicNameValuePair("fb_assign_user_id", fbassignuserid));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(parameters));
                httpPost.setHeader("Authorization", apikey);
                InputStream in = httpClient.execute(httpPost).getEntity().getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                String line;
                String response = "";
                while((line = bufferedReader.readLine()) != null){
                    response += line;
                }

                Log.i(TAG, "ResPonse : ["+response+"]");

                

                try {
                    JSONObject responseObj = new JSONObject(response);

                    //reportForm = new ReportForm(Parcel.obtain());
                    //{"Response":[{"Apikey":"Api key does not exit"}]}

                    JSONArray jsonArray;

                    if(responseObj.isNull("Response") == false) {
                        jsonArray = responseObj.getJSONArray("Response");
                        for (int index = 0; index < jsonArray.length(); index++) {
                            JSONObject jObj = jsonArray.getJSONObject(index);

                            if (jObj != null && jObj.isNull("Apikey") == false) {
                                String msg = jObj.getString("Apikey");
                                message = msg + "\nPlease logout and login again to get your app work.";
                                return null;
                            }
                        }
                    }

                    JSONObject reportformObj = responseObj.getJSONObject("ReportForm");
                    JSONArray formDetails = reportformObj.getJSONArray("FormDetails");
                    List<FormDetails> details = new ArrayList<FormDetails>();
                    for (int i = 0; i < formDetails.length(); i++) {
                        //FormDetails fDetails = new FormDetails(Parcel.obtain());
                        FormDetails fDetails = new FormDetails();
                        fDetails.setFormID(formDetails.getJSONObject(i).getString("formID"));
                        fDetails.setFormName(formDetails.getJSONObject(i).getString("formName"));
                        details.add(fDetails);
                    }
                    reportForm = new ReportForm();
                    reportForm.setFormDetailsList(details);

                    List<FormField> formFieldList = new ArrayList<FormField>();
                    JSONArray feildDetailsArray = reportformObj.getJSONArray("FieldDetails");
                    for (int i = 0; i < feildDetailsArray.length(); i++) {
                        //FieldDetails fdDetails = new FieldDetails(Parcel.obtain());
                        FormField formField = new FormField();
                        FieldDetails fdDetails = new FieldDetails(Parcel.obtain());
                        fdDetails.setFieldID(Integer.parseInt(feildDetailsArray.getJSONObject(i).getString("fieldID")));
                        String fieldType = feildDetailsArray.getJSONObject(i).getString("fieldType");
                        if(fieldType != null && fieldType.trim().length() > 0){
                            if(fieldType.equalsIgnoreCase("textbox")){
                                fdDetails.setFieldType(DynamicForm.FieldType.TEXTBOX);
                            } else if(fieldType.equalsIgnoreCase("textarea")){
                                fdDetails.setFieldType(DynamicForm.FieldType.TEXTAREA);
                            } else if(fieldType.equalsIgnoreCase("file")){
                                fdDetails.setFieldType(DynamicForm.FieldType.FILE);
                            } else if(fieldType.equalsIgnoreCase("date")){
                                fdDetails.setFieldType(DynamicForm.FieldType.DATE);
                            } else if(fieldType.equalsIgnoreCase("select")){
                                fdDetails.setFieldType(DynamicForm.FieldType.SELECT);
                            } else if(fieldType.equalsIgnoreCase("checkbox")){
                                fdDetails.setFieldType(DynamicForm.FieldType.CHECKBOX);
                            } else if(fieldType.equalsIgnoreCase("radio")){
                                fdDetails.setFieldType(DynamicForm.FieldType.RADIO);
                            }
                        }

                        fdDetails.setLabelName(feildDetailsArray.getJSONObject(i).getString("labelName"));
                        fdDetails.setFieldDefaultValue(feildDetailsArray.getJSONObject(i).getString("fieldDefaultValue"));
                        fdDetails.setIs_multiple(feildDetailsArray.getJSONObject(i).getString("is_multiple"));

                        //FiledValidation filedValidation = new FiledValidation(Parcel.obtain());
                        FiledValidation filedValidation = new FiledValidation(Parcel.obtain());
                        JSONObject fvObj = feildDetailsArray.getJSONObject(i).getJSONObject("field_validation");
                        filedValidation.setMin_range(fvObj.getString("min_range"));
                        filedValidation.setMax_range(fvObj.getString("max_range"));
                        filedValidation.setValidation_type(fvObj.getString("validation_type"));
                        filedValidation.setField_is_compulsory(fvObj.getString("field_is_compulsory"));
                        fdDetails.setFieldValidation(filedValidation);

                        JSONObject fieldOptionsObj = feildDetailsArray.getJSONObject(i)
                                .getJSONObject("fieldOptions");
                        //FieldOptions fieldOptions = new FieldOptions(Parcel.obtain());
                        FieldOptions fieldOptions = new FieldOptions(Parcel.obtain());
                        List<Options> opList = new ArrayList<Options>();
                        JSONArray optionsArray = fieldOptionsObj.getJSONArray("options");
                        for (int j = 0; j < optionsArray.length(); j++) {
                            //Options options = new Options(Parcel.obtain());
                            Options options = new Options(Parcel.obtain());
                            options.setOptionName(optionsArray.getJSONObject(j).getString("optionName"));
                            options.setOptionId(Integer.parseInt(optionsArray.getJSONObject(j).getString("optionValue")));
                            opList.add(options);
                        }
                        fieldOptions.setOptions(opList);
                        fdDetails.setFieldOptions(fieldOptions);
                        formField.setFieldId(fdDetails.getFieldID());
                        formField.setFieldDetails(fdDetails);
                        formFieldList.add(formField);
                    }

                    reportForm.setFormFieldList(formFieldList);
                    message = "success";
                    return reportForm;
                } catch (JSONException e) {
                    message = "Server side error found.";
                    Log.e(TAG, "doInBackground()", e);
                    return null;
                }

            } catch(ConnectTimeoutException e){
                message = "Connection time out.\nPlease reset internet connection.";
                return null;
            }catch (UnsupportedEncodingException e) {
                Log.e(TAG, "doInBackground()", e);
                message = "Sorry, Unable to complete process.";
                return null;
            } catch (IllegalStateException e) {
                message = "Sorry, Unable to complete process.";
                return null;
            } catch (ClientProtocolException e) {
                message = "Sorry, Unable to complete process.";
                return null;
            } catch (IOException e) {
                message = "Sorry, Unable to complete process.";
                return null;
            } catch (Exception e){
                message = "Sorry, Unable to complete process.";
                return null;
            } finally {
                if(httpClient != null && httpClient.getConnectionManager() != null) {
                    httpClient.getConnectionManager().shutdown();
                }
            }
        }

        @Override
        protected void onPostExecute(ReportForm result) {
            super.onPostExecute(result);
            lProLayout.setVisibility(View.GONE);
            btnNext.setEnabled(true);
            btnReset.setEnabled(true);
            mPocketBar.progressiveStop();
            if(result != null && message != null && message.equalsIgnoreCase("success")) {
                mReportForm = result;
                DynamicForm.generateForm(mReportForm, null, ReportFromDetailsActivity.this);
                DynamicForm.saveFieldInputedValues(mReportForm);
            } else{
            /*layout = (RelativeLayout) findViewById(R.id.layoutBg);
            animation = AnimationUtils.loadAnimation(ReportFromDetailsActivity.this, R.anim.move_up);
            animation.setAnimationListener(ReportFromDetailsActivity.this);
            moveUp = true;
            moveDown = false;
            layout.setDrawingCacheEnabled(true);
            layout.startAnimation(animation);*/

                customAlert(message, 0, false, null);
            }
        }
    }

    private String validateTypeField(FieldDetails fieldDetails, String val) {

        String message = "valid";

        boolean isCompulsory = fieldDetails.getFieldValidation().getField_is_compulsory().equalsIgnoreCase("y");

        /*if(isCompulsory == false){
            return message;
        }*/

        //For input type validation
        if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldValidation().getValidation_type() != null &&
                fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("Numeric")){

            if(val != null && val.length() > 0){
                boolean isDigitsOnly = TextUtils.isDigitsOnly(val);
                if(!isDigitsOnly) {
                    return fieldDetails.getLabelName() + " : " + "Enter digits only.";
                }
            } else{
                if(isCompulsory){
                    return fieldDetails.getLabelName()+" : "+"Please enter value.";
                }
            }

        } else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldValidation().getValidation_type() != null &&
                fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("varchar")
                || fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("name")){
            if(val != null && val.length() > 0) {
                for (int i = 0; i < val.length(); i++) {
                    if (!(Character.isLetter(val.charAt(i))
                            || (val.charAt(i) == ' ')
                            || (val.charAt(i) == '.'))) {
                        return fieldDetails.getLabelName() + " : " + "Enter only alphabets, space, and dot.";
                    }
                }
            } else{
                if(isCompulsory){
                    return fieldDetails.getLabelName() + " : " + "Please enter value.";
                }
            }
        } else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldValidation().getValidation_type() != null &&
                fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("alpha numeric")){
            if(val != null && val.length() > 0) {
                for (int i = 0; i < val.length(); i++) {
                    if (!((Character.isDigit(val.charAt(i)) == true) ||
                            (Character.isLetter(val.charAt(i)) == true))){
                        return fieldDetails.getLabelName() + " : " + "Enter only alphabets and digits.";
                    }
                }

            } else{
                if(isCompulsory){
                    return fieldDetails.getLabelName() + " : " + "Please enter value.";
                }
            }
        } else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldValidation().getValidation_type() != null &&
                fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("decimal")){
            if(val != null && val.length() > 0) {
                boolean isvalid = true;
                try {
                    Double.parseDouble(val);
                } catch (Exception e) {
                    return fieldDetails.getLabelName() + " : " + "Enter Decimal only.";
                }
            } else{
                if(isCompulsory){
                    return fieldDetails.getLabelName() + " : " + "Please enter value.";
                }
            }

        } else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldValidation().getValidation_type() != null &&
                fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("email")){
            if(val != null && val.length() > 0) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(val).matches()){
                    return fieldDetails.getLabelName() + " : " + "Enter valid email address.";
                }
            } else{
                if(isCompulsory){
                    return fieldDetails.getLabelName() + " : " + "Please enter email address.";
                }
            }
        } else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldValidation().getValidation_type() != null &&
                fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("mobile")){
            if(val != null && val.length() > 0) {
                boolean isDigitsOnly = TextUtils.isDigitsOnly(val);
                if (!isDigitsOnly)
                    return fieldDetails.getLabelName() + " : " + "Enter digits only.";
                if (val.length() != 10)
                    return fieldDetails.getLabelName() + " : " + "Enter 10 digits only.";


            } else{
                if(isCompulsory){
                    return fieldDetails.getLabelName() + " : " + "Please enter value.";
                }
            }
        } /*else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldValidation().getValidation_type() != null &&
                fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("pin")){
            if(val != null && val.length() > 0) {
                if (!TextUtils.isDigitsOnly(val))
                    return fieldDetails.getLabelName() + " : " + "Enter Digits Only.";
                if (val.length() != 6)
                    return fieldDetails.getLabelName() + " : " + "Enter 6 digits only.";
            } else{
                return fieldDetails.getLabelName() + " : " + "Please enter value.";
            }
        } else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldValidation().getValidation_type() != null &&
                fieldDetails.getFieldValidation().getValidation_type().equalsIgnoreCase("address")){
            if(val != null && val.length() > 0) {
                if (val.length() > 1000) {
                    return fieldDetails.getLabelName() + " : " + "Address should not exceed 1000 characters.";
                }
            } else{
                if(isCompulsory){
                    return fieldDetails.getLabelName() + " : " + "Please enter value.";
                }
            }
        }*/ else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldType() != null && isCompulsory &&
                fieldDetails.getFieldType() == DynamicForm.FieldType.FILE){
            if(val == null || val.length() <= 0){
                return fieldDetails.getLabelName()+" : "+"Select a file to upload.";
            }

        } else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldType() != null && isCompulsory &&
                fieldDetails.getFieldType() == DynamicForm.FieldType.DATE){
            if(val == null || val.length() <= 0){
                return fieldDetails.getLabelName()+" : "+"Please enter a date.";
            }

        } else if(fieldDetails != null && fieldDetails.getFieldValidation() != null &&
                fieldDetails.getFieldType() != null && isCompulsory &&
                ((fieldDetails.getFieldType() == DynamicForm.FieldType.SELECT)
                        || (fieldDetails.getFieldType() == DynamicForm.FieldType.RADIO)
                        || (fieldDetails.getFieldType() == DynamicForm.FieldType.CHECKBOX))){
            if(fieldDetails.getFieldOptions() != null && fieldDetails.getFieldOptions().getOptions() != null) {
                boolean isAnyOneSelected = false;

                for (int cnt = 0; cnt < fieldDetails.getFieldOptions().getOptions().size(); cnt++) {
                    Options option = fieldDetails.getFieldOptions().getOptions().get(cnt);
                    if (option.isSelected()) {
                        isAnyOneSelected = true;
                    }
                }
                if (isAnyOneSelected == false) {
                    return fieldDetails.getLabelName() + " : " + "Please select any one.";
                }
            }
        }

        //For Minimum range validation
        if( val != null && val.length() < Integer.parseInt(fieldDetails.getFieldValidation().getMin_range()) && isCompulsory){

            boolean isNumber = TextUtils.isDigitsOnly(fieldDetails.getFieldValidation().getMin_range());

            if(isNumber){
                int minRange = Integer.parseInt(fieldDetails.getFieldValidation().getMin_range());
                if(minRange > 1){
                    return fieldDetails.getLabelName()+" : "+"Enter at least "+fieldDetails.getFieldValidation().getMin_range()+" characters.";
                } else{
                    return fieldDetails.getLabelName()+" : "+"Enter at least "+fieldDetails.getFieldValidation().getMin_range()+" character.";
                }

            } else{
                return fieldDetails.getLabelName()+" : "+"Enter at least 1 character.";
            }
        }

        //For Maximum range validation
        if(val != null &&
                fieldDetails.getFieldType() != DynamicForm.FieldType.DATE
                && fieldDetails.getFieldType() != DynamicForm.FieldType.FILE
                && val.length() > Long.parseLong(fieldDetails.getFieldValidation().getMax_range()) ){
            return fieldDetails.getLabelName()+" : "+"Max range is "+fieldDetails.getFieldValidation().getMax_range();
        }

        fieldDetails.setIsInvalid(false);
        fieldDetails.setErrorMsg(null);

        return message;
    }

    public void startSubmitReportAsync(){
        ReportEntrySubmissionAsyncTask asyncTask = new ReportEntrySubmissionAsyncTask();
        asyncTask.execute();
    }


    private class ReportEntrySubmissionAsyncTask extends AsyncTask<Void, Void, Boolean>{

        String TAG = "ReportEntrySubmissionAsyncTask";
        String userResponseId = null;
        String submitMessage = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lProLayout.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);
            btnReset.setEnabled(false);
            mPocketBar.progressiveStop();
            mPocketBar.progressiveStart();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String orgID = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
            if(orgID == null)
                return null;
            String apikey = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_API_KEY, null);
            if(apikey == null)
                return null;

            String charset = "UTF-8";
            String requestURL = Constants.URL+Constants.REPORT_ENTRY_SUBMIT+orgID;

            try {
                MultipartUtility multipart = new MultipartUtility(requestURL, charset, apikey);

                for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
                    multipart.addFormField("orderElement[] : ", mReportForm.getFormFieldList().get(i).getFieldId()+"");
                    Log.i("Result", "orderElement[] : "+ mReportForm.getFormFieldList().get(i).getFieldId()+"");
                }

                multipart.addFormField("FbField[fb_form_id]", mReportForm.getFormDetailsList().get(0).getFormID());
                Log.i("Result", "FbField[fb_form_id]" + mReportForm.getFormDetailsList().get(0).getFormID());


                for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
                    FieldDetails fieldDetails = mReportForm.getFormFieldList().get(i).getFieldDetails();
                    if(fieldDetails.getFieldType() == DynamicForm.FieldType.FILE){
                        File fileTobeUpload = new File(fieldDetails.getFieldValue());
                        if(fileTobeUpload.exists()) {
                            multipart.addFilePart("FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]", fileTobeUpload);
                            Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
                            multipart.addFormField("FbField[field_" + fieldDetails.getFieldID() + "][name]", fileTobeUpload.getName());
                            Log.i("Result", "FbField[field_" + fieldDetails.getFieldID() + "][tmp_name]" + "");
                        }
                    } else if(fieldDetails.getFieldType() == DynamicForm.FieldType.SELECT
                            || fieldDetails.getFieldType() == DynamicForm.FieldType.CHECKBOX
                            || fieldDetails.getFieldType() == DynamicForm.FieldType.RADIO) {
                        for (int cnt = 0; cnt < fieldDetails.getFieldOptions().getOptions().size(); cnt++) {
                            Options option = fieldDetails.getFieldOptions().getOptions().get(cnt);
                            if(option.isSelected()) {
                                multipart.addFormField("FbField[field_" + fieldDetails.getFieldID() + "][]", option.getOptionId()+"");
                                Log.i("Result", "FbField[field_" + "FbField[field_" + fieldDetails.getFieldID() + "][]"+ option.getOptionId()+"");
                            }
                        }
                    } else{
                        multipart.addFormField("FbField[field_"+fieldDetails.getFieldID()+"]", fieldDetails.getFieldValue());
                        Log.i("Result", "FbField[field_"+fieldDetails.getFieldID()+"]"+ fieldDetails.getFieldValue());
                    }
                }

               /* if(gps != null && gps.canGetLocation()){
                    lattitude = gps.getLocation().getLatitude();
                    longitude = gps.getLocation().getLongitude();
                }
                multipart.addFormField("lattitude", lattitude+"");
                Log.i("Result", "lattitude" + lattitude);
                multipart.addFormField("longitude", longitude + "");
                Log.i("Result", "longitude"+longitude);*/

                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");
                String res = "";
                for (String line : response) {
                    res = res + line + "\n";
                }
                Log.i(TAG, res);
               
               /* {"Response":
                *        [{"submitStatus":"Success",
                * "      submitMessage":"Report submitted successfully.",
                * "   userResponseId":"38"}]
                * }
                */


                JSONArray jsonArray;
                JSONObject jObject;
                try {
                    jObject = new JSONObject(res);
                    jsonArray = jObject.getJSONArray("Response");
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(index);
                        String submitStatus = jsonObject.optString("submitStatus", "");
                        submitMessage = jsonObject.optString("submitMessage", "");
                        userResponseId = jsonObject.optString("userResponseId", "");

                        if(submitStatus.equalsIgnoreCase("failed")){
                            // {"field_344":"This field is required"}

                            String res1 = submitMessage;
                            String str = "";
                            String[] fields = res1.split(":");
                            if(fields != null && fields.length > 0){
                                str = fields[0];
                                str = str.replace("{","");
                                str = str.replace("\"","");
                            }

                            String fieldName = "";
                            for(int i = 0; i < mReportForm.getFormFieldList().size(); i++){
                                String formID = "field_"+mReportForm.getFormFieldList().get(i).getFieldId();
                                if(formID != null && formID.equalsIgnoreCase(str)){
                                    fieldName = mReportForm.getFormFieldList().get(i).getFieldDetails().getLabelName();
                                }
                            }

                            if(fieldName != null && fieldName.length() > 0) {
                                submitMessage = fieldName + " field is required.";
                            }

                            return false;
                        }

                        if(submitStatus.equalsIgnoreCase("Success")){
                            return true;
                        }

                        return false;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "doInBackground()", e);
                }

            } catch(ConnectTimeoutException e){
                submitMessage = "Connection time out.\nPlease reset internet connection.";
            }catch (IOException ex) {
                System.err.println(ex);
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);
            lProLayout.setVisibility(View.INVISIBLE);
            btnNext.setEnabled(true);
            btnReset.setEnabled(true);
            mPocketBar.progressiveStop();

            if(result == true){
                customAlert("Submitted successfully.", 1, true, userResponseId);
            } else{
            /*layout = (RelativeLayout) findViewById(R.id.layoutBg);
            animation = AnimationUtils.loadAnimation(ReportFromDetailsActivity.this, R.anim.move_up);
            animation.setAnimationListener(ReportFromDetailsActivity.this);
            moveUp = true;
            moveDown = false;
            layout.setDrawingCacheEnabled(true);
            layout.startAnimation(animation);*/

                customAlert(submitMessage, 0, false, null);
            }

        }

    }

    private void customAlert(String message, int isSuccess,
                             final boolean isFormsuccessfullySubmitted, final String userResponseId){

        this.isFormsuccessfullySubmitted = isFormsuccessfullySubmitted;
        this.userResponseId = userResponseId;

        layout = (RelativeLayout) findViewById(R.id.layoutBg);
        animation = AnimationUtils.loadAnimation(ReportFromDetailsActivity.this, R.anim.move_up);
        animation.setAnimationListener(ReportFromDetailsActivity.this);
        moveUp = true;
        moveDown = false;
        layout.setDrawingCacheEnabled(true);
        layout.startAnimation(animation);

        TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
        TextView tvOk = (TextView) findViewById(R.id.tvOk);
        ImageView img = (ImageView) findViewById(R.id.img);
        tvMsg.setText(message);

        if(isSuccess == 0){
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_remove));
            if(layout != null) {
                layout.setBackgroundColor(getResources().getColor(R.color.red));
            }
        } else if(isSuccess == 1){
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_accept));
            if(layout != null) {
                layout.setBackgroundColor(getResources().getColor(R.color.login_btn_color_pressed));
            }
        }

        tvOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if(isFormsuccessfullySubmitted && userResponseId != null){
                    MMRSingleTone.getInstance().mReportForm = null;
                    MMRSingleTone.getInstance().mReportForm = mReportForm;
                    Intent intent = new Intent(ReportFromDetailsActivity.this, CameraCaptureActivity.class);
                    intent.putExtra("no_of_times_report_submited", no_of_times_report_submited);
                    intent.putExtra("report_title", title);
                    intent.putExtra("userResponseId", userResponseId);
                    startActivity(intent);
                    finish();
                } else{*/
                layout = (RelativeLayout) findViewById(R.id.layoutBg);
                animation = AnimationUtils.loadAnimation(ReportFromDetailsActivity.this, R.anim.move_down);
                animation.setAnimationListener(ReportFromDetailsActivity.this);
                moveUp = false;
                moveDown = true;
                layout.startAnimation(animation);
                //}
            }
        });
    }

    public void getFilesFromSdCard(ArrayList<FieldDetails> fieldDetails, int fieldid){
//        public void getFilesFromSdCard(ArrayList<FieldDetails> fieldDetails, FieldDetails mCurrentFieldToUpdate){

        Intent intent = new Intent(ReportFromDetailsActivity.this, FileChooser.class);
        intent.putExtra(Constants.INTENT_KEY_CURRENT_FILE_TO_BE_UPDATE, fieldid);
        intent.putParcelableArrayListExtra(Constants.INTENT_KEY_FIELD_DETAILS, fieldDetails);

        startActivityForResult(intent, FILE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult()");
        if(requestCode == FILE_REQ){
            if(resultCode == RESULT_OK){
                String path = data.getExtras().getString(Constants.INTENT_KEY_FILE_PATH);
                File fileToBeUpload = new File(path);
                //FieldDetails currentFieldToBeUpdate = data.getExtras().getParcelable(Constants.INTENT_KEY_CURRENT_FILE_TO_BE_UPDATE);
                int id = data.getExtras().getInt(Constants.INTENT_KEY_CURRENT_FILE_TO_BE_UPDATE);
                List<FieldDetails> fieldDetailsList = data.getExtras().getParcelableArrayList(Constants.INTENT_KEY_FIELD_DETAILS);


                if(fileToBeUpload.exists()){
                    String[] filesCanUpload = {"bmp", "doc", "docx", "gif", "html",
                            "jpeg", "jpg" ,"pdf" ,"png", "xls" ,"xlsx", "rar", "zip", "txt"};

                    boolean is_contain = false;

                    for (int i = 0; i < filesCanUpload.length; i++) {
                        if( fileToBeUpload.getName().contains(filesCanUpload[i])) {
                            is_contain = true;
                            break;
                        }
                    }

                    if(is_contain && id > 0){
                        for(int count = 0; count < fieldDetailsList.size(); count++){
                            if(fieldDetailsList.get(count).getFieldID() == id){
                                //fieldDetailsList.get(count).setFieldValue(fileToBeUpload.getName());
                                fieldDetailsList.get(count).setFieldValue(path);
                            }
                        }
                        DynamicForm.generateForm(mReportForm, fieldDetailsList, ReportFromDetailsActivity.this);
                        DynamicForm.saveFieldInputedValues(mReportForm);
                    } else{
                        DynamicForm.generateForm(mReportForm, fieldDetailsList, ReportFromDetailsActivity.this);
                        DynamicForm.saveFieldInputedValues(mReportForm);
                        Toast.makeText(getApplicationContext(), "Please check the file you are uploading.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else{
                    DynamicForm.generateForm(mReportForm, fieldDetailsList, ReportFromDetailsActivity.this);
                    DynamicForm.saveFieldInputedValues(mReportForm);
                }
            } else{
                FieldDetails currentFieldToBeUpdate = data.getExtras().getParcelable(Constants.INTENT_KEY_CURRENT_FILE_TO_BE_UPDATE);
                List<FieldDetails> fieldDetailsList = data.getExtras().getParcelableArrayList(Constants.INTENT_KEY_FIELD_DETAILS);
                DynamicForm.generateForm(mReportForm, fieldDetailsList, ReportFromDetailsActivity.this);
                DynamicForm.saveFieldInputedValues(mReportForm);
                Toast.makeText(getApplicationContext(), "File not Exists", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        layoutTransparent.startAnimation(animation);
        if(moveUp)
            layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(moveDown){
            moveDown = false;
            moveUp = false;
            layout.setVisibility(View.GONE);
        }
        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
        animation.setDuration(1);
        layoutTransparent.startAnimation(animation);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onBackPressed() {

        boolean isUserLeavingThisFormPageWithHavingData = false;

        if(mReportForm != null && mReportForm.getFormFieldList() != null
                && mReportForm.getFormFieldList().size() > 0) {

            DynamicForm.saveFieldInputedValues(mReportForm);

            for(int i = 0; i < mReportForm.getFormFieldList().size(); i++) {

                FormField formField = mReportForm.getFormFieldList().get(i);

                if(isUserLeavingThisFormPageWithHavingData == false
                        && formField.getFieldDetails() != null
                        && formField.getFieldDetails().getFieldValue() != null
                        && formField.getFieldDetails().getFieldValue().length() > 0){
                    isUserLeavingThisFormPageWithHavingData = true;
                }
            }
        }

        if(isUserLeavingThisFormPageWithHavingData){
            CAlertDialog cdd=new CAlertDialog(ReportFromDetailsActivity.this,
                    getResources().getString(R.string.confirmation_msg_to_leave_page), "OK", "Cancel", true);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        } else {
            super.onBackPressed();
        }
    }
}