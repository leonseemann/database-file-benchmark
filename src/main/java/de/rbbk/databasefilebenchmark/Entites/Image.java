package de.rbbk.databasefilebenchmark.Entites;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="Images")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Lob
    @Column(length = Integer.MAX_VALUE)
    @Nullable
    private byte[] data;

    @Nullable
    private String path;

    private String fileName;
}
