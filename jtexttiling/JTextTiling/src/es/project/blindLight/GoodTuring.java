package es.project.blindLight;

import java.util.ArrayList;
import java.util.Iterator;

import es.project.utilidades.QuickSort;
import es.project.utilidades.RectaRegresion;
import es.project.utilidades.RectaRegresionException;

/**
 * <p>Clase que encapsula las operaciones del estimador Simple Good-Turing</p>
 * @author Daniel Fern�ndez Aller
 */
public class GoodTuring {
	/**
	 * 1� columna r: frecuencias observadas en la muestra
	 * 2� columna n: n�mero de especies observadas con frecuencia r
	 */
	private int[][] arrayFrecuenciasInicial, arrayFrecuenciasFinal;
	private int indiceR = 0;
	private ArrayList<NGrama> lista;
	
	/**
	 * columnas: Z, log r, log Z, r*, p
	 */
	private float[][] arrayReales;
	private int N, NPrima;
	private double P0;
	
	/**
	 * coeficientes para la recta de regresi�n
	 */
	private float a, b;
	
	/**
	 * <p>Constructor de la clase: inicializa los atributos necesarios</p>
	 * @param listaNGramas Lista de n-gramas a la que se van a aplicar las operaciones del
	 * estimador Simple Good-Turing
	 */
	public GoodTuring(ArrayList<NGrama> listaNGramas) {
		this.lista = listaNGramas;
		int size = listaNGramas.size();
		arrayFrecuenciasInicial = new int[size][2];
	}
	
	/* <OPERACIONES PARA COMPONER EL PRIMER VECTOR DEL M�TODO BLINDLIGHT> */
	/**
	 * <p>Inicia el estimador, para lo cual necesitamos rellenar el array de frecuencias
	 * y "frecuencias de frecuencias"</p>
	 */
	public void componerPrimerVector() {
		this.rellenarArray(lista);
	}
	
	/**
	 * <p>Recorre la lista de n-gramas para ir calculando su frecuencia absoluta e ir
	 * insert�ndolas en el array que almacenar� las frecuencias de los n-gramas, y el
	 * n�mero de n-gramas que comparten esa frecuencia ("frecuencias de frecuencias"):
	 * si la frecuencia es nueva, se inserta, si est� repetida, se aumenta su frecuencia
	 * de frecuencia.</p>
	 * @param lista Lista de n-gramas con la que vamos a trabajar
	 */
	private void rellenarArray(ArrayList<NGrama> lista) {
		Iterator<NGrama> i = lista.iterator();
		NGrama aux = null;
		int indiceAuxiliar = -1;
		int frecuenciaAbsoluta = -1;
		
		while (i.hasNext()) {
			aux = i.next();
			frecuenciaAbsoluta = aux.getFrecuenciaAbsoluta();
			indiceAuxiliar = buscarIndiceFrecuencia(frecuenciaAbsoluta);
			
			/* esto quiere decir que la frecuencia es nueva */
			if (indiceAuxiliar == -1) {
				insertarNuevaFrecuencia(frecuenciaAbsoluta);
			}
			/* si la frecuencia estaba ya en el array, aumentamos la n en 1 */
			else {
				aumentarValorN(indiceAuxiliar);
			}
		}
		recortarArray();
	}
	
	/**
	 * <p>Si una frecuencia ya estaba incluida en el array, hay que aumentar el
	 * valor de su "frecuencia de frecuencia" (esto es, el valor de la n para un
	 * r dado)</p>
	 * @param indiceAuxiliar �ndice donde se encuentra la frecuencia en cuesti�n
	 */
	private void aumentarValorN(int indiceAuxiliar) {
		int valorAnterior = arrayFrecuenciasInicial[indiceAuxiliar][1];
		arrayFrecuenciasInicial[indiceAuxiliar][1] = ++valorAnterior;
	}

	/**
	 * <p>Se inserta una nueva frecuencia (con lo cual su frecuencia de frecuencia
	 * ser� 1)</p>
	 * @param frecuenciaAbsoluta Frecuencia a insertar
	 */
	private void insertarNuevaFrecuencia(int frecuenciaAbsoluta) {
		arrayFrecuenciasInicial[indiceR][0] = frecuenciaAbsoluta;
		arrayFrecuenciasInicial[indiceR][1] = 1;
		indiceR++;
	}
	
	/**
	 * <p>Buscamos si la frecuencia ya estaba incluida en el array, en cuyo
	 * caso nos quedamos con su posici�n. Si no estaba, se devuelve -1.</p>
	 * @param frecuencia Frecuencia a buscar en el array
	 * @return Devuelve la posici�n de la frecuencia buscada, y -1 si no existe
	 */
	private int buscarIndiceFrecuencia(int frecuencia){
		for (int i = 0; i < indiceR; i++)
			if (arrayFrecuenciasInicial[i][0] == frecuencia)
				return i;
		
		return -1;
	}
	
