package ch.ipt.kafka.exercise6.windows.solution;

import ch.ipt.kafka.techbier.Payment;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;


//@Component
public class KafkaStreamsWindowedSolution {

    @Value("${source-topic-transactions}")
    private String sourceTopic;

    @Value("${INITIALS}")
    private String initial;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsWindowedSolution.class);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        String sinkTopic = "transactions-last-minute-" + initial;

        //compute the number of transactions per card type within the last minute
        KStream<String, Payment> stream = streamsBuilder.stream(sourceTopic);

        TimeWindows window = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1));
        stream
                .groupBy((k, v) -> v.getCardType().toString())
                .windowedBy(window)
                .count()
                .toStream()
                .peek((key, value) -> LOGGER.info("Total of transactions in the last minute: key={}, value={}", key, value))
                .to(sinkTopic, Produced.keySerde(WindowedSerdes.timeWindowedSerdeFrom(String.class, window.sizeMs)));

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}