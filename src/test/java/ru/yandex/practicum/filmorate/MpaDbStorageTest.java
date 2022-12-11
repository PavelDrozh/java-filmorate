package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceprions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.database.MpaDbStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaDbStorageTest {

    MpaDbStorage mpaDbStorage;
    ResourceSupplier rs;

    @Test
    public void getAllMpaTest() {
        List<Mpa> realMpa = mpaDbStorage.getAll();
        assertThat(realMpa).isEqualTo(rs.getExpectedMpa());
    }

    @Test
    public void getMpaByIdTest() {
        Mpa realMpa = mpaDbStorage.getById(3);
        assertThat(realMpa).isEqualTo(rs.getExpectedMpa().get(2));
    }

    @Test
    public void getMpaByIncorrectIdTest() {
        assertThatThrownBy(() -> mpaDbStorage.getById(1000))
                .isInstanceOf(MpaNotFoundException.class);
    }
}
