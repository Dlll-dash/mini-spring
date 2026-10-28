package com.mini.spring;

public interface Environment {

    String getProperty(String key);

    String resolvePlaceholders(String text);

}
