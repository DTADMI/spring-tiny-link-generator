package ca.dtadmi.tinylink.dao;

import ca.dtadmi.tinylink.exceptions.FirestoreExcecutionException;
import ca.dtadmi.tinylink.firebase.FirebaseInitialization;
import ca.dtadmi.tinylink.model.UrlPair;
import ca.dtadmi.tinylink.service.MarshallService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class UrlPairDao implements Dao<UrlPair> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Optional<UrlPair> get(String longUrl) {
        try {
            DocumentReference docRef = FirebaseInitialization.getUrlsCollection().document(longUrl);
            // asynchronously retrieve the document
            ApiFuture<DocumentSnapshot> future = docRef.get();

            // future.get() blocks on response
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                logger.debug("Document data: {}", document.getData());
                return Optional.ofNullable(MarshallService.getUrlPairFromFirestore(document.getData()));
            } else {
                logger.debug("No such document!");
                return Optional.empty();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (ExecutionException e) {
            throw new FirestoreExcecutionException(e.getMessage(), e);
        }
    }

    @Override
    public Collection<UrlPair> getAll() {
        try {
            // asynchronously retrieve all documents
            ApiFuture<QuerySnapshot> future = FirebaseInitialization.getUrlsCollection().get();
            // future.get() blocks on response
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map((QueryDocumentSnapshot document) -> document.toObject(UrlPair.class))
                    .toList()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } catch (ExecutionException e) {
            throw new FirestoreExcecutionException(e.getMessage(), e);
        }
    }

    @Override
    public UrlPair save(UrlPair urlPair) {
        try {
            UrlPair newUrlPair = new UrlPair(urlPair);
            // Add document data with long url as id.
            DocumentReference addedDocRef = FirebaseInitialization.getUrlsCollection().document(newUrlPair.getLongUrl());
            ApiFuture<WriteResult> result = addedDocRef.set(newUrlPair);
            WriteResult writeResult = result.get();
            logger.debug("Document added at: {}", writeResult.getUpdateTime());


            return MarshallService.getUrlPairFromFirestore(addedDocRef.get().get().getData());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new FirestoreExcecutionException(e.getMessage(), e);
        }
    }
}
