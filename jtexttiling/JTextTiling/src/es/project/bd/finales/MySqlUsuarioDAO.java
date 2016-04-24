package es.project.bd.finales;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import es.project.bd.abstractos.UsuarioDAO;
import es.project.bd.objetos.Archivo;
import es.project.bd.objetos.Usuario;
import es.project.facade.ConectorBD;

/**
 * <p>Implementaci�n de la clase UsuarioDAO particularizada para una base de datos MySQL</p>
 * @author Daniel Fern�ndez Aller
 */
public class MySqlUsuarioDAO extends UsuarioDAO{
	/**
	 * <p>Objeto que encapsula las operaciones que se realizan con los objetos de tipo
	 * Connection</p>
	 */
	private ConectorBD conectorBD = new ConectorBD();
	
	private final String FECHA_ALTA = "fecha_alta";
	private final String ULTIMO_LOGIN = "ultimo_login";
	private final String PASSWORD = "password";
	private final String NOMBRE = "nombre";
	private final String MAIL = "email";
	private final String ACTIVADO = "activado";
	private final String UID = "uuid";
	
	/**
	 * <p>Recorre la tabla que almacena la informaci�n sobre los usuarios, guarda sus
	 * propiedades en una variable temporal, y la muestra por pantalla.</p>
	 */
	public void verUsuarios() {
		ResultSet rs;
		Usuario usuario;
		
		try {
			Statement stat = conectorBD.getStatement();
			rs = stat.executeQuery("select * from usuarios");
			
			while (rs.next()) {
				usuario = new Usuario();
				usuario.setNombre(rs.getString("nombre"));
				usuario.setPassword(rs.getString("password"));
				
				System.out.println(usuario);
			}
			
			rs.close();
			stat.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/verUsuarios: " + sql.getMessage());
		}
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para buscar
	 * un usuario concreto de la base de datos. Llama al m�todo "buscarUsuario", privado, 
	 * con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo Usuario que representa el usuario que se va a buscar
	 * @return Verdadero si el usuario est� en la base de datos
	 */
	public boolean buscarUsuario(Usuario usuario) {
		return buscarUsuario(usuario.getNombre());
	}
	
	/**
	 * <p>Busca en la base de datos el usuario que concuerde con el nombre especificado como
	 * par�metro</p>
	 * @param nombre Nombre del usuario a buscar
	 * @return Verdadero si el usuario est� en la base de datos
	 */
	private boolean buscarUsuario(String nombre) {
		ResultSet rs;
		boolean usuarioAlmacenado = true;
		
		try {
			PreparedStatement ps = 
				conectorBD.getPreparedStatement("select * from usuarios where nombre = ?");
			ps.setString(1, nombre);
			
			rs = ps.executeQuery();
			
			if (!rs.next()) 
				usuarioAlmacenado = false;
			
			rs.close();
			ps.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/buscarUsuario: " + sql.getMessage());
			usuarioAlmacenado = false;
		}
		
		return usuarioAlmacenado;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para realizar
	 * la comprobaci�n de un usuario concreto en la base de datos. Llama al m�todo "comprobarUsuario", 
	 * privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo Usuario que representa el usuario que se va a comprobar
	 * @return Verdadero si todo fue bien
	 */
	public boolean comprobarUsuario(Usuario usuario) {
		return comprobarUsuario(usuario.getNombre(), usuario.getPassword());
	}
	
	/**
	 * <p>Busca en la base de datos si hay una fila en la que el nombre y el password coinciden
	 * con los especificados como par�metro, lo que quiere decir que es un usuario dado de alta en la
	 * aplicaci�n. Tambi�n puede darse el caso de que est� almacenado en la base de datos, pero no
	 * haya terminado de activar su cuenta, lo cual tambi�n se trata como que no est� dado de alta.</p>
	 * @param nombre Nombre del usuario a comprobar
	 * @param password Password del usuario a comprobar
	 * @return Verdadero si el usuario est� dado de alta
	 */
	private boolean comprobarUsuario(String nombre, String password) {
		ResultSet rs;
		boolean usuarioAlmacenado = true;
		
		try {
			PreparedStatement ps = 
				conectorBD.getPreparedStatement("select * from usuarios where nombre = ? and password = ?");
			ps.setString(1, nombre);
			ps.setString(2, password);
			
			rs = ps.executeQuery();
			
			if (!rs.next()) 
				usuarioAlmacenado = false;
				
			else if (!rs.getBoolean("activado")) 
				usuarioAlmacenado = false;
			
			rs.close();
			ps.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/comprobarUsuario: " + sql.getMessage());
			usuarioAlmacenado = false;
		}
		
		return usuarioAlmacenado;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para insertar 
	 * un usuario en la base de datos. Llama al m�todo "insertarUsuario", privado, con los par�metros 
	 * necesarios</p>
	 * @param usuario Objeto de tipo Usuario que representa el usuario que se va a insertar
	 * @return Verdadero si todo fue bien
	 */
	public boolean insertarUsuario(Usuario usuario) {
		return insertarUsuario(usuario.getNombre(), usuario.getPassword(),usuario.getEmail(),true);
	}
	
	/**
	 * <p>Inserta en la tabla "usuarios" una fila con el nombre del usuario y su password, as� como
	 * la fecha en la que se da de alta en el servicio</p>
	 * @param nombre Nombre del usuario a insertar
	 * @param password Password del usuario a insertar
	 * @return Verdadero si todo fue bien
	 */
	private boolean insertarUsuario(String nombre, String password, String email, boolean activado) {
		boolean usuarioAlmacenado = true;
		
		try {
			PreparedStatement ps = 
				conectorBD.getPreparedStatement("insert into usuarios (nombre,password,fecha_alta,ultimo_login,email,activado,uuid) values(?,?,?,?,?,?,?)");
			ps.setString(1, nombre);
			ps.setString(2, password);
			String fecha = this.getFecha();
			ps.setString(3, fecha);
			ps.setString(4, "0000-00-00");
			ps.setString(5, email);
			ps.setBoolean(6, activado);
			ps.setString(7, UUID.randomUUID().toString());
			ps.executeUpdate();
			
			ps.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/insertarUsuario: " + sql.getMessage());
			usuarioAlmacenado = false;
		}
		
		return usuarioAlmacenado;
	}
	
	/**
	 * <p>Calcula la fecha actual (yyyy-mm-dd)</p>
	 * @return Devuelve una cadena con la fecha actual en el formato yyyy-mm-dd
	 */
	private String getFecha() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * <p>Borra toda la informaci�n de la tabla que contiene el nombre y el password de los
	 * usuarios</p>
	 * @return Verdadero si la operaci�n fue bien
	 */
	public boolean borrarUsuarios() {
		boolean borrados = true;
		try {
			Statement stat = conectorBD.getStatement();
			stat.executeUpdate("delete from usuarios");
			
			stat.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/borrarUsuarios: " + sql.getMessage());
			borrados = false;
		}
		return borrados;
	}
	
	/**
	 * <p>Calcula el n�mero total de usuarios mediante la operaci�n "count", y lo
	 * muestra por pantalla. No se cuenta el root ni los usuarios que est�n sin activar.</p>
	 * @return N�mero de usuarios contenidos en la base de datos
	 */
	public int numeroUsuarios() {
		int numUsuarios = 0;
		try {
			Statement stat = conectorBD.getStatement();
			ResultSet rs = stat.executeQuery("select count(nombre) total from usuarios where nombre != 'root' and activado = '1'");
			
			if (rs.next())
				numUsuarios = rs.getInt("total");
			
			rs.close();
			stat.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/numeroUsuarios: " + sql.getMessage());
		}
		return numUsuarios;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para borrar
	 * un usuario concreto de la base de datos. Llama al m�todo "borrarUsuario", privado, 
	 * con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo Usuario que representa el usuario que se va a eliminar
	 * @return Verdadero si todo fue bien
	 */
	public boolean borrarUsuario(Usuario usuario) {
		return this.borrarUsuario(usuario.getNombre());
	}
	
	/**
	 * <p>Busca en la base de datos el usuario que concuerde con el par�metro especificado,
	 * y lo elimina.</p>
	 * @param nombre Nombre del usuario a eliminar
	 * @return Verdadero si todo fue bien
	 */
	private boolean borrarUsuario(String nombre){
		boolean borrado = true;
		try {
			PreparedStatement ps = 
				conectorBD.getPreparedStatement("delete from usuarios where nombre = ?");
			ps.setString(1, nombre);
			ps.executeUpdate();
			
			ps.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/borrarUsuario: " + sql.getMessage());
			borrado = false;
		}
		return borrado;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para calcular
	 * el n�mero de archivos que tiene un usuario concreto. Llama al m�todo "numeroArchivos", privado, 
	 * con los par�metros necesarios</p>
	 * @param usuario Usuario del que se va a contar el n�mero de archivos
	 * @return N�mero de archivos del usuario
	 */
	public int numeroArchivos(Usuario usuario) {
		return this.numeroArchivos(usuario.getNombre());
	}
	
	/**
	 * <p>Realiza una b�squeda en la tabla de archivos, y cuenta el n�mero de archivos que pertenecen
	 * al usuario cuyo nombre se recibe como par�metro</p>
	 * @param nombre Nombre del usuario del que se va a contar el n�mero de archivos
	 * @return N�mero de archivos del usuario
	 */
	private int numeroArchivos(String nombre) {
		int numArchivos = 0;
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = conectorBD.getPreparedStatement("select count(nombreArchivo) num from archivos where nombrePropietario = ?");
			ps.setString(1, nombre);
			rs = ps.executeQuery();
			
			if (rs.next())
				numArchivos = rs.getInt("num"); 
			
			rs.close();
			ps.close();
		
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/numeroArchivos: " + sql.getMessage());
		}
		return numArchivos;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para
	 * la operaci�n de actualizar el nombre de un usuario. Llama al m�todo "actualizarUsuario",
	 * privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo usuario que representa el usuario a modificar
	 * @param valor Nuevo valor que recibir� el nombre del usuario
	 * @return Verdadero si la operaci�n result� bien
	 */
	public boolean actualizarNombre(Usuario usuario,  String valor) {
		return this.actualizarUsuario(usuario.getNombre(), this.NOMBRE, valor);
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para
	 * la operaci�n de actualizar el mail de un usuario. Llama al m�todo "actualizarUsuario",
	 * privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo usuario que representa el usuario a modificar
	 * @param valor Nuevo valor que recibir� el email del usuario
	 * @return Verdadero si la operaci�n result� bien
	 */
	public boolean actualizarMail(Usuario usuario, String valor) {
		return this.actualizarUsuario(usuario.getNombre(), this.MAIL, valor);
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para
	 * la operaci�n de actualizar el password de un usuario. Llama al m�todo "actualizarUsuario",
	 * privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo usuario que representa el usuario a modificar
	 * @param valor Nuevo valor que recibir� el password del usuario
	 * @return Verdadero si la operaci�n result� bien
	 */
	public boolean actualizarPassword (Usuario usuario, String valor) {
		return this.actualizarUsuario(usuario.getNombre(), this.PASSWORD, valor);
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para la
	 * operaci�n que actualiza la fecha del �ltimo login de un usuario. Llama al m�todo 
	 * "actualizarUsuario", privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo usuario que representa el usuario a modificar
	 * @return Verdadero si la operaci�n result� bien
	 */
	public boolean actualizarUltimoLogin(Usuario usuario) {
		return this.actualizarUsuario(usuario.getNombre(), this.ULTIMO_LOGIN, this.getFecha());
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para la
	 * operaci�n que finaliza el alta del usuario. Llama al m�todo 
	 * "actualizarUsuario", privado, con los par�metros necesarios. En este caso, la operaci�n
	 * que se realiza es poner a "1" (osea, verdadero) la variable "activado"</p>
	 * @param usuario Objeto de tipo usuario que representa el usuario a modificar
	 * @return Verdadero si la operaci�n result� bien
	 */
	public boolean actualizarActivado(Usuario usuario) {
		return this.actualizarUsuario(usuario.getNombre(),this.ACTIVADO,"1");
	}
	
	/**
	 * <p>Busca si alguna fila coincide con el nombre del usuario. En caso de que as� sea, 
	 * modifica el campo requerido con el valor especificado. Se utiliza tanto para actualizar 
	 * el nombre como el password del usuario.</p>
	 * @param nombre Nombre del usuario a modificar
	 * @param campo Campo a modificar, puede ser el nombre del usuario o su password
	 * @param valor Nuevo valor que va a tener el campo modificado
	 * @return Verdadero si se actualiz� alg�n archivo, falso en caso contrario
	 */
	private boolean actualizarUsuario(String nombre, String campo, String valor) {
		PreparedStatement ps;
		boolean actualizado = false;
		
		try {
			ps = conectorBD.getPreparedStatement("update usuarios set " + campo + " = ? where nombre = ?");
			ps.setString(1, valor);
			ps.setString(2, nombre);
			
			int n = ps.executeUpdate();
			ps.close();
			
			if (n > 0) 
				actualizado = true;
			
		} catch (SQLException sql) {
			actualizado = false;
			System.err.println("MySqlUsuarioDAO/actualizarUsuario: " + sql.getMessage());
		}
		return actualizado;
	}
	
	/**
	 * <p>Busca el usuario que coincida con el nombre o el uuid especificado y
	 * devuelve un objeto con esta informaci�n.</p>
	 * @param campo Campo en el que se va a basar la b�squeda, que puede ser el nombre
	 * o el uuid
	 * @param valor Valor que toma el campo
	 * @return Un objeto de tipo Usuario si hay alguna fila de la tabla que coincida
	 * el par�metro especificado. Si no, devuelve null.
	 */
	public Usuario getUsuario(String campo, String valor) {
		Usuario retorno = null;
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = conectorBD.getPreparedStatement("select * from usuarios where " + campo + " = ?");
			ps.setString(1, valor);
			rs = ps.executeQuery();
			
			if (rs.next()) 
				retorno = new Usuario(rs.getString("nombre"),rs.getString("password"),rs.getString("email"));
			
			rs.close();
			ps.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlArchivoDAO/getArchivo: " + sql.getMessage());
		}
		return retorno;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para
	 * la operaci�n de obtener la lista de archivos de un usuario. Llama al m�todo "getArchivos",
	 * privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo usuario que representa el usuario a modificar
	 * @return Lista con los archivos del usuario
	 */
	public List<Archivo> getArchivos(Usuario usuario) {
		return this.getArchivos(usuario.getNombre());
	}
	
	/**
	 * <p>Recorre la tabla de archivos y va a�adiendo en una lista auxiliar los que correspondan
	 * al usuario cuyo nombre se recibe como par�metro.</p>
	 * @param nombreUsuario Nombre del usuario del cual se quiere obtener la lista de archivos
	 * @return Lista con los archivos del usuario
	 */
	private List<Archivo> getArchivos(String nombreUsuario) {
		List<Archivo> listaArchivos = new LinkedList<Archivo>();
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = conectorBD.getPreparedStatement("select * from archivos where nombrePropietario = ?");
			ps.setString(1, nombreUsuario);
			rs = ps.executeQuery();
			
			Archivo aux = null;
			
			while (rs.next()) {
				aux = new Archivo(rs.getString("nombreArchivo"),
						rs.getString("nombrePropietario"),rs.getString("rutaArchivo"));
				listaArchivos.add(aux);
			}
			
			rs.close();
			ps.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/getArchivos: " + sql.getMessage());
		}
		return listaArchivos;
	}
	
	/**
	 * <p>M�todo interfaz de la clase con el resto de la aplicaci�n para averiguar la fecha de
	 * alta de un usuario que se recibe como par�metro. Llama al m�todo "getFecha(?,?)", privado,
	 * con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo usuario que representa al usuario del que se quiere averiguar
	 * la fecha de alta
	 * @return Devuelve un String con la fecha en formato yyyy-mm-dd
	 * @throws SQLException Posibles errores en el acceso a la base de datos
	 */
	public String getFechaAlta(Usuario usuario) throws SQLException {
		return this.getFecha(usuario.getNombre(), this.FECHA_ALTA);
	}
	
	/**
	 * <p>M�todo interfaz de la clase con el resto de la aplicaci�n para averiguar la fecha del �ltimo
	 * login de un usuario que se recibe como par�metro. Llama al m�todo "getFecha(?,?)", privado, con los
	 * par�metros necesarios</p>
	 * @param usuario Objeto de tipo usuario que representa al usuario del que se quiere averiguar la
	 * fecha del �ltimo login 
	 * @return Devuelve un String con la fecha en formato yyyy-mm-dd
	 */
	public String getUltimoLogin(Usuario usuario) throws SQLException {
		return this.getFecha(usuario.getNombre(), this.ULTIMO_LOGIN);
	}
	
	/**
	 * <p>Busca la fecha requerida como par�metro en la fila del nombre de usuario que se recibe como
	 * par�metro</p>
	 * @param nombre Nombre del usuario del que se quiere averiguar la fecha
	 * @param fechaRequerida Puede ser la fecha del alta del usuario o la del �ltimo login
	 * @return Devuelve un String con la fecha en formato yyyy-mm-dd
	 * 
	 */
	private String getFecha(String nombre, String fechaRequerida) throws SQLException {
		return this.getValor(fechaRequerida, nombre);
	}
	
	/**
	 * <p>M�todo interfaz de la clase con el resto de la aplicaci�n para averiguar el email de un usuario. 
	 * Llama al m�todo "getEmail", privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo usuario que representa al usuario del que se quiere averiguar el
	 * email
	 * @return Devuelve un String con el email del usuario
	 */
	public String getEmail(Usuario usuario) {
		return this.getEmail(usuario.getNombre());
	}
	
	/**
	 * <p>Busca el email en la fila que se corresponda con el nombre de usuario que se recibe como
	 * par�metro</p>
	 * @param nombreUsuario Nombre del usuario a buscar
	 * @return Devuelve una cadena con el email del usuario
	 */
	private String getEmail(String nombreUsuario) {
		String email = "";
		try {
			email = this.getValor(this.MAIL, nombreUsuario);
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/getEmail " + sql.getMessage());
		}
		return email;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para
	 * la operaci�n de obtener el identificar universal de un usuario. Llama al m�todo "getUuid",
	 * privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo Usuario que representa al usuario del que se quiere
	 * averiguar el uuid
	 * @return Devuelve una cadena con el uuid
	 */
	public String getUuid(Usuario usuario) {
		return this.getUuid(usuario.getNombre());
	}
	
	/**
	 * <p>Busca el uuid en la fila que se corresponda con el nombre de usuario que se recibe como
	 * par�metro</p>
	 * @param nombreUsuario Nombre del usuario a buscar
	 * @return Devuelve una cadena con el uuid del usuario
	 */
	private String getUuid(String nombreUsuario) {
		String uuid = "";
		try {
			uuid = this.getValor(this.UID, nombreUsuario);
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/getUuid " + sql.getMessage());
		}
		return uuid;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para
	 * la operaci�n de finalizar la activaci�n de la cuenta del usuario. Llama al m�todo 
	 * "activarUsuario", privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo Usuario que representa al usuario que se quiere
	 * activar
	 * @return Verdadero si la operaci�n se realiz� correctamente
	 */
	public boolean activarUsuario(Usuario usuario) {
		return this.actualizarActivado(usuario);
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para
	 * la operaci�n de comprobar el estado de la cuenta del usuario. Llama al m�todo 
	 * "estaActivado", privado, con los par�metros necesarios</p>
	 * @param usuario Objeto de tipo Usuario que representa al usuario del que se quiere
	 * saber si est� activado
	 * @return Verdadero si el usuario est� activado en el servicio, falso en cualquier
	 * caso contrario
	 */
	public boolean estaActivado(Usuario usuario) {
		return this.estaActivado(usuario.getNombre());
	}
	
	/**
	 * <p>Comprueba si un usuario est� dado de alta en el servicio, para lo cual, comprueba en
	 * la base de datos si la columna "activado" es "1"</p>
	 * @param nombre Nombre del usuario de quien queremos comprobar su estado
	 * @return Verdadero si el usuario est� dado de alta, falso en cualquier otro caso
	 */
	private boolean estaActivado(String nombre) {
		boolean activado = false;
		
		try {
			String valorTabla = this.getValor(this.ACTIVADO, nombre);
			
			if (valorTabla.compareToIgnoreCase("1") == 0)
				activado = true;
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/estaActivado " + sql.getMessage());
			activado = false;
		}
		return activado;
	}
	
	/**
	 * <p>Accede a la base de datos para buscar un valor de un usuario. El usuario en
	 * cuesti�n y el valor que se quiere averiguar vienen dados por los par�metros.</p>
	 * @param campo Campo que queremos buscar del usuario
	 * @param nombre Nombre del usuario del que queremos obtener el campo
	 * @return Devuelve una cadena con el valor requerido
	 * @throws SQLException Posibles errores en el acceso a la base de datos
	 */
	public String getValor(String campo, String nombre) throws SQLException{
		String valor = "";
		PreparedStatement ps;
		ResultSet rs;
		
		ps = conectorBD.getPreparedStatement("select " + campo + " from usuarios where nombre = ?");
		ps.setString(1, nombre);
		rs = ps.executeQuery();
		
		if (rs.next())
			valor = rs.getString(1);
		
		rs.close();
		ps.close();
		
		return valor;
	}
	
	/**
	 * <p>M�todo que hace de interfaz de la clase con el resto de la aplicaci�n para
	 * la operaci�n de comprobar si el usuario que se est� autenticando es el 
	 * administrador. Llama al m�todo "esRoot", privado, con los par�metros apropiados.</p>
	 * @param usuario Objeto de tipo Usuario que representa al administrador
	 * @return Verdadero si el usuario es el administrador
	 */
	public boolean esRoot(Usuario usuario) {
		return this.esRoot(usuario.getPassword());
	}
	
	/**
	 * <p>Comprueba si el usuario que se est� autenticando es el administrador del servicio,
	 * para lo cual compara la contrase�a introducida con la almacenada en la base de datos</p>
	 * @param usuario Objeto de tipo Usuario que representa al administrador
	 * @return Verdadero si el usuario es el administrador
	 */
	public boolean esRoot(String password) {
		boolean root = true;
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = conectorBD.getPreparedStatement("select * from usuarios where nombre = 'root' and password = ?");
			ps.setString(1, password);
			rs = ps.executeQuery();
			
			if (!rs.next())
				root = false;
			
			rs.close();
			ps.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlUsuarioDAO/esRoot " + sql.getMessage());
			root = false;
		}
		return root;
	}
	
	/**
	 * <p>Copia en una lista la informaci�n de todos los usuarios almacenados en la base de datos</p>
	 * @return Lista enlazada con las propiedades de los usuarios
	 */
	public List<Usuario> getTodosUsuarios() {
		List<Usuario> lista = new LinkedList<Usuario>();
		Statement stat;
		ResultSet rs;
		Usuario aux;
		
		try {
			stat = conectorBD.getStatement();
			rs = stat.executeQuery("select * from usuarios");
			
			while (rs.next()) {
				boolean esRoot = rs.getString("nombre").compareToIgnoreCase("root") == 0;
				boolean estaActivado = rs.getString("activado").compareToIgnoreCase("1") == 0;
				
				if (!esRoot && estaActivado) {
					aux = new Usuario(rs.getString("nombre"), rs.getString("password"),
							rs.getString("email"));
					aux.setUltimo_login(rs.getString("ultimo_login"));
					aux.setFecha_alta(rs.getString("fecha_alta"));
					lista.add(aux);
				}
			}
			
			rs.close();
			stat.close();
			
		} catch (SQLException sql) {
			System.err.println("MySqlArchivoDAO/esRoot " + sql.getMessage());
			lista = null;
		}
		return lista;
	}
 }
