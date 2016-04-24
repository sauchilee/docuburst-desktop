package es.project.utilidades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>Clase que lee el contenido de un archivo que recibe como par�metro y lo convierte en una cadena
 * de caracteres</p>
 * @author Daniel Fern�ndez Aller
 */
public class ArchivoATexto {
	
	/**
	 * <p>Crea java.io.BufferedReader a partir del archivo que recibe como par�metro, y lo utiliza
	 * para leer l�nea a l�nea el contenido de �ste</p>
	 * @param file java.io.File: archivo a leer
	 * @return String que contiene el texto del archivo
	 */
	public static String getTexto(File file) {
		String texto = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
			while (br.ready()) 
				texto += br.readLine();
			
			br.close();
			
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return texto;
	}
}
