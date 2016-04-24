package es.project.bd.finales;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import es.project.bd.abstractos.ArchivoDAO;
import es.project.bd.abstractos.DatosDAO;
import es.project.bd.abstractos.FactoriaDAO;
import es.project.bd.abstractos.UsuarioDAO;
import es.project.bd.configuracion.ConfigBD;

/**
 * <p>Clase que crea la conexi�n con una base de datos MySQL. Implementa el patr�n Singleton.</p>
 * @author Daniel Fern�ndez Aller
 */
public class MySqlFactoriaDAO extends FactoriaDAO {
	
	/**
	 * <p>Objeto que gestiona la conexi�n con la base de datos</p>
	 */
	private static Connection connection = null;
	
	/**
	 * <p>Variable que indica si la conexi�n ha sido creada previamente</p>
	 */
	private static boolean creado = false;
	
	/**
	 * <p>Registra el driver y crea la conexi�n con la base de datos seg�n los valores obtenidos del
	 * fichero de configuraci�n. Esta operaci�n s�lo se realizar� una vez. A partir de la primera llamada, 
	 * se devuelve siempre el objeto de tipo Connection creado la primera vez.</p>
	 * <p>Es un constructor privado para asegurar el patr�n Singleton</p>
	 */
	private MySqlFactoriaDAO () {
		if (!creado) {
			try {
		      Class.forName("com.mysql.jdbc.Driver");
		    }
		    catch (ClassNotFoundException cnfe) {
		      System.err.println("\nDriver no encontrado: " + cnfe.getMessage());
		    }

		    try {
		    	connection = DriverManager.getConnection(ConfigBD.getBDUrl() + ConfigBD.getEsquema(),
		    		ConfigBD.getUser(), ConfigBD.getPassword());
		    }
		      
		    catch (SQLException sql) {
		        System.err.println("\nImposible conectarse a la base de datos: " + sql.getMessage());
		    }

		      creado = true;
		    }
	}
	
	/**
	 * <p>M�todo est�tico que se ocupa de crear la conexi�n con la base de datos, s�lo si esta
	 * no ha sido creada previamente, para lo cual hace una llamada al constructor de la clase.</p>
	 * @return Devuelve una �nica instancia de la clase
	 */
	public static MySqlFactoriaDAO getInstance() {
		return new MySqlFactoriaDAO();
	}
	
	/**
	 * <p>Crea un objeto de tipo UsuarioDAO, particularizado para una base de datos MySQL</p>
	 * @return Devuelve un objeto de tipo MySqlUsuarioDAO (que hereda de UsuarioDAO)
	 */
	public UsuarioDAO getUsuario() {
		return new MySqlUsuarioDAO();
	}
	
	/**
	 * <p>Crea un objeto de tipo ArchivoDAO, particularizado para un base de datos MySQL</p>
	 * @return Devuelve un objeto de tipo MySqlArchivoDAO (que hereda de ArchivoDAO)
	 */
	public ArchivoDAO getArchivo() {
		return new MySqlArchivoDAO();
	}
	
	/**
	 * <p>Crea un objeto de tipo DatosDAO, particularizado para un base de datos MySQL</p>
	 * @return Devuelve un objeto de tipo MySqlDatosDAO (que hereda de DatosDAO)
	 */
	public DatosDAO getDatosDAO() {
		return new MySqlDatosDAO();
	}
	
	/**
	 * <p>Crea el objeto que gestiona los datos de la conexi�n (s�lo la primera vez) y lo
	 * devuelve</p>
	 * @return Objeto de tipo Connection con los datos de la conexi�n con la base de datos
	 * MySQL
	 */
	public static Connection getConexion() {
		if (!creado) 
			new MySqlFactoriaDAO();
		return connection;
		
	}

}
