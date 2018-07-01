package com.hanzhuang42.showme;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ColoredPreference extends Preference {
    public ColoredPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ColoredPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ColoredPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColoredPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        view.setBackground(getContext().getDrawable(R.drawable.red_bkg));
        final TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextColor(Color.WHITE);
        ViewGroup.LayoutParams p=titleView.getLayoutParams();
        p.width= ViewGroup.LayoutParams.MATCH_PARENT;
        titleView.setGravity(Gravity.CENTER);
        titleView.setLayoutParams(p);

    }
}
