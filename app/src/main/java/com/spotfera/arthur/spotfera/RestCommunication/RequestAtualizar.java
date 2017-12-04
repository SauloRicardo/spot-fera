package com.spotfera.arthur.spotfera.RestCommunication;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by arthur on 03/12/17.
 */

public interface RequestAtualizar {
    @FormUrlEncoded
    @POST("/")
    void atulizaDados(@Field("usuario") String usuario, Callback<String> retorno);
}
