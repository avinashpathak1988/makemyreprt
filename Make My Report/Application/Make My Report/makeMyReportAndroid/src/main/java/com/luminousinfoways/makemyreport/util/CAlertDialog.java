package com.luminousinfoways.makemyreport.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.luminousinfoways.makemyreport.R;

/**
 * Created by Suhasini on 30-Mar-15.
 */
public class CAlertDialog extends Dialog implements
        View.OnClickListener {

    public Context c;
    public Dialog d;
    public Button cancel, ok;
    String message;
    String textForPosBtn;
    String textForNegBtn;
    boolean finishOnButtonClick;

    public CAlertDialog(Context a, String message, String textForPosBtn, String textForNegBtn, boolean finishOnButtonClick) {
        super(a);
        this.c = a;
        this.message = message;
        this.textForPosBtn = textForPosBtn;
        this.textForNegBtn = textForNegBtn;
        this.finishOnButtonClick = finishOnButtonClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        TextView txt_dia = (TextView) findViewById(R.id.txt_dia);
        txt_dia.setText(message);

        cancel = (Button) findViewById(R.id.btn_cancel);
        ok = (Button) findViewById(R.id.btn_ok);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);

        if(textForPosBtn != null && textForNegBtn != null){
            cancel.setVisibility(View.VISIBLE);
            ok.setVisibility(View.VISIBLE);
            ok.setText(textForPosBtn);
            cancel.setText(textForNegBtn);
        } else if(textForNegBtn != null && textForPosBtn == null){
            cancel.setVisibility(View.VISIBLE);
            ok.setVisibility(View.GONE);
            cancel.setText(textForNegBtn);
        } else if(textForNegBtn == null && textForPosBtn != null){
            cancel.setVisibility(View.GONE);
            ok.setVisibility(View.VISIBLE);
            ok.setText(textForPosBtn);
        } else{
            cancel.setVisibility(View.GONE);
            ok.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                dismiss();
                if(c instanceof Activity)
                    ((Activity) c).finish();
                break;
            default:
                break;
        }
        dismiss();
    }

}