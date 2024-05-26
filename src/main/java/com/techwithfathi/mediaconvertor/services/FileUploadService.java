package com.techwithfathi.mediaconvertor.services;

import com.techwithfathi.mediaconvertor.models.Convertor;
import com.techwithfathi.mediaconvertor.repository.ConvertorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class FileUploadService {
    @Value("${file.upload.path}")
    private String UPLOAD_DIR;

    private final ConvertorRepository convertorRepository;

    public FileUploadService(ConvertorRepository convertorRepository) {
        this.convertorRepository = convertorRepository;
    }

    public String upload(MultipartFile file, String format) throws IOException {
        if (file.isEmpty())
            return "Please select a file to upload";
        // Create the upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String slugifyFilename = slugifyFilename(Objects.requireNonNull(file.getOriginalFilename()));

        // Save the file to the local file system
        Path filePath = uploadPath.resolve(slugifyFilename);
        Files.copy(file.getInputStream(), filePath, REPLACE_EXISTING);

        // Create the file download URI
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(slugifyFilename)
                .toUriString();
        Convertor newConvertor = new Convertor();
        newConvertor.setInputFile(fileDownloadUri);
        newConvertor.setFormat(format);
        newConvertor.setStatus("PENDING");
        Convertor savedConvertor = convertorRepository.save(newConvertor);
        return savedConvertor.getId();
    }

    private String slugifyFilename(String originalName) {

        return originalName
                .replace(" ", "_")
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
