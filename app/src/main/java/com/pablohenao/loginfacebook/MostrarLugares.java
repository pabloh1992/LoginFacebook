package com.pablohenao.loginfacebook;

import android.app.Activity;
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

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    }
}
