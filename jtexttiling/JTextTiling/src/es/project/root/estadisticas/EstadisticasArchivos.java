package es.project.root.estadisticas;

import java.util.List;

import es.project.bd.objetos.Archivo;
import es.project.facade.FacadeBD;

/**
 * <p>Clase que implementa la interfaz "Estadisticas" y permite al usuario administrador
 * obtener la lista completa de los archivos de los usuarios y el n�mero de �stos</p>
 * @author Daniel Fern�ndez Aller
 */
public class EstadisticasArchivos implements Estadisticas{
	
	private FacadeBD facadeBD = new FacadeBD();
	
	/**
	 * <p>Devuelve una lista conteniendo todos los archivos presentes en la aplicaci�n</p>
	 */
	public List<Archivo> getLista() {
		return facadeBD.getTodosArchivos();
	}

	/**
	 * <p>Devuelve el n�mero total de archivos presentes en la aplicaci�n</p>
	 */
	public int getNum() {
		return facadeBD.getNumeroArchivos();
	}
}
