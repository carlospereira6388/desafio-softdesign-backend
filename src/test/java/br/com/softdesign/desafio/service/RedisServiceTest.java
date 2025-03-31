package br.com.softdesign.desafio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import br.com.softdesign.desafio.service.impl.RedisServiceImpl;

class RedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSave() {
        String key = "book:1";
        String value = "mocked value";

        redisService.save(key, value, 10, TimeUnit.MINUTES);

        verify(valueOperations).set(key, value, 10, TimeUnit.MINUTES);
    }

    @Test
    void testGetWhenValueExistsAndIsCorrectType() {
        String key = "book:1";
        String expected = "mocked value";

        when(valueOperations.get(key)).thenReturn(expected);

        String result = redisService.get(key, String.class);

        assertEquals(expected, result);
    }

    @Test
    void testGetWhenValueIsNull() {
        String key = "book:2";

        when(valueOperations.get(key)).thenReturn(null);

        String result = redisService.get(key, String.class);

        assertNull(result);
    }

    @Test
    void testGetWhenValueIsWrongType() {
        String key = "book:3";
        Object wrongType = 123;

        when(valueOperations.get(key)).thenReturn(wrongType);

        String result = redisService.get(key, String.class);

        assertNull(result);
    }

    @Test
    void testDelete() {
        String key = "book:1";

        redisService.delete(key);

        verify(redisTemplate).delete(key);
    }
}
