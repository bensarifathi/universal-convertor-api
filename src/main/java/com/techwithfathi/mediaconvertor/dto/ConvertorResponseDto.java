package com.techwithfathi.mediaconvertor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConvertorResponseDto {
    private String id;
    private String inputFile;
    private String outputFile;
    private String format;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
