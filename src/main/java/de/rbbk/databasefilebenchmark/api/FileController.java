package de.rbbk.databasefilebenchmark.api;

import de.rbbk.databasefilebenchmark.AppConfig;
import de.rbbk.databasefilebenchmark.Entites.Image;
import de.rbbk.databasefilebenchmark.annotation.measureTime.MeasureTime;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    private final AppConfig appConfig;

    @MeasureTime(fileName = "src/main/resources/database-time")
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
    @MeasureTime(fileName = "src/main/resources/filesystem-time")
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

    @GetMapping("download")
    @MeasureTime(fileName = "src/main/resources/download-time")
    public void downloadAllFiles(HttpServletResponse response) {
        // Query the files you want to download from the database.
        // Note: Replace "findAll()" with your custom method if needed.
        List<Image> images = fileService.getAllFiles();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=download.zip");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (Image image : images) {
                log.debug("Writing image {} in zip", image.getFileName());

                ZipEntry zipEntry = new ZipEntry(image.getFileName());
                zos.putNextEntry(zipEntry);

                // Write file data to zip file
                if (image.getData() != null) {
                    zos.write(image.getData());
                }
                zos.closeEntry();
            }
            // All files added to zip, finishing
            zos.finish();
        } catch (IOException e) {
            throw new RuntimeException("Error while downloading files: " + e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    @ResponseBody
    public void getFile(@PathVariable("id") UUID id, HttpServletResponse response) {
        Optional<Image> imageOpt = fileService.findByIdOnlyData(id);

        if (imageOpt.isPresent()) {
            Image image = imageOpt.get();
            response.setHeader("Content-Disposition", "attachment; filename=" + image.getFileName());

            try {
                response.setContentType(Files.probeContentType(Path.of(image.getFileName())));
                if (image.getData() != null) {
                    response.getOutputStream().write(image.getData());
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.warn("File with id {} not found", id);
        }
    }
}
