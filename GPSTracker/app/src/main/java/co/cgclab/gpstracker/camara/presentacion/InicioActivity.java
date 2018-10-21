package co.cgclab.gpstracker.camara.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.camara.dto.TipoUsuarioDTO;

public class InicioActivity extends AppCompatActivity {

    private Spinner spTipoUsuarios;
    private List<TipoUsuarioDTO> lTipoUsuarios;

    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_camara_inicio);
        spTipoUsuarios = findViewById(R.id.spTipoUsuario);
        cargarTipoUsuarios();
    }


    public void cargarSpinner(){
        ArrayAdapter<TipoUsuarioDTO> adapterUsuario = new ArrayAdapter<TipoUsuarioDTO>(this,
                                                                                        android.R.layout.simple_spinner_item,
                                                                                        lTipoUsuarios);

        adapterUsuario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoUsuarios.setAdapter(adapterUsuario);

        spTipoUsuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spTipoUsuarios.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void cargarTipoUsuarios(){
        lTipoUsuarios = new ArrayList<TipoUsuarioDTO>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference =  firebaseDatabase.getReference("TipoUsuarios");

        ValueEventListener valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Info","FireBase Detetecto Cambios");
                lTipoUsuarios.clear();

                for (DataSnapshot iterado: dataSnapshot.getChildren()){
                        TipoUsuarioDTO tipoUsuarioDTO = iterado.getValue(TipoUsuarioDTO.class);
                        lTipoUsuarios.add(tipoUsuarioDTO);
                }
                cargarSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error",databaseError.getMessage());
            }
        });


    }

    public void accionIngresar(View view){
        Intent intent  = new Intent();
        intent.setClass(this, UsuarioActivity.class);
        startActivity(intent);
        finish();
    }

}
