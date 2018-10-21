package co.cgclab.gpstracker.camara.logica;

import android.graphics.Bitmap;

import co.cgclab.gpstracker.camara.accesodatos.IUsuarioDAO;
import co.cgclab.gpstracker.camara.accesodatos.UsuarioDAO;
import co.cgclab.gpstracker.camara.dto.UsuarioDTO;

public class UsuarioLogic implements IUsuarioLogic {
    @Override
    public void crearUsuario(String cedula, String nombre,
                             String usuario, String clave,
                             Bitmap imagenUsuario) throws Exception {


        if (cedula == null || cedula.equals("")) {
            throw new Exception("La cédula es obligatoria para crear el usuario");
        }

        if (!cedula.matches("[0-9]*")) {
            throw new Exception("La cedula del usuario debe ser numerica");
        }

        if (cedula.length() != 10 && cedula.length() != 11) {
            throw new Exception("La cédula del usuario debe tener 10 o 11 digitos");
        }

        if(imagenUsuario == null){
            throw new Exception("La foto del Usaurio es Obligatoria");
        }


        //TODO: adicionar las demas validaciones de logica
        IUsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setCedula(new Long(cedula));
        usuarioDTO.setNombre(nombre);
        usuarioDTO.setLogin(usuario);
        usuarioDTO.setClave(clave);
        //TODO: cambiar el tipo de usuario para que sea
        //tomado desde un spinner y viaje hasta la logica
        usuarioDTO.setTipoUsuario(1L);

        boolean almacenoFoto=usuarioDAO.almacenarFotoUsuario(new Long(cedula),imagenUsuario);

        if(almacenoFoto){
            boolean creoUsuario =usuarioDAO.crearUsuario(usuarioDTO);

            if(!creoUsuario){
                usuarioDAO.eliminarUsuario(new Long(cedula));
            }
        }

    }

    @Override
    public void modificarUsuario(String cedula, String clave,
                                 String usuario, String nombre,
                                 Bitmap imagenUsuario) throws Exception {

        try {
            if (cedula == null || cedula.equals("")) {
                throw new Exception("La cédula es obligatoria para crear el usuario");
            }

            if (!cedula.matches("[0-9]*")) {
                throw new Exception("La cedula del usuario debe ser numerica");
            }

            Long cedulaUsuario = new Long(cedula);

            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setCedula(new Long(cedula));
            usuarioDTO.setNombre(nombre);
            usuarioDTO.setLogin(usuario);
            usuarioDTO.setClave(clave);
            //TODO: cambiar el tipo de usuario para que sea
            //tomado desde un spinner y viaje hasta la logica
            usuarioDTO.setTipoUsuario(1L);
            IUsuarioDAO iUsuarioDAO = new UsuarioDAO();
            iUsuarioDAO.modificarUsuario(usuarioDTO);

            if (imagenUsuario!=null) {
                iUsuarioDAO.almacenarFotoUsuario(new Long(cedula), imagenUsuario);
            }
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public void eliminarUsuario(String cedulaUsuario) throws Exception {
        try {

            if (cedulaUsuario == null || cedulaUsuario.equals("")) {
                throw new Exception("La cédula es obligatoria para eliminar el usuario");
            }

            if (!cedulaUsuario.matches("[0-9]*")) {
                throw new Exception("La cedula del usuario debe ser numerica");
            }

            IUsuarioDAO iUsuarioDAO = new UsuarioDAO();
            iUsuarioDAO.eliminarUsuario(new Long(cedulaUsuario));

            iUsuarioDAO.eliminarFotoUsuario(new Long(cedulaUsuario));

        }catch(Exception e){
            throw e;
        }
    }
}
