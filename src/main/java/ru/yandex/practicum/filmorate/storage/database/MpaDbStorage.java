package ru.yandex.practicum.filmorate.storage.database;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceprions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class MpaDbStorage implements MpaStorage {

    JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa create(Mpa mpa) {
        return null;
    }

    @Override
    public Mpa updateOrCreate(Mpa mpa) {
        return null;
    }

    @Override
    public List<Mpa> getAll() {
        String findAllMpa = "SELECT rate_id, name " +
                "FROM rate ORDER BY rate_id";
        return jdbcTemplate.query(findAllMpa, this::mapRowToMpa);
    }

    @Override
    public Mpa getById(int id) {
        String findAllMpa = "SELECT rate_id, name " +
                "FROM rate WHERE rate_id = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(findAllMpa, this::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException(String.format("MPA  с id = %d не найден", id));
        }
        return mpa;
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("rate_id"))
                .name(rs.getString("name"))
                .build();
    }
}
