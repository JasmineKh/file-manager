package com.jasmine.filemanager.controller;


import com.jasmine.filemanager.config.ValidFile;
import com.jasmine.filemanager.service.FileService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/file")
public class FileController {


    private final FileService fileService;


    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<Long> uploadFile(@NotNull @ValidFile @RequestParam("file") MultipartFile file) throws Exception {

        Long id = fileService.uploadFile(file);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }


    @GetMapping(value = "/random-line", produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> getOneRandomLineOfLastFile(@RequestParam @NotNull Long id, @RequestHeader(value="Accept") String mediaType) throws Exception{

        final boolean lineDetailRequired = mediaType.equals(MediaType.ALL_VALUE);
        String line = fileService.getOneRandomLine(id, lineDetailRequired);
        return new ResponseEntity<>(line, HttpStatus.OK);

    }

    @GetMapping(value = "/random-line-backward" )
    public ResponseEntity<List<String>> getOneRandomLineBackward() throws Exception{

        List<String> randomLinesBackward = fileService.getRandomLinesBackward();
        return new ResponseEntity<>(randomLinesBackward, HttpStatus.OK);

    }


    @GetMapping(value = "/twenty-longest-line")
    public ResponseEntity<List<String>> getTwentyLongestLines() throws Exception{

        List<String> twentyLongestLines = fileService.getTwentyLongestLinesOfLastUploadedFile();
        return new ResponseEntity<>(twentyLongestLines, HttpStatus.OK);
    }


    @GetMapping(value = "/hundred-longest-line-from-all-files")
    public ResponseEntity<List<String>> getHundredLongestLinesOfAllFiles() throws Exception{

        List<String> hundredLongestLine = fileService.getHundredLongestLineOfAllFiles();
        return new ResponseEntity<>(hundredLongestLine, HttpStatus.OK);
    }

}
