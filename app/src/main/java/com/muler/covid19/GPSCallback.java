package com.muler.covid19;

import android.location.Location;

public interface GPSCallback
{
        public abstract void onGPSUpdate(Location location);
}