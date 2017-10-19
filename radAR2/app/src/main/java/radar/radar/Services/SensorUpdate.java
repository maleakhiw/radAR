package radar.radar.Services;

class SensorUpdate {
    double azimuth;
    double pitch;

    public SensorUpdate(double azimuth, double pitch) {
        this.azimuth = azimuth;
        this.pitch = pitch;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }
}
