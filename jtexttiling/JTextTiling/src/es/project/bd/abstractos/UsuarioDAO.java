package es.project.bd.abstractos;

import java.sql.SQLException;
import java.util.List;

import es.project.bd.objetos.Archivo;
import es.project.bd.objetos.Usuario;

/**
 * <p>Clase abstracta con las declaraciones de los m�todos referidos a las operaciones
 * que se realizan con usuarios en la base de datos</p>
 * @author Daniel Fern�ndez Aller
 */
public abstract class UsuarioDAO {
	
	/**
	 * <p>Visualizaci�n por pantalla de todos los usuarios de la base de datos.
	 * S�LO PARA LAS PRUEBAS</p>
	 */
	public abstract void verUsuarios();
	
	/**
	 * <p>N�mero total de usuario contenidos en la base de datos (menos el root y
	 * los usuarios sin activar)</p>
	 * @return N�mero entero con el recuento
	 */
	public abstract int numeroUsuarios();
	
	/**
	 * <p>Comprueba que el usuario ya est� dentro de la base de datos, para lo cual
	 * compara el nombre y el password con los almacenados.</p>
	 * @param usuario Usuario a comprobar
	 * @return Verdadero si el usuario est� almacenado
	 */
	public abstract boolean comprobarUsuario(Usuario usuario);
	
	/**
	 * <p>Inserta un usuario en la base de datos</p>
	 * @param usuario Usuario a insertar
	 * @return Verdadero si la operaci�n fue bien, falso en caso contrario
	 */
	public abstract boolean insertarUsuario(Usuario usuario);
	
	/**
	 * <p>Elimina toda la informaci�n de los usuarios de la base de datos</p>
	 * @return Verdadero si la operaci�n fue bien
	 */
	public abstract boolean borrarUsuarios();
	
	/**
	 * <p>Elimina un usuario de la base de datos</p>
	 * @param usuario Usuario a eliminar
	 * @return Verdadero si la operaci�n fue bien, falso en caso contrario
	 */
	public abstract boolean borrarUsuario(Usuario usuario);
	
	/**
	 * <p>Busca un usuario en la base de datos</p>
	 * @param usuario Usuario a buscar
	 * @return Verdadero si y s�lo si el usuario est� dentro de la base de datos
	 */
	public abstract boolean buscarUsuario(Usuario usuario);
	
	/**
	 * <p>Hace un recuento del n�mero de archivos almacenados en la base de datos, 
	 * pertenecientes al usuario que se recibe como par�metro</p>
	 * @param usuario Usuario propietario de los archivos
	 * @return N�mero entero con el recuento
	 */
	public abstract int numeroArchivos(Usuario usuario);
	
	/**
	 * <p>Actualiza el nombre del usuario que se recibe como par�metro</p>
	 * @param usuario Usuario a modificar
	 * @param valor Nuevo valor para el nombre
	 * @return Verdadero si y s�lo si el nombre se modific�
	 */
	public abstract boolean actualizarNombre(Usuario usuario, String valor);
	
	/**
	 * <p>Modifica el password del usuario que se recibe como par�metro</p>
	 * @param usuario Usuario a modificar
	 * @param valor Nuevo valor para el password
	 * @return Verdadero si y s�lo si el password se modific�
	 */
	public abstract boolean actualizarPassword(Usuario usuario, String valor);
	
	/**
	 * <p>Modifica el email del usuario que recibe como par�metro</p>
	 * @param usuario Usuario a modificar
	 * @param valor Nuevo valor para el mail
	 * @return Verdadero si y s�lo si el password se modific�
	 */
	public abstract boolean actualizarMail(Usuario usuario, String valor);
	
	/**
	 * <p>Cuando un usuario cierra sesi�n, se actualiza la fecha de su �ltimo login
	 * (la fecha actual)</p>
	 * @param usuario Usuario a actualizar 
	 * @return Verdadero si se pudo realizar la operaci�n
	 */
	public abstract boolean actualizarUltimoLogin(Usuario usuario);
	/**
	 * <p>Devuelve una lista con todos los archivos del usuario</p>
	 * @param usuario Usuario del que se quieren obtener los archivos
	 * @return Lista con los archivos del usuario
	 */
	public abstract List<Archivo> getArchivos(Usuario usuario);
	
	/**
	 * <p>Devuelve el usuario correspondiente al campo especificado, que puede ser
	 * el nombreo el uuid del usuario</p>
	 * @param campo Campo en base al cual se va a buscar el usuario (nombre o uuid)
	 * @param valor Valor del campo 
 	 * @return Objeto de tipo Usuario
	 */
	public abstract Usuario getUsuario(String campo, String valor);
	
	/**
	 * <p>Devuelve una cadena con la fecha del �ltimo login del usuario</p>
	 * @param usuario Usuario del que se va a buscar la fecha
	 * @return Cadena con formato de fecha yyyy-mm-dd
	 */
	public abstract String getUltimoLogin(Usuario usuario) throws SQLException;
	
	/**
	 * <p>Devuelve una cadena con la fecha del alta del usuario</p>
	 * @param usuario Usuario del que se va a buscar la fecha
	 * @return Cadena con formato de fecha yyyy-mm-dd
	 * @throws SQLException Posibles errores en el acceso a la base de datos
	 */
	public abstract String getFechaAlta(Usuario usuario) throws SQLException;
	
	/**
	 * <p>Devuelve una cadena con el identificador universal del usuario</p>
	 * @param usuario Usuario del que se va a buscar el uuid
	 * @return Cadena con el uuid del usuario
	 * @throws SQLException Posibles errores en el acceso a la base de datos
	 */
	public abstract String getUuid(Usuario usuario);
	
	/**
	 * <p>Devuelve una cadena con el email del usuario</p>
	 * @param usuario Usuario del que se quiere obtener el email
	 * @return Cadena con el email del usuario
	 */
	public abstract String getEmail(Usuario usuario);
	/**
	 * <p>Finaliza la activaci�n de la cuenta del usuario modificando el valor
	 * de un campo de la base de datos</p>
	 * @param usuario Usuario a activar
	 * @return Verdadero si se pudo realizar la operaci�n
	 */
	public abstract boolean activarUsuario(Usuario usuario);
	
	/**
	 * <p>Comprueba si un usuario est� dado de alta en el servicio, para lo cual, comprueba en
	 * la base de datos si la columna "activado" es "1"</p>
	 * @param usuario Objeto de tipo Usuario que representa al usuario del que se quiere
	 * saber si est� activado
	 * @return Verdadero si el usuario est� dado de alta, falso en cualquier otro caso
	 */
	public abstract boolean estaActivado(Usuario usuario);
	
	/**
	 * <p>Comprueba si el usuario que se est� autenticando es el administrador del servicio</p>
	 * @param usuario Objeto de tipo Usuario que representa al administrador
	 * @return Verdadero si el usuario es el administrador
	 */
	public abstract boolean esRoot(Usuario usuario);
	
	/**
	 * <p>Copia en una lista la informaci�n de todos los usuarios almacenados en la base de datos</p>
	 * @return Lista enlazada con las propiedades de los usuarios
	 */
	public abstract List<Usuario> getTodosUsuarios();
}
