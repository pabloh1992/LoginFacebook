package com.pablohenao.loginfacebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class MostrarLugares extends Activity {

    ListViewAdapterLugares adapter;

    String[] titulo = new String[]{
            "Restaurantes",
            "Bares",
            "Cafes",
            "Discotecas",
    };

    int[] imagenes = {
            R.drawable.restaurant,
            R.drawable.bar,
            R.drawable.coffee,
            R.drawable.disco
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_lugares);

        final ListView lista = (ListView) findViewById(R.id.listView1);
        adapter = new ListViewAdapterLugares(this, titulo, imagenes);

        lista.setAdapter(adapter);

  /*      lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "presiono " + i, Toast.LENGTH_SHORT).show();
            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "presiono LARGO " + i, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    */

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int posicion, long l) {
                //Toast.makeText(getApplicationContext(), "posicion " + (i + 1) + personas[i], Toast.LENGTH_SHORT).show();
                switch (posicion) {
                    case 0 :
                        Intent ii = new Intent(getApplicationContext(), Restaurantes.class);
                        startActivity(ii);
                        break;
                    case 1 :
                    //    Toast.makeText(getApplicationContext(), "Bares", Toast.LENGTH_SHORT).show();
                        Intent j = new Intent(getApplicationContext(), RegisterActivity.class);
                        startActivity(j);
                        break;
                    case 2 :
                   //     Toast.makeText(getApplicationContext(), "Cafes", Toast.LENGTH_SHORT).show();
                        Intent k = new Intent(getApplicationContext(), Register.class);
                        startActivity(k);
                        break;
                    case 3 :
                        Toast.makeText(getApplicationContext(), "Discotecas", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "imposible", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
