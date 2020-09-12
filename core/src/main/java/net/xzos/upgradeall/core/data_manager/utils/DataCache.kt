package net.xzos.upgradeall.core.data_manager.utils

import net.xzos.upgradeall.core.data.config.AppValue
import net.xzos.upgradeall.core.data.coroutines_basic_data_type.CoroutinesMutableMap
import net.xzos.upgradeall.core.data.coroutines_basic_data_type.coroutinesMutableMapOf
import net.xzos.upgradeall.core.route.ReleaseListItem
import java.util.*


object DataCache {

    private val cache = Cache()

    private var dataExpirationTime = AppValue.data_expiration_time

    private fun Pair<Any?, Calendar>.isExpired(): Boolean {
        val time = this.second
        time.add(Calendar.MINUTE, dataExpirationTime)
        return Calendar.getInstance().after(time)
    }

    fun <E> getAnyCache(key: String): E? {
        cache.anyCacheMap[key]?.also {
            if (!it.isExpired()) {
                @Suppress("UNCHECKED_CAST")
                return it.first as E
            } else cache.anyCacheMap.remove(key)
        }
        return null
    }

    fun cacheAny(key: String, value: Any) {
        cache.anyCacheMap[key] = Pair(value, Calendar.getInstance())
    }

    fun getAppRelease(
            hubUuid: String, auth: Map<String, String?>, appIdList: Map<String, String?>
    ): List<ReleaseListItem>? {
        val key = hubUuid + auth + appIdList
        cache.appReleaseMap[key]?.also {
            if (!it.isExpired()) {
                return it.first
            } else cache.appReleaseMap.remove(key)
        }
        return null
    }

    fun cacheAppStatus(
            hubUuid: String, auth: Map<String, String?>, appIdList: Map<String, String?>,
            releaseList: List<ReleaseListItem>?
    ) {
        val key = hubUuid + auth + appIdList
        cache.appReleaseMap[key] = Pair(releaseList, Calendar.getInstance())
    }

    data class Cache(
            internal val anyCacheMap: CoroutinesMutableMap<String,
                    Pair<Any, Calendar>> = coroutinesMutableMapOf(true),
            internal val appReleaseMap: CoroutinesMutableMap<String,
                    Pair<List<ReleaseListItem>?, Calendar>> = coroutinesMutableMapOf(true)
    )
}
