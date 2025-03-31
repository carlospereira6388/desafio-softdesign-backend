package br.com.softdesign.desafio.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
	
    <T> void save(String key, T value, long timeout, TimeUnit unit);
    <T> T get(String key, Class<T> clazz);
    void delete(String key);
}
