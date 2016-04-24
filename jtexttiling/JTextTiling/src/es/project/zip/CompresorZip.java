package es.project.zip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import es.project.bd.objetos.Archivo;
import es.project.facade.FacadeBD;
import es.project.ficheros.configuracion.ConfigFicheros;

/**
 * <p>Esta clase crea un zip a partir de un archivo cuya ruta se pasa como par�metro. S�lo
 * funciona para directorios que contengan �nicamente ficheros simples (no pueden contener
 * otros directorios)</p>
 * @author Daniel Fern�ndez Aller
 */
public class CompresorZip {
	
	private FileInputStream entrada;
	private ZipOutputStream zos;
	private FacadeBD facadeBD;
	
	/**
	 * <p>Recibe la ruta del archivo a comprimir y obtiene la lista de sus "hijos".
	 * Recorre esta lista y, para cada uno de los ficheros simples, realiza el 
	 * proceso de compresi�n mediante el m�todo privado "realizarCompresion". Finalmente,
	 * actualiza la base de datos asignando el archivo creado al usuario propietario.</p>
	 * @param rutaFuente Ruta del directorio a comprimir
	 * @param rutaCompresion Ruta donde se va a colocar el archivo comprimido
	 * @param nombreUsuario Nombre del usuario propietario del archivo comprimido
	 * @throws IOException java.io.IOException
	 */
	public void comprimirArchivo(String rutaFuente, String rutaCompresion, String nombreUsuario,
			String nombreZip) 
		throws IOException {
		
		File inicial = new File(rutaFuente);
		String lista[] = inicial.list();
		
		if (lista != null) {
			zos = new ZipOutputStream(new FileOutputStream(new File(rutaCompresion)));
		
			for (int i = 0; i < lista.length; i++) {
				String rutaNueva = rutaFuente + ConfigFicheros.getSeparador() + lista[i];
				File aux = new File(rutaNueva);
				realizarCompresion(aux.getName(), rutaNueva, rutaCompresion);
			}
			
			actualizarBD(nombreZip,nombreUsuario,rutaCompresion);
			cerrarConexion();
		}
	}
	
	/**
	 * <p>Inserta el nuevo archivo (el archivo zip) en la base de datos, indicando su nombre,
	 * su propietario y su ruta</p>
	 * @param nombreArchivo Nombre del archivo a insertar
	 * @param nombrePropietario Nombre del propietario del archivo a insertar
	 * @param ruta Ruta del archivo a insertar
	 */
	private void actualizarBD(String nombreArchivo, String nombrePropietario, String ruta) {
		facadeBD = new FacadeBD();
		Archivo aux = new Archivo(nombreArchivo, nombrePropietario, ruta);
		facadeBD.insertarArchivo(aux);
	}
	
	/**
	 * <p>M�todo que realiza la compresi�n del fichero que recibe como par�metro, lo a�ade al fichero
	 * zip y lo escribe mediante el ZipOutputStream en la direcci�n</p>
	 * @param nombre Nombre del fichero a comprimir
	 * @param rutaFuente Ruta donde se encuentra el archivo a comprimir
	 * @param rutaCompresion Ruta donde se va a colocar el archivo comprimido
	 * @throws IOException java.io.IOException
	 */
	private void realizarCompresion(String nombre, String rutaFuente, String rutaCompresion) 
		throws IOException {
		
		entrada = new FileInputStream(new File(rutaFuente));
		ZipEntry entry = new ZipEntry(nombre);
		zos.putNextEntry(entry);
			
		BufferedReader br = new BufferedReader(new InputStreamReader(entrada));
			
		while (br.ready()) 
			zos.write(br.read());
			
		br.close();
		entrada.close();
	}
	
	/**
	 * <p>Cierra el ZipOutputStream</p>
	 * @throws IOException java.io.IOException
	 */
	private void cerrarConexion() throws IOException {
		zos.close();
	}
}
