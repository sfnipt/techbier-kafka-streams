package ch.ipt.kafka.exercise1;

import ch.ipt.kafka.clients.avro.Payment;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;


//@Component
public class KafkaStreamsFilter {

    @Value("${source-topic-transactions}")
    private String sourceTopic;
    private String sinkTopic = "filtered-transactions";

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsFilter.class);
    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<Payment> PAYMENT_SERDE = new SpecificAvroSerde<>();
    private static final double LIMIT = 500.00;

    public KafkaStreamsFilter(@Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistry) {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        streamsConfiguration.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        final Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        PAYMENT_SERDE.configure(serdeConfig, false); // `false` for record values
    }

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        //implement a filter which only sends payments over 500.- to a sink topic

        KStream<String, Payment> messageStream = streamsBuilder
                .stream(sourceTopic, Consumed.with(STRING_SERDE, PAYMENT_SERDE))
                .filter((key, payment) -> payment.getAmount() > LIMIT)
                .peek((key, payment) -> LOGGER.debug("Message: key={}, value={}", key, payment));
        messageStream.to(sinkTopic, Produced.with(STRING_SERDE, PAYMENT_SERDE));

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}