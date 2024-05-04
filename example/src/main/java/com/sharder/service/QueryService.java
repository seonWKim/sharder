package com.sharder.service;

import java.util.List;
import java.util.Map;

public interface QueryService {
    List<Map<String, Object>> select(String query);

    boolean insert(String query);

    boolean update(String query);

    boolean delete(String query);
}
