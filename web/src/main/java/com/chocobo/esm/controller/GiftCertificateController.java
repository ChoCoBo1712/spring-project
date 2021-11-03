package com.chocobo.esm.controller;

import com.chocobo.esm.dto.GiftCertificateDto;
import com.chocobo.esm.service.GiftCertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/certificates")
public class GiftCertificateController {

    private final GiftCertificateService giftCertificateService;

    public GiftCertificateController(GiftCertificateService giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    @GetMapping
    public ResponseEntity<List<GiftCertificateDto>> getCertificates(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String sort
    ) {
        List<GiftCertificateDto> certificateDtos = giftCertificateService.filter(tagName, name, description, sort);
        return new ResponseEntity<>(certificateDtos, OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiftCertificateDto> getCertificate(@PathVariable long id) {
        GiftCertificateDto certificateDto = giftCertificateService.findById(id);
        return new ResponseEntity<>(certificateDto, OK);
    }

    @PostMapping
    public ResponseEntity<GiftCertificateDto> createCertificate(@RequestBody GiftCertificateDto certificateDto) {
        GiftCertificateDto createdCertificateDto = giftCertificateService.create(certificateDto);
        return new ResponseEntity<>(createdCertificateDto, CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GiftCertificateDto> updateCertificate(
            @PathVariable long id, @RequestBody GiftCertificateDto certificateDto
    ) {
        certificateDto.setId(id);
        GiftCertificateDto updatedDto = giftCertificateService.update(certificateDto);
        return new ResponseEntity<>(updatedDto, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable long id) {
        giftCertificateService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