	/**
	 * <p>Como el array se inicializa con el tama�o de la lista, siempre van a quedar
	 * muchas posiciones vac�as, por lo tanto hay que recortarlo: se copian los elementos
	 * en otro array con el tama�o exacto.</p>
	 */
	private void recortarArray() {
		int size = 0;
		for (int i = 0; i < arrayFrecuenciasInicial.length; i++)
			if (arrayFrecuenciasInicial[i][0] == 0) {
				size = i;
				i = arrayFrecuenciasInicial.length;
			}
		
		arrayFrecuenciasFinal = new int[size][2];
		for (int i = 0; i < arrayFrecuenciasFinal.length; i++)
			for (int j = 0; j < arrayFrecuenciasFinal[0].length; j++)
				arrayFrecuenciasFinal[i][j] = arrayFrecuenciasInicial[i][j];
		
		ordenarArray();
		calcularN();
		calcularP0();
		
		arrayReales = new float[arrayFrecuenciasFinal.length][5];
	}
	
	/**
	 * <p>P<sub>0</sub> = n1/N, donde n1 es el valor n correspondiente a la primera fila del array</p>
	 */
	private void calcularP0() {
		P0 = (arrayFrecuenciasFinal[0][1]/(double)N);
	}
	
	/**
	 * <p>N es el sumatorio de cada r<sub>i</sub> * n<sub>i</sub> (donde i va desde 0 hasta la �ltima fila
	 * del array)</p>
	 */
	private void calcularN() {
		for (int i = 0; i < arrayFrecuenciasFinal.length; i++)
				N += (arrayFrecuenciasFinal[i][0] * arrayFrecuenciasFinal[i][1]);
	}
	
	/**
	 * <p>Para poder aplicar el m�todo Simple Good-Turing, las frecuencias tienen que
	 * estar ordenadas ascendentemente, pero a la hora de calcularlas a partir de la
	 * lista de n-gramas puede que se encuentren desordenadas: se aplica el m�todo de
	 * ordenaci�n QuickSort al array.</p>
	 */
	private void ordenarArray() {
		QuickSort qs = new QuickSort(arrayFrecuenciasFinal);
		
		if (arrayFrecuenciasFinal.length >= 20)
			qs.quicksort(0, (arrayFrecuenciasFinal.length - 1));
		else qs.insercionDirecta();
		
		arrayFrecuenciasFinal = qs.getArray();
	}
	/* </OPERACIONES PARA COMPONER EL PRIMER VECTOR DEL M�TODO BLINDLIGHT> */
	
	/* <SEGUNDO VECTOR DEL M�TODO BLINDLIGHT> */
	/**
	 * <p>En este m�todo se rellena el otro array que utilizamos en la aplicaci�n del
	 * estimador Simple Good-Turing. Los valores en la columna Z (la primera) se insertan
	 * de la siguiente manera: para cada fila j, i y k son los valores anterior y posterior
	 * respectivamente, de la columna r (perteneciente al primer array). Si j es la primera
	 * fila, i = 0, y si es la �ltima, k = 2*j - i. Finalmente Z<sub>j</sub> = 2 * n<sub>j</sub>/(k - i).
	 * A la vez, se van calculando los valores de las siguientes columnas: log r y log Z.
	 * Por �ltimo, se llama al m�todo que calcula los coeficientes a y b de la recta de regresi�n,
	 * necesarios para obtener el valor de la cuarta columna, la r aproximada. </p>
	 */
	public void componerSegundoVector() {
		int i, k = 0;
		for (int j = 0; j < arrayReales.length; j++) {
			if (j == 0)
				i = 0;
			else i = arrayFrecuenciasFinal[(j-1)][0];
			
			if (j == (arrayReales.length - 1))
				k = (2*j) - i;
				//k = (2*j) - 1;
			else k = arrayFrecuenciasFinal[(j+1)][0];
			
			int nj = arrayFrecuenciasFinal[j][1];
			float numerador = (2 * nj);
			float denominador = (k-i);
			arrayReales[j][0] = (numerador/denominador);
			arrayReales[j][1] = (float)Math.log10(arrayFrecuenciasFinal[j][0]);
			arrayReales[j][2] = (float)Math.log10(arrayReales[j][0]);
		}
		
		calcularCoeficientesRecta();
		calcularRAproximada();
	}
	
