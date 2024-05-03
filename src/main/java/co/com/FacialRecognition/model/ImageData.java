package co.com.FacialRecognition.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Clase que representa los datos de una imagen")
public class ImageData {

    @ApiModelProperty(value = "Imagen en formato Base64", required = true, example = "iVBORw0KGgoAAAANSUhEUgAAA...")
    private String base64Image;



    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

}
