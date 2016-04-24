package es.project.mail;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import es.project.bd.objetos.Usuario;
import es.project.mail.configuracion.ConfigMail;

/**
 * <p>Clase abstracta que implementa las funcionalidades b�sicas para enviar emails</p>
 * @author Daniel Fern�ndez Aller
 */
public abstract class Mail {
	private String user = ConfigMail.getFrom();
	private String password = ConfigMail.getPassword();
	private String port = ConfigMail.getPuerto();
	private String host = ConfigMail.getHost();
	private String subject = ConfigMail.getAsunto();
	protected String rutaXml = ConfigMail.getRutaXml();
	protected String rutaHtml = ConfigMail.getRutaHtml();
	
	protected Message mensaje;
	protected Multipart mp;

	/**
	 * <p>Interfaz de la clase con el resto de la aplicaci�n: encapsula la serie de pasos
	 * que se siguen a la hora de enviar los correos</p>
	 * @param usuario Objeto que representa al usuario que va a recibir el mail
	 * @throws MessagingException Posibles errores en el env�o del mensaje
	 */
	public abstract void enviarMail(Usuario usuario) throws MessagingException;
	
	/**
	 * <p>Borra archivos residuales derivados de la creaci�n del fichero html que se
	 * enviar� en el cuerpo del correo.</p>
	 */
	protected abstract void borrarArchivos();
	
	/**
	 * <p>Crea los objetos necesarios para enviar el mensaje e inicializa las propiedades
	 * seg�n los valores obtenidos del fichero de propiedades.</p>
	 * <p>Como �ltimo paso, adjunta una imagen que ser� la cabecera del mensaje</p>
	 * @param usuario Objeto que representa al usuario que va a recibir el mail
	 * @throws MessagingException Posibles errores en el env�o del mensaje
	 */
	protected void pasosInicialesMail(Usuario usuario) throws MessagingException {
		Properties props = System.getProperties();
		this.setProperties(props);
		Authenticator auth = new SMTPAuthenticator();
		Session sesion = Session.getInstance(props, auth);
		
		mensaje = new MimeMessage(sesion);
		mensaje.setSubject(subject);
		mensaje.setFrom(new InternetAddress(user));
		mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(usuario.getEmail()));
		mp = new MimeMultipart();
	    //this.adjuntarArchivo(rutaImagen);
	}
	
	/**
	 * <p>Crea el texto personalizado de cada mensaje y llama al m�todo "crearCuerpo", que se
	 * encarga de incluirlo en el mensaje</p>
	 * @param usuario Objeto que representa al usuario que va a recibir el mail
	 * @param mensaje Mensaje en el que se incluir� el texto
	 * @param mp Objeto contenedor en el que se incluyen las partes del mail
	 * @throws MessagingException Posibles errores en el env�o del mensaje
	 */
	protected abstract void crearTextoMail(Usuario usuario, Message mensaje, Multipart mp)
		throws MessagingException;
	
	/**
	 * <p>A�ade el texto e indica que el mensaje es de tipo "Multipart", para permitir que contenga
	 * tanto im�genes como texto en formato html</p>
	 * @param mensaje Mensaje en el que a�adimos el texto
	 * @param mp Objeto contenedor en el que se incluyen las partes del mail: en este caso, se le
	 * a�ade el texto y se indica el tipo de mensaje
	 * @param texto Texto del mensaje
	 * @throws MessagingException Posibles errores en el env�o del mensaje
	 */
	protected void crearCuerpo(Message mensaje, Multipart mp, String texto) throws MessagingException{
		BodyPart bodyText = new MimeBodyPart();
		bodyText.setContent(texto, "text/html");
		mp.addBodyPart(bodyText);
		mensaje.setContent(mp, "multipart/mixed");
	}
	
	/**
	 * <p>Adjunta un archivo en el mensaje, en base a la ruta que se recibe como par�metro. Esto
	 * quiere decir que sirve tanto para adjuntar ficheros de texto, im�genes, etc</p>
	 * @param ruta Ruta del fichero a adjuntar
	 * @throws MessagingException Posibles errores en el env�o del mensaje
	 */
	protected void adjuntarArchivo(String ruta) throws MessagingException{
		BodyPart adjunto = new MimeBodyPart();
		File fichero = new File(ruta);
		DataSource source = new FileDataSource(fichero);
		adjunto.setDataHandler(new DataHandler(source));
		adjunto.setFileName(fichero.getName());
		mp.addBodyPart(adjunto);
	}
	
	/**
	 * <p>Establece las propiedades de la conexi�n, tales como el host, el puerto, el usuario para
	 * autenticarse, etc. Estos valores se obtienen del fichero de propiedades</p>
	 * @param props Objeto que permite manejar el conjunto de propiedades del sistema
	 */
	private void setProperties(Properties props) {
		props.put("mail.smtp.user", user);
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.starttls.enable","true");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
	}
	
	/**
	 * <p>Clase que proporciona la autenticaci�n a la hora de registrar las propiedades en
	 * la sesi�n</p>
	 * @author Daniel Fern�ndez Aller
	 */
	private class SMTPAuthenticator extends Authenticator {
		/**
		 * <p>M�todo invocado desde el sistema cuando la autenticaci�n es requerida</p>
		 */
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password);
        }
    }
}
