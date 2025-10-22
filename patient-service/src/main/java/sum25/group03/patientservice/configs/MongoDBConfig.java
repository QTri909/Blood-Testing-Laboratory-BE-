package sum25.group03.patientservice.configs;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDBConfig {

    /*
    @Value("${spring.data.mongodb.uri}")
    private String MONGODB_URI;

    @Value("${group3.mongodb.name}")
    private String DATABASE_NAME;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(MONGODB_URI);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), DATABASE_NAME);
    }
    */
}
