package com.vocbuild.backend.util;

import com.vocbuild.backend.exceptions.ServerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.vocbuild.backend.exceptions.ValidationException;
import com.vocbuild.backend.model.SubtitleModel;

@Component
@Slf4j
public class SubtitlesParser {

    public List<SubtitleModel> parseSubtitles(@NonNull final MultipartFile subtitleFile) {
        try {
            InputStream is = subtitleFile.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder content = new StringBuilder();
            String line;
            List<SubtitleModel> subtitles = new ArrayList<>();
            long lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
                lineNumber++;
                if (StringUtils.isEmpty(line)) {
                    if(!StringUtils.isBlank(content.toString())) {
                        SubtitleModel subtitle = buildSubtitleSegment(content.toString(), lineNumber);
                        subtitles.add(subtitle);
                    }
                    content = new StringBuilder();
                }
            }
            if(!StringUtils.isBlank(content.toString())) {
                SubtitleModel subtitle = buildSubtitleSegment(content.toString(), lineNumber);
                subtitles.add(subtitle);
            }

            log.info("Processed subtitles file with {} lines", subtitles.size());

            return subtitles;
        } catch (IOException ex) {
            throw new ServerException(ex);
        }
    }

    private SubtitleModel buildSubtitleSegment(final String segment, final long lineSegment) {
        Scanner scanner = new Scanner(segment);
        SubtitleModel model = new SubtitleModel();
        String textLine = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Pattern indexPattern = Pattern.compile("([0-9]+)");
            Pattern timePattern = Pattern.compile("([0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}) --> ([0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})");
            /** Handle few cases:
             * 1. When the text is just a number
             * 2. When the text is empty: skip that subtitle
             * 3. When there's extra line/s gap between the timestamp and subtitle
             * 4. Handle srt files which are not UTF-8 encoded
             */

            if(line.matches("\\d+")) {
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
        model.setText(stripHtmlTags(textLine));
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

    private static String stripHtmlTags(String html) {
        Document doc = Jsoup.parse(html);
        // Safelist.none() allows no tags, effectively stripping all HTML tags
        // If you want to allow certain tags, you can customize the Whitelist accordingly.
        String cleanText = Jsoup.clean(doc.text(), Safelist.none());
        return cleanText;
    }
}
