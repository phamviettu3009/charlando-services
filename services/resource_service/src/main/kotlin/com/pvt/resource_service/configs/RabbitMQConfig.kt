package com.pvt.resource_service.configs

import com.pvt.resource_service.constants.RabbitMQ
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
    fun createRecordLevelAccessQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.queue())
    }

    @Bean
    fun createRecordLevelAccessBinding(): Binding {
        return BindingBuilder
            .bind(createRecordLevelAccessQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.route())
    }

    @Bean
    fun callbackCreateRecordLevelAccessQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackQueue())
    }

    @Bean
    fun callbackCreateRecordLevelAccessBinding(): Binding {
        return BindingBuilder
            .bind(callbackCreateRecordLevelAccessQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_CREATE_RECORD_LEVEL_ACCESS.callbackRoute())
    }

    @Bean
    fun getResourceByIDAndUserIDQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_GET_RESOURCE_BY_ID_AND_USERID.queue())
    }

    @Bean
    fun getResourceByIDAndUserIDBinding(): Binding {
        return BindingBuilder
            .bind(getResourceByIDAndUserIDQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_GET_RESOURCE_BY_ID_AND_USERID.route())
    }

    @Bean
    fun callbackResourceByIDAndUserIDQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_GET_RESOURCE_BY_ID_AND_USERID.callbackQueue())
    }

    @Bean
    fun callbackResourceByIDAndUserIDBinding(): Binding {
        return BindingBuilder
            .bind(callbackResourceByIDAndUserIDQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_GET_RESOURCE_BY_ID_AND_USERID.callbackRoute())
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