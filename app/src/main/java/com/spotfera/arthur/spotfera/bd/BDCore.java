package com.spotfera.arthur.spotfera.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * Created by arthur on 26/09/17.
 */

public class BDCore extends SQLiteOpenHelper {

    private static final String Name_BD = "BancoSpotfera.db";
    private static final int Versao_BD = 1;

    public BDCore(Context context)
    {
        super(context, Name_BD, null, Versao_BD);
    }

    public boolean checkDataBase(Context context)
    {
        File database = context.getDatabasePath(Name_BD);
        if(database.exists())
        {
            Log.d("BD", "Banco existe");
            return true;
        }
        else
        {
            Log.d("BD", "Banco não existente");
            return false;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        criaTabelaUsuario(sqLiteDatabase);
        criarTabelaPlaylist(sqLiteDatabase);
        criarTabelaConfig(sqLiteDatabase);
    }

    public void criaTabelaUsuario(SQLiteDatabase bd)
    {
        String sqlCriarTabela = "CREATE TABLE IF NOT EXISTS usuario (" +
                "usuId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "usuIdSpotify VARCHAR(50) NOT NULL," +
                "usuNome VARCHAR(50)," +
                "usuLatitude VARCHAR(50)," +
                "usuLongitude VARCHAR(50)," +
                "usuHora VARCHAR(50)," +
                "usuImg VARCHAR(200));";
        bd.execSQL(sqlCriarTabela);
    }

    public void criarTabelaPlaylist(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE IF NOT EXISTS playlist(" +
                "plId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "plNome VARCHAR(50) NOT NULL," +
                "plImg VARCHAR(200)," +
                "pl_usuId INTEGER);";
        db.execSQL(sql);
    }

    public void criarTabelaConfig(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE IF NOT EXISTS configs(" +
                "conTipoMapa INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
