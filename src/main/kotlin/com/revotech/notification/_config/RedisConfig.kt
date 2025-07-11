package com.revotech.notification._config

import com.revotech.notification.business.sse.redis.RedisMessageNotificationSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

const val NOTIFICATION_CHANNEL = "notification-channel"

@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        return template
    }

    @Bean
    fun redisContainer(
        connectionFactory: RedisConnectionFactory,
        listenerAdapter: MessageListenerAdapter,
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer().apply {
            setConnectionFactory(connectionFactory)
            addMessageListener(listenerAdapter, ChannelTopic(NOTIFICATION_CHANNEL))
        }
        return container
    }

    @Bean
    fun listenerAdapter(subscriber: RedisMessageNotificationSubscriber): MessageListenerAdapter {
        return MessageListenerAdapter(subscriber)
    }
}
