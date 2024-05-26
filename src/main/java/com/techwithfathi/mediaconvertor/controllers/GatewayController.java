package com.techwithfathi.mediaconvertor.controllers;
import com.techwithfathi.mediaconvertor.dto.FileUploadResponseDto;
import com.techwithfathi.mediaconvertor.services.FileDownloadService;
import com.techwithfathi.mediaconvertor.services.FileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("api/")
public class GatewayController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FileUploadService fileUploadService;
    private final FileDownloadService fileDownloadService;

    public GatewayController(KafkaTemplate<String, String> kafkaTemplate, FileUploadService fileUploadService, FileDownloadService fileDownloadService) {
        this.kafkaTemplate = kafkaTemplate;
        this.fileUploadService = fileUploadService;
        this.fileDownloadService = fileDownloadService;
    }

    @PostMapping("upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("format") String format
    ) {
        try {
            String taskId = fileUploadService.upload(file, format);
            FileUploadResponseDto fileUploadResponseDto = new FileUploadResponseDto();
            fileUploadResponseDto.setTaskId(taskId);
            kafkaTemplate.send("t.media.conversion", taskId);
            return ResponseEntity.ok(fileUploadResponseDto);

        } catch (IOException e) {
            System.out.println("#".repeat(200));
            System.out.println(format);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new FileUploadResponseDto());
        }
    }

    @GetMapping("files/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = fileDownloadService.download(filename);
            if (resource == null)
                return ResponseEntity.notFound().build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}


