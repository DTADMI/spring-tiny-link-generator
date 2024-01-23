package ca.dtadmi.tinylink.dao;

import ca.dtadmi.tinylink.model.UrlPair;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlPairMongoRepository extends MongoRepository<UrlPair, String> {
}
