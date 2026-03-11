package com.shop.aishop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class DbTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/testdb")
    public String testDb() {
        try (Connection conn = dataSource.getConnection()) {
            return "Successfully connected to DB! Catalog: " + conn.getCatalog();
        } catch (Exception e) {
            return "DB Connection failed: " + e.getMessage();
        }
    }
}
