package de.rbbk.databasefilebenchmark.Entites;

import jakarta.persistence.*;
import java.io.File;

@Entity
@Table(name="Images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String base64;
    private File path;

    public Image(String base64) {
        this.base64 = base64;
    }

    public Image(File path) {
        this.path = path;
    }

    public Image() {}
}
