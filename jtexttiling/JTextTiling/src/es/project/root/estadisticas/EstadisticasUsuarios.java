package es.project.root.estadisticas;

import java.util.List;

import es.project.bd.objetos.Usuario;
import es.project.facade.FacadeBD;

/**
 * <p>Clase que implementa la interfaz "Estadisticas" y permite al usuario administrador
 * obtener la lista completa de los usuarios y el n�mero de �stos</p>
 * @author Daniel Fern�ndez Aller
 */
public class EstadisticasUsuarios implements Estadisticas{
	
	private FacadeBD facadeBD = new FacadeBD();
	
	/**
	 * <p>Devuelve una lista conteniendo todos los usuarios presentes en la aplicaci�n</p>
	 */
	public List<Usuario> getLista() {
		return facadeBD.getTodosUsuarios();
	}
	
	/**
	 * <p>Devuelve el n�mero total de usuarios presentes en la aplicaci�n</p>
	 */
	public int getNum() {
		return facadeBD.getNumeroUsuarios();
	}
}
