package co.com.FacialRecognition.controller;

import co.com.FacialRecognition.model.Face;
import co.com.FacialRecognition.model.ImageData;
import co.com.FacialRecognition.service.FacialRecognitionService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("recognition-facial")
@Api(tags = "Facial Recognition API", description = "API para el reconocimiento facial")
public class FacialRecognitionController {

    private final FacialRecognitionService facialRecognitionService;

    @Autowired
    public FacialRecognitionController(FacialRecognitionService facialRecognitionService) {
        this.facialRecognitionService = facialRecognitionService;
    }

    @PostMapping("/add-face")
    @ApiOperation("Agregar un nuevo rostro a la base de datos")
    @ApiResponses({
            @ApiResponse(code = 201, message = "El rostro se ha agregado correctamente"),
            @ApiResponse(code = 400, message = "Solicitud incorrecta"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public ResponseEntity<String> addFace(
            @ApiParam(value = "Datos del rostro en formato base64", required = true)
            @RequestBody Face request) {
        try {
            // Validar que la imagen en base64 sea válida
            if (!isValidBase64Image(request.getImage())) {
                return ResponseEntity.badRequest().body("La imagen proporcionada no es válida.");
            }
            // Eliminar el prefijo "data:image/jpeg;base64," si está presente
            String base64Data = request.getImage().substring(request.getImage().indexOf(",") + 1);

            // Llamar al servicio para agregar el rostro a la base de datos
            facialRecognitionService.addFace(base64Data, request.getName());

            return ResponseEntity.status(HttpStatus.CREATED).body("El rostro se ha agregado correctamente a la base de datos.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al agregar el rostro: " + e.getMessage());
        }
    }

    // Método para validar si una cadena base64 representa una imagen válida
    private boolean isValidBase64Image(String base64Image) {
        // Verificar si la cadena base64 comienza con el prefijo "data:image" y tiene un formato de imagen válido
        return base64Image != null && base64Image.startsWith("data:image/") && base64Image.matches("^data:image/(jpeg|png|gif|jpg);base64,.*");
    }

    @PostMapping("/recognize-face")
    @ApiOperation("Reconocer un rostro en una imagen")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Rostro reconocido correctamente"),
            @ApiResponse(code = 400, message = "Solicitud incorrecta"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public ResponseEntity<String> recognizeFace(
            @ApiParam(value = "Datos de la imagen en formato JSON", required = true)
            @RequestBody ImageData imageData) {
        try {
            String base64Image = imageData.getBase64Image();
            if (base64Image == null) {
                return ResponseEntity.badRequest().body("No se proporcionó una imagen válida.");
            }

            // Eliminar el prefijo "data:image/jpeg;base64," si está presente
            String base64Data = base64Image.substring(base64Image.indexOf(",") + 1);

            // Llamar al servicio para reconocer el rostro
            String recognizedName = facialRecognitionService.recognizeFace(base64Data);
            if ("Persona Desconocida".equals(recognizedName)) {
                return ResponseEntity.ok("No se reconoció ningún rostro en la imagen.");
            } else {
                return ResponseEntity.ok("Rostro reconocido como: " + recognizedName);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al reconocer el rostro: " + e.getMessage());
        }
    }

    // Resto de los métodos del controlador...
}
