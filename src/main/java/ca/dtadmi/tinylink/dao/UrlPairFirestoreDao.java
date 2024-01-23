package ca.dtadmi.tinylink.dao;

import ca.dtadmi.tinylink.exception.ApiRuntimeException;
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
public class UrlPairFirestoreDao implements Dao<UrlPair> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Optional<UrlPair> findById(String id) {
        try {
            DocumentReference docRef = FirebaseInitialization.getUrlPairCollection().document(id);
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
            throw new ApiRuntimeException(e.getMessage(), new Date(), e);
        }
    }

    @Override
    public Collection<UrlPair> findAll() {
        try {
            // asynchronously retrieve all documents
            ApiFuture<QuerySnapshot> future = FirebaseInitialization.getUrlPairCollection().get();
            // future.get() blocks on response
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return documents.stream().map((QueryDocumentSnapshot document) -> {
                UrlPair urlPair = document.toObject(UrlPair.class);
                urlPair.setId(urlPair.getId());
                return urlPair;
                    })
                    .toList()
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } catch (ExecutionException e) {
            throw new ApiRuntimeException(e.getMessage(), new Date(), e);
        }
    }

    @Override
    public UrlPair save(UrlPair urlPair) {
        try{
            UrlPair newUrlPair = new UrlPair(urlPair);
            // Add document data with long url as id.
            ApiFuture<DocumentReference> addedDocRef = FirebaseInitialization.getUrlPairCollection().add(newUrlPair);
            logger.debug("Document added with id: {}", addedDocRef.get().getId());

            // Update an existing document
            DocumentReference docRef = FirebaseInitialization.getUrlPairCollection().document(addedDocRef.get().getId());

            // (async) Update one field
            docRef.update("id", addedDocRef.get().getId());
            newUrlPair.setId(docRef.getId());
            return newUrlPair;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            throw new ApiRuntimeException(e.getMessage(), new Date(), e);
        }
    }

    @Override
    public void deleteAllById(List<String> ids) {
        ids.forEach(id -> {
            try {
                // asynchronously delete a document
                ApiFuture<WriteResult> writeResult = FirebaseInitialization.getUrlPairCollection().document(id).delete();
                logger.debug("Delete time : {}", writeResult.get().getUpdateTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ApiRuntimeException(e.getMessage(), new Date(), e);
            } catch (ExecutionException e) {
                throw new ApiRuntimeException(e.getMessage(), new Date(), e);
            }
        });
    }

    @Override
    public void deleteAll() {
        List<UrlPair> urlPairs = findAll().stream().toList();
        if(!urlPairs.isEmpty()) {
            urlPairs.forEach(urlPair -> deleteAllById(List.of(urlPair.getId())));
        }
    }
}
