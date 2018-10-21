package co.cgclab.gpstracker.camara.accesodatos;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import co.cgclab.gpstracker.camara.dto.UsuarioDTO;

public class UsuarioDAO implements IUsuarioDAO {


    @Override
    public boolean almacenarFotoUsuario(Long cedulaUsuario, Bitmap fotoUsuario) throws Exception {
         boolean  almacenoFoto = false;

        try {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://gpstraker-5fe83.appspot.com");
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference saveStorageReference = storageReference.child("fotos/" + cedulaUsuario + ".png");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            fotoUsuario.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);

            byte[] bitmapData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bitmapData);

            UploadTask uploadTask = saveStorageReference.putStream(byteArrayInputStream);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Error", "Fallo subir la foto " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("INFO", "Se almaceno con exito");

                }
            });

            almacenoFoto=true;

          /*  UploadTask.TaskSnapshot snapshot= uploadTask.getResult();
             if(snapshot.getError()==null){
                 almacenoFoto=true;
             }*/


        } catch (Exception e) {
            //throw new Exception("Error Almacenando la Foto " + e.getMessage());
            Log.e("Error",e.getMessage());
        }

        return almacenoFoto;
    }

    @Override
    public void eliminarFotoUsuario(Long cedulaUsuario) throws Exception {
        try {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://gpstraker-5fe83.appspot.com");
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference deleteReference = storageReference.child("fotos/"+cedulaUsuario+".png");
            deleteReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("INFO", "Foto eliminada");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERROR", "Foto no eliminada");
                }
            });

        } catch (Exception ex) {
            throw new Exception("Error al eliminar");
        }
    }

    @Override
    public boolean crearUsuario(UsuarioDTO usuarioDTO) {
        boolean usuarioCreado = false;
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");
            databaseReference.child(usuarioDTO.getCedula() + "").setValue(usuarioDTO);
            usuarioCreado = true;
        } catch (Exception e) {
            Log.e("Error",e.getMessage());
        }
        return usuarioCreado;
    }

    @Override
    public void modificarUsuario(UsuarioDTO usuarioDTO) throws Exception {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios");

            HashMap<String, Object> atributosActualizar = new HashMap<String, Object>();
            atributosActualizar.put("/" + usuarioDTO.getCedula(), usuarioDTO.actualizarUsuario());
            databaseReference.updateChildren(atributosActualizar);

        } catch (Exception e) {
            throw new Exception("Error modificando el usuario " + e.getMessage());
        }

    }

    @Override
    public void eliminarUsuario(Long cedulaUsuario) throws Exception {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Usuarios/" + cedulaUsuario);
            databaseReference.removeValue();
        } catch (Exception e) {
            throw new Exception("No se pudo eliminar el usuario con cédula " + cedulaUsuario);
        }

    }

}
