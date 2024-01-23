package ca.dtadmi.tinylink.model;

import ca.dtadmi.tinylink.dto.UrlPairDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class UrlPair {

    @Id
    private String id;
    private String longUrl;
    private String shortUrl;
    private String creationDate;

    public UrlPair(UrlPair urlPair) {
        this.longUrl = urlPair.longUrl;
        this.shortUrl = urlPair.shortUrl;
        this.creationDate = urlPair.getCreationDate() != null ? urlPair.getCreationDate() : new Date().toString();
    }

    public UrlPair(UrlPairDto urlPairDto) {
        this.longUrl = urlPairDto.getLongUrl();
        this.shortUrl = urlPairDto.getShortUrl();
        this.creationDate = urlPairDto.getCreationDate() != null ? urlPairDto.getCreationDate() : new Date().toString();
    }
}
