package com.spotfera.arthur.spotfera.RestCommunication;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by arthur on 03/12/17.
 */

public interface RequestUsuarios {
    @FormUrlEncoded
    @POST("/")
    void pegaUsuariosProximos(@Field("coordenadas") String pedido, Callback<List<UserRest>> ped);
}
