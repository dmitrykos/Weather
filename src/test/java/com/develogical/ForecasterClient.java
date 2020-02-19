package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Region;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

public class ForecasterClient
{
    public static final long TTL = 60*60;

    class ForecastEntry
    {
        long guid;
        Forecast forecast;
        long timestamp;

        public ForecastEntry(long guid, Forecast forecast)
        {
            this.guid = guid;
            this.forecast = forecast;
            this.timestamp = clock.getTimeMs();
        }
    }

    public ForecasterInterface forecaster = null;
    ArrayList<ForecastEntry> cache = new ArrayList<ForecastEntry>();
    int maxCacheSize = 0;
    Clock clock = null;

    public ForecasterClient(ForecasterInterface forecaster, int maxCacheSize, Clock clock)
    {
        this.forecaster = forecaster;
        this.maxCacheSize = maxCacheSize;
        this.clock = clock;
    }

    private void removeExpiredEntries()
    {
        ArrayList<ForecastEntry> queue = new ArrayList<ForecastEntry>();

        long timeNow = clock.getTimeMs();
        for (ForecastEntry itr : cache)
        {
            if (timeNow > (itr.timestamp + TTL))
                queue.add(itr);
        }

        cache.removeAll(queue);
    }

    public Forecast forecastFor(Region region, Day day) {

        long guid = getHashValue(region, day);

        if (!cache.isEmpty() && (cache.size() >= maxCacheSize))
            cache.remove(0);

        removeExpiredEntries();

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
