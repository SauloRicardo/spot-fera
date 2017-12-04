package com.spotfera.arthur.spotfera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPublic;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetalheUsuario extends AppCompatActivity {

    ImageView imagemUser;
    TextView textUser;
    ListView listView;

    AdapterPlaylist salv;

    String tokenAcess;
    SpotifyService spotify;
    SpotifyApi api;

    ProgressDialog dialog;

    UserPublic userP;
    List<PlaylistSimple> listPlaylist;

    String userId;
    List<String> listPlayLists;
    List<String> donoPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listPlayLists = new ArrayList<>();
        donoPlaylist = new ArrayList<>();

        imagemUser = (ImageView) findViewById(R.id.userImage);
        textUser = (TextView) findViewById(R.id.userId);

        Bitmap userImg = Utils.uncompressBitmap(getIntent().getByteArrayExtra("UserImage"));
        userId = getIntent().getStringExtra("UserId");
        tokenAcess = getIntent().getStringExtra("Token");

        api = new SpotifyApi();
        api.setAccessToken(tokenAcess);
        spotify = api.getService();

        imagemUser.setImageBitmap(userImg);
        textUser.setText(userId);

        listView = (ListView) findViewById(R.id.listview);
        salv = new AdapterPlaylist(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DetalheUsuario.this, DetalhesPlaylist.class);

                intent.putExtra("User_id", donoPlaylist.get(i));
                intent.putExtra("Token", tokenAcess);
                intent.putExtra("playlist_id", listPlayLists.get(i));

                startActivity(intent);
            }
        });

        dialog = ProgressDialog.show(this, "Busca de Usuário", "Aguarde...");

        inicializaUsuario();
    }

    void inicializaUsuario()
    {
        spotify.getUser(userId, new Callback<UserPublic>() {
            @Override
            public void success(final UserPublic userPublic, Response response) {
                userP = userPublic;
                textUser.setText(userPublic.display_name);

                spotify.getPlaylists(userPublic.id, new Callback<Pager<PlaylistSimple>>() {
                    @Override
                    public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                        listPlaylist = playlistSimplePager.items;

                        final List<String> nomes = new ArrayList<String>();
                        final List<String> imgsUrl = new ArrayList<String>();
                        for(PlaylistSimple pl : listPlaylist)
                        {
                            if(pl.is_public) {
                                nomes.add(pl.name);
                                //String playlistUrl = "/user/"+userPublic.id+"/playlist/"+pl.id;
                                listPlayLists.add(pl.id);
                                donoPlaylist.add(pl.owner.id);

                                if(pl.images.size() > 0)
                                    imgsUrl.add(pl.images.get(0).url);
                                else
                                    imgsUrl.add(null);
                                atualizaListView(nomes, imgsUrl);
                            }
                        }

                        dialog.dismiss();

                    }
                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Falha na busca dos dados", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Falha na busca dos dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizaListView(List<String> nomes, List<String> imgsUrl)
    {
        salv.setListaNomes(nomes);
        salv.setListaImagensUrl(imgsUrl);
        listView.setAdapter(salv);
        listView.requestFocus();
    }

}
