package co.com.FacialRecognition.service;

import co.com.FacialRecognition.model.Face;
import co.com.FacialRecognition.repository.FaceRepository;
import io.swagger.annotations.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class FacialRecognitionServiceImpl implements FacialRecognitionService {

    private final FaceRepository faceRepository;
    private final CascadeClassifier faceCascade;
    private final String facesDirectory = "faces";


    @Autowired
    public FacialRecognitionServiceImpl(FaceRepository faceRepository) {
        this.faceRepository = faceRepository;
        // Inicializar el clasificador de rostros
        String cascadeFilePath = getClass().getResource("/opencv/build/etc/haarcascades/haarcascade_frontalface_alt.xml").getPath();
        File cascadeFile = new File(cascadeFilePath);
        if (!cascadeFile.exists()) {
            System.err.println("El archivo del clasificador de rostros no existe.");
        }

        this.faceCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
        if (this.faceCascade.empty()) {
            System.err.println("No se pudo cargar el clasificador de rostros.");
        }
    }

    @Override
    @ApiOperation("Agregar un nuevo rostro a la base de datos")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Rostro agregado correctamente"),
            @ApiResponse(code = 400, message = "Error al procesar la imagen"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base64Image", value = "Imagen en formato base64", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "name", value = "Nombre asociado al rostro", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "facePattern", value = "Patrón del rostro", required = true, dataType = "string", paramType = "body")
    })

    public void addFace(String base64Image, String name) {
        try {
            if (base64Image == null || base64Image.isEmpty()) {
                throw new IllegalArgumentException("La imagen base64 no puede ser nula o vacía");
            }

            // Decodificar la imagen base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Convertir los bytes de la imagen en una matriz OpenCV
            Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
            if (image.empty()) {
                throw new RuntimeException("No se pudo decodificar la imagen");
            }

            // Extraer el patrón del rostro
            String facePattern = extractFacePattern(image);

            // Guardar el patrón del rostro en un archivo
            String patternFileName = savePatternToFile(facePattern);

            // Guardar la referencia del archivo en la base de datos
            Face face = new Face(patternFileName, base64Image, name);
            faceRepository.save(face);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la imagen", e);
        }
    }

    private String savePatternToFile(String facePattern) throws IOException {
        String fileName = "faces/face_pattern_" + UUID.randomUUID().toString() + ".txt";
        Path filePath = Paths.get("src/main/resources", fileName); // Ruta del archivo dentro de src/main/resources
        Files.createDirectories(filePath.getParent()); // Crea los directorios si no existen
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(facePattern);
        }
        return fileName;
    }




    private String extractFacePattern(Mat image) {
        // Inicializar el clasificador de rostros Haar cascade
        String cascadeFilePath = getClass().getResource("/opencv/build/etc/haarcascades/haarcascade_frontalface_alt.xml").getPath();
        File cascadeFile = new File(cascadeFilePath);
        CascadeClassifier faceDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
        if (faceDetector.empty()) {
            throw new RuntimeException("No se pudo cargar el clasificador de rostros");
        }

        // Detectar rostros en la imagen
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        // Verificar si se detectó al menos un rostro
        if (faceDetections.toArray().length == 0) {
            throw new RuntimeException("No se detectaron rostros en la imagen");
        }

        // Extraer características del primer rostro detectado
        Rect faceRect = faceDetections.toArray()[0];
        Mat faceRegion = new Mat(image, faceRect);

        // Convertir la región del rostro a escala de grises para un mejor procesamiento
        Mat grayFace = new Mat();
        Imgproc.cvtColor(faceRegion, grayFace, Imgproc.COLOR_BGR2GRAY);

        // Aplicar un redimensionamiento para tener una imagen fija de tamaño
        Size targetSize = new Size(100, 100);
        Imgproc.resize(grayFace, grayFace, targetSize);

        // Normalizar la imagen (opcional)
        Core.normalize(grayFace, grayFace, 0, 255, Core.NORM_MINMAX);

        // Aplicar un filtro gaussiano para suavizar la imagen (opcional)
        Imgproc.GaussianBlur(grayFace, grayFace, new Size(5, 5), 0);



        // Convertir la información del rostro en un patrón (en este ejemplo, lo convertimos a una cadena)
        String facePattern = grayFace.dump();

        return facePattern;
    }


    @Override
    @ApiOperation("Reconoce un rostro en una imagen")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operación exitosa"),
            @ApiResponse(code = 400, message = "Error al procesar la imagen"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base64Image", value = "Imagen en formato base64", required = true, dataType = "string", paramType = "body")
    })

    public String recognizeFace(String base64Image) {
        try {
            if (base64Image == null || base64Image.isEmpty()) {
                throw new IllegalArgumentException("La imagen base64 no puede ser nula o vacía");
            }

            // Decodificar la imagen base64
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Convertir los bytes de la imagen en una matriz OpenCV
            Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
            if (image.empty()) {
                throw new RuntimeException("No se pudo decodificar la imagen");
            }

            // Extraer el patrón del rostro
            String facePattern = extractFacePattern(image);

            // Realizar el reconocimiento facial por patrón
            return recognizeFaceByPattern(facePattern);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar la excepción para manejarla en un nivel superior
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al procesar la imagen", e);
        }
    }

    @Override
    public String recognizeFaceByPattern(String facePattern) {
        try {
            if (facePattern == null || facePattern.isEmpty()) {
                throw new IllegalArgumentException("El patrón del rostro no puede ser nulo o vacío");
            }

            // Buscar en los archivos de patrones
            File[] patternFiles = getPatternFiles();
            for (File patternFile : patternFiles) {
                String storedPattern = readPatternFromFile(patternFile);
                if (facePattern.equals(storedPattern)) {
                    String fileName = patternFile.getName();
                    String faceName = fileName.substring(fileName.indexOf("face_") + 5, fileName.indexOf(".txt"));
                    return "Rostro reconocido: " + faceName;
                }
            }

            return "Rostro no reconocido";
        } catch (Exception e) {
            throw new RuntimeException("Error al reconocer el rostro por patrón", e);
        }
    }

    private File[] getPatternFiles() {
        String directoryPath = Objects.requireNonNull(getClass().getClassLoader().getResource(facesDirectory)).getFile();
        File directory = new File(directoryPath);
        return directory.listFiles((dir, name) -> name.endsWith(".txt"));
    }

    private String readPatternFromFile(File file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file.getPath())));
    }

}
