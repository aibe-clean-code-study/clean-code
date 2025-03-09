import java.io.IOException;
import java.text.ParseException;

public interface Weather {
    String WeatherAPI(String weatherApiKey) throws IOException, ParseException;
}
