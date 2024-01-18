package ca.dtadmi.tinylink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlPair {

    private String id;
    private String longUrl;
    private String shortUrl;
    private String creationDate;

    public UrlPair(UrlPair urlPair) {
        this.longUrl = urlPair.longUrl;
        this.shortUrl = urlPair.shortUrl;
        this.creationDate = new Date().toString();
    }
}
