package com.data.factory

import com.data.factory.exceptions.RequestException
import com.typesafe.scalalogging.Logger
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

import java.util.Properties

object App extends Serializable{
    //private val log = Logger("App")
    def main(args: Array[String]): Unit = {
        //log.info("We are beginning")
        //val encodedInput = args(0)
        try {
            //log.info("Creating Flink Consumer.")
            val env = StreamExecutionEnvironment.getExecutionEnvironment


            val kafkaSource = KafkaSource.builder()
              .setBootstrapServers("localhost:9092")
              .setTopics("flink-example")
              .setGroupId("first-topic")
              .setStartingOffsets(OffsetsInitializer.latest())
              .setValueOnlyDeserializer(new SimpleStringSchema())
              .build()

            val serializer = KafkaRecordSerializationSchema.builder()
              .setValueSerializationSchema(new SimpleStringSchema())
              .setTopic("second-topic")
              .build()

            val kafkaSink = KafkaSink.builder()
              .setBootstrapServers("localhost:9092")
              .setRecordSerializer(serializer)
              .build()

            val lines = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
            lines.print()

            //log.info("Process ended successfully.")
        } catch {
            case e: Exception => throw RequestException(e.getClass.toString.concat(":").concat(e.getMessage))
        }
    }
}
