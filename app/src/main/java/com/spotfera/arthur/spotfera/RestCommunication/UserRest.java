package com.spotfera.arthur.spotfera.RestCommunication;

/**
 * Created by arthur on 03/12/17.
 */

public class UserRest {
    private String latitude;
    private String longitude;
    private String usuID;
    private String hora;
    private String usuIdSpotify;
    private String usuImg;

    public UserRest(String latitude, String longitude, String usuID, String hora, String usuIdSpotify, String usuImg) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.usuID = usuID;
        this.hora = hora;
        this.usuIdSpotify = usuIdSpotify;
        this.usuImg = usuImg;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUsuID() {
        return usuID;
    }

    public void setUsuID(String usuID) {
        this.usuID = usuID;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getUsuIdSpotify() {
        return usuIdSpotify;
    }

    public void setUsuIdSpotify(String usuIdSpotify) {
        this.usuIdSpotify = usuIdSpotify;
    }

    public String stringParaPegarProximos() {
        return "{\"usuIdSpotify\":\""+this.getUsuIdSpotify()+"\", \"latitude\":\""+this.getLatitude()+"\", " +
                "\"longitude\":\""+this.getLongitude()+"\", \"hora\":\""+this.getHora()+"\"}";
    }

    public String stringParaAtulizarDados()
    {
        return "{\"latitude\":\""+this.latitude+
                "\", \"longitude\":\""+this.longitude+
                "\", \"hora\":\""+this.hora+
                "\", \"usuIdSpotify\": \""+this.usuIdSpotify+
                "\", \"usuImg\": \""+this.usuImg+"\"}";
    }

    @Override
    public String toString() {
        return "UserRest{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", usuID='" + usuID + '\'' +
                ", hora='" + hora + '\'' +
                ", usuIdSpotify='" + usuIdSpotify + '\'' +
                ", usuImg='" + usuImg + '\'' +
                '}';
    }
}
