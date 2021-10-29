package com.chocobo.esm.repository.impl;

import com.chocobo.esm.entity.GiftCertificate;
import com.chocobo.esm.entity.Tag;
import com.chocobo.esm.repository.GiftCertificateRepository;
import com.chocobo.esm.repository.OrderingType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private static final String ID_PARAM = "id";
    private static final String NAME_PARAM = "name";
    private static final String DESCRIPTION_PARAM = "description";
    private static final String PRICE_PARAM = "price";
    private static final String DURATION_PARAM = "duration";
    private static final String CREATE_DATE_PARAM = "create_date";
    private static final String LAST_UPDATE_DATE_PARAM = "last_update_date";
    private static final String CERTIFICATE_ID_PARAM = "certificate_id";
    private static final String TAG_ID_PARAM = "tag_id";
    private static final String TAG_NAME_PARAM = "tag_name";
    private static final String CERTIFICATE_NAME_PARAM = "certificate_name";

    private static final String SELECT_BY_PARAMS_QUERY_PATTERN = """
            SELECT DISTINCT gc.id, gc.name, description, price, duration, create_date, last_update_date
            FROM gift_certificates AS gc
            LEFT OUTER JOIN certificates_tags AS ct ON ct.certificate_id = gc.id
            LEFT OUTER JOIN tags ON ct.tag_id = tags.id
            """;

    private static final String SELECT_BY_ID = """
            SELECT id, name, description, price, duration, create_date, last_update_date
            FROM gift_certificates
            WHERE id = :id;
            """;

    private static final String INSERT = """
            INSERT INTO gift_certificates (name, description, price, duration, create_date, last_update_date)
            VALUES (:name, :description, :price, :duration, :create_date, :last_update_date);
            """;

    private static final String UPDATE = """
            UPDATE gift_certificates
            SET name = :name, description = :description, price = :price, duration = :duration, last_update_date = :last_update_date
            WHERE id = :id;
            """;

    private static final String DELETE = """
            DELETE FROM gift_certificates
            WHERE id = :id;
            """;

    private static final String INSERT_CERTIFICATES_TAGS_RELATION = """
            INSERT INTO certificates_tags (certificate_id, tag_id)
            VALUES (:certificate_id, :tag_id);
            """;

    private static final String DELETE_CERTIFICATES_TAGS_RELATION = """
            DELETE FROM certificates_tags
            WHERE certificate_id = :certificate_id AND tag_id = :tag_id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final BeanPropertyRowMapper<GiftCertificate> rowMapper;

    public GiftCertificateRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate,
                                         BeanPropertyRowMapper<GiftCertificate> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public List<GiftCertificate> filter(String tagName, String certificateName, String description,
                                        OrderingType orderByName, OrderingType orderByCreateDate) {
        String queryString = SELECT_BY_PARAMS_QUERY_PATTERN;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (tagName != null) {
            queryString += "WHERE tags.name LIKE CONCAT('%', :" + TAG_NAME_PARAM + ", '%') ";
            parameters.addValue(TAG_NAME_PARAM, tagName);
        }

        if (certificateName != null) {
            queryString += "WHERE gc.name LIKE CONCAT('%', :" + CERTIFICATE_NAME_PARAM + ", '%') ";
            parameters.addValue(CERTIFICATE_NAME_PARAM, certificateName);
        }

        if (description != null) {
            queryString += "WHERE gc.description LIKE CONCAT('%', :" + DESCRIPTION_PARAM + ", '%') ";
            parameters.addValue(DESCRIPTION_PARAM, description);
        }

        if (orderByName != null || orderByCreateDate != null) {
            queryString += "ORDER BY ";

            if (orderByName != null) {
                queryString += NAME_PARAM + " " + orderByName;
            }

            if (orderByCreateDate != null) {
                queryString += ", " + CREATE_DATE_PARAM + " " + orderByCreateDate;
            }
        }

        queryString += ";";
        return jdbcTemplate.query(queryString, parameters, rowMapper);
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);
        List<GiftCertificate> giftCertificates = jdbcTemplate.query(SELECT_BY_ID, parameters, rowMapper);
        return Optional.ofNullable(giftCertificates.size() == 1 ? giftCertificates.get(0) : null);
    }

    @Override
    public void addTag(long certificateId, long tagId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID_PARAM, certificateId)
                .addValue(TAG_ID_PARAM, tagId);
        jdbcTemplate.update(INSERT_CERTIFICATES_TAGS_RELATION, parameters);
    }

    @Override
    public void removeTag(long certificateId, long tagId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID_PARAM, certificateId)
                .addValue(TAG_ID_PARAM, tagId);
        jdbcTemplate.update(DELETE_CERTIFICATES_TAGS_RELATION, parameters);
    }

    @Override
    public long create(GiftCertificate certificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(NAME_PARAM, certificate.getName())
                .addValue(DESCRIPTION_PARAM, certificate.getDescription())
                .addValue(PRICE_PARAM, certificate.getPrice())
                .addValue(DURATION_PARAM, certificate.getDuration().getDays())
                .addValue(CREATE_DATE_PARAM, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE_PARAM, certificate.getLastUpdateDate());

        jdbcTemplate.update(INSERT, parameters, keyHolder);
        Number generatedKey = Objects.requireNonNull(keyHolder).getKey();
        return Objects.requireNonNull(generatedKey).longValue();
    }

    @Override
    public boolean update(GiftCertificate certificate) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID_PARAM, certificate.getId())
                .addValue(NAME_PARAM, certificate.getName())
                .addValue(DESCRIPTION_PARAM, certificate.getDescription())
                .addValue(PRICE_PARAM, certificate.getPrice())
                .addValue(DURATION_PARAM, certificate.getDuration().getDays())
                .addValue(CREATE_DATE_PARAM, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE_PARAM, certificate.getLastUpdateDate());
        return jdbcTemplate.update(UPDATE, parameters) > 0;
    }

    @Override
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);
        return jdbcTemplate.update(DELETE, parameters) > 0;
    }
}
