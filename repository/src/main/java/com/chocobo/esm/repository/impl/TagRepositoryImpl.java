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
            FROM tags;
            """;

    private static final String SELECT_BY_ID = """
            SELECT id, name
            FROM tags
            WHERE id = :id;
            """;

    private static final String SELECT_BY_NAME = """
            SELECT id, name
            FROM tags
            WHERE name = :name;
            """;

    private static final String SELECT_BY_CERTIFICATE_ID = """
            SELECT tags.id, tags.name
            FROM tags
            INNER JOIN certificates_tags ON tags.id = certificates_tags.tag_id
            WHERE certificate_tags.certificate_id = :certificate_id;
            """;

    private static final String INSERT = """
            INSERT INTO tags (name)
            VALUES (:name);
            """;

    private static final String DELETE = """
            DELETE FROM tags
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
        // TODO: 28.10.2021 check if it is possible to inject source
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);
        List<Tag> tags = jdbcTemplate.query(SELECT_BY_ID, parameters, rowMapper);
        return Optional.ofNullable(tags.size() == 1 ? tags.get(0) : null);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(NAME_PARAM, name);
        List<Tag> tags = jdbcTemplate.query(SELECT_BY_NAME, parameters, rowMapper);
        return Optional.ofNullable(tags.size() == 1 ? tags.get(0) : null);
        // TODO: 28.10.2021 size != 0
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
        // TODO: 28.10.2021 check for jdbc template methods returning key
        Number generatedKey = Objects.requireNonNull(keyHolder).getKey();
        return Objects.requireNonNull(generatedKey).longValue();
    }

    @Override
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);
        return jdbcTemplate.update(DELETE, parameters) > 0;
    }
}
