package com.spotfera.arthur.spotfera;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by arthur on 11/09/17.
 */

public class Usuario implements Parcelable{
    private String id;
    private String nome;
    private String hora;
    private String imgUrl;
    private Bitmap imagem;
    private LatLng localizacao;

    Usuario(String id, Bitmap imagem)
    {
        this.id = id;
        this.imagem = imagem;
    }

    public Usuario() {
    }

    Usuario(String id, Bitmap imagem, LatLng localizacao)
    {
        this.id = id;
        this.imagem = imagem;
        this.localizacao = localizacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getImagem() {
        return imagem;
    }

    public void setImagem(Bitmap imagem) {
        this.imagem = imagem;
    }

    public LatLng getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(LatLng localizacao) {
        this.localizacao = localizacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    protected Usuario(Parcel in) {
        id = in.readString();
        imagem = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        localizacao = (LatLng) in.readValue(LatLng.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeValue(imagem);
        dest.writeValue(localizacao);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Usuario> CREATOR = new Parcelable.Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };
}
