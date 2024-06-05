package de.rbbk.databasefilebenchmark;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Data
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private Path filePath;
    private Path executionTimeFilePath;
}
