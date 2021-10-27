package com.chocobo.esm.repository.impl;

import com.chocobo.esm.entity.Tag;
import com.chocobo.esm.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private static final String ID_PARAM = "id";
    private static final String NAME_PARAM = "name";
    private static final String CERTIFICATE_ID_PARAM = "certificate_id";

    private static final String SELECT_ALL = """
            SELECT id, name
            FROM tag;
            """;

    private static final String SELECT_BY_ID = """
            SELECT id, name
            FROM tag
            WHERE id = :id;
            """;

    private static final String SELECT_BY_NAME = """
            SELECT id, name
            FROM tag
            WHERE name = :name;
            """;

    private static final String SELECT_BY_CERTIFICATE_ID = """
            SELECT tag.id, tag.name
            FROM tag
            INNER JOIN certificate_tag ON tag.id = certificate_tag.id_tag
            WHERE certificate_tag.id_certificate = :certificate_id;
            """;

    private static final String INSERT = """
            INSERT INTO tag (name)
            VALUES (:name);
            """;

    private static final String DELETE = """
            DELETE FROM tag
            WHERE id = :id;
            """;

    private NamedParameterJdbcTemplate jdbcTemplate;
    private RowMapper<Tag> rowMapper;

    @Autowired
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Autowired
    public void setRowMapper(RowMapper<Tag> rowMapper) {
        this.rowMapper = rowMapper;
    }

    @Override
    public List<Tag> findAll() {
        return jdbcTemplate.query(SELECT_ALL, rowMapper);
    }

    @Override
    public Optional<Tag> findById(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);

        List<Tag> tags = jdbcTemplate.query(SELECT_BY_ID, parameters, rowMapper);
        return Optional.ofNullable(tags.size() == 1 ? tags.get(0) : null);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(NAME_PARAM, name);

        List<Tag> tags = jdbcTemplate.query(SELECT_BY_NAME, parameters, rowMapper);
        return Optional.ofNullable(tags.size() == 1 ? tags.get(0) : null);
    }

    @Override
    public List<Tag> findByCertificateId(long certificateId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(CERTIFICATE_ID_PARAM, certificateId);
        return jdbcTemplate.query(SELECT_BY_CERTIFICATE_ID, parameters, rowMapper);
    }

    @Override
    public long create(Tag tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(NAME_PARAM, tag.getName());
        jdbcTemplate.update(INSERT, parameters, keyHolder);

        Number generatedKey = Objects.requireNonNull(keyHolder).getKey();
        return Objects.requireNonNull(generatedKey).longValue();
    }

    @Override
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);

        return jdbcTemplate.update(DELETE, parameters) > 0;
    }
}
