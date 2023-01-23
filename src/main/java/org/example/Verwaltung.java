package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.model.BestellPosition;
import org.example.model.Bestellung;
import org.example.model.Produkt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Verwaltung {
    private static Logger LOG = LoggerFactory.getLogger(Verwaltung.class);


    // Will create connections
    private HikariDataSource ds;

    private static final String DB_URL = "jdbc:mariadb://localhost/th_eshop";
    private static final String USER = "th_eshop";
    private static final String PWD = System.getenv("TH_ESHOP_DB_PWD");

    public Verwaltung() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PWD);

        ds = new HikariDataSource(config);
        // Test connection
        ds.getConnection().createStatement().executeQuery("SELECT 1;");
    }

    public String getAnyUserFirstName()  {
        String sql = "SELECT vorname FROM Nutzer LIMIT ?";

        try(PreparedStatement stmt = ds.getConnection().prepareStatement(sql)){
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("vorname");
        } catch (SQLException e) {
            return null;
        }
    }

    public List<Bestellung> getOrders() {

        String sql = "SELECT *\n" +
                "FROM Bestellposition\n" +
                "\n" +
                "JOIN Bestellung ON Bestellposition.bestellung = Bestellung.id\n" +
                "JOIN Nutzer ON Bestellung.nutzer = Nutzer.id\n" +
                "JOIN Produkt ON Bestellposition.produkt = Produkt.id\n" +
                "JOIN Verkaeufer ON Bestellposition.produkt = Verkaeufer.id\n" +
                "\n" +
                "ORDER BY Bestellung.id ASC, Bestellposition.id ASC";

        try(PreparedStatement stmt = ds.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            List<Bestellung> bestellungen = new ArrayList<>();
            int i=0;
            while(rs.next()) {
                if(i < rs.getInt("Bestellung.id")) {
                    i++;
                    bestellungen.add(new Bestellung(rs));
                }
                bestellungen.get(i-1).addBestellPosition(new BestellPosition(rs));
            }
            return bestellungen;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

}
