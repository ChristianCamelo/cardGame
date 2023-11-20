package com.chrisandbrown.cardgame;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.widget.TextView;
public class Carta {
    String TAG = "MAIN";
    int carta;
    String status;

    Tablero tablero;
    RelativeLayout relativeLayout;
    TextView front;
    TextView back;

    boolean complete;
    private Context context;
    Animator front_anim;
    Animator back_anim;

    public Carta(int carta, RelativeLayout relativeLayout, Context context, Tablero tablero){
        complete = false;
        front_anim = AnimatorInflater.loadAnimator(context, R.animator.front_anim);
        back_anim = AnimatorInflater.loadAnimator(context, R.animator.back_anim);
        front = (TextView)relativeLayout.getChildAt(0);
        back = (TextView)relativeLayout.getChildAt(1);

        this.tablero = tablero;
        this.relativeLayout = relativeLayout;

        int marginRelation = (Resources.getSystem().getDisplayMetrics().heightPixels)/5;
        double width = (Resources.getSystem().getDisplayMetrics().widthPixels)-marginRelation;
        double height = (Resources.getSystem().getDisplayMetrics().heightPixels)-marginRelation;
        double heightAdjusted;
        double widthAdjusted;
// Obtiene el ancho del RelativeLayout

        //SI FILAS < COLUMNAS
        //if(tablero.cardsAmount/tablero.columnas <= tablero.columnas){
        //    widthAdjusted = (double) width/(tablero.columnas);
        //    heightAdjusted = widthAdjusted * 1.6;
        //}
        //else{
            heightAdjusted = (double) height/(tablero.cardsAmount/(tablero.columnas)+0.2);
            Log.d(TAG, "CALCULANDO TAMAÑO "+ heightAdjusted );
            widthAdjusted  = heightAdjusted * 0.625;
        //}

// Establece el alto calculado
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                (int)widthAdjusted,
                (int)heightAdjusted
            );

        Log.d(TAG, "Carta: relative es "+relativeLayout.getClass());
        relativeLayout.setLayoutParams(layoutParams);

        this.context = context;
        this.carta = carta;

        status = "hide";
        front.setBackgroundResource(context.getResources().getIdentifier("carta"+carta, "drawable", context.getPackageName()));
        back.setBackgroundResource(context.getResources().getIdentifier("cartas_x", "drawable", context.getPackageName()));

        eventManager();

        Log.d(TAG, "onClick: Carta "+carta+" creada");
    }
    public boolean girarCarta(){
        if(status.equals("front")){
            status="hide";
            front_anim.setTarget(back);
            back_anim.setTarget(front);
            front_anim.start();
            back_anim.start();
            return  true;
        }
        if(status.equals("hide")){
            status="front";
            front_anim.setTarget(front);
            back_anim.setTarget(back);
            front_anim.start();
            back_anim.start();
            return true;
        }
        return false;
    }
    public boolean isHide(){
        if(status.equals("hide"))return true;
        else return false;
    }
    public int getCarta(){
        return carta;
    }
    public void setComplete(){
        complete = true;
    }

    public void eventManager(){
        relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(complete==false && isHide()){
                            tablero.seleccionarCartas(Carta.this);
                            girarCarta();
                            Log.d(TAG, "onClick: clickada carta: "+ carta + "isHide?: "+isHide());
                        }
                    }
        });
    }


}
