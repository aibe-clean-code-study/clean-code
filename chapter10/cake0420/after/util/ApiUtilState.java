package util;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpClient;
import java.util.logging.Logger;

public abstract class ApiUtilState {
    public final Logger logger = Logger.getLogger(this.getClass().getName());
    public final HttpClient client = HttpClient.newHttpClient();
    public final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

}
