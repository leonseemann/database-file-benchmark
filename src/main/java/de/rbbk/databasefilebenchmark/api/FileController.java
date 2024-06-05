package de.rbbk.databasefilebenchmark.api;

import de.rbbk.databasefilebenchmark.Entites.Image;
import de.rbbk.databasefilebenchmark.Repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("helloWorld")
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("test")
    public ResponseEntity<List<Image>> test() {
        List<Image> images = fileService.test();
        if (images.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(images, HttpStatus.OK);
    }

    @PostMapping("database/upload")
    public void fileToDatabase(@RequestParam("files") List<MultipartFile> files) {
        List<Image> fileEntities = files.stream()
                .map(file -> {
                    Image newImage = new Image();
                    try {
                        newImage.setData(file.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return newImage;
                })
                .toList();
        this.imageRepository.saveAll(fileEntities);
    }
}
