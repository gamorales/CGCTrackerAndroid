package co.cgclab.gpstracker.usuarios.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.usuarios.controllers.IUsuariosController;
import co.cgclab.gpstracker.usuarios.controllers.UsuariosController;

public class UsuarioActivity extends AppCompatActivity {

    private TextInputEditText txtFirebaseCorreo, txtFirebaseClave, txtFirebaseClaveConf;
    private Button btnRegistrar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        Toolbar usuarioToolbar = findViewById(R.id.usuarioToolbar);
        setSupportActionBar(usuarioToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        txtFirebaseCorreo = findViewById(R.id.txtFirebaseCorreo);
        txtFirebaseClave = findViewById(R.id.txtFirebaseClave);
        txtFirebaseClaveConf = findViewById(R.id.txtFirebaseClaveConf);
        btnRegistrar = findViewById(R.id.btnFirebaseRegistrar);

        firebaseAuth = firebaseAuth.getInstance();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IUsuariosController iUsuariosController = new UsuariosController();
                    iUsuariosController.registrarUsuarioFirebase(
                            txtFirebaseCorreo.getText().toString(),
                            txtFirebaseClave.getText().toString(),
                            txtFirebaseClaveConf.getText().toString()
                    );

                    firebaseAuth.createUserWithEmailAndPassword(
                            txtFirebaseCorreo.getText().toString(),
                            txtFirebaseClave.getText().toString()
                    ).addOnCompleteListener(UsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getBaseContext(),"Se creó el usuario exitosamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UsuarioActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getBaseContext(),"Hubo problemillas al crear el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception ex) {
                    Toast.makeText(getBaseContext(), "ERROR: "+ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
