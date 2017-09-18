package com.oxygen.radar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rtanudjaja on 19/09/17.
 */
public class MapsActivityTest {
    private GoogleMap mMap;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void onCreate() throws Exception {
    }

    @Test
    public void onMapReady() throws Exception {
        assertEquals(1,1);
    }

}