package com.techwithfathi.mediaconvertor.repository;

import com.techwithfathi.mediaconvertor.models.Convertor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConvertorRepository extends JpaRepository<Convertor, String> {
}
