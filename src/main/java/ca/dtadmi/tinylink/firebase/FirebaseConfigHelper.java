package ca.dtadmi.tinylink.firebase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FirebaseConfigHelper {

    private final String type;
    private final String projectId;
    private final String privateKeyId;
    private final String privateKey;
    private final String clientEmail;
    private final String clientId;
    private final String authUri;
    private final String tokenUri;
    private final String authProviderX509CertUrl;
    private final String clientX509CertUrl;
    private final String universeDomain;

    public FirebaseConfigHelper(
        @Value("${firestore.type}")
        String type,
        @Value("${firestore.project_id}")
        String projectId,
        @Value("${firestore.private_key_id}")
        String privateKeyId,
        @Value("${firestore.private_key}")
        String privateKey,
        @Value("${firestore.client_email}")
        String clientEmail,
        @Value("${firestore.client_id}")
        String clientId,
        @Value("${firestore.auth_uri}")
        String authUri,
        @Value("${firestore.token_uri}")
        String tokenUri,
        @Value("${firestore.auth_provider_x509_cert_url}")
        String authProviderX509CertUrl,
        @Value("${firestore.client_x509_cert_url}")
        String clientX509CertUrl,
        @Value("${firestore.universe_domain}")
        String universeDomain
    ) {
        this.type = type;
        this.projectId = projectId;
        this.privateKeyId = privateKeyId;
        this.privateKey = privateKey;
        this.clientEmail = clientEmail;
        this.clientId = clientId;
        this.authUri = authUri;
        this.tokenUri = tokenUri;
        this.authProviderX509CertUrl = authProviderX509CertUrl;
        this.clientX509CertUrl = clientX509CertUrl;
        this.universeDomain = universeDomain;
    }

    public String serviceAccountBuilder() {
        return  "{\n" +
                "  \"type\": \"" +
                type + "\",\n" + "  \"project_id\": \"" +
                projectId + "\",\n" + "  \"private_key_id\": \"" +
                privateKeyId + "\",\n" +
                "  \"private_key\": \"" +
                privateKey + "\",\n" +
                "  \"client_email\": \"" +
                clientEmail + "\",\n" +
                "  \"client_id\": \"" +
                clientId + "\",\n" +
                "  \"auth_uri\": \"" +
                authUri + "\",\n" +
                "  \"token_uri\": \"" +
                tokenUri + "\",\n" +
                "  \"auth_provider_x509_cert_url\": \"" +
                authProviderX509CertUrl + "\",\n" +
                "  \"client_x509_cert_url\": \"" +
                clientX509CertUrl + "\",\n" +
                "  \"universe_domain\": \"" +
                universeDomain + "\"\n" +
                "}";
    }
}
