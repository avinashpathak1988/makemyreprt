package com.luminousinfoways.makemyreportandroid.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.luminousinfoways.makemyreportandroid.Asynctask.CorporateLoginAsyncTask;
import com.luminousinfoways.makemyreportandroid.Asynctask.UserLoginAsyncTask;
import com.luminousinfoways.makemyreportandroid.R;
import com.luminousinfoways.makemyreportandroid.ReportList.MainActivity;
import com.luminousinfoways.makemyreportandroid.util.Constants;
import com.luminousinfoways.makemyreportandroid.util.FlipAnimation;
import com.luminousinfoways.makemyreportandroid.util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends ActionBarActivity
        implements CorporateLoginAsyncTask.OnCompleteCorporateLoginListener,
        UserLoginAsyncTask.OnCompleteUserLoginListener{

    private ActionProcessButton btnSignIn = null;
    private ActionProcessButton btnCorSignIn = null;
    private MaterialEditText editCorId = null;
    private MaterialEditText editUserId = null;
    private MaterialEditText editPassword = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUserId = (MaterialEditText) findViewById(R.id.userid);
        editPassword = (MaterialEditText) findViewById(R.id.password);
        editCorId = (MaterialEditText) findViewById(R.id.corId);

        btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validate Filed
                String user = editUserId.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (user == null || user.length() <= 0) {
                    editUserId.setError("Please enter user Id.");
                    editUserId.setErrorColor(getResources().getColor(R.color.red_error));
                    return;
                }

                if (password == null || password.length() <= 0) {
                    editPassword.setError("Please enter password.");
                    editPassword.setErrorColor(getResources().getColor(R.color.red_error));
                    return;
                }

                btnSignIn.setProgress(50);
                String orgId = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);

                if(orgId == null){
                    //TODO show alert here
                    return;
                }

                UserLoginAsyncTask userLoginAsync = new UserLoginAsyncTask(LoginActivity.this, LoginActivity.this);
                userLoginAsync.execute(user, password, orgId);
                btnSignIn.setEnabled(false);
                editUserId.setEnabled(false);
                editPassword.setEnabled(false);
            }
        });

        btnCorSignIn = (ActionProcessButton) findViewById(R.id.btnCorSignIn);
        btnCorSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnCorSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validate Filed
                String corporateId = editCorId.getText().toString().trim();

                if(corporateId == null || corporateId.length() <= 0){
                    editCorId.setError("Please enter Corporate ID.");
                    editCorId.setErrorColor(getResources().getColor(R.color.red_error));
                    return;
                }

                btnCorSignIn.setProgress(50);
                CorporateLoginAsyncTask corLoginAsync = new CorporateLoginAsyncTask(LoginActivity.this, LoginActivity.this);
                corLoginAsync.execute(corporateId);
                btnSignIn.setEnabled(false);
                editUserId.setEnabled(false);
                editPassword.setEnabled(false);
            }
        });

        String orgId = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_ID, null);
        String orgName = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getString(Constants.SP_ORG_NAME, null);
        int loginSts = getSharedPreferences(Constants.SHARED_PREFERENCE_KEY, 1).getInt(Constants.SP_USER_LOGIN_STS, 0);

        if(orgId != null && orgName != null){

            if(loginSts == 1) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                flipCard();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof MaterialEditText &&
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
    public void onCompleteCorporateLogin(String statusMessage) {
        btnCorSignIn.setProgress(100);
        if(statusMessage == null){

            btnCorSignIn.setText("Log In");
            btnCorSignIn.setEnabled(true);
            editCorId.setEnabled(true);
            editCorId.setError("Failed to log in.");
            editCorId.setErrorColor(getResources().getColor(R.color.red_error));

            return;
        }

        if(statusMessage.equalsIgnoreCase("Success")){
            Toast.makeText(this, "Success Login", Toast.LENGTH_SHORT).show();
            flipCard();
            btnSignIn.setEnabled(true);
            editUserId.setEnabled(true);
            editPassword.setEnabled(true);
        } else{
            btnCorSignIn.setEnabled(true);
            editCorId.setEnabled(true);
            editCorId.setError(statusMessage);
            editCorId.setErrorColor(getResources().getColor(R.color.red_error));
            btnCorSignIn.setText("Log In");
        }
    }

    @Override
    public void onCompleteUserLogin(String statusMessage) {
        btnSignIn.setProgress(100);

        if(statusMessage != null && statusMessage.equalsIgnoreCase("Success")){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else{
            btnSignIn.setText("Log In");
            btnSignIn.setEnabled(true);
            editUserId.setEnabled(true);
            editPassword.setEnabled(true);
            editUserId.setError("Please enter correct User Id.");
            editPassword.setError("Please enter correct password.");
            editUserId.setErrorColor(getResources().getColor(R.color.red_error));
            editPassword.setErrorColor(getResources().getColor(R.color.red_error));
            btnSignIn.setText("Sign In");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_corporate_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void flipCard()
    {
        View rootLayout = findViewById(R.id.main_layout);
        View cardFace = findViewById(R.id.corporate_login_layout);
        View cardBack = findViewById(R.id.user_login_layout);

        FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

        if (cardFace.getVisibility() == View.GONE)
        {
            flipAnimation.reverse();
        }
        rootLayout.startAnimation(flipAnimation);
    }
}
