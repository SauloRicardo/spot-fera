package com.spotfera.arthur.spotfera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DetalhesPlaylist extends AppCompatActivity {

    ImageView imagemPlaylist;
    TextView nomePlaylist;
    ListView listaMusicas;

    String acessToken;
    SpotifyService spotify;
    SpotifyApi api;

    ImageLoader imageLoader;

    String nomeUsuario;
    String idPlaylist;

    List<Track> musicas;

    AdapterMusicas salv;

    ProgressDialog dialog;

    Button btn_abrir_spotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_playlist);

        imagemPlaylist = (ImageView) findViewById(R.id.playlistImage);
        nomePlaylist = (TextView) findViewById(R.id.playlist_nome);
        listaMusicas = (ListView) findViewById(R.id.list_musicas);

        imageLoader = ImageLoader.getInstance();

        btn_abrir_spotify = (Button) findViewById(R.id.btn_abrir_spotify);
        btn_abrir_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri web = Uri.parse("https://open.spotify.com/user/"+nomeUsuario+"/playlist/"+idPlaylist);
                Intent intent = new Intent(Intent.ACTION_VIEW, web);
                startActivity(intent);
            }
        });

        nomeUsuario = getIntent().getStringExtra("User_id");
        idPlaylist = getIntent().getStringExtra("playlist_id");
        acessToken = getIntent().getStringExtra("Token");

        api = new SpotifyApi();
        api.setAccessToken(acessToken);
        spotify = api.getService();

        salv = new AdapterMusicas(this);

        dialog = ProgressDialog.show(this, "Busca de Playlist", "Aguarde...");

        spotify.getPlaylist(nomeUsuario, idPlaylist, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                nomePlaylist.setText(playlist.name);

                String urlImg = playlist.images.get(0).url;
                Bitmap imgDefault = BitmapFactory.decodeResource(getResources(), R.drawable.playlist_icon);
                Bitmap img = Utils.downloadImage(imageLoader, urlImg, imgDefault);
                imagemPlaylist.setImageBitmap(Utils.getCroppedBitmap(img));

                musicas = new ArrayList<Track>();

                for(PlaylistTrack tk : playlist.tracks.items)
                {
                    musicas.add(tk.track);
                    atualizaLV(musicas);
                }

                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Falha na busca", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void atualizaLV(List<Track> musica)
    {
        List<String> nome = new ArrayList<>();
        List<String> artista = new ArrayList<>();
        List<String> disco = new ArrayList<>();

        for(Track tk : musica)
        {
            nome.add(tk.name);
            disco.add(tk.album.name);

            String artistas = "";

            if(tk.artists.size() > 1) {
                for (int i = 0; i < tk.artists.size(); i++) {
                    ArtistSimple as = tk.artists.get(i);
                    if(i == 0)
                        artistas = as.name;
                    else
                        artistas = artistas + ", " + as.name;
                }
            }
            else
            {
                artistas = tk.artists.get(0).name;
            }

            artista.add(artistas);
        }

        salv.setListaArtista(artista);
        salv.setListaDisco(disco);
        salv.setListaNome(nome);
        listaMusicas.setAdapter(salv);
        listaMusicas.requestFocus();
    }
}
