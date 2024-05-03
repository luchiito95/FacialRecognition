package co.com.FacialRecognition;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class FacialRecognitionApplication {

	public static void main(String[] args) {
		// Cargar OpenCV
		org.apache.tomcat.jni.Library.loadLibrary("opencv_java490");

		// Iniciar la aplicación Spring
		SpringApplication.run(FacialRecognitionApplication.class, args);
	}
}
