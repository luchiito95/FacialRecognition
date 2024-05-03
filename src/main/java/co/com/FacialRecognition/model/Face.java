package co.com.FacialRecognition.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "faces")
@ApiModel(description = "Clase que representa un rostro")
public class Face {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("ID del rostro")
    private Long id;

    @Column(nullable = false)
    @ApiModelProperty("Patrón del rostro (características)")
    private String pattern; // Aquí almacenaremos el patrón del rostro

    @Lob
    @Column(nullable = false)
    @ApiModelProperty("Imagen del rostro en formato Base64")
    private String image; // Aquí almacenaremos la imagen del rostro en Base64

    @Column(length = 100)
    @ApiModelProperty("Nombre asociado al rostro")
    private String name;

    @ApiModelProperty("Fecha de creación del rostro")
    private LocalDateTime createdDate;

    @ApiModelProperty("Fecha de última actualización del rostro")
    private LocalDateTime lastUpdatedDate;

    public Face() {
        this.createdDate = LocalDateTime.now();
        this.lastUpdatedDate = LocalDateTime.now();
    }

    public Face(String pattern, String image, String name) {
        this.pattern = pattern;
        this.image = image;
        this.name = name;
        this.createdDate = LocalDateTime.now();
        this.lastUpdatedDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
