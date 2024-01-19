package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserForBookingDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoToReturnTest {
    @Autowired
    private JacksonTester<BookingDtoToReturn> bookingJacksonTester;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        JavaTimeModule module = new JavaTimeModule();
        LocalDateTimeDeserializer localDateTimeDeserializer =
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        mapper = Jackson2ObjectMapperBuilder.json()
                .modules(module)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Test
    @SneakyThrows
    void testDeSerialization() {
        String jsonValue = "{\"id\":\"1\",\"start\":\"2016-11-08 12:00\",\"end\":\"2016-11-08 12:00\",\"status\":\"WAITING\", " +
                "\"booker\":{\"id\":\"1\"}}";
        BookingDtoToReturn expectedBooking = BookingDtoToReturn.builder()
                .id(1L)
                .start(LocalDateTime.of(2016,11,8,12,00))
                .end(LocalDateTime.of(2016,11,8,12,00))
                .status(BookingStatus.WAITING)
                .booker(new UserForBookingDto(1))
                .item(null)
                .build();

        BookingDtoToReturn actualBooking = mapper.readValue(jsonValue, BookingDtoToReturn.class);

        assertThat(actualBooking.getId()).isEqualTo(expectedBooking.getId());
        assertThat(LocalDateTime.parse("2016-11-08 12:00",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .isEqualTo(expectedBooking.getStart());
        assertThat(LocalDateTime.parse("2016-11-08 12:00",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .isEqualTo(expectedBooking.getEnd());
        assertThat(actualBooking.getStatus()).isEqualTo(expectedBooking.getStatus());
        assertThat(actualBooking.getBooker()).isEqualTo(expectedBooking.getBooker());
        assertThat(actualBooking.getItem()).isEqualTo(expectedBooking.getItem());
    }
}