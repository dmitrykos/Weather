package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Region;

import java.util.HashMap;

public class ForecasterClient
{
    public ForecasterInterface forecaster = null;
    HashMap<Long, Forecast> cache = new HashMap<Long, Forecast>();

    ForecasterClient(ForecasterInterface wrapper)
    {
        forecaster = wrapper;
    }

    public Forecast forecastFor(Region region, Day day)
    {
        if (isCached(region, day))
        {
            //cache
        }

        return forecaster.forecastFor(region, day);
    }

    private boolean isCached(Region region, Day day)
    {
        long hash = getHashValue(region, day);



        return false;
    }

    private Long getHashValue(Region region, Day day)
    {
        long index = (long)region.hashCode() | ((long)day.hashCode()) << 32;
        return index;
    }

}
