package ru.practicum.shareit.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantKeeperTest {

    @Test
    void testConstant() {
        assertEquals("X-Sharer-User-Id", ConstantKeeper.USER_REQUEST_HEADER);
    }

}