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

public class PutYourTestCodeInThisDirectoryTest {
    ForecasterInterface wrapper = mock(ForecasterInterface.class);
    ForecasterClient client = new ForecasterClient(wrapper);

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



}
