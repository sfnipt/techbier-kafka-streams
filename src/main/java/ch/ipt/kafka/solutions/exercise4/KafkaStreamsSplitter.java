package ch.ipt.kafka.solutions.exercise4;

import ch.ipt.kafka.clients.avro.Payment;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;


//@Component
public class KafkaStreamsSplitter {

    @Value("${source-topic-transactions}")
    private String sourceTopic;
    private String creditSinkTopic = "credit-transactions";
    private String debitSinkTopic = "debit-transactions";
    private String undefinedSinkTopic = "undefined-transactions";


    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsSplitter.class);
    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final Serde<Payment> PAYMENT_SERDE = new SpecificAvroSerde<>();

    public KafkaStreamsSplitter(@Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistry) {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        streamsConfiguration.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        final Map<String, String> serdeConfig = Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        PAYMENT_SERDE.configure(serdeConfig, false); // `false` for record values
    }

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {

        //split the topic in two different sink topics: one for debit payments (into debit-transactions) and one for credit transactions (credit-transactions).

        streamsBuilder
                .stream("transactions", Consumed.with(STRING_SERDE, PAYMENT_SERDE))
                .split()
                .branch(
                        (key, value) -> value.getCardType().toString().equals("Debit"),
                        Branched.withConsumer(stream -> stream.to(debitSinkTopic))
                )
                .branch(
                        (key, value) -> value.getCardType().toString().equals("Credit"),
                        Branched.withConsumer(stream -> stream.to(creditSinkTopic))
                )
                .branch(
                        (key, value) -> true, //catch unknown events
                        Branched.withConsumer(stream -> stream.to(undefinedSinkTopic))
                );

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

}