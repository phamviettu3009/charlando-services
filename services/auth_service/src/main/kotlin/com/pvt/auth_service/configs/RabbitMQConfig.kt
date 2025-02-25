package com.pvt.auth_service.configs

import com.pvt.auth_service.constants.RabbitMQ
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
    fun getUserByIDQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_GET_USER_BY_ID.queue())
    }

    @Bean
    fun getUserByIDBinding(): Binding {
        return BindingBuilder
            .bind(getUserByIDQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_GET_USER_BY_ID.route())
    }

    @Bean
    fun callbackGetUserByIDQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_GET_USER_BY_ID.callbackQueue())
    }

    @Bean
    fun callbackGetUserByIDBinding(): Binding {
        return BindingBuilder
            .bind(callbackGetUserByIDQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_GET_USER_BY_ID.callbackRoute())
    }

    @Bean
    fun createUserRecordMSCQueue(): Queue {
        return Queue(RabbitMQ.MSC_CREATE_RECORD_USER.queue())
    }

    @Bean
    fun createUserRecordMSCBinding(): Binding {
        return BindingBuilder
            .bind(createUserRecordMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSC_CREATE_RECORD_USER.route())
    }

    @Bean
    fun updateUserRecordMSCQueue(): Queue {
        return Queue(RabbitMQ.MSC_UPDATE_RECORD_USER.queue())
    }

    @Bean
    fun updateUserRecordMSCBinding(): Binding {
        return BindingBuilder
            .bind(updateUserRecordMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSC_UPDATE_RECORD_USER.route())
    }

    @Bean
    fun callbackCreateUserRecordMSCQueue(): Queue {
        return Queue(RabbitMQ.MSC_CREATE_RECORD_USER.callbackQueue())
    }

    @Bean
    fun callbackCreateUserRecordMSCBinding(): Binding {
        return BindingBuilder
            .bind(callbackCreateUserRecordMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSC_CREATE_RECORD_USER.callbackRoute())
    }

    @Bean
    fun callbackUpdateUserRecordMSCQueue(): Queue {
        return Queue(RabbitMQ.MSC_UPDATE_RECORD_USER.callbackQueue())
    }

    @Bean
    fun callbackUpdateUserRecordMSCBinding(): Binding {
        return BindingBuilder
            .bind(callbackUpdateUserRecordMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSC_UPDATE_RECORD_USER.callbackRoute())
    }

    @Bean
    fun revokeRecordLevelAccessForMembersQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS.queue())
    }

    @Bean
    fun revokeRecordLevelAccessForMembersBinding(): Binding {
        return BindingBuilder
            .bind(revokeRecordLevelAccessForMembersQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS.route())
    }

    @Bean
    fun callbackRevokeRecordLevelAccessForMembersQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS.callbackQueue())
    }

    @Bean
    fun callbackRevokeRecordLevelAccessForMembersBinding(): Binding {
        return BindingBuilder
            .bind(callbackRevokeRecordLevelAccessForMembersQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_REVOKE_RECORD_LEVEL_ACCESS_FOR_MEMBERS.callbackRoute())
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
    fun getDevicesQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_GET_DEVICE.queue())
    }

    @Bean
    fun getDevicesBinding(): Binding {
        return BindingBuilder
            .bind(getDevicesQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_GET_DEVICE.route())
    }

    @Bean
    fun callbackGetDevicesQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_GET_DEVICE.callbackQueue())
    }

    @Bean
    fun callbackGetDevicesBinding(): Binding {
        return BindingBuilder
            .bind(callbackGetDevicesQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_GET_DEVICE.callbackRoute())
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