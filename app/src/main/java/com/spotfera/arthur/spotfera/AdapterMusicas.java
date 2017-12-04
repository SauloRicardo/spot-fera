package com.spotfera.arthur.spotfera;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by arthur on 16/10/17.
 */

public class AdapterMusicas extends BaseAdapter {

    private Context context;
    private List<String> nome;
    private List<String> artista;
    private List<String> disco;

    TextView tv_nomeMusica;
    TextView tv_nomeArtista_album;

    public AdapterMusicas(Context context)
    {
        super();
        this.context = context;
    }

    public void setListaNome(List<String> nome)
    {
        this.nome = nome;
    }

    public void setListaArtista(List<String> artista)
    {
        this.artista = artista;
    }

    public void setListaDisco(List<String> disco)
    {
        this.disco = disco;
    }

    @Override
    public int getCount() {
        return nome.size();
    }

    @Override
    public Object getItem(int i) {
        return nome.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        String nomeMusica = nome.get(i);
        String nomeArtista = artista.get(i);
        String nomeAlbum = disco.get(i);

        //if(convertView == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_musicas, parent, false);
            tv_nomeMusica = view.findViewById(R.id.musica_nome);
            tv_nomeArtista_album = view.findViewById(R.id.musica_artista_album);
        //}

        tv_nomeMusica.setText(nomeMusica);
        tv_nomeArtista_album.setText(nomeArtista + " . " + nomeAlbum);

        return view;
    }
}
