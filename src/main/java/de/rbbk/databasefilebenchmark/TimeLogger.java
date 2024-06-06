package de.rbbk.databasefilebenchmark;

import de.rbbk.databasefilebenchmark.api.FileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeLogger {
    private final FileService fileService;

    @SneakyThrows
    public void logTimeInFile(long executionTime,String uploadType, String fileName) {
        log.info("Time needed to upload files: {}nsec", executionTime);

        Path path = Path.of("src/main/resources", "logs", uploadType, fileName + ".txt");

        Files.createDirectories(path.getParent());

        this.fileService.writeNewLineToFile(path.toString(), String.valueOf(executionTime));
    }
}
