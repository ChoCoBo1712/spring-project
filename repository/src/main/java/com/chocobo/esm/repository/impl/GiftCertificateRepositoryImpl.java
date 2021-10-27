package com.chocobo.esm.repository.impl;

import com.chocobo.esm.entity.GiftCertificate;
import com.chocobo.esm.entity.Tag;
import com.chocobo.esm.repository.GiftCertificateRepository;
import com.chocobo.esm.repository.Ordering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private RowMapper<Tag> rowMapper;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Autowired
    public void setRowMapper(RowMapper<Tag> rowMapper) {
        this.rowMapper = rowMapper;
    }

    @Override
    public List<GiftCertificate> find(long tagId, String certificateName, BigDecimal price, Ordering orderByName, Ordering orderByCreateDate) {
        return null;
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
        return Optional.empty();
    }

    @Override
    public void addTag(long certificateId, long tagId) {

    }

    @Override
    public void removeTag(long certificateId, long tagId) {

    }

    @Override
    public long create(GiftCertificate certificate) {
        return 0;
    }

    @Override
    public boolean update(GiftCertificate certificate) {
        return false;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }
}
