package com.luminousinfoways.makemyreport.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.luminousinfoways.makemyreport.R;

/**
 * Created by Suhasini on 07-Apr-15.
 */
public class CustomFontButton extends Button {

    public CustomFontButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CustomFontButton(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs!=null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomFontButton);
            String fontName = a.getString(R.styleable.CustomFontButton_buttonFontName);
            if (fontName!=null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "font/"+fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }

}