package com.chocobo.esm.repository.impl;

import com.chocobo.esm.entity.GiftCertificate;
import com.chocobo.esm.entity.Tag;
import com.chocobo.esm.repository.GiftCertificateRepository;
import com.chocobo.esm.repository.OrderingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
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

    private static final String SELECT_BY_PARAMS = """
            SELECT DISTINCT gc.id, gc.name, description, price, duration, create_date, last_update_date
            FROM gift_certificates AS gc
            LEFT OUTER JOIN certificates_tags AS ct ON ct.certificate_id = gc.id
            LEFT OUTER JOIN tags ON ct.tag_id = tag.id;
            """;
    // TODO: 28.10.2021 complete query 

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
            DELETE FROM gift_certificate
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
    public List<GiftCertificate> filter(String tagName, String certificateName, String description,
                                        OrderingType orderByName, OrderingType orderByCreateDate) {
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
