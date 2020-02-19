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

    public Forecast forecastFor(Region region, Day day) {

        Long guid = getHashValue(region, day);

        Forecast forecast = cache.get(guid);

        if (forecast == null)
        {
            forecast = forecaster.forecastFor(region, day);
            cache.put(guid, forecast);
        }

        return forecast;
    }

    private Long getHashValue(Region region, Day day)
    {
        long index = (long)region.hashCode() | ((long)day.hashCode()) << 32;
        return index;
    }

}
