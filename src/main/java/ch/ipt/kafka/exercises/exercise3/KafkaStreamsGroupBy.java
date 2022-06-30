package ch.ipt.kafka.exercises.exercise3;

import ch.ipt.kafka.clients.avro.Payment;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;


//@Component
public class KafkaStreamsGroupBy {

    @Value("${source-topic-transactions}")
    private String sourceTopic;
    private String sinkTopic = "grouped-transactions";
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsGroupBy.class);
    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<Long> LONG_SERDE = Serdes.Long();
    private static final Serde<Payment> PAYMENT_SERDE = new SpecificAvroSerde<>();

    public KafkaStreamsGroupBy(@Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistry) {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        streamsConfiguration.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        final Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        PAYMENT_SERDE.configure(serdeConfig, false); // `false` for record values
    }

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        //count the number of payments grouped by the cardtype (e.g. "Debit": 12, "Credit": 27)

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}