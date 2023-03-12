package com.jasmine.filemanager.service;

import com.jasmine.filemanager.data.File;
import com.jasmine.filemanager.data.FileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FileService.class})
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @MockBean
    private FileRepository fileRepository;



    @Test
    void uploadFile_returns_when() throws Exception {

        ClassPathResource fileResource = new ClassPathResource("/text1.txt");
        File persistedFile = new File(1L, fileResource.getFilename(), fileResource.getInputStream().readAllBytes());
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "text1.txt",
                MediaType.TEXT_PLAIN_VALUE,
                fileResource.getInputStream()
        );

        Mockito.when(fileRepository.save(any(File.class))).thenReturn(persistedFile);
        Long id = fileService.uploadFile(multipartFile);
        Assertions.assertNotNull(id);

    }

    @Test
    void getOneRandomLine_returnsLineWithDetail_whenLineDetailParamIsTrue() throws Exception {

        ClassPathResource fileResource = new ClassPathResource("/text1.txt");
        File persistedFile = new File(1L, fileResource.getFilename(), fileResource.getInputStream().readAllBytes());

        Mockito.when(fileRepository.findById(1L)).thenReturn(Optional.of(persistedFile));
        String line = fileService.getOneRandomLine(1L, true);

        Assertions.assertNotNull(line);
        Assertions.assertTrue(line.contains("lineNumber"));
        Assertions.assertTrue(line.contains("fileName"));
        Assertions.assertTrue(line.contains("mostUsedLetter"));

    }


    @Test
    void getOneRandomLine_returnsLineWithoutDetail_whenLineDetailParamIsFalse() throws Exception {

        ClassPathResource fileResource = new ClassPathResource("/text1.txt");
        File persistedFile = new File(1L, fileResource.getFilename(), fileResource.getInputStream().readAllBytes());

        Mockito.when(fileRepository.findById(1L)).thenReturn(Optional.of(persistedFile));
        String line = fileService.getOneRandomLine(1L, false);

        Assertions.assertNotNull(line);
        Assertions.assertFalse(line.contains("lineNumber"));
        Assertions.assertFalse(line.contains("fileName"));
        Assertions.assertFalse(line.contains("mostUsedLetter"));

    }


    @Test
    void getRandomLinesBackward_returnsLinesBackward_whenFilesAreExisted() throws Exception {

        ClassPathResource file1 = new ClassPathResource("/text1.txt");
        File persistedFile1 = new File(1L, file1.getFilename(), file1.getInputStream().readAllBytes());

        ClassPathResource file2 = new ClassPathResource("/text2.txt");
        File persistedFile2 = new File(2L, file2.getFilename(), file2.getInputStream().readAllBytes());

        List<File> persistedFiles = new ArrayList<>();
        persistedFiles.add(persistedFile1);
        persistedFiles.add(persistedFile2);

        Mockito.when(fileRepository.findAll()).thenReturn(persistedFiles);
        List<String> linesBackward = fileService.getRandomLinesBackward();

        Assertions.assertTrue(linesBackward.size()>1);

    }


    @Test
    void getTwentyLongestLinesOfLastUploadedFile_returnsLongestLines_whenPersistedFileLinesAreLessThan20() throws Exception {

        ClassPathResource file = new ClassPathResource("/text1.txt");
        File persistedFile = new File(1L, file.getFilename(), file.getInputStream().readAllBytes());

        String content = new String(persistedFile.getContent(), StandardCharsets.UTF_8);
        List<String> persistedFileLines = Arrays.asList(content.split("\n"));


        Mockito.when(fileRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(persistedFile));
        List<String> lines = fileService.getTwentyLongestLinesOfLastUploadedFile();

        Assertions.assertEquals(lines.size(), persistedFileLines.size());

    }

    @Test
    void getTwentyLongestLinesOfLastUploadedFile_returns20LongestLines_whenPersistedFileLinesAreMoreThan20() throws Exception {

        ClassPathResource file = new ClassPathResource("/text2.txt");
        File persistedFile = new File(1L, file.getFilename(), file.getInputStream().readAllBytes());

        Mockito.when(fileRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.of(persistedFile));
        List<String> lines = fileService.getTwentyLongestLinesOfLastUploadedFile();

        Assertions.assertEquals(lines.size(), 20);

    }


    @Test
    void getHundredLongestLineOfAllFiles_returnsLongestLines_whenAllPersistedFileLinesAreLessThan100() throws Exception {

        ClassPathResource file1 = new ClassPathResource("/text1.txt");
        File persistedFile1 = new File(1L, file1.getFilename(), file1.getInputStream().readAllBytes());

        ClassPathResource file2 = new ClassPathResource("/text2.txt");
        File persistedFile2 = new File(2L, file2.getFilename(), file2.getInputStream().readAllBytes());

        List<File> persistedFiles = new ArrayList<>();
        persistedFiles.add(persistedFile1);
        persistedFiles.add(persistedFile2);

        Mockito.when(fileRepository.findAll()).thenReturn(persistedFiles);
        List<String> lines = fileService.getHundredLongestLineOfAllFiles();

        Assertions.assertTrue(lines.size()<100);

    }


    @Test
    void getHundredLongestLineOfAllFiles_returns100LongestLines_whenAllPersistedFileLinesAreMoreThan100() throws Exception {

        ClassPathResource file1 = new ClassPathResource("/text2.txt");
        File persistedFile1 = new File(1L, file1.getFilename(), file1.getInputStream().readAllBytes());

        ClassPathResource file2 = new ClassPathResource("/text3.txt");
        File persistedFile2 = new File(2L, file2.getFilename(), file2.getInputStream().readAllBytes());

        List<File> persistedFiles = new ArrayList<>();
        persistedFiles.add(persistedFile1);
        persistedFiles.add(persistedFile2);

        Mockito.when(fileRepository.findAll()).thenReturn(persistedFiles);
        List<String> lines = fileService.getHundredLongestLineOfAllFiles();

        Assertions.assertEquals(lines.size() , 100);

    }


}
