package com.chocobo.esm.mapper;

import com.chocobo.esm.entity.GiftCertificate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Period;

import static com.chocobo.esm.mapper.TableColumn.*;

@Component
public class GiftCertificateMapper implements RowMapper<GiftCertificate> {

    @Override
    public GiftCertificate mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        GiftCertificate giftCertificate = new GiftCertificate();

        giftCertificate.setId(resultSet.getLong(ID));
        giftCertificate.setName(resultSet.getString(CERTIFICATE_NAME));
        giftCertificate.setDescription(resultSet.getString(CERTIFICATE_DESCRIPTION));
        giftCertificate.setPrice(resultSet.getBigDecimal(CERTIFICATE_PRICE));
        giftCertificate.setDuration(Period.ofDays(resultSet.getInt(CERTIFICATE_DURATION)));
        giftCertificate.setCreateDate(resultSet.getTimestamp(CERTIFICATE_CREATE_DATE).toInstant());
        giftCertificate.setLastUpdateDate(resultSet.getTimestamp(CERTIFICATE_LAST_UPDATE_DATE).toInstant());

        return giftCertificate;
    }
}
