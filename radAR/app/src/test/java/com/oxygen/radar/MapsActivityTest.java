package com.oxygen.radar;

import com.google.android.gms.maps.GoogleMap;

import org.junit.Before;
import org.junit.Test;

import static android.provider.Settings.Global.getString;
import static org.junit.Assert.assertEquals;

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
        assertEquals(getString(R.string.melbourne_university_lat),"-37.7963689");
        assertEquals(getString(R.string.melbourne_university_lng),"144.9611738");
    }

}