package com.pvt.realtime_service.configs

import com.pvt.realtime_service.constants.RabbitMQ
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {
    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(RabbitMQ.Exchange.QUEUE_EXCHANGE)
    }

    @Bean
    fun validationJWTQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_VALIDATION_JWT.queue())
    }

    @Bean
    fun validationJWTBinding(): Binding {
        return BindingBuilder
            .bind(validationJWTQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_VALIDATION_JWT.route())
    }

    @Bean
    fun callbackValidationJWTQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_VALIDATION_JWT.callbackQueue())
    }

    @Bean
    fun callbackValidationJWTBinding(): Binding {
        return BindingBuilder
            .bind(callbackValidationJWTQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_VALIDATION_JWT.callbackRoute())
    }

    @Bean
    fun sendRealtimeMessageQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_SEND_REALTIME_MESSAGE.queue())
    }

    @Bean
    fun sendRealtimeMessageBinding(): Binding {
        return BindingBuilder
            .bind(sendRealtimeMessageQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_SEND_REALTIME_MESSAGE.route())
    }

    @Bean
    fun updateOnlineStatusUserRecordMSCQueue(): Queue {
        return Queue(RabbitMQ.MSC_UPDATE_ONLINE_STATUS_RECORD_USER.queue())
    }

    @Bean
    fun updateOnlineStatusUserRecordMSCBinding(): Binding {
        return BindingBuilder
            .bind(updateOnlineStatusUserRecordMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSC_UPDATE_ONLINE_STATUS_RECORD_USER.route())
    }

    @Bean
    fun callbackUpdateOnlineStatusUserRecordMSCQueue(): Queue {
        return Queue(RabbitMQ.MSC_UPDATE_ONLINE_STATUS_RECORD_USER.callbackQueue())
    }

    @Bean
    fun callbackUpdateOnlineStatusUserRecordMSCBinding(): Binding {
        return BindingBuilder
            .bind(callbackUpdateOnlineStatusUserRecordMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSC_UPDATE_ONLINE_STATUS_RECORD_USER.callbackRoute())
    }

    @Bean
    fun converter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun amqpTemplate(connectionFactory: ConnectionFactory): AmqpTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.setMessageConverter(converter())
        return rabbitTemplate
    }
}