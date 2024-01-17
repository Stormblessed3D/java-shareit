package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> userJacksonTester;

    @Test
    @SneakyThrows
    void testSerialization() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();

        JsonContent<UserDto> jsonContent = userJacksonTester.write(userDto);
        assertThat(jsonContent)
                .extractingJsonPathNumberValue("$.id")
                .isEqualTo(userDto.getId().intValue());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.name")
                .isEqualTo(userDto.getName());
        assertThat(jsonContent)
                .extractingJsonPathStringValue("$.email")
                .isEqualTo(userDto.getEmail());
    }

    @Test
    @SneakyThrows
    void testDeSerialization() {
        String jsonValue = "{\"id\":\"1\",\"name\":\"name\",\"email\":\"email@gmail.com\"}";
        UserDto expectedUser = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@gmail.com")
                .build();

        UserDto deSerializedUser = userJacksonTester.parseObject(jsonValue);

        assertThat(deSerializedUser.getId()).isEqualTo(expectedUser.getId());
        assertThat(deSerializedUser.getName()).isEqualTo(expectedUser.getName());
        assertThat(deSerializedUser.getEmail()).isEqualTo(expectedUser.getEmail());
    }
}