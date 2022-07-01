package ch.ipt.kafka.exercise4.groupby;

import ch.ipt.kafka.techbier.Payment;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


//@Component
public class KafkaStreamsGroupBy {

    @Value("${source-topic-transactions}")
    private String sourceTopic;
    @Value("${INITIALS}")
    private String initial;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsGroupBy.class);
    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<Long> LONG_SERDE = Serdes.Long();
    private static final Serde<Payment> PAYMENT_SERDE = new SpecificAvroSerde<>();


    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        String sinkTopic = "grouped-transactions-" + initial;

        //count the number of payments grouped by the cardtype (e.g. "Debit": 12, "Credit": 27)

        //        KStream<String, Long> messageStream = streamsBuilder
        //                .stream(sourceTopic);
        //TODO...

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}