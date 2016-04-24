package es.project.algoritmo.configuracion;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * <p>Clase que accede al fichero de propiedades que contiene la informaci�n referente a los
 * par�metros de funcionamiento del algoritmo. Tambi�n contiene las extensiones de archivos
 * permitidas por el algoritmo.</p>
 * @author Daniel Fern�ndez Aller
 */
public class ConfigAlgoritmo {

	private static ResourceBundle propiedades = ResourceBundle.getBundle("es.project.utilidades.algoritmo");
	private static Set<String> conjunto = new HashSet<String>();
	private static boolean conjuntoCargado = false;
	
	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve una cadena con el par�metro "window"
	 */
	public static String getWindow() {
		return propiedades.getString("window");
	}
	
	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve una cadena con el par�metro "step"
	 */
	public static String getStep() {
		return propiedades.getString("step");
	}
	
	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve una cadena con la ruta de la lista "stopwords"
	 */
	public static String getStopwordsPath() {
		return propiedades.getString("stopwordsPath");
	}
	
	/**
	 * <p>Accede al conjunto de las extensiones permitidas por el algoritmo. Si es la
	 * primera vez que se utiliza el m�todo, se inicializa el conjunto.</p>
	 * @return Devuelve un conjunto (java.util.Set) con las extensiones permitidas 
	 */
	public static Set<String> getExtensiones() {
		if (!conjuntoCargado) {
			conjunto.add("txt");
			conjunto.add("rtf");
			conjunto.add("doc");

			conjuntoCargado = true;
		}
		return conjunto;
	}
}
