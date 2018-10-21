package co.cgclab.gpstracker.camara.presentacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Freezable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import co.cgclab.gpstracker.R;
import co.cgclab.gpstracker.camara.dto.UsuarioDTO;
import co.cgclab.gpstracker.camara.logica.IUsuarioLogic;
import co.cgclab.gpstracker.camara.logica.UsuarioLogic;

public class UsuarioActivity extends AppCompatActivity {
    private EditText txtCedula;
    private EditText txtNombre;
    private EditText txtUsuario;
    private EditText txtClave;
    private Button btnCrear;
    private Button btnModificar;
    private Button btnLimpiar;
    private Button btnEliminar;
    private ImageButton btnTomarFoto;
    private Bitmap fotoUsuario;
    private static final int REQUEST_CODE;
    private ImageView imagen;

    static {
        REQUEST_CODE = 1888;
    }

    public void accederCamara(View view) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE);
        } catch (Exception e) {
            Log.e("Error", "Error Abriendo La Camara " + e.getMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE &&
                intent != null && intent.getExtras() != null &&
                intent.getExtras().get("data") != null) {
            fotoUsuario = (Bitmap) intent.getExtras().get("data");
            imagen.setImageBitmap(fotoUsuario);

        }
    }


    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_camara_usuario);
        txtCedula = findViewById(R.id.txtCedula);
        txtNombre = findViewById(R.id.txtNombre);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtClave = findViewById(R.id.txtClave);
        btnCrear = findViewById(R.id.btnCrear);
        btnModificar = findViewById(R.id.btnModificar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnEliminar = findViewById(R.id.btnEliminar);
//        btnCrear.setClickable(false);
//        btnModificar.setClickable(false);
//        btnEliminar.setClickable(false);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
//        btnTomarFoto.setEnabled(false);
        imagen = findViewById(R.id.imagen);
        txtCedula.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                accionConsultarUsuario();
            }
        });
    }


    public void consultarImagenUsuario() {
        if (txtCedula.getText()!=null &&
            !txtCedula.getText().toString().trim().equals("")) {
            long sizeFoto = 1024 * 1024;
            FirebaseStorage firebaseStorage =
                    FirebaseStorage.getInstance("gs://gpstraker-5fe83.appspot.com");
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference descargaStorage =
                    storageReference.child("fotos/"+txtCedula.getText().toString().trim()+".png");

            descargaStorage.getBytes(sizeFoto).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    fotoUsuario = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("UsuarioActivity", "ERROR: "+e.getMessage());
                }
            });
        }

    }

    public void accionConsultarUsuario() {
        if (txtCedula != null && txtCedula.getText() != null &&
                (!txtCedula.getText().toString().equals(""))) {

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");
            Query query = databaseReference.orderByChild("cedula").equalTo(new Long(txtCedula.getText().toString()));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildren().iterator().hasNext()) {
                        UsuarioDTO usuarioDTO = dataSnapshot.getChildren().iterator().next().getValue(UsuarioDTO.class);
                        txtNombre.setText(usuarioDTO.getNombre());
                        txtUsuario.setText(usuarioDTO.getLogin());
                        txtClave.setText(usuarioDTO.getClave());
                        txtCedula.setEnabled(false);
//                        btnModificar.setClickable(true);
//                        btnEliminar.setClickable(true);
                        consultarImagenUsuario();
                    } else {
                        btnCrear.setClickable(true);
                    }
                    btnTomarFoto.setEnabled(true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("Error", databaseError.getMessage());
                }
            });
        }
    }

    public void accionCrear(View view) {
        try {
            IUsuarioLogic iUsuarioLogic = new UsuarioLogic();
            iUsuarioLogic.crearUsuario(txtCedula.getText() + "", txtNombre.getText() + "",
                    txtUsuario.getText() + "", txtClave.getText() + "", fotoUsuario);
            Toast.makeText(this,"Se creo con exito",Toast.LENGTH_LONG).show();
            limpiar();

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void accionModificar(View view) {
        try {
            IUsuarioLogic iUsuarioLogic = new UsuarioLogic();
            iUsuarioLogic.modificarUsuario(txtCedula.getText().toString(),
                    txtClave.getText().toString(),
                    txtUsuario.getText().toString(),
                    txtNombre.getText().toString(),
                    fotoUsuario);
            limpiar();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void limpiar() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtUsuario.setText("");
        txtClave.setText("");
//        txtCedula.setEnabled(true);
//        btnCrear.setClickable(false);
//        btnModificar.setClickable(false);
//        btnEliminar.setClickable(false);
        imagen.setImageBitmap(null);
    }


    public void accionLimpiar(View view) {
        limpiar();
    }


    public void accionEliminar(View view) {
        try {
            IUsuarioLogic iUsuarioLogic = new UsuarioLogic();
            iUsuarioLogic.eliminarUsuario(txtCedula.getText().toString());
            limpiar();
            Toast toast = Toast.makeText(this, "usuario Eliminado con éxito",
                    Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }


}
