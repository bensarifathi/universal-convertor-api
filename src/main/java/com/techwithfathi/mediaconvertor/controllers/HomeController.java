package com.techwithfathi.mediaconvertor.controllers;

import com.techwithfathi.mediaconvertor.dto.HomeResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class HomeController {

    @GetMapping("")
    public ResponseEntity<HomeResponseDto> welcome() {
        HomeResponseDto homeResponseDto = new HomeResponseDto();
        homeResponseDto.setDocumentation("https://github.com/bensarifathi/universal-convertor-api");
        return ResponseEntity.ok(homeResponseDto);
    }
}
