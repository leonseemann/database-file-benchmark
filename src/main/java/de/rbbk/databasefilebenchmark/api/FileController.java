package de.rbbk.databasefilebenchmark.api;

import de.rbbk.databasefilebenchmark.Entites.Image;
import de.rbbk.databasefilebenchmark.annotation.measureTime.MeasureTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;

    @MeasureTime
    @PostMapping("database/upload")
    public void fileToDatabase(@RequestParam("files") List<MultipartFile> files) {
        List<Image> fileEntities = files.stream()
                .map(file -> {
                    Image newImage = new Image();
                    try {
                        newImage.setData(file.getBytes());
                        newImage.setFileName(file.getOriginalFilename());
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    return newImage;
                })
                .toList();
        this.fileService.saveAllFilesToDatabase(fileEntities);
    }
    @MeasureTime
    @PostMapping("filesystem/upload")
    public ResponseEntity<String> uploadFilesystem(@RequestParam("files") MultipartFile[] files) {
        if (files.length == 0) {
            return new ResponseEntity<>("Sie muesssen mindestens eine Datei hochladen", HttpStatus.NO_CONTENT);
        }

        boolean success = fileService.saveFileToFilesystem(files);
        if (!success) {
            return new ResponseEntity<>("Fehler beim Speichern der Dateien", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
