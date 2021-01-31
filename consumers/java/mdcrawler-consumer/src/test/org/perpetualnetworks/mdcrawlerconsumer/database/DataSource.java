package org.perpetualnetworks.mdcrawlerconsumer.database;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;

@Slf4j
//@SpringBootTest
public class DataSource {

    @Disabled("works with local db)
    @Test
    void bob () {
        String sqlSelectAllPersons = "SELECT * FROM api_keyword";

        String connectionUrl = "jdbc:mysql://localhost:3306/mdcrawler_consumer_d?serverTimezone=UTC";

        try {
            Connection conn = DriverManager.getConnection(connectionUrl);
             PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
             ResultSet rs = ps.executeQuery();

            System.out.println("result set: " + rs);
            while (rs.next()) {
                long id = rs.getLong("ID");
                System.out.println(id);
            }
        } catch (SQLException e) {
            log.error("unable to complete request", e);
        }
    }
}
