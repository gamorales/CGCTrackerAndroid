package co.cgclab.gpstracker.carros.views;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.carros.controllers.CarrosController;
import co.cgclab.gpstracker.carros.controllers.ICarrosController;
import co.cgclab.gpstracker.carros.models.CarrosModel;

public class CarroActivity extends AppCompatActivity {

    private TextInputEditText txtCarroNombre, txtCarroPlaca, txtCarroPassword;
    private TextInputEditText txtCarroTelefono, txtCarroIMEI;
    private Button btnCarroRegistrar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    String userID, placa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro);
        Toolbar carroToolbar = findViewById(R.id.carroToolbar);
        setSupportActionBar(carroToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        Intent intent = getIntent();
        placa = intent.getStringExtra("placa");
        // Sólo se consultará si hay placa
        if (placa!=null && !placa.equals("")) {
            consultarVehiculo(placa);
        }

        txtCarroNombre = findViewById(R.id.txtCarroNombre);
        txtCarroPlaca = findViewById(R.id.txtCarroPlaca);
        txtCarroTelefono = findViewById(R.id.txtCarroTelefono);
        txtCarroPassword = findViewById(R.id.txtCarroPassword);
        txtCarroIMEI = findViewById(R.id.txtCarroIMEI);
        btnCarroRegistrar = findViewById(R.id.btnCarroRegistrar);

        btnCarroRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                // Se consulta el usuario conectado desde la cuenta firebase
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                ICarrosController iCarrosController = new CarrosController();
                iCarrosController.crearVehiculo(
                    txtCarroNombre.getText().toString().toUpperCase(),
                    txtCarroPlaca.getText().toString().toUpperCase(),
                    txtCarroTelefono.getText().toString(),
                    txtCarroPassword.getText().toString(),
                    txtCarroIMEI.getText().toString(),
                    firebaseUser.getUid().toString(),
                    "1"
                );

                Toast.makeText(
                        CarroActivity.this,
                        getResources().getString(R.string.success_car_created),
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(CarroActivity.this, CarrosActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("CarroActivity", "71) ERROR: "+e.getMessage());
            }
            }
        });

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
                                txtCarroNombre.setText(vehiculo.getNombre());
                                txtCarroPlaca.setText(vehiculo.getPlaca());
                                txtCarroTelefono.setText(vehiculo.getTelefono());
                                txtCarroPassword.setText(vehiculo.getPassword());
                                txtCarroIMEI.setText(vehiculo.getImei());
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
}
