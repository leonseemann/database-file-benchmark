package de.rbbk.databasefilebenchmark.api;

import de.rbbk.databasefilebenchmark.AppConfig;
import de.rbbk.databasefilebenchmark.Entites.Image;
import de.rbbk.databasefilebenchmark.Repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    private final ImageRepository imageRepository;
    private final AppConfig appConfig;

    public List<Image> test() {
        return imageRepository.findAll().stream().toList();
    }

    public boolean saveFileToFilesystem(MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue; //next pls
                }

                byte[] bytes = file.getBytes();
                Path pathToFile = Path.of(appConfig.getFilePath().toString(), file.getOriginalFilename());
                log.debug("Writing file {} to {}", file.getOriginalFilename(), pathToFile);
                Files.write(pathToFile, bytes);
            }

            return true;

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return false;
    }
}
