package com.chrisandbrown.cardgame;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;

public class Tablero {
    String TAG = "MAIN";
    public ArrayList<Carta> cartasEnJuego;
    ArrayList<Carta>cartas;
    ArrayList<Integer>valoresCartas;
    int cardsAmount;
    int score;
    int height;
    int columnas;

    int levelActual;
    TextView scoreTxt;
    TableLayout tablero;
    Context context;
    public Tablero(TableLayout tablero, Context context, TextView scoreTxt, int levelActual){
        Log.d("MAIN", "Tablero: creando tablero");
        this.tablero = tablero;
        this.levelActual = levelActual;
        this.context = context;
        this.scoreTxt = scoreTxt;
        score = 0;
        columnas = 0;
        cardsAmount = 0;
        valoresCartas = new ArrayList<Integer>();
        cartasEnJuego = new ArrayList<Carta>();
        cartas = new ArrayList<Carta>();
        leerEspacios();
        setRandomCards();
        setCartas();
        height = tablero.getHeight();
    }
    public void setRandomCards(){
        Log.d("MAIN", "Tablero: Asignando orden");
        for(int j=0;j<cardsAmount;j++){
            int valor;
            int veces;
            do{
                veces=0;
                //Obtenemos un valor
                valor=(int)Math.floor(Math.random()*(cardsAmount/2));
                //Pasamos por los valores obtenidos antes
                for(int i=0;i<valoresCartas.size();i++){
                    //Si ya estaba ese valor aumentamos la cantidad de veces
                    if(valoresCartas.get(i)==valor){
                        veces++;
                    }
                }
                //si la cantidad de veces es 2 reiteramos
            }while(veces==2);
            valoresCartas.add(valor);
        }
        Log.d("MAIN", "setRandomCards: valores: "+valoresCartas);
    }
    public void leerEspacios(){
        Log.d("MAIN", "Tablero: leyendo espacios");
        for (int i=0;i<tablero.getChildCount();i++){
           TableRow tr = (TableRow)tablero.getChildAt(i);
            if(tr instanceof TableRow){
               for (int j = 0;j<tr.getChildCount();j++){
                    cardsAmount++;
                    if(i==0)columnas++;
                }
            }
        }
        Log.d("MAIN", "Tablero: espacios "+cardsAmount);
    }

    public void setCartas(){
        Log.d("MAIN", "Tablero: Asignando cartas");
        for (int i=0;i<tablero.getChildCount();i++){
            TableRow tr = (TableRow)tablero.getChildAt(i);
            for (int j = 0;j<tr.getChildCount();j++){
                Carta newCarta = new Carta(valoresCartas.get(j+(i*tr.getChildCount())),(RelativeLayout)tr.getChildAt(j),context,this);
                cartas.add(newCarta);
            }
        }
    }

    public void seleccionarCartas(Carta carta){
        if(carta.complete!=true){
            cartasEnJuego.add(carta);
            Log.d(TAG, "seleccionarCartas: seleccionadas = "+cartasEnJuego);
            if(cartasEnJuego.size()==2){
                if(cartasEnJuego.get(0).getCarta()==cartasEnJuego.get(1).getCarta()){
                    //IGUALES
                    Log.d(TAG, "compararCartas: IGUALES // PUNTUA");
                    cartasEnJuego.get(0).setComplete();
                    cartasEnJuego.get(1).setComplete();
                    score++;
                    scoreTxt.setText(String.valueOf(score));
                    winMatch();
                }
                else{
                    Carta a = cartasEnJuego.get(0);
                    Carta b = cartasEnJuego.get(1);
                    //DIFERENTES
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        a.girarCarta();
                        b.girarCarta();
                    }, 1000);
                }
                cartasEnJuego = new ArrayList<Carta>();
            }
        }
    }

    public int getScore(){
        return score;
    }
    public int getAmount(){
        return cardsAmount;
    }
    public void winMatch(){
        if(score>=cardsAmount/2){
            saveMatch();
            Log.d(TAG, "winMatch: GANO");
        }
    }

    public void saveMatch(){
        SharedPreferences sh = context.getSharedPreferences("levelsComplete", MODE_PRIVATE);
        int maxLvl = sh.getInt("maxLevel",0);
        SharedPreferences.Editor editor = sh.edit();
        if(maxLvl<levelActual){
            editor.putInt("maxLevel", levelActual);
            editor.apply();
        }
    }

}
