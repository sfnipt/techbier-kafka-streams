package ch.ipt.kafka.exercise6.join.solution;

import org.apache.kafka.streams.StreamsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


//@Component
public class KafkaStreamsJoin {

    @Value("${source-topic-transactions}")
    private String sourceTopic;

    @Value("${INITIALS}")
    private String initial;

    private String sinkTopic = "average-of-transactions-" + initial;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsJoin.class);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        //filter all Payments for the customers with last name "Fischer"

        //TODO...

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}