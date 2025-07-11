package com.revotech.notification.business._init.index

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.revotech.config.multitenant.mongodb.cache.TenantCache
import com.revotech.init.IndexCreator
import com.revotech.notification.business.data.DB_NOTIFICATION
import org.bson.Document
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class NotificationIndexCreator(tenantCache: TenantCache) : IndexCreator() {

    override var dbs: List<MongoDatabase> = tenantCache.tenants.values.map { it.getDatabase() }
    override val collectionName = DB_NOTIFICATION
    override val mapIndex = mutableMapOf(
        Document(
            mapOf(
                "userId" to 1,
                "read" to 1
            )
        ) to IndexOptions(),
        Document(
            mapOf(
                "userId" to 1,
                "new" to -1
            )
        ) to IndexOptions(),
        Document("time", -1) to IndexOptions().expireAfter(30, TimeUnit.DAYS)
    )
}
