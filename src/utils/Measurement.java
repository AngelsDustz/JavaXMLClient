package utils;

public class Measurement {
    private int     station, winddir;
    private String  date, time;
    private float   temp, dewp, stp, slp, visib, wdsp, prcp, sndp, cldc;
    private boolean frost, rain, snow, hail, thunder, tornado;

    public Measurement() {
    }

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    public int getWinddir() {
        return winddir;
    }

    public void setWinddir(int winddir) {
        this.winddir = winddir;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getDewp() {
        return dewp;
    }

    public void setDewp(float dewp) {
        this.dewp = dewp;
    }

    public float getStp() {
        return stp;
    }

    public void setStp(float stp) {
        this.stp = stp;
    }

    public float getSlp() {
        return slp;
    }

    public void setSlp(float slp) {
        this.slp = slp;
    }

    public float getVisib() {
        return visib;
    }

    public void setVisib(float visib) {
        this.visib = visib;
    }

    public float getWdsp() {
        return wdsp;
    }

    public void setWdsp(float wdsp) {
        this.wdsp = wdsp;
    }

    public float getPrcp() {
        return prcp;
    }

    public void setPrcp(float prcp) {
        this.prcp = prcp;
    }

    public float getSndp() {
        return sndp;
    }

    public void setSndp(float sndp) {
        this.sndp = sndp;
    }

    public float getCldc() {
        return cldc;
    }

    public void setCldc(float cldc) {
        this.cldc = cldc;
    }

    public boolean isFrost() {
        return frost;
    }

    public void setFrost(boolean frost) {
        this.frost = frost;
    }

    public boolean isRain() {
        return rain;
    }

    public void setRain(boolean rain) {
        this.rain = rain;
    }

    public boolean isSnow() {
        return snow;
    }

    public void setSnow(boolean snow) {
        this.snow = snow;
    }

    public boolean isHail() {
        return hail;
    }

    public void setHail(boolean hail) {
        this.hail = hail;
    }

    public boolean isThunder() {
        return thunder;
    }

    public void setThunder(boolean thunder) {
        this.thunder = thunder;
    }

    public boolean isTornado() {
        return tornado;
    }

    public void setTornado(boolean tornado) {
        this.tornado = tornado;
    }
}
