package org.vocbuild.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.vocbuild.exceptions.ValidationException;
import org.vocbuild.model.SubtitleModel;

@Component
@Slf4j
public class SubtitlesParser {

    public List<SubtitleModel> parseSubtitles(@NonNull final MultipartFile subtitleFile) throws IOException {
        InputStream is = subtitleFile.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder content = new StringBuilder();
        String line;
        List<SubtitleModel> subtitles = new ArrayList<>();
        long lineNumber = 0;
        while((line = reader.readLine()) != null) {
            content.append(line).append("\n");
            lineNumber++;
            if(line.isEmpty()) {
                SubtitleModel subtitle = buildSubtitleSegment(content.toString(), lineNumber);
                subtitles.add(subtitle);
                content = new StringBuilder();
            }
        }

        log.info("Processed subtitles file with {} lines", subtitles.size());

        return subtitles;
    }

    private SubtitleModel buildSubtitleSegment(final String segment, final long lineSegment) {
        Scanner scanner = new Scanner(segment);
        SubtitleModel model = new SubtitleModel();
        String textLine = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Pattern indexPattern = Pattern.compile("([0-9]+)");
            Pattern timePattern = Pattern.compile("([0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}) --> ([0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})");

            if(indexPattern.matcher(line).matches()) {
                model.setSeq(Integer.parseInt(line));
            } else if(timePattern.matcher(line).matches()) {
                Matcher matcher = timePattern.matcher(line);
                if(matcher.find()) {
                    model.setStartTime(getDuration(matcher.group(1), lineSegment));
                    model.setEndTime(getDuration(matcher.group(2), lineSegment));
                }
            } else {
                textLine = textLine.concat(line + " ");
            }

        }
        model.setText(textLine);
        if(model.getText().isEmpty() || model.getStartTime() == null || model.getEndTime() == null) {
            throw new ValidationException("An error occurred while processing subtitles above line: " + lineSegment);
        }
        scanner.close();
        return model;
    }

    private Duration getDuration(final String time, final long lineSegment) {
        try {
            LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss,SSS"));
            return Duration.between(LocalTime.MIN, localTime);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Time format is incorrect above line: " + lineSegment);
        }
    }


}
