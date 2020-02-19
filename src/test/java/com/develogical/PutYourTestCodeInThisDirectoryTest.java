package com.develogical;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Region;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

public class PutYourTestCodeInThisDirectoryTest
{
    ClockImpl clock = new ClockImpl();
    ForecasterInterface wrapper = mock(ForecasterInterface.class);
    ForecasterClient client = new ForecasterClient(wrapper, 2, clock);

    @Test
    public void createForecastClientUsesWrapperDelegate() throws Exception
    {
        when(wrapper.forecastFor(Region.LONDON, Day.MONDAY)).thenReturn(new Forecast("sunny", 10));

        Forecast forecast = client.forecastFor(Region.LONDON, Day.MONDAY);

        assertThat(forecast.summary(), equalTo("sunny"));
        assertThat(forecast.temperature(), equalTo(10));
    }

    @Test
    public void createForecastCachingforDuplicateQueries() throws Exception
    {
        when(wrapper.forecastFor(Region.LONDON, Day.MONDAY)).thenReturn(new Forecast("sunny", 10));

        client.forecastFor(Region.LONDON, Day.MONDAY);
        client.forecastFor(Region.LONDON, Day.MONDAY);

        verify(wrapper, times(1)).forecastFor(Region.LONDON, Day.MONDAY);
    }

    @Test
    public void testCacheExpiry() throws Exception
    {
        when(wrapper.forecastFor(Region.LONDON, Day.MONDAY)).thenReturn(new Forecast("sunny", 10));
        when(wrapper.forecastFor(Region.LONDON, Day.FRIDAY)).thenReturn(new Forecast("sunny", 11));

        client.forecastFor(Region.LONDON, Day.MONDAY);
        client.forecastFor(Region.LONDON, Day.FRIDAY);
        client.forecastFor(Region.LONDON, Day.MONDAY);

        verify(wrapper, times(2)).forecastFor(Region.LONDON, Day.MONDAY);
    }

    @Test
    public void testCacheExpiryAfterHour() throws Exception
    {
        Clock clock = mock(Clock.class);

        ForecasterClient client1 = new ForecasterClient(wrapper, 2, clock);

        when(clock.getTimeMs()).thenReturn(1L);

        when(wrapper.forecastFor(Region.LONDON, Day.MONDAY)).thenReturn(new Forecast("sunny", 10));

        client1.forecastFor(Region.LONDON, Day.MONDAY);

        // 1 hour passed
        when(clock.getTimeMs()).thenReturn(client1.TTL + 5L);

        client1.forecastFor(Region.LONDON, Day.MONDAY);

        verify(wrapper, times(2)).forecastFor(Region.LONDON, Day.MONDAY);
    }

    @Test
    public void testNoCacheBehavior() throws Exception
    {
        ForecasterClient client1 = new ForecasterClient(wrapper, 0, clock);

        when(wrapper.forecastFor(Region.LONDON, Day.MONDAY)).thenReturn(new Forecast("sunny", 10));

        client1.forecastFor(Region.LONDON, Day.MONDAY);
        client1.forecastFor(Region.LONDON, Day.MONDAY);

        verify(wrapper, times(2)).forecastFor(Region.LONDON, Day.MONDAY);
    }
}
