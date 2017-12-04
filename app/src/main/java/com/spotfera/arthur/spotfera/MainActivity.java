package com.spotfera.arthur.spotfera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.spotfera.arthur.spotfera.RestCommunication.RequestAtualizar;
import com.spotfera.arthur.spotfera.RestCommunication.RequestUsuarios;
import com.spotfera.arthur.spotfera.RestCommunication.UserRest;
import com.spotfera.arthur.spotfera.bd.BDConfig;
import com.spotfera.arthur.spotfera.bd.BDCore;
import com.spotfera.arthur.spotfera.bd.BDUsuario;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.okhttp.OkHttpClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import kaaes.spotify.webapi.android.models.UserPublic;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
                                                               LocationListener,
                                                               GoogleMap.OnGroundOverlayClickListener{

    GoogleMap map;
    LocationManager locationManager;

    ImageLoader imageLoader;

    Button locBotao;

    private static final int REQUEST_CODE = 1337;

    private int tamanhoImagem = 80;

    private boolean logado = false;
    private boolean mapaOk = false;

    LatLng userLogadoLocalizacao;

    SpotifyApi api;
    SpotifyService spotify;
    String tokenAcesso;

    String userLogadoId;

    private Handler handler;

    private final Map<String, Usuario> usuarios = Collections.synchronizedMap(new HashMap<String, Usuario>());

    RequestUsuarios apiGetUserProximos;
    RequestAtualizar apiAtualizarUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RestAdapter restAdapterUserProximos = new RestAdapter.Builder()
                .setEndpoint("http://fera.000webhostapp.com/rest.php")
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL).build();

        RestAdapter restAdapterAtualizar = new RestAdapter.Builder()
                .setEndpoint("http://fera.000webhostapp.com/restInsereUsuarioBanco.php")
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL).build();

        apiGetUserProximos = restAdapterUserProximos.create(RequestUsuarios.class);
        apiAtualizarUsuarios = restAdapterAtualizar.create(RequestAtualizar.class);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);

        imageLoader = ImageLoader.getInstance();
        handler = new Handler();
        iniciaTarefaVerificador();

        locBotao = (Button) findViewById(R.id.botaoLoc);
        locBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPosition myLocation = new CameraPosition.Builder().target(userLogadoLocalizacao)
                        .zoom(17).build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(myLocation));
            }
        });

        //criacao do map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //pega a localidade
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1, this);

        String tokenDeAcesso = getIntent().getStringExtra("TOKEN_ACCESS");
        loginInSpotify(tokenDeAcesso);
    }

    public void loginInSpotify(String token)
    {
        if (token != null && !token.equals("")) {
            Toast.makeText(this, "Spotify Conectado com sucesso", Toast.LENGTH_LONG).show();
            api = new SpotifyApi();
            api.setAccessToken(token);
            spotify = api.getService();
            tokenAcesso = token;

            logado = true;

            spotify.getMe(new Callback<UserPrivate>() {
                @Override
                public void success(final UserPrivate userPrivate, Response response) {

                    if(userPrivate.images.isEmpty())
                    {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        Bitmap userDefault = BitmapFactory.decodeResource(getResources(), R.drawable.default_person);
                        Usuario userLogado = new Usuario(userPrivate.id, Utils.getCroppedBitmap(userDefault), latLng);

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        userLogado.setHora(df.format(new Date()));

                        userLogadoId = userLogado.getId();

                        usuarios.put(userLogado.getId(), userLogado);

                        GroundOverlayOptions overlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(userLogado.getImagem()))
                                .position(userLogado.getLocalizacao(), tamanhoImagem);
                        GroundOverlay imgOverlay = map.addGroundOverlay(overlay);
                        imgOverlay.setTag(userLogado.getId());
                        imgOverlay.setClickable(true);
                    }
                    else
                    {
                        Bitmap bitmap = Utils.downloadImage(imageLoader, userPrivate.images.get(0).url, BitmapFactory.decodeResource(getResources(), R.drawable.default_person));

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        Usuario userLogado = new Usuario(userPrivate.id, Utils.getCroppedBitmap(bitmap), latLng);
                        userLogado.setImgUrl(userPrivate.images.get(0).url);

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        userLogado.setHora(df.format(new Date()));

                        userLogadoId = userLogado.getId();
                        usuarios.put(userLogado.getId(), userLogado);

                        map.clear();

                        GroundOverlayOptions overlay = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(userLogado.getImagem()))
                                .position(userLogado.getLocalizacao(), tamanhoImagem);
                        GroundOverlay imgOverlay = map.addGroundOverlay(overlay);
                        imgOverlay.setTag(userLogado.getId());
                        imgOverlay.setClickable(true);

                        Log.d("LoadImage", "carragou a imagem com sucesso");
                    }

                    atualizarDadosBD();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("GetMe()", "Erro");
                }
            });
        }
        else
        {
            Toast.makeText(MainActivity.this, "Erro no login do Spotify", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Usando dados armazenados, fique online para obter novas informações", Toast.LENGTH_LONG).show();
            pegarDadosBD();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if(logado && mapaOk) {
            BDConfig config = new BDConfig(getApplicationContext());
            if (config.getTipoMapa() == -1 || config.getTipoMapa() == 1) {
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
            } else {
                switch (config.getTipoMapa()) {
                    case 2:
                        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_dark));
                        break;
                    case 3:
                        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_night));
                        break;
                    case 4:
                        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_padrao));
                        break;
                }
            }
        }

    }

    //inflar o menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //pega que item do menu foi selecionado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.configuracoes) {
            Intent intent = new Intent(this, Menu_Config.class);
            startActivity(intent);
        }
        else if(id == R.id.sair)
        {
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(getResources().getString(R.string.client_id), AuthenticationResponse.Type.TOKEN, getResources().getString(R.string.redirect_uri));

            builder.setScopes(new String[]{"streaming", "playlist-read-private", "user-read-private"});
            builder.setShowDialog(true);
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }

        return super.onOptionsItemSelected(item);
    }

    //quando o mapa estiver carregado
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP", "Entrou no onMapReady");
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setMaxZoomPreference(17);

        BDConfig config = new BDConfig(getApplicationContext());

        if(config.getTipoMapa() == -1 || config.getTipoMapa() == 1) {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        }
        else
        {
            switch (config.getTipoMapa())
            {
                case 2:
                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_dark));
                    break;
                case 3:
                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_night));
                    break;
                case 4:
                    map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_padrao));
                    break;
            }
        }

        map.setOnGroundOverlayClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));

        mapaOk = true;
    }

    //quando a localizacao do usuario muda
    @Override
    public void onLocationChanged(Location location) {
        LatLng localizacao = new LatLng(location.getLatitude(), location.getLongitude());
        userLogadoLocalizacao = localizacao;

        if(usuarios.size() > 0 && logado)//pelo menos o usuario que ta logado existe
        {
            Usuario user = usuarios.get(userLogadoId);
            if(user != null)
                user.setLocalizacao(localizacao);
        }
    }



    private void desenhaImagens()
    {
        if(usuarios.size() > 0 && mapaOk) {
            map.clear();//limpa o mapa

            for(Object o : usuarios.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                Usuario user = (Usuario) pair.getValue();
                GroundOverlayOptions overlay = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromBitmap(user.getImagem()))
                        .position(user.getLocalizacao(), tamanhoImagem);
                GroundOverlay imgOverlay = map.addGroundOverlay(overlay);
                imgOverlay.setTag(user.getId());
                imgOverlay.setClickable(true);

                Log.d("Thread", "Desenhou o user " + user.getId() + " em " + user.getLocalizacao().toString());
            }
        }
    }

    private void atualizaDados()
    {
        if(usuarios.size() > 0 && logado)
        {
            Usuario user = usuarios.get(userLogadoId);
            UserRest userRest = new UserRest(String.valueOf(user.getLocalizacao().latitude), String.valueOf(user.getLocalizacao().longitude),
                    "USUID", user.getHora(), user.getId(), user.getImgUrl());

            Log.d("ATUALIZAR", userRest.stringParaAtulizarDados());
            apiAtualizarUsuarios.atulizaDados(userRest.stringParaAtulizarDados(), new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.d("ATUALIZAR", s);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("ATUALIZAR", "DEU RUIM");
                }
            });
        }
    }

    private void pegaUsuarios()
    {
        if(usuarios.size() > 0 && logado) {
            Usuario user = usuarios.get(userLogadoId);
            UserRest userRest = new UserRest(String.valueOf(user.getLocalizacao().latitude), String.valueOf(user.getLocalizacao().longitude),
                    "USUID", user.getHora(), userLogadoId, user.getImgUrl());
            Log.d("PROXIMOS", userRest.stringParaPegarProximos());
            apiGetUserProximos.pegaUsuariosProximos(userRest.stringParaPegarProximos(), new Callback<List<UserRest>>() {
                @Override
                public void success(List<UserRest> userRests, Response response) {
                    for (final UserRest u : userRests) {
                        //if(u.getUsuIdSpotify().equals("evaladao")) {
                            spotify.getUser(u.getUsuIdSpotify(), new Callback<UserPublic>() {
                                @Override
                                public void success(UserPublic userPublic, Response response) {
                                    Bitmap bitmap;
                                    if (userPublic.images.isEmpty()) {
                                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_person);
                                    } else {
                                        bitmap = Utils.downloadImage(imageLoader, userPublic.images.get(0).url, BitmapFactory.decodeResource(getResources(), R.drawable.default_person));
                                    }

                                    LatLng latLngUser = new LatLng(Double.valueOf(u.getLatitude()), Double.valueOf(u.getLongitude()));
                                    Usuario userProximo = new Usuario(u.getUsuIdSpotify(), Utils.getCroppedBitmap(bitmap), latLngUser);

                                    Log.d("ASDQWE", u.toString());

                                    usuarios.put(userProximo.getId(), userProximo);
                                    Log.d("PROXIMOS", "DEU BOM");
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e("PROXIMOS", "DEU RUIM NA HORA DE PEGAR OS DADOS DO USUARIO");
                                    Log.e("PROXIMOS", error.toString());
                                }
                            });
                        //}
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("PROXIMOS", "DEU RUIM NA REST DO TULIO");
                }
            });
        }
    }

    Runnable verificadorDeEstados = new Runnable() {
        @Override
        public void run() {
            try{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        desenhaImagens();
                    }
                });

                atualizaDados();
                pegaUsuarios();

                ConnectivityManager cm =
                        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                Log.d("NETWORK", "Entrou pra verificar a rede");
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if(isConnected && !logado)
                {
                    Log.d("NETWORK", "Ta online, conecta");
                    AuthenticationRequest.Builder builder =
                            new AuthenticationRequest.Builder(getResources().getString(R.string.client_id), AuthenticationResponse.Type.TOKEN, getResources().getString(R.string.redirect_uri));

                    builder.setScopes(new String[]{"streaming", "playlist-read-private", "user-read-private"});
                    builder.setShowDialog(true);
                    AuthenticationRequest request = builder.build();

                    AuthenticationClient.openLoginActivity(MainActivity.this, REQUEST_CODE, request);
                }

            }
            finally {
                handler.postDelayed(verificadorDeEstados, 10000);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            loginInSpotify(response.getAccessToken());
        }
    }

    void iniciaTarefaVerificador()
    {
        verificadorDeEstados.run();
    }

    void paraTarefaVerificadora()
    {
        handler.removeCallbacks(verificadorDeEstados);
    }

    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        Intent intent = new Intent(this, DetalheUsuario.class);

        //intent.putExtra("UserImage", usuarios.get(groundOverlay.getTag()).getImagem());
        intent.putExtra("UserImage", Utils.compressBitmap(usuarios.get(groundOverlay.getTag()).getImagem()));
        intent.putExtra("UserId", usuarios.get(groundOverlay.getTag()).getId());
        intent.putExtra("Token", tokenAcesso);

        startActivity(intent);
    }

    private void pegarDadosBD()
    {
        BDUsuario bdUsuario = new BDUsuario(getApplicationContext());

        List<Usuario> listUsers = bdUsuario.getUsuarios();

        if(listUsers.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Não existe nenhum usuário em cache", Toast.LENGTH_LONG).show();
        }

        for(Usuario user : listUsers)
        {
            Log.d("BD_APP", "Pegou o user " + user.getId() + " url: "+user.getImgUrl());

            user.setImagem(Utils.getCroppedBitmap(Utils.downloadImage(imageLoader, user.getImgUrl(), null)));

            usuarios.put(user.getId(), user);
        }

        //logado = true;
    }

    private void atualizarDadosBD()
    {
        BDUsuario user = new BDUsuario(getApplicationContext());

        user.insertOrUpdate(usuarios.get(userLogadoId));

        Log.d("BD_APP", "Usuario Salvo no banco");

    }

    //funcoes que nao sao usadas no gps
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
