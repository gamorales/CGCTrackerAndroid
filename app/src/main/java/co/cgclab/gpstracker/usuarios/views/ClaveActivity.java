package co.cgclab.gpstracker.usuarios.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.main.views.MainActivity;
import co.cgclab.gpstracker.usuarios.controllers.IUsuariosController;
import co.cgclab.gpstracker.usuarios.controllers.UsuariosController;

public class ClaveActivity extends AppCompatActivity {
    private TextInputEditText txtCorreo;
    private Button btnClaveRecuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave);
        Toolbar claveToolbar = findViewById(R.id.claveToolbar);
        setSupportActionBar(claveToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String login = intent.getStringExtra("login");
        final String activity = intent.getStringExtra("activity");

        txtCorreo = findViewById(R.id.txtCorreo);
        btnClaveRecuperar = findViewById(R.id.btnClaveRecuperar);


        txtCorreo.setText(login);

        btnClaveRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    IUsuariosController iUsuariosController = new UsuariosController();
                    iUsuariosController.validarPassword(txtCorreo.getText().toString());

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.sendPasswordResetEmail(
                            txtCorreo.getText().toString()
                    ).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(
                                        ClaveActivity.this,
                                        getResources().getString(R.string.success_email),
                                        Toast.LENGTH_LONG
                                );
                                Intent intent = new Intent(
                                        ClaveActivity.this,
                                        (activity.equals("login")?LoginActivity.class: MainActivity.class));
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(
                                        ClaveActivity.this,
                                        getResources().getString(R.string.fail_email),
                                        Toast.LENGTH_LONG
                                );
                            }
                        }
                    });
                } catch (Exception ex) {
                    Toast.makeText(
                            getBaseContext(),
                            "ERROR: "+ex.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }
}
