package org.vocbuild.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubtitleModel implements ElasticSearchModel {
    private long seq;
    private String text;

    @JsonSerialize(using = DurationSerializer.class)
    private Duration startTime;

    @JsonSerialize(using = DurationSerializer.class)
    private Duration endTime;
    private String id;
}
