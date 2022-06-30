package ch.ipt.kafka.exercise5.windows.solution;

import org.apache.kafka.streams.StreamsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


//@Component
public class KafkaStreamsWindowedSolution {

    @Value("${source-topic-transactions}")
    private String sourceTopic;

    @Value("${INITIALS}")
    private String initial;

    private String sinkTopic = "average-of-transactions-" + initial;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsWindowedSolution.class);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        //compute the average of all transactions in the last minute

        //TODO

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}