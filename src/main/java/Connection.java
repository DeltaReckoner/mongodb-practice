import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Connection {
    private final String MONGODB_STRING = "";
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        ConnectionString connectionString = new ConnectionString(dotenv.get("MONGODB_CONNECTION_STRING"));
        try {
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();
            MongoClient client = MongoClients.create(settings);
            List<Document> databases = client.listDatabases().into(new ArrayList<>());
            databases.forEach(database -> {
                System.out.println(database.toJson());
            });
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
