package com.spotfera.arthur.spotfera;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.spotfera.arthur.spotfera.bd.BDConfig;

import java.io.File;

public class Menu_Config extends AppCompatActivity {

    Spinner spinner;
    Button button;
    private String[] tiposMapa = {"Padrão", "Dark", "Night", "Google"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__config);

        spinner = (Spinner) findViewById(R.id.cb_tipo_mapa);
        button = (Button) findViewById(R.id.btn_clear_cache);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposMapa);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        BDConfig config = new BDConfig(getApplicationContext());
        int tipoSelecionado = config.getTipoMapa();
        spinner.setSelection(tipoSelecionado-1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageLoader img = ImageLoader.getInstance();
                img.clearDiskCache();
                img.clearMemoryCache();

                deleteCache(getApplicationContext());

                Toast.makeText(getApplicationContext(), "Cache limpa com sucesso", Toast.LENGTH_SHORT).show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                BDConfig config = new BDConfig(getApplicationContext());
                config.insertOrUpdate(posicao + 1);
                Toast.makeText(getApplicationContext(), "Tipo de mapa atualizado com sucesso", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
