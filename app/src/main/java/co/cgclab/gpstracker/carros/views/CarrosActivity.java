package co.cgclab.gpstracker.carros.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.carros.adapters.CarrosAdapter;
import co.cgclab.gpstracker.carros.controllers.CarrosController;
import co.cgclab.gpstracker.carros.controllers.ICarrosController;
import co.cgclab.gpstracker.carros.models.CarrosModel;

public class CarrosActivity extends AppCompatActivity {

    private ListView lvVehiculos;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    String userID, nombre, placa, telefono, password, estado, imei;
    private List<CarrosModel> lVehiculos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carros);
        Toolbar toolbar = findViewById(R.id.carrosToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Se obtienen los datos del usuario logueado
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        lvVehiculos = findViewById(R.id.lvVehiculos);
        // Se pone el ListView como long clickable y se agrega un context menú
        lvVehiculos.setLongClickable(true);
        registerForContextMenu(lvVehiculos);

        lvVehiculos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                nombre = lVehiculos.get(i).getNombre();
                placa = lVehiculos.get(i).getPlaca();
                telefono = lVehiculos.get(i).getTelefono().toString();
                password = lVehiculos.get(i).getPassword().toString();
                estado = lVehiculos.get(i).getActivo();
                imei = lVehiculos.get(i).getImei();
                return false;
            }
        });

        cargarListView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarrosActivity.this, CarroActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        if(item.getTitle()=="Editar") {
            Intent intent = new Intent(CarrosActivity.this, CarroActivity.class);
            intent.putExtra("placa", placa);
            startActivity(intent);
        }
        if(item.getTitle()=="Activar" || item.getTitle()=="Inactivar") {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(
                        CarrosActivity.this,
                        android.R.style.Theme_Material_Dialog_Alert
                );
            } else {
                builder = new AlertDialog.Builder(CarrosActivity.this);
            }

            String mensaje = "";
            if (estado.equals("1")) {
                mensaje = "Desea inactivar el vehículo "+placa+"?";
            } else {
                mensaje = "Desea activar el vehículo "+placa+"?";
            }

            builder.setTitle("Advertencia!!!")
                    .setMessage(mensaje)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ICarrosController iCarrosController = new CarrosController();
                            try {
                                iCarrosController.crearVehiculo(
                                        nombre,
                                        placa,
                                        telefono,
                                        password,
                                        imei,
                                        userID,
                                        (estado.equals("1")?"0":"1")
                                );

                                // Se recarga el ListView
                                cargarListView();
                            } catch (Exception e) {
                                Log.e("CarrosActivity", "125) ERROR: "+e.getMessage());
                            }

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Editar");
        menu.add(0, v.getId(), 0, estado.equals("1")?"Inactivar":"Activar");
    }

    private void cargarListView() {
        lVehiculos = new ArrayList<CarrosModel>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Vehiculos");

        Query query = databaseReference.orderByChild("idUsuario").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lVehiculos.clear();
                    try {
                        for (DataSnapshot elemento : dataSnapshot.getChildren()) {
                            CarrosModel vehiculo = elemento.getValue(CarrosModel.class);
                            lVehiculos.add(vehiculo);
                        }
                    } catch (Exception ex) {
                        Log.e("CarrosActivity", "124) ERROR: " + ex.getMessage());
                    }

                    lvVehiculos.setAdapter(
                            new CarrosAdapter(
                                    CarrosActivity.this,
                                    lVehiculos
                            )
                    );

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
