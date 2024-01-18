package ca.dtadmi.tinylink.dto;

import ca.dtadmi.tinylink.model.UrlPair;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedUrlPairsResultDto {

    private List<UrlPair> data;
    @JsonProperty("_metadata")
    private Metadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadata {
        private Integer page;
        @JsonProperty("per_page")
        private Integer perPage;
        @JsonProperty("page_count")
        private Integer pageCount;
        @JsonProperty("total_count")
        private Integer totalCount;
    }
}
