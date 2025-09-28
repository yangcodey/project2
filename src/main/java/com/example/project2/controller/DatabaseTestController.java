package com.example.project2.controller;

import com.example.project2.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库测试控制器
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/database")
    public Result<String> testDatabase() {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT 1");
            if (resultSet.next()) {
                return Result.success("数据库连接成功");
            }
            connection.close();
        } catch (Exception e) {
            return Result.error("数据库连接失败: " + e.getMessage());
        }
        return Result.error("数据库连接失败");
    }

    @GetMapping("/tables")
    public Result<List<String>> testTables() {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            List<String> tables = new ArrayList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            connection.close();
            return Result.success(tables);
        } catch (Exception e) {
            return Result.error("查询表失败: " + e.getMessage());
        }
    }
}
