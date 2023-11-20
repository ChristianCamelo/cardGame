package com.chrisandbrown.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    static String TAG = "MAIN";
    static Tablero tablero;
    int nivel;
    ViewGroup relativeLayout;
    LayoutInflater inflater;
    LinearLayout header;
    TableLayout tableroLy;
    TableRow tableRowLy;
    RelativeLayout cartaLy;
    LinearLayout menu;
    View salirBt;

    int maxLvl;

    boolean jugando;

    ArrayList<TextView> levelsBt = new ArrayList<>();
    Layout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savedInstanceState = getIntent().getExtras();
        readMatch();

        if(savedInstanceState!=null){
            nivel=savedInstanceState.getInt("nivel");
            jugando=savedInstanceState.getBoolean("jugando");
        }
        else{
            jugando=false;
            nivel=0;
        }
        loadUtilities();
        loadTablero();
        eventManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readMatch();
        eventManager();
    }
    public void loadUtilities(){
        inflater = (LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        //PARA LA PANTALLA
        relativeLayout = (ViewGroup) findViewById(R.id.mainScreen);
        //PARA EL UI
        header = (LinearLayout)inflater.inflate(R.layout.header,null);
        menu = (LinearLayout)inflater.inflate(R.layout.menu,null);
        //PARA EL TABLERO
        tableroLy = (TableLayout)inflater.inflate(R.layout.tablero,null);
        cartaLy = (RelativeLayout)inflater.inflate(R.layout.carta,null);
        tableRowLy = (TableRow)inflater.inflate(R.layout.fila,null);
    }

    private void loadTablero(){
        TableLayout tableLayout = null;
        TextView textView = (TextView)findViewById(R.id.scoreTxt);
        switch (nivel){
            case 0:
                    ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
                    layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    menu.setLayoutParams(layoutParams);
                    relativeLayout.addView(menu);
                    break;
            case 1: jugando=true;
                    tableLayout = crearTablero(this,2,2);
                    break;
            case 2: jugando=true;
                    tableLayout = crearTablero(this,3,2);
                    break;
            case 3: jugando=true;
                    tableLayout = crearTablero(this,10,16);
                    break;
            case 4: jugando=true;
                    tableLayout = crearTablero(this,4,4);
                    break;
            case 5: jugando=true;
                    tableLayout = crearTablero(this,5,4);
                    break;
            case 6: jugando=true;
                    tableLayout = crearTablero(this,8,6);
                    break;
            default:break;
        }


        if(jugando==true){

            //----------------------- TABLERO
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            int marginRelation = (Resources.getSystem().getDisplayMetrics().heightPixels)/10;
            params.topMargin = marginRelation;
            params.bottomMargin = marginRelation/2;
            tableLayout.setLayoutParams(params);
            //------------------------- HEADER
            LinearLayout.LayoutParams paramsHeader = new LinearLayout.LayoutParams(
                    Resources.getSystem().getDisplayMetrics().widthPixels,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                    );
            header.setLayoutParams(paramsHeader);

            relativeLayout.addView(header);
            relativeLayout.addView(tableLayout);

            TextView scoreUI = relativeLayout.findViewById(R.id.scoreTxt);
            tablero = new Tablero(tableLayout,MainActivity.this,scoreUI, nivel);
        }
        else{
            //LEER LOS BOTONES
            for(int i = 1;i<menu.getChildCount();i++){
                LinearLayout nav = (LinearLayout) menu.getChildAt(i);
                for(int j=0;j<nav.getChildCount();j++){
                    if(nav.getChildAt(j).getTag().equals("levelBt")){
                        levelsBt.add((TextView)nav.getChildAt(j));
                    }
                }
            }
        }
    }

    private void changeLevel(String level){
        nivel = Integer.valueOf(level);
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("nivel",nivel);
        intent.putExtra("jugando",jugando);
        startActivity(intent);
    }

    //AGREGAR ACCIONES A LOS BOTONES
    private void eventManager(){

        for(TextView textView: levelsBt) {
            boolean able = false;
            // EL NIVEL SE DETERMINA POR EL TEXTO DEL BOTON
            String level = String.valueOf(textView.getText());
            if(maxLvl+1>=Integer.valueOf(level.split("")[6])){
                textView.setBackgroundColor(getResources().getColor(R.color.able));
                able=true;
            }
            else {
                able=false;
                textView.setBackgroundColor(getResources().getColor(R.color.disable));
            }
            if(able){
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeLevel(level.split("")[6]);
                    }
                });
            }
        }

        if(nivel!=0){
            Button menuBt = findViewById(R.id.goBack);
            menuBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    jugando=false;
                    changeLevel("0");
                }
            });
        }

    }

    public void readMatch(){
        SharedPreferences sh = getApplicationContext().getSharedPreferences("levelsComplete", MODE_PRIVATE);
        maxLvl = sh.getInt("maxLevel",0);
        Log.d(TAG, "readMatch: Maximo nivel alcanzado es "+String.valueOf(maxLvl));
    }

    public TableLayout crearTablero(Activity activity, int numRows, int numCols) {

        LayoutInflater inflater = activity.getLayoutInflater();
        TableLayout tableroLy = (TableLayout) inflater.inflate(R.layout.tablero, null);

        for (int i = 0; i < numRows; i++) {
            TableRow tableRowLy = (TableRow) inflater.inflate(R.layout.fila, null);
            for (int j = 0; j < numCols; j++) {
                RelativeLayout cartaLy = (RelativeLayout) inflater.inflate(R.layout.carta, null);
                tableRowLy.addView(cartaLy);
            }
            tableroLy.addView(tableRowLy);
        }
        return  tableroLy;
    }

    @Override
    public void onBackPressed() {
        jugando=false;
        changeLevel("0");
        super.onBackPressed();
    }

}