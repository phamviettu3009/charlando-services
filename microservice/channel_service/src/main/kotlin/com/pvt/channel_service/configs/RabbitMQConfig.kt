package com.pvt.channel_service.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.pvt.channel_service.constants.RabbitMQ
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
    fun sendNotificationMessageQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_SEND_NOTIFICATION_MESSAGE.queue())
    }

    @Bean
    fun sendNotificationMessageBinding(): Binding {
        return BindingBuilder
            .bind(sendNotificationMessageQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_SEND_NOTIFICATION_MESSAGE.route())
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
    fun typingMSCQueue(): Queue {
        return Queue(RabbitMQ.MSC_TYPING.queue())
    }

    @Bean
    fun typingMSCBinding(): Binding {
        return BindingBuilder
            .bind(typingMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSC_TYPING.route())
    }

    @Bean
    fun callbackTypingMSCQueue(): Queue {
        return Queue(RabbitMQ.MSC_TYPING.callbackQueue())
    }

    @Bean
    fun callbackTypingMSCBinding(): Binding {
        return BindingBuilder
            .bind(callbackTypingMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSC_TYPING.callbackRoute())
    }

    @Bean
    fun wakeUpDevicesMSCQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_WAKE_UP_DEVICES.queue())
    }

    @Bean
    fun wakeUpDevicesMSCBinding(): Binding {
        return BindingBuilder
            .bind(wakeUpDevicesMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_WAKE_UP_DEVICES.route())
    }

    @Bean
    fun callbackWakeUpDevicesMSCQueue(): Queue {
        return Queue(RabbitMQ.MSCMN_WAKE_UP_DEVICES.callbackQueue())
    }

    @Bean
    fun callbackWakeUpDevicesMSCBinding(): Binding {
        return BindingBuilder
            .bind(callbackWakeUpDevicesMSCQueue())
            .to(exchange())
            .with(RabbitMQ.MSCMN_WAKE_UP_DEVICES.callbackRoute())
    }

    @Bean
    fun converter(): MessageConverter {
        val mapper = ObjectMapper()
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        return Jackson2JsonMessageConverter(mapper)
    }

    @Bean
    fun amqpTemplate(connectionFactory: ConnectionFactory): AmqpTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.setMessageConverter(converter())
        return rabbitTemplate
    }
}