package de.rbbk.databasefilebenchmark.api;

import de.rbbk.databasefilebenchmark.Entites.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;

    @GetMapping("helloWorld")
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("test")
    public ResponseEntity<List<Image>> test() {
        List<Image> images = fileService.test();
        if (images.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    @PostMapping("/filesystem/upload")
    public ResponseEntity<String> uploadFilesystem(@RequestParam("file") MultipartFile[] files) {
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
