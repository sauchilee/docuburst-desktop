package es.project.bd.abstractos;

import es.project.bd.finales.MySqlFactoriaDAO;

/**
 * <p>Clase que crea la conexi�n con la base de datos seg�n el tipo especificado</p>
 * @author Daniel Fern�ndez Aller
 */
public abstract class FactoriaDAO {
	private boolean esTest = false;
	
    /**
	 * <p>Devuelve un objeto heredado de UsuarioDAO, que manejar� las operaciones con usuarios
	 * de la base de datos especificada</p>
	 * @return Objeto derivado de UsuarioDAO
	 */
	public abstract UsuarioDAO getUsuario();
	
	/**
	 * <p>Devuelve un objeto heredado de ArchivoDAO, que manejar� las operaciones con archivos
	 * de la base de datos especificada</p>
	 * @return Objeto derivado de ArchivoDAO
	 */
	public abstract ArchivoDAO getArchivo();
	
	/**
	 * <p>Devuelve un objeto heredado de DatosDAO, que manejar� las operaciones con archivos
	 * de la base de datos especificada</p>
	 * @return Objeto derivado de DatosDAO
	 */
	public abstract DatosDAO getDatosDAO();
	
	/**
	 * <p>Variable global que se le asigna a la base de datos MySQL. Estas variables son necesarias
	 * para elegir la conexi�n con la base de datos que se crea con el m�todo "getFactoriaDAO"</p>
	 */
	public static final int mySql = 1;
	
	/**
	 * <p>Devuelve un objeto heredado de FactoriaDAO, que maneja la conexi�n con la base de datos
	 * especificada por el par�metro</p>
	 * @param factoria N�mero entero que determina el tipo de base de datos con la que se va a crear
	 * la conexi�n
	 * @return Objeto derivado de FactoriaDAO
	 */
	public static FactoriaDAO getFactoriaDAO (int factoria) {
		switch (factoria) {
		
		case mySql:
			return MySqlFactoriaDAO.getInstance();
		default: 
			return null;
		}
	}

	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve el valor de la variable booleana "esTest", que indica si la acci�n que se
	 * est� realizando es perteneciente a las bater�as de pruebas de la aplicaci�n
	 */
	public boolean isEsTest() {
		return esTest;
	}

	/**
	 * <p>Da valor al atributo</p>
	 * @param esTest Valor que indica si la acci�n que se est� realizando es perteneciente a las 
	 * bater�as de pruebas de la aplicaci�n
	 */
	public void setEsTest(boolean esTest) {
		this.esTest = esTest;
	}
}
