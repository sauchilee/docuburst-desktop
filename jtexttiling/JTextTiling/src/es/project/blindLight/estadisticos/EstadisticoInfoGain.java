package es.project.blindLight.estadisticos;

import java.util.ArrayList;

import es.project.blindLight.DescomposicionNGrama;
import es.project.blindLight.NGrama;

/**
 * <p>Clase que realiza los c�lculos referidos al estad�stico conocido como "InfoGain".
 * La f�rmula puede verse <a href="http://petra.euitio.uniovi.es/~i6952349/pmwiki/pmwiki.php?n=Main.Estadisticos">aqu�</a></p>
 * @author Daniel Fern�ndez Aller
 */
public class EstadisticoInfoGain extends EstadisticoPonderacion {
	
	/**
	 * <p>Constructor de la clase</p>
	 */
	protected EstadisticoInfoGain() {
		super();
	}

	/**
	 * <p>Calcula la significatividad del n-grama seg�n la f�rmula explicada. Para
	 * ello, se basa en varios m�todos de su clase base.</p>
	 * @param ngrama N-grama del cual daremos un c�lculo del peso
	 * @param probabilidad Probabilidad estimada mediante Good-Turing del n-grama 
	 * (no se utiliza en este m�todo)
	 * @param listaDesc Lista de fragmentos de los n-gramas del texto
	 * @return El resultado es la significatividad (peso) del n-grama en el texto
	 * calculado mediante el estimador InfoGain
	 */
	public float calcularEstadistico(NGrama ngrama, 
			float probabilidad, ArrayList<DescomposicionNGrama> listaDesc) {
		
		float retorno = 0.0f;
		float acumulador = 0.0f;
		int longitud = ngrama.getLongitud();;
		DescomposicionNGrama aux1, aux2;
		String texto = ngrama.getTexto();
		
		for (int i = 1; i < longitud; i++) {
			aux1 = new DescomposicionNGrama(texto.substring(0, i));
			aux2 = new DescomposicionNGrama(texto.substring(i, longitud));
			float p1 = super.buscarCaracteristica(aux1, listaDesc, true);
			float p2 = super.buscarCaracteristica(aux2, listaDesc, true);
			float termino1 = this.calcularTermino(p1);
			float termino2 = this.calcularTermino(p2);
			
			acumulador += (termino1 + termino2);
		}
		
		retorno = acumulador/(longitud-1);
		return retorno;
	}
	
	/**
	 * <p>Realiza el c�lculo de los t�rminos del sumatorio utilizado en el
	 * c�lculo del m�todo InfoGain</p>
	 * @param probabilidad Probabilidad del fragmento
	 * @return Valor real
	 */
	private float calcularTermino(float probabilidad) {
		float retorno = 0.0f;
		float inverso = (1/probabilidad);
		float logaritmo = (float)Math.log10(inverso);
		retorno = logaritmo * probabilidad;
		return retorno;
	}
}
