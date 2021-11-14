package com.epam.esm.controller;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.InvalidSortStringException;
import com.epam.esm.service.GiftCertificateService;
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

/**
 * This class contains public REST API endpoints related to {@code GiftCertificate} entity.
 *
 * @author Evgeniy Sokolchik
 */
@RestController
@RequestMapping("/api/certificates")
public class GiftCertificateController {

  private final GiftCertificateService giftCertificateService;

  public GiftCertificateController(GiftCertificateService giftCertificateService) {
    this.giftCertificateService = giftCertificateService;
  }

  /**
   * Retrieve certificates according to specified parameters. All parameters are optional, so if
   * they are not present, all certificates will be retrieved.
   *
   * @param tagName String specifying {@code Tag} entity name value
   * @param name String specifying {@code GiftCertificate} entity name value
   * @param description String specifying {@code GiftCertificate} entity description value
   * @param sort String specifying sorting params
   * @throws InvalidSortStringException in case when sort param is not specified right
   * @return JSON {@link ResponseEntity} object that contains list of {@link GiftCertificateDto}
   */
  @GetMapping
  public ResponseEntity<List<GiftCertificateDto>> getCertificates(
      @RequestParam(required = false) String tagName,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String description,
      @RequestParam(required = false) String[] sort) {
    List<GiftCertificateDto> certificateDtos =
        giftCertificateService.filter(tagName, name, description, sort);
    return new ResponseEntity<>(certificateDtos, OK);
  }

  /**
   * Retrieve certificate by its unique id.
   *
   * @param id certificate id
   * @throws EntityNotFoundException in case when certificate with this id does not exist
   * @return JSON {@link ResponseEntity} object that contains {@link GiftCertificateDto} object
   */
  @GetMapping("/{id}")
  public ResponseEntity<GiftCertificateDto> getCertificate(@PathVariable long id) {
    GiftCertificateDto certificateDto = giftCertificateService.findById(id);
    return new ResponseEntity<>(certificateDto, OK);
  }

  /**
   * Create a new certificate.
   *
   * @param certificateDto {@link GiftCertificateDto} instance
   * @throws InvalidEntityException in case when passed DTO object contains invalid data
   * @return JSON {@link ResponseEntity} object that contains created {@link GiftCertificateDto}
   *     object
   */
  @PostMapping
  public ResponseEntity<GiftCertificateDto> createCertificate(
      @RequestBody GiftCertificateDto certificateDto) {
    GiftCertificateDto createdCertificate = giftCertificateService.create(certificateDto);
    return new ResponseEntity<>(createdCertificate, CREATED);
  }

  /**
   * Update an existing certificate.
   *
   * @param id certificate id
   * @param certificateDto {@link GiftCertificateDto} instance
   * @throws EntityNotFoundException in case when certificate with this id does not exist
   * @throws InvalidEntityException in case when passed DTO object contains invalid data
   * @return JSON {@link ResponseEntity} object that contains updated {@link GiftCertificateDto}
   *     object
   */
  @PatchMapping("/{id}")
  public ResponseEntity<GiftCertificateDto> updateCertificate(
      @PathVariable long id, @RequestBody GiftCertificateDto certificateDto) {
    certificateDto.setId(id);
    GiftCertificateDto updatedDto = giftCertificateService.update(certificateDto);
    return new ResponseEntity<>(updatedDto, OK);
  }

  /**
   * Delete an existing certificate.
   *
   * @param id certificate id
   * @throws EntityNotFoundException in case when certificate with this id does not exist
   * @return empty {@link ResponseEntity}
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCertificate(@PathVariable long id) {
    giftCertificateService.delete(id);
    return new ResponseEntity<>(NO_CONTENT);
  }
}
