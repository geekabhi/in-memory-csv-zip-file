package com.example.keerthi.demo;



import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.zip.GZIPOutputStream;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.InputStream;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class MyController {

    @GetMapping(path = "/test", produces = "application/gzip")
    public ResponseEntity test() throws Exception {

        String [] strings = {"a", ",", "b", ",", "c", "\n", "d", ",", "e", ",", "f", "\n"};

        String full = String.join("", strings);

        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        String csvFile = "in_memory_file.csv";
        Path path = fileSystem.getPath("");
        Path csvFilePath = path.resolve(csvFile);

        String zipFile = "in_memory_file.gz";
        Path zipFilePath = path.resolve(zipFile);

        GZIPOutputStream gzipOS;
        try {
            Files.createFile(csvFilePath);
            Files.write(csvFilePath, full.getBytes());
            
            InputStream fis = Files.newInputStream(csvFilePath);

            gzipOS = new GZIPOutputStream(Files.newOutputStream(zipFilePath));

            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            fis.close();
            gzipOS.close();

        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachement; filename=\"test.gz\"")
                .body(Files.readAllBytes(zipFilePath));
    }
}
