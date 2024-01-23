package ca.dtadmi.tinylink.dto;

import ca.dtadmi.tinylink.model.UrlPair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlPairDto {
    private String id;
    private String longUrl;
    private String shortUrl;
    private String creationDate;

    public UrlPairDto(UrlPairDto urlPairDto) {
        this.longUrl = urlPairDto.longUrl;
        this.shortUrl = urlPairDto.shortUrl;
        this.creationDate = urlPairDto.getCreationDate() != null ? urlPairDto.getCreationDate() : new Date().toString();
    }

    public UrlPairDto(UrlPair urlPair) {
        this.longUrl = urlPair.getLongUrl();
        this.shortUrl = urlPair.getShortUrl();
        this.creationDate = urlPair.getCreationDate() != null ? urlPair.getCreationDate() : new Date().toString();
    }
}
