package ch.ipt.kafka.exercise4.groupby.solution;

import ch.ipt.kafka.techbier.Payment;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


//@Component
public class KafkaStreamsGroupBySolution {

    @Value("${source-topic-transactions}")
    private String sourceTopic;
    @Value("${INITIALS}")
    private String initial;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsGroupBySolution.class);
    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<Long> LONG_SERDE = Serdes.Long();
    private static final Serde<Payment> PAYMENT_SERDE = new SpecificAvroSerde<>();


    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        String sinkTopic = "grouped-transactions-" + initial;

        //count the number of payments grouped by the cardtype (e.g. "Debit": 12, "Credit": 27)

        KStream<String, Long> messageStream = streamsBuilder
                .stream(sourceTopic, Consumed.with(STRING_SERDE, PAYMENT_SERDE))
                .map((key, value) -> new KeyValue<>(
                        value.getCardType().toString(), value
                ))
                .groupByKey()
                .count()
                .toStream()
                .peek((key, value) -> LOGGER.info("Message: key={}, value={}", key, value));

        messageStream.to(sinkTopic, Produced.with(STRING_SERDE, LONG_SERDE));

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}
