package com.epam.esm.repository.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
            WHERE name LIKE CONCAT('%', :name, '%');
            """;

    private static final String SELECT_BY_CERTIFICATE_ID = """
            SELECT tags.id, tags.name
            FROM tags
            INNER JOIN certificates_tags ON tags.id = certificates_tags.tag_id
            WHERE certificates_tags.certificate_id = :certificate_id;
            """;

    private static final String INSERT = """
            INSERT INTO tags (name)
            VALUES (:name);
            """;

    private static final String DELETE = """
            DELETE FROM tags
            WHERE id = :id;
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final BeanPropertyRowMapper<Tag> rowMapper;

    public TagRepositoryImpl(DataSource dataSource, BeanPropertyRowMapper<Tag> rowMapper) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
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
        return Optional.ofNullable(tags.size() != 0 ? tags.get(0) : null);
    }

    @Override
    public Set<Tag> findByCertificateId(long certificateId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(CERTIFICATE_ID_PARAM, certificateId);
        return new HashSet<>(jdbcTemplate.query(SELECT_BY_CERTIFICATE_ID, parameters, rowMapper));
    }

    @Override
    public long create(Tag tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(NAME_PARAM, tag.getName());
        jdbcTemplate.update(INSERT, parameters, keyHolder, new String[] { "id" });
        Number generatedKey = Objects.requireNonNull(keyHolder).getKey();
        return Objects.requireNonNull(generatedKey).longValue();
    }

    @Override
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);
        return jdbcTemplate.update(DELETE, parameters) > 0;
    }
}
