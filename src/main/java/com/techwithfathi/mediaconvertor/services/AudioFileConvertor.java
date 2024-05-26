package com.techwithfathi.mediaconvertor.services;

import com.techwithfathi.mediaconvertor.models.Convertor;
import com.techwithfathi.mediaconvertor.repository.ConvertorRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
@Service
public class AudioFileConvertor {

    @Value("${file.upload.path}")
    private String UPLOAD_DIR;
    private final ConvertorRepository convertorRepository;

    public AudioFileConvertor(ConvertorRepository convertorRepository) {
        this.convertorRepository = convertorRepository;
    }

    @Transactional
    public void convert(String taskId) {
        Convertor convertor = convertorRepository.getReferenceById(taskId);
        String inputFilePath = getInputFilePath(convertor.getInputFile());
        String outputFilePath = getOutputFilePath(convertor);
        int statusCode = execCommand(inputFilePath, outputFilePath);
        if (statusCode == 0) {
            try {
                String fileDownloadUri = getBaseUrl(convertor.getInputFile()) + "/api/files/download/" + getFilename(outputFilePath);
                convertor.setOutputFile(fileDownloadUri);
                convertor.setStatus("DONE");
            } catch (MalformedURLException e) {
                convertor.setStatus("FAILED");
            }
        } else {
            convertor.setStatus("FAILED");
        }
        convertorRepository.save(convertor);
    }

    private int execCommand(String inputFile, String outputFile) {
        String[] command = { "ffmpeg", "-i", inputFile, outputFile, "-y" };
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            // Start the process
            Process process = processBuilder.start();
            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode != 0)
                System.out.println(output);
            return exitCode;
        } catch (IOException | InterruptedException e) {
            return -1;
        }
    }

    private String getOutputFilePath(Convertor convertor) {
        String inputFileUrl = convertor.getInputFile();
        String filename = getFilename(inputFileUrl);
        String baseName = getBaseName(filename);
        String outputFilename = baseName + "." + convertor.getFormat();
        Path filePath = Paths.get(UPLOAD_DIR).resolve(outputFilename).normalize();
        return filePath.toString();
    }

    private static String getFilename(String inputFileUrl) {
        String[] parts = inputFileUrl.split("/");
        return parts[parts.length - 1];
    }

    private String getBaseName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }

    private String getInputFilePath(String inputFileUrl) {
        String filename = getFilename(inputFileUrl);
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
        return filePath.toString();
    }

    private String getBaseUrl(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        int port = url.getPort(); // Get the port number
        String baseUrl;
        if (port != -1) {
            // URL contains a port number
            baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + port;
        } else {
            // URL does not contain a port number
            baseUrl = url.getProtocol() + "://" + url.getHost();
        }
        return baseUrl;
    }
}
