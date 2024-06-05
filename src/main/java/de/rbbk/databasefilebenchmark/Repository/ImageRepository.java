package de.rbbk.databasefilebenchmark.Repository;

import de.rbbk.databasefilebenchmark.Entites.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