	/**
	 * <p>Calcula la recta de regresi�n para los valores de las columnas log r y log Z.
	 * De aqu� se obtienen los coeficientes a y b, necesarios para el c�lculo de la
	 * funci�n antilog. </p>
	 */
	private void calcularCoeficientesRecta() {
		int size = arrayReales.length;
		float[] x = new float[(size-1)];
		float[] y = new float[(size-1)];
		
		for (int i = 0; i < (size-1); i++) {
			x[i] = arrayReales[i][1];
			y[i] = arrayReales[i][2];
		}

		try {
			RectaRegresion rr = new RectaRegresion(x,y);
			rr.calcularRectaRegresion();
			this.a = rr.getB0();
			this.b = rr.getB1();
		} catch (RectaRegresionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Rellena la columna correspondiente a la r aproximada (el pen�ltimo paso del algoritmo).
	 * Para ello recorremos el array desde la primera fila, y para cada valor de r seguimos el proceso
	 * descrito <a href=http://petra.euitio.uniovi.es/~i6952349/pmwiki/pmwiki.php?n=Main.Procedimiento>aqu�</a>
	 * </p>
	 * <p>El siguiente paso es calcular N', que ser� el resultado del sumatorio de los productos n<sub>r</sub> * r*.
	 * Por �ltimo, calculamos el valor del estimador para cada fila: (1-P<sub>0</sub>) * r*\/N'. El valor
	 * p<sub>r</sub> de esta columna ser� el estimador SGT para la frecuencia de poblaci�n de una especie cuya
	 * frecuencia en la muestra sea r.</p>
	 */
	private void calcularRAproximada() {
		boolean cumpleInecuacion = true;
		float x, y = 0.0f;
		float valorFinal = 0.0f;
		
		for (int i = 0; i < arrayFrecuenciasFinal.length; i++) {
			int r = arrayFrecuenciasFinal[i][0];
			y = this.calcularY(r);
			
			if (cumpleInecuacion) {
				x = this.calcularX(r);
				if (Math.abs(x - y) > this.calcularValorInecuacion(r))
					valorFinal = x;
				else {
					valorFinal = y;
					cumpleInecuacion = false;
				}
			} 
			else valorFinal = y;
			
			arrayReales[i][3] = valorFinal;
		}
		
		this.calcularNPrima();
		calcularEstimadorFinal();
	}
	
	/**
	 * <p>Calcula el valor X durante la ejecuci�n del m�todo "calcularRAproximada". </p>
	 * @param r Frecuencia actual 
	 * @return Valor X seg�n la ecuaci�n (3) 
	 * explicada <a href="http://petra.euitio.uniovi.es/~i6952349/pmwiki/pmwiki.php?n=Main.Procedimiento">aqu�</a>
	 */
	private float calcularX(int r) {
		float numerador = arrayFrecuenciasFinal[r][1];
		float denominador = arrayFrecuenciasFinal[(r-1)][1];
		return (r + 1) * (numerador/denominador);
	}
	
	/**
	 * <p>Calcula el valor X durante la ejecuci�n del m�todo "calcularRAproximada". </p>
	 * @param r Frecuencia actual
	 * @return Valor X seg�n la ecuaci�n (4) 
	 * explicada <a href="http://petra.euitio.uniovi.es/~i6952349/pmwiki/pmwiki.php?n=Main.Procedimiento">aqu�</a>
	 */
	private float calcularY(int r) {
		return (r+1)*(antilog(r+1)/antilog(r));
	}
	
	/**
	 * <p>Calcula el valor de la funci�n antilog, necesario para averiguar el valor de Y</p>
	 * @param r Frecuencia actual
	 * @return Valor seg�n la funci�n 10<sup>(a + b*log r)</sup>
	 */
	private float antilog (int r) {
		float exponente = (float)(this.a + (this.b * Math.log10(r)));
		return (float)Math.pow(10, exponente);
	}
	
	/**
	 * <p>Calcula el valor de la inecuaci�n durante la ejecuci�n del m�todo "calcularRAproximada"</p>
	 * @param r Frecuencia actual
	 * @return Valor seg�n la inecuaci�n (5) 
	 * explicada <a href="@return Valor X seg�n la ecuaci�n (3) explicada <a href="http://petra.euitio.uniovi.es/~i6952349/pmwiki/pmwiki.php?n=Main.Procedimiento">aqu�</a>">aqu�</a>
	 */
	private float calcularValorInecuacion(int r) {
		float numerador1 = arrayFrecuenciasFinal[r][1];
		float denominador1 = arrayFrecuenciasFinal[(r-1)][1];
		float numerador2 = numerador1;
		float denominador2 = denominador1;
		
		return (float)(1.96 * Math.sqrt(Math.pow((r+1), 2) * (numerador1/Math.pow(denominador1, 2)) * (1 + (numerador2/denominador2))));
	}
	
	/**
	 * <p>Calcula N': esto es, el sumatorio de n<sub>r</sub> * r*</p>
	 */
	private void calcularNPrima() {
		for (int i = 0; i < arrayFrecuenciasFinal.length; i++)
			NPrima += arrayFrecuenciasFinal[i][1]*arrayReales[i][3];
	}
	
	/**
	 * <p>Asigna a cada frecuencia su estimador SGT: (1-P<sub>0</sub>) * r*\/N'</p>
	 */
	private void calcularEstimadorFinal() {
		for (int i = 0; i < arrayFrecuenciasFinal.length; i++)
			arrayReales[i][4] = (float)((1 - this.getP0())*(arrayReales[i][3]/this.getNPrima()));
	}
	/* </SEGUNDO VECTOR DEL M�TODO BLINDLIGHT> */
	
	/**
	 * <p>Recibe una lista con los n-gramas de un texto, y le asigna a cada uno la probabilidad
	 * estimada que le corresponde. Para ello, busca el n-grama en base a su frecuencia absoluta
	 * (ver informaci�n del m�todo "buscarProbabilidad")</p>
	 * @param lista Lista de n-gramas a los cuales vamos a asignar su estimador de probabilidad
	 * @return Devuelve una lista de n-gramas con sus probabilidades estimadas
	 * @throws NGramaException Esta excepci�n se lanza si la longitud del texto de un n-grama no 
	 * coincide con el tama�o de n-grama estipulado 
	 */
	public ArrayList<NGrama> cruzarListas(ArrayList<NGrama> lista) throws NGramaException{
		Iterator<NGrama> i = lista.iterator();
		ArrayList<NGrama> listaNueva = new ArrayList<NGrama>();
		NGrama nuevo;
		
		while (i.hasNext()) {
			NGrama aux = i.next();
			float f = buscarProbabilidad(aux);
			nuevo = aux;
			nuevo.setProbabilidadEstimada(f);
			listaNueva.add(nuevo);
		}
		
		return listaNueva;
	}
	
	/**
	 * <p>Busca, en el array de frecuencias, la frecuencia absoluta del n-grama que recibe
	 * como par�metro. Una vez obtenido el �ndice donde se encuentra esta frecuencia, busca
	 * la probabilidad que le corresponde en el segundo array, donde se almacena los valores
	 * del estimador SGT</p>
	 * @param aux N-grama del que queremos obtener su probabilidad estimada
	 * @return Valor de la probabilidad estimada del n-grama
	 */
	private float buscarProbabilidad(NGrama aux) {
		int frecuenciaAbsoluta = aux.getFrecuenciaAbsoluta();
		float probabilidadEstimada = 0.0f;
		
		for (int i = 0; i < arrayFrecuenciasFinal.length; i++)
			if (arrayFrecuenciasFinal[i][0] == frecuenciaAbsoluta) {
				probabilidadEstimada = arrayReales[i][4];
				i = (arrayFrecuenciasFinal.length + 1);
			}
		
		return probabilidadEstimada;
	}
	
	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve el sumatorio de cada r<sub>i</sub> * n<sub>i</sub>
	 */
	public int getN() {
		return N;
	}
	
	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve el sumatorio de cada n<sub>r</sub> * r*
	 */
	public float getNPrima() {
		return NPrima;
	}
	
	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve P<sub>0</sub> = n1/N
	 */
	public double getP0() {
		return P0;
	}
	
	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve el array de frecuencias - frecuencias de frecuencias
	 */
	public int[][] getArrayFrecuencias() {
		return arrayFrecuenciasFinal;
	}
	
	/**
	 * <p>Accede al atributo</p>
	 * @return Devuelve el segundo array, que contiene el valor del estimador SGT
	 */
	public float[][] getArrayReales() {
		return arrayReales;
	}
	
	/**
	 * <p>Compone una cadena con los valores del array de frecuencias y frecuencias de
	 * frecuencias</p>
	 * @return Devuelve una cadena con la informaci�n explicada
	 */
	public String verArrayFrecuencias() {
		String retorno = "r\tn";
		for (int i = 0; i < arrayFrecuenciasFinal.length; i++) {
			retorno += "\n";
			for (int j = 0; j < arrayFrecuenciasFinal[0].length; j++)
				retorno += arrayFrecuenciasFinal[i][j] + "\t";
		}
		return retorno;
	}
	
	/**
	 * <p>Compone una cadena con los valores r* y p, una vez ya ha sido calculado el
	 * estimador SGT</p>
	 * @return Devuelve una cadena con la informaci�n explicada
	 */
	public String verArrayReales() {
		//String retorno = "Z\t\tlog r\t\tlog Z\t\tr*\t\tp";
		String retorno = "r*\t\tp";
		for (int i = 0; i < arrayReales.length; i++) {
			retorno += "\n";
			for (int j = 3; j < arrayReales[0].length; j++)
				retorno += arrayReales[i][j] + "\t\t";
		}
		return retorno;
	}
}
