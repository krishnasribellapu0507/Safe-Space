package com.safespace.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/** Optional 30-second wellbeing detail flow. */
final class WellbeingCheckInView extends FrameLayout {
    private static final String[] QUESTIONS = {
            "How was your sleep?", "How stressed do you feel?", "How is your energy?",
            "How safe do you feel right now?", "How connected do you feel?"
    };
    private static final String[] LOW = {"Very poor","Very low","Very low","Not safe","Disconnected"};
    private static final String[] HIGH = {"Restful","Very high","Very high","Very safe","Connected"};

    private final Activity activity;
    private final ScreenNavigator navigator;
    private final int[] values = {3,3,3,4,3};
    private int step;
    private final TextView progress;
    private final TextView question;
    private final TextView value;
    private final TextView next;
    private final SeekBar slider;

    WellbeingCheckInView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity=activity;
        this.navigator=navigator;
        setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFFE4F2FF,0xFFF5F2FF,0xFFFFF2F7}));

        LinearLayout page=new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22),dp(18),dp(22),dp(28));
        addView(page,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        page.addView(FinalScreenUi.titledHeader(activity,this,navigator,"Detailed check-in","About 30 seconds"),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT,dp(60),0,0,0,16,this));

        progress=FinalScreenUi.text(activity,"1 of 5",12,FinalScreenUi.PURPLE,true);
        page.addView(progress);

        question=FinalScreenUi.text(activity,QUESTIONS[0],26,FinalScreenUi.NAVY,true);
        question.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams qp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f);
        page.addView(question,qp);

        value=FinalScreenUi.text(activity,"3 • In the middle",15,FinalScreenUi.MUTED,true);
        value.setGravity(Gravity.CENTER);
        page.addView(value,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(48)));

        slider=new SeekBar(activity);
        slider.setMax(4);
        slider.setProgress(values[0]-1);
        slider.setContentDescription("Wellbeing response slider from 1 to 5");
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar,int p,boolean fromUser){values[step]=p+1;refreshValue();}
            public void onStartTrackingTouch(SeekBar seekBar){}
            public void onStopTrackingTouch(SeekBar seekBar){}
        });
        page.addView(slider,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp(64)));

        LinearLayout buttons=new LinearLayout(activity);
        buttons.setGravity(Gravity.CENTER);
        TextView back=FinalScreenUi.text(activity,"Back",14,FinalScreenUi.NAVY,true);
        back.setGravity(Gravity.CENTER);
        back.setBackground(FinalScreenUi.ripple(0xEEFFFFFF,18,this));
        back.setOnClickListener(v->previous());
        buttons.addView(back,new LinearLayout.LayoutParams(0,dp(54),1f));

        next=FinalScreenUi.text(activity,"Next",14,Color.WHITE,true);
        next.setGravity(Gravity.CENTER);
        next.setBackground(FinalScreenUi.ripple(0xFF7558DE,18,this));
        next.setOnClickListener(v->advance());
        LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(0,dp(54),1f);np.setMarginStart(dp(12));
        buttons.addView(next,np);
        page.addView(buttons,FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT,dp(54),0,20,0,0,this));

        setOnApplyWindowInsetsListener((v,insets)->{page.setPadding(dp(22),insets.getSystemWindowInsetTop()+dp(12),dp(22),insets.getSystemWindowInsetBottom()+dp(24));return insets;});
        requestApplyInsets();
        refresh();
    }

    private void advance(){
        if(step<QUESTIONS.length-1){step++;refresh();return;}
        int mood=activity.getSharedPreferences(SupportSignalEngine.PREFS,Context.MODE_PRIVATE).getInt("last_mood_value",3);
        SupportSignalEngine.recordDetailedWellbeing(activity,mood,values[0],values[1],values[2],values[3],values[4]);
        new AlertDialog.Builder(activity)
                .setTitle("Check-in complete")
                .setMessage("Thanks for taking a moment for yourself. Your wellbeing snapshot now reflects this check-in.")
                .setPositiveButton("View insights",(d,w)->navigator.openScreen(14))
                .setNegativeButton("Done",(d,w)->navigator.openScreen(8))
                .show();
    }

    private void previous(){
        if(step==0){navigator.goBack();return;}
        step--;refresh();
    }

    private void refresh(){
        progress.setText((step+1)+" of "+QUESTIONS.length);
        question.setText(QUESTIONS[step]);
        slider.setProgress(values[step]-1);
        next.setText(step==QUESTIONS.length-1?"Complete":"Next");
        refreshValue();
        MotionSystem.enter(question,0);
    }

    private void refreshValue(){
        int v=values[step];
        String label=v==1?LOW[step]:v==5?HIGH[step]:v==3?"In the middle":v==2?"A little low":"A little high";
        value.setText(v+" • "+label);
        value.setContentDescription(QUESTIONS[step]+" "+label);
    }

    private int dp(float value){return FinalScreenUi.dp(this,value);}
}
