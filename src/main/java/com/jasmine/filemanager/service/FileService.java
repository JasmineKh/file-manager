package com.jasmine.filemanager.service;

import com.jasmine.filemanager.data.File;
import com.jasmine.filemanager.data.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
public class FileService {


    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Long uploadFile(MultipartFile multipartFile) throws Exception {

        File file = new File(null, multipartFile.getOriginalFilename(), multipartFile.getBytes());
        file = fileRepository.save(file);
        return file.getId();
    }

    public String getOneRandomLine(Long id, boolean lineDetailRequired) throws Exception {

        File file = fileRepository.findById(id).orElseThrow(() -> new Exception("file with id " + id + " not found"));
        List<String> lineList = getFileLineList(file);

        Random random = new Random();
        int randomIndex = random.nextInt(lineList.size());
        String randomLine = lineList.get(randomIndex);

        if(lineDetailRequired){
            randomLine = randomLine.concat("\n" + "lineNumber: " + randomIndex + "\n" + "fileName: " + file.getName() + "\n" + "mostUsedLetter: " + getMostUsedLetter(randomLine.replaceAll(" ", "")));
        }

        return randomLine;

    }


    public List<String> getRandomLinesBackward() {

        List<File> files = fileRepository.findAll();
        List<String> allRandomBackwardLines = new ArrayList<>();
        List<String> allLines = files.stream().map(file -> new String(file.getContent(), StandardCharsets.UTF_8)).toList();

        Random random = new Random();
        for(String line: allLines){
            List<String> list = Arrays.asList(line.split("\n"));
            allRandomBackwardLines.add(new StringBuilder(list.get(random.nextInt(list.size()))).reverse().toString());
        }

        return allRandomBackwardLines;

    }


    public List<String> getTwentyLongestLinesOfLastUploadedFile() throws Exception{

        File file = fileRepository.findFirstByOrderByIdDesc().orElseThrow(() -> new Exception("latest file could not fount"));
        List<String> fileLineList = getFileLineList(file);
        return getNLongestLinesOfLineList(fileLineList, 20);

    }


    public List<String> getHundredLongestLineOfAllFiles() throws Exception{
        List<File> allFiles = fileRepository.findAll();

        List<List<String>> lineListOfAllFiles = allFiles.stream().map(this::getFileLineList).toList();
        List<String> lineOfAllFiles = new ArrayList<>();
        lineListOfAllFiles.forEach(lineOfAllFiles::addAll);

        return getNLongestLinesOfLineList(lineOfAllFiles, 100);

    }


    private List<String> getFileLineList(File file){

        String content = new String(file.getContent(), StandardCharsets.UTF_8);
        return Arrays.asList(content.split("\n"));
    }

    private List<String> getNLongestLinesOfLineList(List<String> fileLineList, long lineNumberToBeReturned) {

        Collections.sort(fileLineList, Comparator.comparing(String::length).reversed());
        long lineNumber = Math.min(fileLineList.size(), lineNumberToBeReturned);
        return fileLineList.stream().limit(lineNumber).toList();
    }


    private String getMostUsedLetter(String line) {

        HashMap<Character, Integer> charMap = new HashMap<>();
        int lineLength = line.length();
        char answer = 0;
        int count = 0;

        for(int i = 0; i < lineLength; i++) {
            char charAt = line.charAt(i);
            charMap.put(charAt, charMap.getOrDefault(charAt, 0) + 1);
            if(count < charMap.get(charAt)) {
                answer = charAt;
                count = charMap.get(charAt);
            }
        }

        return String.valueOf(answer);
    }


}
