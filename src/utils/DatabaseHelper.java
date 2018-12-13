package utils;

import java.sql.*;
import java.util.Properties;

public class DatabaseHelper {
    private Connection dbcon;
    private Properties dbinfo;
    private int[] stationCache;

    public DatabaseHelper() {
        this.dbcon          = null;
        this.dbinfo         = new Properties();
        this.stationCache   = new int[8000];

        this.dbinfo.put("user", "root");
        this.dbinfo.put("password", "root");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            this.dbcon = DriverManager.getConnection("jdbc:mariadb://localhost:3306/unwdmi", this.dbinfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.getStationCache();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public boolean insert(XMLHelper helper) {
        for (Measurement measurement : helper.getDataArray()) {
            // check if station id is legit.
            if (!this.checkStation(measurement.getStation())) {
                // Not legit so skip.
                continue;
            }

            try {
                this.processRecord(measurement);
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }


        return true;
    }

    private void processRecord(Measurement measurement) throws SQLException {
        //
    }

    private boolean checkStation(int stationId) {
        for (int i : this.stationCache) {
            if (i == stationId) {
                return true;
            }
        }

        return false;
    }

    private void getStationCache() throws SQLException {
        if (this.dbcon != null) {
            PreparedStatement selectStations = this.dbcon.prepareStatement("SELECT `stn` FROM `stations`");
            ResultSet resultSet = selectStations.executeQuery();

            int count = 0;
            while (resultSet.next()) {
                this.stationCache[count] = Integer.parseInt(resultSet.getString(1));
                count++;
            }
        }
    }
}
