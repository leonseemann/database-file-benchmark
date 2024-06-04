package de.rbbk.databasefilebenchmark.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {
    @GetMapping("helloWorld")
    public String helloWorld() {
        return "Hello World!";
    }
}
