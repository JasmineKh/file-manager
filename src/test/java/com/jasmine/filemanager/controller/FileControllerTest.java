package com.jasmine.filemanager.controller;

import com.jasmine.filemanager.service.FileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
public class FileControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void uploadFile_throwsBadRequestException_whenFileIsEmpty() throws Exception{

        ClassPathResource fileResource = new ClassPathResource("/emptyfile.txt");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "emptyfile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                fileResource.getInputStream()
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/file/upload").file(multipartFile)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadFile_throwsBadRequestException_whenFileFormatIsNotText() throws Exception{

        ClassPathResource fileResource = new ClassPathResource("/test.png");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                fileResource.getInputStream()
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/file/upload").file(file)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest());
    }


    @Test
    void uploadFile_uploadSuccessfully_whenTextFileIsCorrect() throws Exception{

        Mockito.when(fileService.uploadFile(any(MultipartFile.class))).thenReturn(1L);

        ClassPathResource fileResource = new ClassPathResource("/text1.txt");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "text1.txt",
                MediaType.TEXT_PLAIN_VALUE,
                fileResource.getInputStream()
        );

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/file/upload").file(file)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk()).andReturn();
        String id = result.getResponse().getContentAsString();
        Assertions.assertNotNull(id);

    }


    @Test
    void getOneRandomLineOfLastFile_throws5xxServerException_whenIdParamIsNotSent() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/file/random-line")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().is5xxServerError());

    }

    @Test
    void getOneRandomLineOfLastFile_returnsRandomLineAndResponseContentTypeIsTextPlain_whenMediaTypeIsTextPlain() throws Exception{

        String randomLine = "Donec ut ultricies sapien, at semper lectus. Nulla efficitur vitae arcu nec molestie.";
        Mockito.when(fileService.getOneRandomLine(1L, false)).thenReturn(randomLine);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/file/random-line")
                        .param("id", String.valueOf(1L))
                        .accept(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        String contentType = result.getResponse().getContentType();

        Mockito.verify(fileService, Mockito.atLeastOnce()).getOneRandomLine(1L, false);
        Assertions.assertNotNull(content);
        Assertions.assertEquals(contentType.substring(0, contentType.indexOf(";")), MediaType.TEXT_PLAIN_VALUE);
    }


    @Test
    void getOneRandomLineOfLastFile_returnsRandomLineAndResponseContentTypeIsApplicationJson_whenMediaTypeIsApplicationJson() throws Exception{

        String randomLine = "Donec ut ultricies sapien, at semper lectus. Nulla efficitur vitae arcu nec molestie.";
        Mockito.when(fileService.getOneRandomLine(1L, false)).thenReturn(randomLine);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/file/random-line")
                        .param("id", String.valueOf(1L))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        String contentType = result.getResponse().getContentType();

        Mockito.verify(fileService, Mockito.atLeastOnce()).getOneRandomLine(1L, false);
        Assertions.assertNotNull(content);
        Assertions.assertEquals(contentType, MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void getOneRandomLineOfLastFile_returnsRandomLineAndResponseContentTypeIsApplicationXML_whenMediaTypeIsApplicationXML() throws Exception{

        String randomLine = "Donec ut ultricies sapien, at semper lectus. Nulla efficitur vitae arcu nec molestie.";
        Mockito.when(fileService.getOneRandomLine(1L, false)).thenReturn(randomLine);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/file/random-line")
                        .param("id", String.valueOf(1L))
                        .accept(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        String contentType = result.getResponse().getContentType();

        Mockito.verify(fileService, Mockito.atLeastOnce()).getOneRandomLine(1L, false);
        Assertions.assertNotNull(content);
        Assertions.assertEquals(contentType.substring(0, contentType.indexOf(";")), MediaType.APPLICATION_XML_VALUE);
    }


    @Test
    void getOneRandomLineOfLastFile_returnsLineDetailAndResponseContentTypeIsTextPlain_whenMediaTypeIsALL() throws Exception{

        String randomLine = "Cras blandit lorem sed luctus tempus. Morbi et iaculis arcu. Mauris malesuada fermentum egestas. Nulla malesuada mollis velit.\n" +
                "lineNumber: 4\n" +
                "fileName: text1.txt\n" +
                "mostUsedLetter: a";
        Mockito.when(fileService.getOneRandomLine(1L, true)).thenReturn(randomLine);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/file/random-line")
                        .param("id", String.valueOf(1L))
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        String contentType = result.getResponse().getContentType();

        Mockito.verify(fileService, Mockito.atLeastOnce()).getOneRandomLine(1L, true);
        Assertions.assertNotNull(content);
        Assertions.assertTrue(content.contains("lineNumber"));
        Assertions.assertTrue(content.contains("fileName"));
        Assertions.assertTrue(content.contains("mostUsedLetter"));
        Assertions.assertEquals(contentType.substring(0, contentType.indexOf(";")), MediaType.TEXT_PLAIN_VALUE);
    }



    @Test
    void getOneRandomLineBackward_returnsRandomLineBackward_whenFileExist() throws Exception{

        List<String> randomLinesBackward = new ArrayList<>();
        randomLinesBackward.add(".secirtlu sisilicaf ucra te surup sitrobol siruaM .mes sucal cen alluN .odommoc mauqila lsin non hbin dnefiele niorP");
        Mockito.when(fileService.getRandomLinesBackward()).thenReturn(randomLinesBackward);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/file/random-line-backward")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();

        Mockito.verify(fileService, Mockito.atLeastOnce()).getRandomLinesBackward();
        Assertions.assertNotNull(content);
    }


    @Test
    void getTwentyLongestLine_returnsTwentyLongestLines_whenFileExist() throws Exception{

        List<String> twentyLongestLines = new ArrayList<>();
        twentyLongestLines.add("Fusce vitae facilisis elit. Integer tincidunt luctus facilisis. Nam semper, orci eget tincidunt fringilla, lacus sem imperdiet mauris, a lacinia libero erat et justo.");
        Mockito.when(fileService.getTwentyLongestLinesOfLastUploadedFile()).thenReturn(twentyLongestLines);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/file/twenty-longest-line")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();

        Mockito.verify(fileService, Mockito.atLeastOnce()).getTwentyLongestLinesOfLastUploadedFile();
        Assertions.assertNotNull(content);
    }


    @Test
    void getHundredLongestLineOfAllFiles_returnsHundredLongestLineOfAllFiles_whenFileExist() throws Exception{

        List<String> hundredLongestLineOfAllFiles = new ArrayList<>();
        hundredLongestLineOfAllFiles.add("Fusce vitae facilisis elit. Integer tincidunt luctus facilisis. Nam semper, orci eget tincidunt fringilla, lacus sem imperdiet mauris, a lacinia libero erat et justo.");
        Mockito.when(fileService.getHundredLongestLineOfAllFiles()).thenReturn(hundredLongestLineOfAllFiles);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/file/hundred-longest-line-from-all-files")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();

        Mockito.verify(fileService, Mockito.atLeastOnce()).getHundredLongestLineOfAllFiles();
        Assertions.assertNotNull(content);
    }

}
