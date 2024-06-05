package de.rbbk.databasefilebenchmark.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.UUID;

@Entity
@Table(name="Images")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String base64;
    private File path;
}
