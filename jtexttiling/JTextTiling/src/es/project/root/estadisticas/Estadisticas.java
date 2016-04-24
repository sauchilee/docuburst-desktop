package es.project.root.estadisticas;

import java.util.List;

/**
 * <p>Interfaz que van a implementar las clases que permiten al root obtener las estad�sticas
 * de los usuarios y los archivos</p>
 * @author Daniel Fern�ndez Aller
 */
public interface Estadisticas {
	
	/**
	 * <p>Recorre, en cada caso, la tabla de usuarios o la de archivos, y devuelve una lista que
	 * contendr� todos los elementos</p>
	 * @return Lista de usuarios o de archivos
	 */
	public abstract List getLista();
	
	/**
	 * <p>Hace un recuento del n�mero de usuarios o archivos, seg�n el caso</p>
	 * @return N�mero total de usuarios o de archivos
	 */
	public abstract int getNum();
	
}
