import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Connection {
    public static MongoClient mongoClient;
    public static Scanner scanner = new Scanner(System.in);
    public static boolean userQuit = false;

    public static void main(String[] args) {
        boolean clientCreated = createMongoClient();
        if (!clientCreated) {
            System.out.println("Client could not be instantiated");
            System.exit(0);
        }

        do {
            try {
                System.out.print("Choose the following options" +
                        "\n1. Print databases as JSON" +
                        "\n2. Get cluster description" +
                        "\n3. Print collection documents to JSON" +
                        "\n4. Quit program" +
                        "\n\nYour choice: ");

                int choice = Integer.parseInt(scanner.next());

                switch (choice) {
                    case 1:
                        printDatabasesToJson();
                        break;
                    case 2:
                        printClusterDescription();
                        break;
                    case 3:
                        printDatabaseCollectionContentsToJson();
                        break;
                    case 4:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Please enter a number from 1 to 4");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Incorrect input!");
            }
        } while (!userQuit);
    }

    private static boolean createMongoClient() {
        Dotenv dotenv = Dotenv.load();
        ConnectionString connectionString = new ConnectionString(dotenv.get("MONGODB_CONNECTION_STRING"));
        try {
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();
            mongoClient = MongoClients.create(settings);
            return true;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static void printDatabasesToJson() {
        List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
        databases.forEach(database -> {
            System.out.println(database.toJson());
        });
    }

    private static void printDatabaseCollectionContentsToJson() {
        System.out.print("What database would you like to search? ");
        String databaseInput = scanner.next();

        try {
            MongoDatabase database = mongoClient.getDatabase(databaseInput);

            System.out.printf("What collection from %s would you like to search? ", databaseInput);
            String connectionInput = scanner.next();

            MongoCollection<Document> collection = database.getCollection(connectionInput);
            List<Document> collectionDocuments = collection.find().into(new ArrayList<>());
            collectionDocuments.forEach(document -> {
                System.out.println(document.toJson());
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printClusterDescription() {
        System.out.println("The description of this cluster is: " + mongoClient.getClusterDescription().getShortDescription() + "\n");
    }
}
