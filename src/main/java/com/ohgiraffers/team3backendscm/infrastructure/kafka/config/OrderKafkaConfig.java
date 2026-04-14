package com.ohgiraffers.team3backendscm.infrastructure.kafka.config;

import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.AssignmentSnapshotEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.MissionProgressEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderDifficultyAnalyzedEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderDifficultySnapshotEvent;
import com.ohgiraffers.team3backendscm.infrastructure.kafka.dto.OrderRegisteredEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class OrderKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ProducerFactory<String, OrderRegisteredEvent> orderRegisteredProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, OrderRegisteredEvent> orderRegisteredKafkaTemplate() {
        return new KafkaTemplate<>(orderRegisteredProducerFactory());
    }

    @Bean
    public ProducerFactory<String, OrderDifficultySnapshotEvent> orderDifficultySnapshotProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, OrderDifficultySnapshotEvent> orderDifficultySnapshotKafkaTemplate() {
        return new KafkaTemplate<>(orderDifficultySnapshotProducerFactory());
    }

    @Bean
    public ProducerFactory<String, AssignmentSnapshotEvent> assignmentSnapshotProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, AssignmentSnapshotEvent> assignmentSnapshotKafkaTemplate() {
        return new KafkaTemplate<>(assignmentSnapshotProducerFactory());
    }

    @Bean
    public ProducerFactory<String, MissionProgressEvent> missionProgressProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, MissionProgressEvent> missionProgressKafkaTemplate() {
        return new KafkaTemplate<>(missionProgressProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, OrderDifficultyAnalyzedEvent> orderDifficultyAnalyzedConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-order");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<OrderDifficultyAnalyzedEvent> deserializer =
            new JsonDeserializer<>(OrderDifficultyAnalyzedEvent.class);
        deserializer.ignoreTypeHeaders();
        deserializer.addTrustedPackages(
            "com.ohgiraffers.team3backendscm.infrastructure.kafka.dto",
            "com.ohgiraffers.team3backendbatch.infrastructure.kafka.dto"
        );

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDifficultyAnalyzedEvent>
    orderDifficultyAnalyzedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderDifficultyAnalyzedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderDifficultyAnalyzedConsumerFactory());
        return factory;
    }
}
