package com.vesit.imaginar.one;

public class SolarSettings{
    private float orbitSpeedMultiplier = 1.0f;
    private float rotationSpeedMultiplier = 1.0f;

    public void setOrbitSpeedMultiplier(float orbitSpeedMultiplier) {
        this.orbitSpeedMultiplier = orbitSpeedMultiplier;
    }

    public float getOrbitSpeedMultiplier() {
        return orbitSpeedMultiplier;
    }

    public void setRotationSpeedMultiplier(float rotationSpeedMultiplier) {
        this.rotationSpeedMultiplier = rotationSpeedMultiplier;
    }

    public float getRotationSpeedMultiplier() {
        return rotationSpeedMultiplier;
    }
}
