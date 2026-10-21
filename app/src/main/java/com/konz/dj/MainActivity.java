package com.konz.dj;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.*;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.view.*;
import android.widget.Toast;
import java.util.Locale;

/** KONZ 0.2 touchscreen prototype. MediaPlayer is NOT a DJ-grade low-latency engine. */
public class MainActivity extends Activity {
    final MediaPlayer[] player = new MediaPlayer[2];
    final String[] names={"LOAD A TRACK","LOAD A TRACK"};
    final float[] bpm={174f,174f}, speed={1f,1f};
    final int[] cue={0,0}, loopStart={-1,-1}, loopEnd={-1,-1};
    int cross=50; DeckView ui;
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);ui=new DeckView();setContentView(ui);}
    void load(int d){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("audio/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,100+d);}
    @Override protected void onActivityResult(int r,int result,Intent data){super.onActivityResult(r,result,data);if(result!=RESULT_OK||data==null||data.getData()==null||r<100||r>101)return;int d=r-100;Uri u=data.getData();try{if(player[d]!=null)player[d].release();MediaPlayer p=new MediaPlayer();p.setDataSource(this,u);p.prepare();player[d]=p;names[d]=u.getLastPathSegment();cue[d]=0;loopStart[d]=loopEnd[d]=-1;volume();ui.invalidate();}catch(Exception ex){Toast.makeText(this,"Load failed: "+ex.getMessage(),Toast.LENGTH_LONG).show();}}
    void volume(){float x=cross/100f;for(int d=0;d<2;d++)if(player[d]!=null){float v=d==0?(float)Math.cos(x*Math.PI/2):(float)Math.sin(x*Math.PI/2);player[d].setVolume(v,v);}}
    void tempo(int d){MediaPlayer p=player[d];if(p==null)return;try{p.setPlaybackParams(new PlaybackParams().setSpeed(speed[d]).setPitch(1f));}catch(Exception e){Toast.makeText(this,"Speed not supported on this file",Toast.LENGTH_SHORT).show();}}
    void action(int d,String a){MediaPlayer p=player[d];if(a.equals("LOAD")){load(d);return;}if(p==null){Toast.makeText(this,"Load a track first",Toast.LENGTH_SHORT).show();return;}
        try{switch(a){case "PLAY":if(p.isPlaying())p.pause();else {p.start();tempo(d);}break;
        case "CUE":if(p.isPlaying())p.pause();p.seekTo(cue[d]);break;
        case "SET CUE":cue[d]=p.getCurrentPosition();break;
        case "LOOP":if(loopStart[d]>=0){loopStart[d]=loopEnd[d]=-1;}else{loopStart[d]=p.getCurrentPosition();loopEnd[d]=Math.min(p.getDuration(),loopStart[d]+(int)(4*60000f/(bpm[d]*speed[d])));if(loopEnd[d]<=loopStart[d])loopStart[d]=loopEnd[d]=-1;}break;
        case "SYNC":int other=1-d;speed[d]=Math.max(.8f,Math.min(1.2f,bpm[other]*speed[other]/bpm[d]));tempo(d);break;
        case "BPM+":bpm[d]+=1;break;case "BPM-":bpm[d]=Math.max(40,bpm[d]-1);break;
        case "PITCH+":speed[d]=Math.min(1.2f,speed[d]+.005f);tempo(d);break;case "PITCH-":speed[d]=Math.max(.8f,speed[d]-.005f);tempo(d);break;}}
        catch(Exception e){Toast.makeText(this,"Playback error: "+e.getMessage(),Toast.LENGTH_SHORT).show();}ui.invalidate();}
    @Override protected void onDestroy(){for(MediaPlayer p:player)if(p!=null)p.release();super.onDestroy();}
    class DeckView extends View {
        Paint paint=new Paint(3); Canvas canvas;float sx,sy;final int red=Color.rgb(255,58,73),blue=Color.rgb(34,168,255),muted=Color.rgb(145,167,192);final RectF rect=new RectF();
        DeckView(){super(MainActivity.this);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
        void box(float x,float y,float w,float h,int color,float radius){paint.setColor(color);paint.setStyle(Paint.Style.FILL);canvas.drawRoundRect(x,y,x+w,y+h,radius,radius,paint);}
        void stroke(float x,float y,float w,float h,int color){paint.setColor(color);paint.setStrokeWidth(1.5f);paint.setStyle(Paint.Style.STROKE);canvas.drawRoundRect(x,y,x+w,y+h,9,9,paint);paint.setStyle(Paint.Style.FILL);}
        void text(String s,float x,float y,int color,int size,boolean bold){paint.setTypeface(bold?Typeface.create("sans-serif-condensed",Typeface.BOLD):Typeface.create("sans-serif",Typeface.NORMAL));paint.setTextSize(size);paint.setColor(color);paint.setStyle(Paint.Style.FILL);canvas.drawText(s,x,y,paint);}
        void button(String s,float x,float y,float w,float h,int color){box(x,y,w,h,0xff101d2d,7);stroke(x,y,w,h,color);text(s,x+9,y+h/2+5,color,14,true);}
        @Override protected void onDraw(Canvas c){super.onDraw(c);canvas=c;sx=getWidth()/1000f;sy=getHeight()/500f;c.save();c.scale(sx,sy);c.drawColor(0xff05090f);
            box(8,7,984,48,0xff0c1625,11);text("KONZ",24,41,Color.WHITE,39,true);text("DARK SIDE  //  LIGHT SIDE",176,36,muted,14,true);text("MIX    •    LOOP    •    PREPARE",685,36,muted,13,true);
            for(int d=0;d<2;d++)deck(d,d==0?10:510,d==0?red:blue);
            box(10,391,980,99,0xff0b1523,12);stroke(10,391,980,99,0xff23364b);text("CROSSFADER",32,416,muted,13,true);text("A",40,460,red,19,true);text("B",955,460,blue,19,true);
            box(87,446,822,6,0xff27374a,3);float fx=87+822*cross/100f;box(fx-10,434,20,30,Color.WHITE,5);
            text("KONZ 0.2 • TOUCHSCREEN PREVIEW",340,483,muted,11,false);
            c.restore();postInvalidateDelayed(80);
        }
        void deck(int d,int x,int accent){box(x,64,480,317,0xff0b1422,12);stroke(x,64,480,317,accent);text(d==0?"A  /  DARK SIDE":"B  /  LIGHT SIDE",x+17,90,accent,19,true);
            String name=names[d];if(name.length()>43)name=name.substring(name.length()-43);text(name,x+18,114,Color.WHITE,13,false);
            MediaPlayer p=player[d];int pos=0,dur=1;try{if(p!=null){pos=p.getCurrentPosition();dur=Math.max(1,p.getDuration());if(loopStart[d]>=0&&pos>=loopEnd[d])p.seekTo(loopStart[d]);}}catch(Exception ignored){}
            box(x+17,126,446,72,0xff060d17,6);paint.setColor(accent);paint.setStrokeWidth(1.2f);for(int i=0;i<218;i++){float height=(float)(8+25*Math.abs(Math.sin(i*1.73)*Math.cos(i*.17)));float px=x+23+i*2;canvas.drawLine(px,163-height/2,px,163+height/2,paint);}box(x+20+440f*pos/dur,128,2,67,Color.WHITE,0);
            text(String.format(Locale.US,"%.1f BPM",bpm[d]),x+18,225,Color.WHITE,20,true);text(String.format(Locale.US,"%+.1f%%",(speed[d]-1)*100),x+352,225,accent,18,true);
            button("LOAD",x+18,239,83,38,accent);button("PLAY",x+111,239,83,38,accent);button("CUE",x+204,239,75,38,accent);button("SET CUE",x+289,239,100,38,accent);
            button("SYNC",x+18,288,79,34,accent);button("LOOP 4",x+107,288,83,34,accent);button("BPM -",x+200,288,82,34,accent);button("BPM +",x+292,288,83,34,accent);
            button("PITCH -",x+18,333,111,34,accent);button("PITCH +",x+139,333,111,34,accent);text(loopStart[d]>=0?"LOOP ON":"LOOP OFF",x+285,354,loopStart[d]>=0?accent:muted,14,true);
        }
        @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_DOWN&&e.getAction()!=MotionEvent.ACTION_MOVE)return true;float x=e.getX()/sx,y=e.getY()/sy;if(y>425&&y<475){cross=Math.max(0,Math.min(100,Math.round((x-87)*100/822)));volume();invalidate();return true;}if(e.getAction()!=MotionEvent.ACTION_DOWN)return true;
            if(y>=64&&y<=380){int d=x<500?0:1;float local=x-(d==0?10:510);if(local<0||local>480)return true;
                if(y>=126&&y<200&&local>=17&&local<463){MediaPlayer p=player[d];if(p!=null)try{p.seekTo((int)(p.getDuration()*(local-17)/446));}catch(Exception ignored){}return true;}
                if(y>=239&&y<=277){if(local>=18&&local<101)action(d,"LOAD");else if(local>=111&&local<194)action(d,"PLAY");else if(local>=204&&local<279)action(d,"CUE");else if(local>=289&&local<389)action(d,"SET CUE");}
                if(y>=288&&y<=322){if(local>=18&&local<97)action(d,"SYNC");else if(local>=107&&local<190)action(d,"LOOP");else if(local>=200&&local<282)action(d,"BPM-");else if(local>=292&&local<375)action(d,"BPM+");}
                if(y>=333&&y<=367){if(local>=18&&local<129)action(d,"PITCH-");else if(local>=139&&local<250)action(d,"PITCH+");}}
            invalidate();return true;}
    }
}
