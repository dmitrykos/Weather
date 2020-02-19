package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Region;

import java.util.ArrayList;
import java.util.HashMap;

public class ForecasterClient
{
    class ForecastEntry
    {
        long guid;
        Forecast forecast;

        public ForecastEntry(long guid, Forecast forecast) {
            this.guid = guid;
            this.forecast = forecast;
        }
    }

    public ForecasterInterface forecaster = null;
    ArrayList<ForecastEntry> cache = new ArrayList<ForecastEntry>();
    int maxCacheSize = 0;

    ForecasterClient(ForecasterInterface wrapper)
    {
        forecaster = wrapper;
    }

    public ForecasterClient(ForecasterInterface wrapper, int maxCacheSize)
    {
        forecaster = wrapper;
        this.maxCacheSize = maxCacheSize;
    }

    public Forecast forecastFor(Region region, Day day) {

        long guid = getHashValue(region, day);

        if (cache.size() >= maxCacheSize)
            cache.remove(0);

        Forecast forecast = null;
        for (ForecastEntry itr : cache)
        {
            if ((long)itr.guid == (long)guid)
            {
                forecast = itr.forecast;
                break;
            }
        }

        if (forecast == null)
        {
            forecast = forecaster.forecastFor(region, day);

            cache.add(new ForecastEntry(guid, forecast));
        }

        return forecast;
    }

    private long getHashValue(Region region, Day day)
    {
        return (long)region.hashCode() | ((long)day.hashCode()) << 32;
    }

}
