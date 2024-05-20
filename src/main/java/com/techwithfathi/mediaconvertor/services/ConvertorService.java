package com.techwithfathi.mediaconvertor.services;

import com.techwithfathi.mediaconvertor.models.Convertor;
import com.techwithfathi.mediaconvertor.repository.ConvertorRepository;
import org.springframework.stereotype.Service;

@Service
public class ConvertorService {
    private final ConvertorRepository convertorRepository;

    public ConvertorService(ConvertorRepository convertorRepository) {
        this.convertorRepository = convertorRepository;
    }

    public Convertor getConvertorById(String taskId) {
        return convertorRepository.getReferenceById(taskId);
    }
}
