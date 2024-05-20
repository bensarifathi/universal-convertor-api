package com.techwithfathi.mediaconvertor.controllers;

import com.techwithfathi.mediaconvertor.dto.ConvertorResponseDto;
import com.techwithfathi.mediaconvertor.models.Convertor;
import com.techwithfathi.mediaconvertor.services.ConvertorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/convertor")
public class ConvertorController {

    private final ConvertorService convertorService;

    public ConvertorController(ConvertorService convertorService) {
        this.convertorService = convertorService;
    }

    @GetMapping("{taskId}")
    public ResponseEntity<ConvertorResponseDto> getById(@PathVariable String taskId) {
        Convertor convertor = convertorService.getConvertorById(taskId);
        ConvertorResponseDto convertorResponseDto = new ConvertorResponseDto();
        convertorResponseDto.setId(convertor.getId());
        convertorResponseDto.setInputFile(convertor.getInputFile());
        convertorResponseDto.setOutputFile(convertor.getOutputFile());
        convertorResponseDto.setStatus(convertor.getStatus());
        convertorResponseDto.setFormat(convertor.getFormat());
        convertorResponseDto.setCreatedAt(convertor.getCreatedAt());
        convertorResponseDto.setUpdatedAt(convertor.getUpdatedAt());
        return ResponseEntity.ok(convertorResponseDto);
    }
}
