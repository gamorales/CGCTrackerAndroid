package co.cgclab.gpstracker.usuarios.views;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.main.views.MainActivity;
import co.cgclab.gpstracker.main.views.MapsActivity;
import co.cgclab.gpstracker.usuarios.controllers.IUsuariosController;
import co.cgclab.gpstracker.usuarios.controllers.UsuariosController;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText txtLogin, txtPassword;
    private Button btnLogin;
    private FloatingActionButton fab;
    private ProgressBar pbLogin;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Se valida que esté conectado por WiFi o GPRS
        if (isNetDisponible()==false) {
            Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.network_disable),
                Toast.LENGTH_LONG
            ).show();
            closeApp();
        }
        // Se verifica si hay salida a Internet
        if (isOnlineNet()==false) {
            Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.internet_offline),
                Toast.LENGTH_LONG
            ).show();
            closeApp();
        }

        // fab = findViewById(R.id.fab);
        txtLogin = findViewById(R.id.txtLogin);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        pbLogin = findViewById(R.id.pbLogin);
        pbLogin.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            pbLogin.setVisibility(View.VISIBLE);
            try {
                IUsuariosController iUsuariosController = new UsuariosController();
                iUsuariosController.signIn(
                    txtLogin.getText().toString(),
                    txtPassword.getText().toString()
                );

                firebaseAuth.signInWithEmailAndPassword(
                    txtLogin.getText().toString(),
                    txtPassword.getText().toString()
                ).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                            LoginActivity.this,
                            getResources().getString(R.string.success_connection),
                            Toast.LENGTH_SHORT
                        ).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("email", txtLogin.getText().toString());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(
                            LoginActivity.this,
                            getResources().getString(R.string.fail_connection),
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                    }
                });
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            pbLogin.setVisibility(View.GONE);
            }
        });

/*        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            Intent intent = new Intent(LoginActivity.this, UsuarioActivity.class);
            startActivity(intent);
            }
        });*/

        iniciarConexion();
    }

    // Cerrará la app por problemas de conexión
    private void closeApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME)
              .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void iniciarConexion() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            if (firebaseUser!=null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("email", ""+firebaseUser.getEmail());
                startActivity(intent);
                finish();
            }
            }
        };
    }

    public void goRecuperarClave(View view) {
        Intent intent = new Intent(this, ClaveActivity.class);
        txtLogin = findViewById(R.id.txtLogin);
        String login = txtLogin.getText().toString();
        intent.putExtra("login", login);
        intent.putExtra("activity", "login");
        startActivity(intent);
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



    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        if (actNetInfo != null && actNetInfo.isConnected()) {
            Log.i("LoginActivity", "si");
        } else {
            Log.i("LoginActivity", "no");
        }

        boolean conectado = (actNetInfo != null && actNetInfo.isConnected());

        return conectado;
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec(
                getResources().getString(R.string.ping_command)
            );

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
