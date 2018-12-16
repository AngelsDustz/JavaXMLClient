package utils;

import models.Measurement;
import java.sql.*;
import java.util.Properties;

public class DatabaseHelper {
    private Connection dbcon;
    private int[] stationCache;
    private Measurement[] measureCache;
    private boolean cacheWarmed;

    public DatabaseHelper() {
        Properties dbinfo;

        this.dbcon          = null;
        dbinfo              = new Properties();
        this.stationCache   = new int[8000];
        this.measureCache   = new Measurement[30];
        this.cacheWarmed    = false;

        dbinfo.put("user", "root");
        dbinfo.put("password", "root");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            this.dbcon = DriverManager.getConnection("jdbc:mariadb://localhost:3306/unwdmi", dbinfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.getStationCache();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Inserts all records from an XMLHelper.
     *
     * @param helper
     */
    public void insert(XMLHelper helper) {
        for (Measurement measurement : helper.getDataArray()) {
            // check if station id is legit.
            if (!this.checkStation(measurement.getStation())) {
                System.out.println("Received record with non-existing station ID.");
                // Not legit so skip.
                continue;
            }

            try {
                this.processRecord(measurement);
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }

    private void processRecord(Measurement measurement) throws SQLException {
        // save 30 cache items for extrapolation.
        // Check all values if one or more is not set extrapolate.
        // Set the value to the extrapolated value and insert.

        if (!measurement.isValid()) {
            if (!this.cacheWarmed) {
                // If we have an invalid record, and not enough cached record for extrapolation drop.
                return;
            }

            // Fix missing items.
            measurement = this.repairRecord(measurement);
        }

        // Insert record.
        this.addToCache(measurement);
        this.insertMeasurement(measurement);
    }

    private void addToCache(Measurement measurement) {
        // Check if cache is full.
        if (measurement.isValid()) {
            if (this.cacheWarmed) {
                // Cache warmed.
                // Shift array.
                this.shiftCacheArray();
                // Insert at pos 0.
                this.measureCache[0] = measurement;
            } else {
                int firstFree = this.findFirstFreeInCache();
                this.measureCache[firstFree] = measurement;
            }
        }
    }

    private int findFirstFreeInCache() {
        for (int i=0;i<this.measureCache.length;i++) {
            if (this.measureCache[i] == null) {
                return i;
            }
        }

        this.cacheWarmed = true;
        return 0;
    }

    private void shiftCacheArray() {
        for (int i=this.measureCache.length-1;i>0;i--) {
            this.measureCache[i] = this.measureCache[i-1];
        }

        this.measureCache[0] = null;
    }

    // @TODO fix performance issues?
    private void insertMeasurement(Measurement measurement) throws SQLException {
        // Insert into database.
        if (this.dbcon != null) {
            String query = "INSERT INTO `measurements`" +
                    "(`unwdmi_id`, `temp`, `dewp`, `stp`, `slp`, `visibility`, `wind_speed`, `prcp`, `sndp`, `cloud`, `wind_dir`, `ev_freeze`, `ev_rain`, `ev_snow`, `ev_hail`, `ev_thunder`, `ev_tornado`, `measured_at`, `created_at`) VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
            PreparedStatement insertStatement = this.dbcon.prepareStatement(query);
            /*
                1. unwdmi_id
                2. temp
                3. dewp
                4. stp
                5. slp
                6. visibility
                7. wind_speed
                8. prcp
                9. sndp
                10. cloud
                11. wind_dir
                12. ev_freeze
                13. ev_rain
                14. ev_snow
                15. ev_hail
                16. ev_thunder
                17. ev_tornado
                18. measured_at
             */

            insertStatement.setInt(1, measurement.getStation());
            insertStatement.setFloat(2, measurement.getTemp());
            insertStatement.setFloat(3, measurement.getDewp());
            insertStatement.setFloat(4, measurement.getStp());
            insertStatement.setFloat(5, measurement.getSlp());
            insertStatement.setFloat(6, measurement.getVisib());
            insertStatement.setFloat(7, measurement.getWdsp());
            insertStatement.setFloat(8, measurement.getPrcp());
            insertStatement.setFloat(9, measurement.getSndp());
            insertStatement.setFloat(10, measurement.getCldc());
            insertStatement.setFloat(11, measurement.getWinddir());
            insertStatement.setBoolean(12, measurement.isFrost());
            insertStatement.setBoolean(13, measurement.isRain());
            insertStatement.setBoolean(14, measurement.isSnow());
            insertStatement.setBoolean(15, measurement.isHail());
            insertStatement.setBoolean(16, measurement.isThunder());
            insertStatement.setBoolean(17, measurement.isTornado());
            insertStatement.setString(18, measurement.getDate() + " " + measurement.getTime());

            insertStatement.execute();
        }
    }

    private Measurement repairRecord(Measurement measurement) {
        // Generate all missing values.
        int avgWinddir=0, varWinddir=0, avgCount=0, varCount=0;
        int avgTemp=0, avgDewp=0, avgStp=0, avgSlp=0, avgVisib=0, avgWdsp=0, avgPrcp=0, avgSndp=0, avgCldc=0;
        int varTemp=0, varDewp=0, varStp=0, varSlp=0, varVisib=0, varWdsp=0, varPrcp=0, varSndp=0, varCldc=0;

        for (int i=0;i<this.measureCache.length;i++) {
            Measurement m = this.measureCache[i];

            if (i<this.measureCache.length-1) {
                Measurement mn = this.measureCache[i+1];
                // Set differences
                varWinddir  += Math.abs((mn.getWinddir() - m.getWinddir()));
                varTemp     += Math.abs((mn.getTemp() - m.getTemp()));
                varDewp     += Math.abs((mn.getDewp() - m.getDewp()));
                varStp      += Math.abs((mn.getStp() - m.getStp()));
                varSlp      += Math.abs((mn.getSlp() - m.getSlp()));
                varVisib    += Math.abs((mn.getVisib() - m.getVisib()));
                varWdsp     += Math.abs((mn.getWdsp() - m.getWdsp()));
                varPrcp     += Math.abs((mn.getPrcp() - m.getPrcp()));
                varSndp     += Math.abs((mn.getSndp() - m.getSndp()));
                varCldc     += Math.abs((mn.getCldc() - m.getCldc()));

                varCount++; //Increase variation count.
            }

            // Set averages.
            avgWinddir  += m.getWinddir();
            avgTemp     += m.getTemp();
            avgDewp     += m.getDewp();
            avgStp      += m.getStp();
            avgSlp      += m.getSlp();
            avgVisib    += m.getVisib();
            avgWdsp     += m.getWdsp();
            avgPrcp     += m.getPrcp();
            avgSndp     += m.getSndp();
            avgCldc     += m.getCldc();

            avgCount++; //Increase average count.
        }

        if (measurement.getWinddir() == 0) {
            float avg = (float) avgWinddir / (float) avgCount;
            float var = (float) varWinddir / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setWinddir((int) newVal);
        }

        if (measurement.getTemp() == 0.0f) {
            float avg = (float) avgTemp / (float) avgCount;
            float var = (float) varTemp / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setTemp(newVal);
        }

        if (measurement.getDewp() == 0.0f) {
            float avg = (float) avgDewp / (float) avgCount;
            float var = (float) varDewp / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setDewp(newVal);
        }

        if (measurement.getStp() == 0.0f) {
            float avg = (float) avgStp / (float) avgCount;
            float var = (float) varStp / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setStp(newVal);
        }

        if (measurement.getSlp() == 0.0f) {
            float avg = (float) avgSlp / (float) avgCount;
            float var = (float) varSlp / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setSlp(newVal);
        }

        if (measurement.getVisib() == 0.0f) {
            float avg = (float) avgVisib / (float) avgCount;
            float var = (float) varVisib / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setVisib(newVal);
        }

        if (measurement.getWdsp() == 0.0f) {
            float avg = (float) avgWdsp / (float) avgCount;
            float var = (float) varWdsp / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setWdsp(newVal);
        }

        if (measurement.getPrcp() == 0.0f) {
            float avg = (float) avgPrcp / (float) avgCount;
            float var = (float) varPrcp / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setPrcp(newVal);
        }

        if (measurement.getSndp() == 0.0f) {
            float avg = (float) avgSndp / (float) avgCount;
            float var = (float) varSndp / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setSndp(newVal);
        }

        if (measurement.getCldc() == 0.0f) {
            float avg = (float) avgCldc / (float) avgCount;
            float var = (float) varCldc / (float) varCount;
            float newVal = avg+var; // Average + variation = new value.

            measurement.setCldc(newVal);
        }

        measurement.checkValidity();

        return measurement;
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
