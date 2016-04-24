package es.project.bd.abstractos;

import es.project.bd.objetos.Usuario;

/**
 * <p>Clase abstracta con las declaraciones de los m�todos referidos a las operaciones
 * que se realizan con los datos sobre los logins y las altas en la base de datos</p>
 * @author Daniel Fern�ndez Aller
 */
public abstract class DatosDAO {

	/**
	 * <p>Actualiza el nombre del �ltimo usuario que ha utilizado el servicio</p>
	 * @param usuario Objeto que representa al �ltimo usuario que ha utilizado
	 * el servicio
	 * @return Verdadero si la operaci�n sali� bien, falso en caso contrario
	 */
	public abstract boolean actualizarUltimoLogin(Usuario usuario);
	
	/**
	 * <p>Actualiza el nombre del �ltimo usuario que se ha dado de alta en el
	 * servicio</p>
	 * @param usuario Objeto que representa al �ltimo usuario dado de alta en el
	 * servicio
	 * @return Verdadero si la operaci�n sali� bien, falso en caso contrario
	 */
	public abstract boolean actualizarUltimaAlta(Usuario usuario);
	
	/**
	 * <p>Accede al nombre del �ltimo usuario en loguearse</p>
	 * @return Objeto usuario que representa el �ltimo login en el servicio
	 */
	public abstract Usuario getUltimoLogin();
	
	/**
	 * <p>Accede al nombre del �ltimo usuario en darse de alta</p>
	 * @return Objeto usuario que representa al �ltimo usuario que se ha dado
	 * de alta en el servicio
	 */
	public abstract Usuario getUltimaAlta();
}
