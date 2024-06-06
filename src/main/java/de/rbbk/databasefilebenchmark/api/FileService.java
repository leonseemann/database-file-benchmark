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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
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

    public boolean saveFileToFilesystem(MultipartFile file) {
        try {
                Path filePath = Path.of(appConfig.getFilePath().toString(), file.getOriginalFilename());

                Files.createDirectories(appConfig.getFilePath());

                if (!Files.exists(filePath)) {
                    Files.createDirectories(filePath.getParent());
                }

                log.debug("Writing file {} to {}", file.getOriginalFilename(), filePath);
                file.transferTo(filePath);

                Image image = new Image();
                image.setPath(filePath.toString());
                image.setFileName(file.getOriginalFilename());
                imageRepository.save(image);

            return true;

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return false;
    }

    public void saveAllFilesToDatabase(Image fileEntities) {
        imageRepository.saveAndFlush(fileEntities);
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

    public void writeNewLineToFile(String fileName, String text) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(fileName, true);
        fileWriter.write(text + "\n");
        fileWriter.close();
    }

    @SneakyThrows
    public void deleteAll() {
        imageRepository.deleteAll();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(appConfig.getFilePath())) {
            for (Path path : directoryStream) {
                Files.delete(path);
            }
        }
    }
}
