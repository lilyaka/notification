package com.revotech.notification.business.firebase

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.InputStream


@Configuration
class FirebaseConfig {
    @Value("\${firebase.project-id}")
    private lateinit var projectId: String

    @Value("\${firebase.private-key-id}")
    private lateinit var privateKeyId: String

    @Value("\${firebase.private-key}")
    private lateinit var privateKey: String

    @Value("\${firebase.client-email}")
    private lateinit var clientEmail: String

    @Value("\${firebase.client-id}")
    private lateinit var clientId: String

    @Value("\${firebase.auth-uri}")
    private lateinit var authUri: String

    @Value("\${firebase.token-uri}")
    private lateinit var tokenUri: String

    @Value("\${firebase.auth-provider-x509-cert-url}")
    private lateinit var authProviderX509CertUrl: String

    @Value("\${firebase.client-x509-cert-url}")
    private lateinit var clientX509CertUrl: String

    @Bean
    fun initializeFirebase(): FirebaseApp? {
        val credentials: MutableMap<String, Any> = HashMap()
        credentials["type"] = "service_account"
        credentials["project_id"] = projectId
        credentials["private_key_id"] = privateKeyId
        credentials["private_key"] = privateKey
        credentials["client_email"] = clientEmail
        credentials["client_id"] = clientId
        credentials["auth_uri"] = authUri
        credentials["token_uri"] = tokenUri
        credentials["auth_provider_x509_cert_url"] = authProviderX509CertUrl
        credentials["client_x509_cert_url"] = clientX509CertUrl

        val credentialsStream: InputStream = ObjectMapper().writeValueAsBytes(credentials).inputStream()
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(credentialsStream))
            .build()
        return FirebaseApp.initializeApp(options)
    }
}
