package com.spotfera.arthur.spotfera.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;
import com.spotfera.arthur.spotfera.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 26/09/17.
 */

public class BDUsuario {

    private SQLiteDatabase db, dbr;

    public BDUsuario(Context context)
    {
        BDCore auxBd = new BDCore(context);

        db = auxBd.getWritableDatabase();
        dbr = auxBd.getReadableDatabase();
    }

    public void insertOrUpdate(Usuario usuario)
    {
        if(getUsuario(usuario.getId()) != null)
        {
            String sql = "UPDATE usuario SET usuLatitude = '" +
                    usuario.getLocalizacao().latitude + "', usuLongitude = '" +
                    usuario.getLocalizacao().longitude + "', usuHora = '" +
                    usuario.getHora() + "' WHERE usuIdSpotify = '" +
                    usuario.getId() + "';";
            db.execSQL(sql);
        }
        else
        {
            insertUsuario(usuario);
        }
    }

    public long insertUsuario(Usuario usuario)
    {
        ContentValues values = new ContentValues();
        values.put("usuIdSpotify", usuario.getId());
        values.put("usuNome", usuario.getNome());
        values.put("usuLatitude", String.valueOf(usuario.getLocalizacao().latitude));
        values.put("usuLongitude", String.valueOf(usuario.getLocalizacao().longitude));
        values.put("usuHora", usuario.getHora());
        values.put("usuImg", usuario.getImgUrl());

        return db.insert("usuario", null, values);
    }

    public Usuario getUsuario(String usuIdSpotify)
    {
        String sql = "SELECT * FROM usuario WHERE usuIdSpotify = '" + usuIdSpotify + "';";
        Cursor c = dbr.rawQuery(sql, null);

        if(c.moveToFirst())
        {
            Usuario usu = new Usuario();
            usu.setId(c.getString(1));
            usu.setNome(c.getString(2));
            LatLng loc = new LatLng(Double.valueOf(c.getString(3)), Double.valueOf(c.getString(4)));
            usu.setLocalizacao(loc);
            usu.setHora(c.getString(5));
            usu.setImgUrl(c.getString(6));

            return usu;
        }

        return null;
    }

    public List<Usuario> getUsuarios()
    {
        String sql = "SELECT * FROM usuario";
        Cursor cursor = dbr.rawQuery(sql, null);

        List<Usuario> listUsu = new ArrayList<>();

        if(cursor.moveToFirst())
        {
            do {
                Usuario usu = new Usuario();
                usu.setId(cursor.getString(1));
                usu.setNome(cursor.getString(2));
                LatLng loc = new LatLng(Double.valueOf(cursor.getString(3)), Double.valueOf(cursor.getString(4)));
                usu.setLocalizacao(loc);
                usu.setHora(cursor.getString(5));
                usu.setImgUrl(cursor.getString(6));

                listUsu.add(usu);
            }while(cursor.moveToNext());
        }

        return listUsu;
    }
}
