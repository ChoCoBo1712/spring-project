package com.chocobo.esm.mapper;

import com.chocobo.esm.entity.GiftCertificate;
import org.springframework.beans.BeanWrapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.time.Period;

public class GiftCertificateBeanPropertyRowMapper extends BeanPropertyRowMapper<GiftCertificate> {

  @Override
  protected void initBeanWrapper(BeanWrapper bw) {
    super.initBeanWrapper(bw);
    bw.registerCustomEditor(Period.class, new PeriodEditor());
  }
}
