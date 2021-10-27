package com.chocobo.esm.mapper;

import com.chocobo.esm.entity.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.chocobo.esm.mapper.TableColumn.*;

public class TagMapper implements RowMapper<Tag> {

    @Override
    public Tag mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        Tag tag = new Tag();

        tag.setId(resultSet.getLong(ID));
        tag.setName(resultSet.getString(NAME));

        return tag;
    }
}
