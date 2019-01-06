package com.zhangfang.hive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HiveManage {
    private static final Logger LOGGER = LoggerFactory.getLogger(HiveManage.class);

    private static final String URLHIVE = "jdbc:hive2://127.0.0.1:10001/default";
    private static Connection connection = null;


    public static Connection getHiveConnection() {
        if (null == connection) {
            synchronized (HiveManage.class) {
                if (null == connection) {
                    try {
                        Class.forName("org.apache.hive.jdbc.HiveDriver");
                        connection = DriverManager.getConnection(URLHIVE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return connection;
    }

    public static void main(String args[]) throws SQLException {

        String sql= "select * from default.student";
        PreparedStatement pstm = getHiveConnection().prepareStatement(sql);
        ResultSet rs = pstm.executeQuery(sql);

        while (rs.next()) {
            LOGGER.info(rs.getString(1) + "	" + rs.getString(2) +
                    "		" + rs.getString(3) + "		" + rs.getString(4));
        }
        pstm.close();
        rs.close();
    }
}
