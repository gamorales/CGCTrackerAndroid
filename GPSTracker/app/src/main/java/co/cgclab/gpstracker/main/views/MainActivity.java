package co.cgclab.gpstracker.main.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.carros.models.CarrosModel;
import co.cgclab.gpstracker.carros.views.CarrosActivity;
import co.cgclab.gpstracker.usuarios.views.ClaveActivity;
import co.cgclab.gpstracker.web.controllers.IComandosVehiculo;
import co.cgclab.gpstracker.usuarios.views.LoginActivity;
import co.cgclab.gpstracker.web.models.CommandResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView btnEncendidoOn, btnEncendidoOff, btnMovimientoOn, btnMovimientoOff;
    private ImageView btnBloquear, btnDesbloquear, btnVelocidadOn, btnVelocidadOff;
    private ImageView btnEscuchar, btnTracker, btnAbout, btnStatus;
    private TextView tvMainMenuEmail;
    private Spinner spVehiculos;
    private NavigationView navigationView;
    private View headerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private Retrofit retrofit;

    String login, userID;
    String[] placa;
    private List<String> lVehiculos;
    private IComandosVehiculo iComandosVehiculo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRetrofit();

        Intent intent = getIntent();
        login = intent.getStringExtra("email");

        // Se obtienen los datos del usuario logueado
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        spVehiculos = findViewById(R.id.spVehiculos);
        cargarSpinner();

        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

        if (login != null) {
            try {
                /*
                 * Como el TextView está dentro de un NavigationView,
                 * el setText se hace de esta forma
                 */
                tvMainMenuEmail = headerView.findViewById(R.id.tvMainMenuEmail);
                tvMainMenuEmail.setText(firebaseUser.getEmail());
            } catch (Exception ex) {
                Log.e("MainActivity", "" + ex.getMessage());
            }
        }

        // Menú lateral
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnEscuchar = findViewById(R.id.btnEscuchar);
        btnTracker = findViewById(R.id.btnTracker);
        btnStatus = findViewById(R.id.btnStatus);
        btnAbout = findViewById(R.id.btnAbout);
        btnBloquear = findViewById(R.id.btnBloquear);
        btnDesbloquear = findViewById(R.id.btnDesbloquear);
        btnVelocidadOn = findViewById(R.id.btnVelocidadOn);
        btnVelocidadOff = findViewById(R.id.btnVelocidadOff);
        btnEncendidoOn = findViewById(R.id.btnEncendidoOn);
        btnEncendidoOff = findViewById(R.id.btnEncendidoOff);
        btnMovimientoOn = findViewById(R.id.btnMovimientoOn);
        btnMovimientoOff = findViewById(R.id.btnMovimientoOff);

        // Se hará la llamada, cuando cuelgue, vuelve al aplicativo
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        btnEscuchar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placa = spVehiculos.getSelectedItem().toString().split(" - ");

                if (placa.length==2) {
                    mostrarMensaje(
                            getResources().getString(R.string.calling_car)+" "+placa[1],
                            1
                    );
                    consultarVehiculo(placa[0]);
                } else {
                    mostrarMensaje(getResources().getString(R.string.select_car), 1);
                }
            }
        });
        btnTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("check", 1);
                enviarComandos("status");
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }
        });

        /******************************* Comandos *******************************/
        btnBloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("Se bloqueará el vehículo", 1);
                enviarComandos("lock");
            }
        });
        btnDesbloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("lock off", 1);
                enviarComandos("unlock");
            }
        });
        btnVelocidadOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("stop quickstop", 1);
                enviarComandos("speed_on");
            }
        });
        btnVelocidadOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("stop noquickstop", 1);
                enviarComandos("speed_off");
            }
        });
        btnEncendidoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("ACC on", 1);
                enviarComandos("engine_on");
            }
        });
        btnEncendidoOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("no ACC", 1);
                enviarComandos("engine_off");
            }
        });
        btnMovimientoOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("fix inteligente", 1);
                enviarComandos("movement_on");
            }
        });
        btnMovimientoOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensaje("fix cantidad", 1);
                enviarComandos("movement_off");
            }
        });

        iniciarConexion();
    }

    private void showAboutDialog() {
        TextView cgclab_url;
        ImageView dialog_logo_cerrar;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_about);

        cgclab_url = dialog.findViewById(R.id.cgclab_url);
        cgclab_url.setText(Html.fromHtml(getResources().getString(R.string.base_url_click)));
        cgclab_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openBrowser = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.base_url))
                );
                startActivity(openBrowser);
            }
        });

        dialog.setCanceledOnTouchOutside(true);

        dialog_logo_cerrar = dialog.findViewById(R.id.dialog_logo_cerrar);
        dialog_logo_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void enviarComandos(String comando) {
        placa = spVehiculos.getSelectedItem().toString().split(" - ");
        try {
            if (placa.length==2) {
                Call<CommandResponse> commandResponseCall =
                        iComandosVehiculo.comandoVehiculo(
                                comando.trim(),
                                placa[0]
                        );

                commandResponseCall.enqueue(new Callback<CommandResponse>() {
                    @Override
                    public void onResponse(Call<CommandResponse> call, Response<CommandResponse> response) {
                        try {
                            Log.i("MainActivity", response.body()+"");
                            CommandResponse comandos = response.body();
                            if (comandos.getSuccess()==1) {
                                mostrarMensaje(comandos.getData(),1);
                            } else {
                                mostrarMensaje(
                                        getResources().getString(R.string.check_data),
                                        1
                                );
                            }
                        } catch (Exception ex) {
                            Log.e("MainActivity", "ERROR 2: "+ex.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<CommandResponse> call, Throwable t) {
                        Log.e("MainActivity", "onFailure: "+t.getMessage());
                    }
                });

            } else {
                mostrarMensaje(getResources().getString(R.string.select_car), 1);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "lock On "+e.getMessage());
        }
    }

    public void initRetrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(getApplicationContext().getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        iComandosVehiculo = retrofit.create(IComandosVehiculo.class);
    }

    private void consultarVehiculo(final String placa) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos");
        Query query = databaseReference.orderByChild("idUsuario")
                .equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        for (DataSnapshot elemento : dataSnapshot.getChildren()) {
                            CarrosModel vehiculo = elemento.getValue(CarrosModel.class);
                            if (vehiculo.getPlaca().equals(placa)) {

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+vehiculo.getTelefono().toString()));
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                startActivity(callIntent);
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("CarroActivity", "111) ERROR: " + ex.getMessage());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cargarSpinner() {
        lVehiculos = new ArrayList<String>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos");

        /*
         * Con Query se hace consulta por un campo específico dentro de la tabla.
         * Si hubiera un child sería,
         * databaseReference.child("nombreChild").orderByChild("idUsuario").equalTo(userID);
         */

        Query query = databaseReference.orderByChild("idUsuario").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lVehiculos.clear();
                    lVehiculos.add(getResources().getString(R.string.choose_car));
                    try {
                        for (DataSnapshot elemento: dataSnapshot.getChildren()) {
                            CarrosModel vehiculo = elemento.getValue(CarrosModel.class);

                            if (vehiculo.getActivo().equals("1")) {
                                lVehiculos.add(vehiculo.getPlaca() + " - " + vehiculo.getNombre());
                            }
                        }
                        mostrarMensaje(
                                getResources().getString(R.string.update_car_list),
                                1
                        );
                    } catch (Exception ex) {
                        Log.e("MainActivity", "402) ERROR: "+ex.getMessage());
                    }

                    ArrayAdapter<String> adapterVehiculo = new ArrayAdapter<String>(
                            MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            lVehiculos
                    );
                    adapterVehiculo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spVehiculos.setAdapter(adapterVehiculo);

                    // Cuando se seleccione un elemento, quede mostrándose en el spinner
                    spVehiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            spVehiculos.setSelection(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

/*
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mostrarMensaje("FireBase Detectó cambios", 1);
                lVehiculos.clear();

                try {
                    for (DataSnapshot elemento: dataSnapshot.getChildren()) {
                        CarrosModel vehiculo = elemento.getValue(CarrosModel.class);
                        Log.i(
                            "MainActivity",
                                vehiculo.getPlaca()+" -> "+userID+"=="+vehiculo.getIdUsuario()
                        );

                        if (userID==vehiculo.getIdUsuario()) {
                            lVehiculos.add(vehiculo.getPlaca() + " - " + vehiculo.getNombre());
                        }
                    }
                } catch (Exception ex) {
                    Log.e("MainActivity", "216) ERROR: "+ex.getMessage());
                }

                Log.e("MainActivity", "Tamaño: "+lVehiculos.size());

                if (lVehiculos.size()<1) {
                    lVehiculos.add("No hay vehículos relacionados...");
                }

                ArrayAdapter<String> adapterVehiculo = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        lVehiculos
                );
                adapterVehiculo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spVehiculos.setAdapter(adapterVehiculo);

                // Cuando se seleccione un elemento, quede mostrándose en el spinner
                spVehiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spVehiculos.setSelection(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mostrarMensaje("ERROR: "+databaseError.getMessage(), 1);
            }
        });
*/
    }

    private void cargarSpinner2() {
        lVehiculos = new ArrayList<String>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos");

        /*
         * Con Query se hace consulta por un campo específico dentro de la tabla.
         * Si hubiera un child sería,
         * databaseReference.child("nombreChild").orderByChild("idUsuario").equalTo(userID);
         */
        Query query = databaseReference.orderByChild("idUsuario").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mostrarMensaje(
                            getResources().getString(R.string.update_car_list),
                            1
                    );
                    lVehiculos.clear();
                    lVehiculos.add(getResources().getString(R.string.all_cars));
                    try {
                        for (DataSnapshot elemento: dataSnapshot.getChildren()) {
                            CarrosModel vehiculo = elemento.getValue(CarrosModel.class);
                            if (vehiculo.getActivo().equals("1")) {
                                lVehiculos.add(vehiculo.getPlaca() + " - " + vehiculo.getNombre());
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("MainActivity", "214) ERROR: "+ex.getMessage());
                    }

                    ArrayAdapter<String> adapterVehiculo = new ArrayAdapter<String>(
                            MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            lVehiculos
                    );
                    adapterVehiculo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spVehiculos.setAdapter(adapterVehiculo);

                    // Cuando se seleccione un elemento, quede mostrándose en el spinner
                    spVehiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            spVehiculos.setSelection(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

/*
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mostrarMensaje("FireBase Detectó cambios", 1);
                lVehiculos.clear();

                try {
                    for (DataSnapshot elemento: dataSnapshot.getChildren()) {
                        CarrosModel vehiculo = elemento.getValue(CarrosModel.class);
                        Log.i(
                            "MainActivity",
                                vehiculo.getPlaca()+" -> "+userID+"=="+vehiculo.getIdUsuario()
                        );

                        if (userID==vehiculo.getIdUsuario()) {
                            lVehiculos.add(vehiculo.getPlaca() + " - " + vehiculo.getNombre());
                        }
                    }
                } catch (Exception ex) {
                    Log.e("MainActivity", "216) ERROR: "+ex.getMessage());
                }

                Log.e("MainActivity", "Tamaño: "+lVehiculos.size());

                if (lVehiculos.size()<1) {
                    lVehiculos.add("No hay vehículos relacionados...");
                }

                ArrayAdapter<String> adapterVehiculo = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        lVehiculos
                );
                adapterVehiculo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spVehiculos.setAdapter(adapterVehiculo);

                // Cuando se seleccione un elemento, quede mostrándose en el spinner
                spVehiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        spVehiculos.setSelection(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mostrarMensaje("ERROR: "+databaseError.getMessage(), 1);
            }
        });
*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void mostrarMensaje(String mensaje, Integer largo) {
        Toast.makeText(
                MainActivity.this,
                mensaje,
                largo==1?Toast.LENGTH_LONG:Toast.LENGTH_SHORT
        ).show();
    }

    public void iniciarConexion() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser==null) {
                    Toast.makeText(
                            getApplicationContext(),
                            "No hay usuario conectado",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        };
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.mnu_add_car:
                intent = new Intent(MainActivity.this, CarrosActivity.class);
                intent.putExtra("mail", login);
                startActivity(intent);
                break;
            case R.id.mnu_modificar:
                intent = new Intent(MainActivity.this, ClaveActivity.class);
                intent.putExtra("login", login);
                intent.putExtra("activity", "main");
                startActivity(intent);
                break;
            case R.id.mnu_logout:
                firebaseAuth.signOut();
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.mnu_acercade:
                showAboutDialog();
                break;
            case R.id.mnu_ayuda:
                mostrarMensaje("Ayuda", 0);
                break;
        }

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }
}
