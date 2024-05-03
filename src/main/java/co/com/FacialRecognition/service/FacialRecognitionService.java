package co.com.FacialRecognition.service;

import io.swagger.annotations.*;

public interface FacialRecognitionService {



    @ApiOperation("Agregar un nuevo rostro a la base de datos")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Rostro agregado correctamente"),
            @ApiResponse(code = 400, message = "Error al procesar la imagen"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "base64Image", value = "Imagen en formato base64", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "name", value = "Nombre asociado al rostro", required = true, dataType = "string", paramType = "body")
    })
    void addFace(String base64Image, String name);

    String recognizeFace(String base64Image);

    String recognizeFaceByPattern(String facePattern);
}
