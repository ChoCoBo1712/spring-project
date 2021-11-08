package com.epam.esm.repository.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Arrays;
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

    private static final String WHERE_CLAUSE_PATTERN = "%s LIKE CONCAT('%%', :%s, '%%')";
    private static final String WHERE_CLAUSE_START = "WHERE ";
    private static final String WHERE_CLAUSE_SEPARATOR = " AND ";
    private static final String ORDER_BY_CLAUSE_PATTERN = "%s %s";
    private static final String ORDER_BY_CLAUSE_START = "ORDER BY ";
    private static final String ORDER_BY_CLAUSE_SEPARATOR = ", ";
    private static final String SEMICOLON = ";";
    private static final String COMMA = ",";
    private static final String DOT = "\\.";
    private static final String SORT_PARAM = "sortParam";
    private static final String ASCENDING_PARAM = "asc";
    private static final String ASCENDING = "ASC";
    private static final String DESCENDING = "DESC";
    private static final String TAGS_NAME = "tags.name";
    private static final String CERTIFICATES_NAME = "gc.name";
    private static final String CERTIFICATES_DESCRIPTION = "gc.description";

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
    private final RowMapper<GiftCertificate> rowMapper;

    public GiftCertificateRepositoryImpl(DataSource dataSource, RowMapper<GiftCertificate> rowMapper) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.rowMapper = rowMapper;
    }

    @Override
    public List<GiftCertificate> filter(String tagName, String name, String description, String sort) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String queryString = buildFilterQuery(tagName, name, description, sort, parameters);
        return jdbcTemplate.query(queryString, parameters, rowMapper);
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);
        List<GiftCertificate> giftCertificates = jdbcTemplate.query(SELECT_BY_ID, parameters, rowMapper);
        return Optional.ofNullable(giftCertificates.size() == 1 ? giftCertificates.get(0) : null);
    }

    @Override
    public void addCertificateTagRelation(long certificateId, long tagId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID_PARAM, certificateId)
                .addValue(TAG_ID_PARAM, tagId);
        jdbcTemplate.update(INSERT_CERTIFICATES_TAGS_RELATION, parameters);
    }

    @Override
    public void removeCertificateTagRelation(long certificateId, long tagId) {
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
                .addValue(CREATE_DATE_PARAM, Timestamp.from(certificate.getCreateDate()))
                .addValue(LAST_UPDATE_DATE_PARAM, Timestamp.from(certificate.getLastUpdateDate()));

        jdbcTemplate.update(INSERT, parameters, keyHolder, new String[] { "id" });
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
                .addValue(CREATE_DATE_PARAM, Timestamp.from(certificate.getCreateDate()))
                .addValue(LAST_UPDATE_DATE_PARAM, Timestamp.from(certificate.getLastUpdateDate()));
        return jdbcTemplate.update(UPDATE, parameters) > 0;
    }

    @Override
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);
        return jdbcTemplate.update(DELETE, parameters) > 0;
    }

    private String buildFilterQuery(
            String tagName, String name, String description, String sort, MapSqlParameterSource parameters
    ) {
        StringBuilder query = new StringBuilder(SELECT_BY_PARAMS_QUERY_PATTERN);
        ClauseBuilder whereClauseBuilder = new ClauseBuilder(WHERE_CLAUSE_START, WHERE_CLAUSE_SEPARATOR);
        ClauseBuilder orderByClauseBuilder = new ClauseBuilder(ORDER_BY_CLAUSE_START, ORDER_BY_CLAUSE_SEPARATOR);

        if (tagName != null) {
            whereClauseBuilder.addPart(String.format(WHERE_CLAUSE_PATTERN, TAGS_NAME, TAG_NAME_PARAM));
            parameters.addValue(TAG_NAME_PARAM, tagName);
        }
        if (name != null) {
            whereClauseBuilder.addPart(String.format(WHERE_CLAUSE_PATTERN, CERTIFICATES_NAME, CERTIFICATE_NAME_PARAM));
            parameters.addValue(CERTIFICATE_NAME_PARAM, name);
        }
        if (description != null) {
            whereClauseBuilder.addPart(String.format(WHERE_CLAUSE_PATTERN, CERTIFICATES_DESCRIPTION, DESCRIPTION_PARAM));
            parameters.addValue(DESCRIPTION_PARAM, description);
        }
        if (sort != null) {
            buildFilterSort(sort, orderByClauseBuilder);
        }

        query.append(whereClauseBuilder.build()).append(orderByClauseBuilder.build()).append(SEMICOLON);
        return query.toString();
    }

    private void buildFilterSort(String sort, ClauseBuilder orderByClauseBuilder) {
        String[] sortParams = sort.split(COMMA);
        Arrays.stream(sortParams).forEach(sortParam -> {
            String[] paramOrderPair = sortParam.split(DOT);
            String orderParam = Objects.equals(paramOrderPair[1], ASCENDING_PARAM) ? ASCENDING : DESCENDING;

            String columnParam;
            if (Objects.equals(paramOrderPair[0], NAME_PARAM)) {
                columnParam = CERTIFICATES_NAME;
            } else {
                columnParam = LAST_UPDATE_DATE_PARAM;
            }
            orderByClauseBuilder.addPart(String.format(ORDER_BY_CLAUSE_PATTERN, columnParam, orderParam));
        });
    }
}
