package com.meetup.hacktory.ghostdetector;

import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.estimote.sdk.cloud.model.BroadcastingPower;

import java.util.Date;

public class NearableModel {

    public NearableModel(Nearable nearable) {
        isMoving = nearable.isMoving;
        identifier = nearable.identifier;
        firmwareState = nearable.firmwareState;
        hardwareVersion = nearable.hardwareVersion;
        temperature = nearable.temperature;
        rssi = nearable.rssi;
        xAcceleration = nearable.xAcceleration;
        yAcceleration = nearable.yAcceleration;
        zAcceleration = nearable.zAcceleration;
        currentMotionStateDuration = nearable.currentMotionStateDuration;
        orientation = nearable.orientation;
        lastMotionStateDuration = nearable.lastMotionStateDuration;
        power = nearable.power;
        region = nearable.region;
        timestamp = new Date().getTime();
    }

    public final String identifier;
    public final String hardwareVersion;
    public final Nearable.FirmwareState firmwareState;
    public final double temperature;
    public final int rssi;
    public final boolean isMoving;
    public final double xAcceleration;
    public final double yAcceleration;
    public final double zAcceleration;
    public final Nearable.Orientation orientation;
    public final long currentMotionStateDuration;
    public final long lastMotionStateDuration;
    public final BroadcastingPower power;
    public final Region region;
    public final long timestamp;
}
