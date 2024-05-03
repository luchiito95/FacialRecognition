package co.com.FacialRecognition.config;



public class OpenCvLoader {

    public static void load() {
        try {
            // Obtén la ruta del archivo de la biblioteca nativa desde el directorio de recursos
            String nativeLibraryPath = OpenCvLoader.class.getResource("/x64/opencv_java490.dll").getPath();


            // Carga la biblioteca nativa
            System.load(nativeLibraryPath);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Error al cargar la librería nativa de OpenCV: " + e.getMessage());
            System.exit(1);
        }
    }

}
