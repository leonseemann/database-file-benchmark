package de.rbbk.databasefilebenchmark.api;

import de.rbbk.databasefilebenchmark.Entites.Image;
import de.rbbk.databasefilebenchmark.TimeLogger;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
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
    private final TimeLogger timeLogger;

    @PostMapping("database/upload")
    @Operation(summary = "Upload file into database", description = "It uploads files into the database as a binary.")
    public void fileToDatabase(@RequestParam("files") MultipartFile file) {
        long startTime = System.nanoTime();

        Image newImage = new Image();
        try {
            newImage.setData(file.getBytes());
            newImage.setFileName(file.getOriginalFilename());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        this.fileService.saveAllFilesToDatabase(newImage);
        long endTime = System.nanoTime() - startTime;
        timeLogger.logTimeInFile(endTime, file.getOriginalFilename());
    }

    @PostMapping("filesystem/upload")
    @Operation(summary = "Upload file into filesystem", description = "It uploads files into the filesystem and saving only the path in the database. Can handle larger files")
    public ResponseEntity<String> uploadFilesystem(@RequestParam("files") MultipartFile file) {
        long startTime = System.nanoTime();

        if (file == null) {
            return new ResponseEntity<>("Sie muesssen mindestens eine Datei hochladen", HttpStatus.NO_CONTENT);
        }

        boolean success = fileService.saveFileToFilesystem(file);
        if (!success) {
            return new ResponseEntity<>("Fehler beim Speichern der Dateien", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        long endTime = System.nanoTime() - startTime;
        timeLogger.logTimeInFile(endTime, file.getOriginalFilename());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("download")
    @Operation(summary = "Downloads all files", description = "Downloads all file that are existing in the Database as a .zip")
    public void downloadAllFiles(HttpServletResponse response) {
        long startTime = System.nanoTime();

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
        long endTime = System.nanoTime() - startTime;
        timeLogger.logTimeInFile(endTime, "download" + LocalDateTime.now());
    }

    @GetMapping("/download/{id}")
    @ResponseBody
    @Operation(summary = "Downloads one file", description = "Downloads one file by id")
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

    @DeleteMapping
    @Operation(summary = "Deletes everything", description = "Deletes all files on filesystem and in the database")
    public void deleteAll() {
        fileService.deleteAll();
    }
}
