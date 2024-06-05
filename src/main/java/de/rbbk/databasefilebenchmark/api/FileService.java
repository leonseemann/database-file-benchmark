package de.rbbk.databasefilebenchmark.api;

import de.rbbk.databasefilebenchmark.Entites.Image;
import de.rbbk.databasefilebenchmark.Repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    private final ImageRepository imageRepository;

    public List<Image> test() {
        return imageRepository.findAll().stream().toList();
    }
}
