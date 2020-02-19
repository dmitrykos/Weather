package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Region;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ForecasterClient
{
    public static final long TTL = 60*60;

    private class ForecastEntry
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

    // source: http://www.baeldung.com/java-linked-hashmap
    private class LimitedHashMap<K, V> extends LinkedHashMap<K, V>
    {
        private int maxSize;

        public LimitedHashMap(int maxSize)
        {
            super(16, 0.75f, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest)
        {
            return (size() >= maxSize);
        }
    }

    private ForecasterInterface forecaster = null;
    private LimitedHashMap<Long, ForecastEntry> cache = null;
    private int maxCacheSize = 0;
    private Clock clock = null;

    public ForecasterClient(ForecasterInterface forecaster, int maxCacheSize, Clock clock)
    {
        this.forecaster = forecaster;
        this.maxCacheSize = maxCacheSize;
        this.clock = clock;

        cache = new LimitedHashMap<Long, ForecastEntry>(maxCacheSize);
    }

    private void removeExpiredEntries()
    {
        if (cache.isEmpty())
            return;

        ArrayList<Long> queue = new ArrayList<Long>();

        long timeNow = clock.getTimeMs();
        for (ForecastEntry itr : cache.values())
        {
            if (timeNow > (itr.timestamp + TTL))
                queue.add(itr.guid);
        }

        for (Long itr : queue)
            cache.remove(itr);
    }

    public Forecast forecastFor(Region region, Day day) {

        long guid = getHashValue(region, day);

        removeExpiredEntries();

        ForecastEntry entry = cache.get(guid);
        if (entry == null)
        {
            Forecast forecast = forecaster.forecastFor(region, day);
            if (forecast == null)
                return null;

            cache.put(guid, entry = new ForecastEntry(guid, forecast));
        }

        return entry.forecast;
    }

    private long getHashValue(Region region, Day day)
    {
        return (long)region.hashCode() | ((long)day.hashCode() << 32);
    }
}
