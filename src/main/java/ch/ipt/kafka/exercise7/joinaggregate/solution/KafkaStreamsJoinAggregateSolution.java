package ch.ipt.kafka.exercise7.joinaggregate.solution;

import org.apache.kafka.streams.StreamsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


//@Component
public class KafkaStreamsJoinAggregateSolution {

    @Value("${source-topic-transactions}")
    private String sourceTopic;

    @Value("${INITIALS}")
    private String initial;

    private String sinkTopic = "average-of-transactions-" + initial;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsJoinAggregateSolution.class);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        //Compute the total of all payments for every single customer and create a new schema containing the account information plus the total amount
        //TODO...

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}