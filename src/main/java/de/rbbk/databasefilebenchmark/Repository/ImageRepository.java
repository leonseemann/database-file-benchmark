package de.rbbk.databasefilebenchmark.Repository;

import de.rbbk.databasefilebenchmark.Entites.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
