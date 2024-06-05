package de.rbbk.databasefilebenchmark.api;

import de.rbbk.databasefilebenchmark.AppConfig;
import de.rbbk.databasefilebenchmark.Entites.Image;
import de.rbbk.databasefilebenchmark.Repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    private final ImageRepository imageRepository;
    private final AppConfig appConfig;

    public boolean saveFileToFilesystem(MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                Path filePath = Path.of(appConfig.getFilePath().toString(), file.getOriginalFilename());

                if (!Files.exists(filePath)) {
                    Files.createDirectories(filePath.getParent());
                }

                log.debug("Writing file {} to {}", file.getOriginalFilename(), filePath);
                file.transferTo(filePath);

                Image image = new Image();
                image.setPath(filePath.toString());
                image.setFileName(file.getOriginalFilename());
                imageRepository.save(image);
            }

            return true;

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return false;
    }

    private void saveFilePath(File file) {
        Image image = new Image();
        image.setPath(file.getAbsolutePath());
        image.setFileName(file.getName());
        imageRepository.save(image);
    }

    public void saveAllFilesToDatabase(List<Image> fileEntities) {
        imageRepository.saveAllAndFlush(fileEntities);
    }

    public List<Image> getAllFiles() {
        List<Image> images = imageRepository.findAll();
        images.forEach(image -> {
            if (image.getData() == null && image.getPath() != null) {
                try {
                    image.setData(Files.readAllBytes(Path.of(image.getPath())));
                    image.setPath(null);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        });

        return images;
    }

    @SneakyThrows
    public Optional<Image> findByIdOnlyData(UUID id) {
        Optional<Image> image = imageRepository.findById(id);

        if (image.isPresent()) {
            Image imageEntity = image.get();
            if (imageEntity.getData() == null && imageEntity.getPath() != null) {
                imageEntity.setData(Files.readAllBytes(Path.of(imageEntity.getPath())));
                imageEntity.setPath(null);
            }
        }

        return image;
    }
}
