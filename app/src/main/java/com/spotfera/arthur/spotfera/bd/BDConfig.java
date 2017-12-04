package com.spotfera.arthur.spotfera.bd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by arthur on 27/09/17.
 */

public class BDConfig {

    private SQLiteDatabase db, dbr;

    public BDConfig(Context context)
    {
        BDCore auxBd = new BDCore(context);

        db = auxBd.getWritableDatabase();
        dbr = auxBd.getReadableDatabase();
    }

    public void insertOrUpdate(int mapaTipo)
    {
        if(getTipoMapa() == -1)
        {
            String sql = "INSERT INTO configs (conTipoMapa) VALUES ("+mapaTipo+");";
            db.execSQL(sql);
        }
        else
        {
            String sql = "UPDATE configs SET conTipoMapa = "+mapaTipo+";";
            db.execSQL(sql);
        }
    }

    public int getTipoMapa()
    {
        String sql = "SELECT * FROM configs;";
        Cursor c = dbr.rawQuery(sql, null);

        if(c.moveToFirst())
        {
            return c.getInt(0);
        }

        return -1;
    }

}
