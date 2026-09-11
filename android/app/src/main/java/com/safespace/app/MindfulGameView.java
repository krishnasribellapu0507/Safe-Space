package com.safespace.app;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Four simple original mindful games inspired by common grounding-play mechanics. */
final class MindfulGameView extends PastelScreenView {
    private final FrameLayout gameHost;
    private final TextView status;
    private int game;

    MindfulGameView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, -1);
        setContentDescription("Mindful calming games");
        content.addView(header("Play", "Small games to bring attention back to the present", true, "", ""),
                marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(76), 0, 0, 0, dp(8)));

        LinearLayout tabs = segmentedControl(new String[]{"Bubbles", "Zen", "Fireflies"}, 0, v -> {
            game = (Integer) v.getTag();
            renderGame();
        });
        content.addView(tabs, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(46), 0, 0, 0, dp(12)));

        gameHost = new FrameLayout(activity);
        gameHost.setBackground(rounded(0xE9FFFFFF, dp(24), dp(1), 0x337B61D9));
        content.addView(gameHost, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(360), 0, 0, 0, dp(12)));

        status = text("Tap the bubbles gently", 13, NAVY, true);
        status.setGravity(Gravity.CENTER);
        content.addView(status, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(42), 0, 0, 0, dp(8)));

        LinearLayout blocks = bottomRowCard("▦", 0xFFFFE4EA, 0xFFE16F8F,
                "Calm Blocks", "Tap to stack one block at a time", "Play", "");
        blocks.setOnClickListener(v -> {
            game = 3;
            renderGame();
        });
        content.addView(blocks, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(76), 0, 0, 0, 0));
        renderGame();
    }

    private void renderGame() {
        gameHost.removeAllViews();
        View v;
        if (game == 1) { v = new ZenView(activity); status.setText("Draw slow lines through the sand"); }
        else if (game == 2) { v = new FireflyView(activity); status.setText("Tap the glowing fireflies as they appear"); }
        else if (game == 3) { v = new BlocksView(activity); status.setText("Tap to build a calm little tower"); }
        else { v = new BubbleView(activity); status.setText("Tap the bubbles gently"); }
        gameHost.addView(v, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private static final class BubbleView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random r = new Random();
        private final List<float[]> bubbles = new ArrayList<>();
        BubbleView(Activity a) { super(a); setBackgroundColor(0xFFE8FBFF); }
        private void reset() {
            bubbles.clear();
            for (int i=0;i<12;i++) bubbles.add(new float[]{40+r.nextFloat()*Math.max(40,getWidth()-80), 40+r.nextFloat()*Math.max(40,getHeight()-80), 20+r.nextFloat()*28});
        }
        @Override protected void onSizeChanged(int w,int h,int ow,int oh){ reset(); }
        @Override protected void onDraw(Canvas c){ super.onDraw(c); int[] cs={0x8892D5FF,0x8872E6DB,0x88C5A4FF,0x88FFB9D0}; for(int i=0;i<bubbles.size();i++){float[] b=bubbles.get(i);p.setColor(cs[i%cs.length]);c.drawCircle(b[0],b[1],b[2],p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(0xAAFFFFFF);c.drawCircle(b[0]-b[2]*.2f,b[1]-b[2]*.2f,b[2]*.55f,p);p.setStyle(Paint.Style.FILL);} }
        @Override public boolean onTouchEvent(MotionEvent e){ if(e.getAction()!=MotionEvent.ACTION_DOWN)return true; for(int i=bubbles.size()-1;i>=0;i--){float[] b=bubbles.get(i);float dx=e.getX()-b[0],dy=e.getY()-b[1];if(dx*dx+dy*dy<=b[2]*b[2]){bubbles.remove(i);invalidate(); if(bubbles.isEmpty()) reset(); return true;}} return true; }
    }

    private static final class ZenView extends View {
        private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private final Path path=new Path();
        ZenView(Activity a){super(a);setBackgroundColor(0xFFFFF1E6);p.setColor(0xAA9B785C);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(9);p.setStrokeCap(Paint.Cap.ROUND);}
        @Override protected void onDraw(Canvas c){super.onDraw(c); p.setColor(0x22765F4D); for(int y=28;y<getHeight();y+=28)c.drawLine(0,y,getWidth(),y,p); p.setColor(0xAA9B785C);c.drawPath(path,p);}
        @Override public boolean onTouchEvent(MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN)path.moveTo(e.getX(),e.getY());else if(e.getAction()==MotionEvent.ACTION_MOVE)path.lineTo(e.getX(),e.getY());invalidate();return true;}
    }

    private static final class FireflyView extends View {
        private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private final Random r=new Random(); private float x=150,y=160;
        FireflyView(Activity a){super(a);setBackgroundColor(0xFF102445);postDelayed(this::move,800);}
        private void move(){if(getWidth()>80&&getHeight()>80){x=40+r.nextFloat()*(getWidth()-80);y=40+r.nextFloat()*(getHeight()-80);invalidate();}postDelayed(this::move,1200);}
        @Override protected void onDraw(Canvas c){super.onDraw(c);p.setColor(0x33FFF59A);c.drawCircle(x,y,34,p);p.setColor(0xFFFFF59A);c.drawCircle(x,y,10,p);}
        @Override public boolean onTouchEvent(MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){float dx=e.getX()-x,dy=e.getY()-y;if(dx*dx+dy*dy<1600)move();}return true;}
    }

    private static final class BlocksView extends View {
        private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private int count=1;
        BlocksView(Activity a){super(a);setBackgroundColor(0xFFFFF6F8);}
        @Override protected void onDraw(Canvas c){super.onDraw(c);float w=Math.min(150,getWidth()*.42f),h=34;for(int i=0;i<count;i++){float left=(getWidth()-w)/2+(i%2==0?-8:8),bottom=getHeight()-38-i*(h+5);p.setColor(i%2==0?0xFFFFA9BC:0xFFC6A5FF);c.drawRoundRect(new RectF(left,bottom-h,left+w,bottom),10,10,p);}}
        @Override public boolean onTouchEvent(MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){count=count>=7?1:count+1;invalidate();}return true;}
    }
}
