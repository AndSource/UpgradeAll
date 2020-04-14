package net.xzos.upgradeall.core.data_manager

import net.xzos.upgradeall.core.data.database.HubDatabase
import net.xzos.upgradeall.core.data.json.gson.HubConfig
import net.xzos.upgradeall.core.data.json.nongson.ObjectTag
import net.xzos.upgradeall.core.system_api.api.DatabaseApi


object HubDatabaseManager {

    private const val TAG = "HubDatabaseManager"
    private val objectTag = ObjectTag("Core", TAG)

    // 读取 hub 数据库
    var hubDatabases: List<HubDatabase> = DatabaseApi.hubDatabases
        private set

    init {
        DatabaseApi.register(this)
    }

    /**
     * 刷新数据库
     */
    @net.xzos.upgradeall.core.system_api.annotations.DatabaseApi.databaseChanged
    private fun refreshDatabaseList(database: Any) {
        if (database is HubDatabase) {
            hubDatabases = DatabaseApi.hubDatabases
        }
    }

    fun getDatabase(uuid: String?): HubDatabase? {
        var hubDatabase: HubDatabase? = null
        for (database in hubDatabases) {
            if (database.uuid == uuid) {
                hubDatabase = database
            }
        }
        return hubDatabase
    }

    fun exists(uuid: String?) = getDatabase(
        uuid
    ) != null

    internal fun saveDatabase(hubDatabase: HubDatabase): Boolean {
        return DatabaseApi.saveHubDatabase(hubDatabase)
    }

    internal fun deleteDatabase(hubDatabase: HubDatabase): Boolean {
        return DatabaseApi.deleteHubDatabase(hubDatabase)
    }


    fun addDatabase(hubConfigGson: HubConfig): Boolean {
        val name: String? = hubConfigGson.info.hubName
        val uuid: String? = hubConfigGson.uuid

        // 如果设置了名字与 UUID，则存入数据库
        if (name != null && uuid != null) {
            // 修改数据库
            (getDatabase(uuid)
                ?: HubDatabase.newInstance()).apply {
                this.uuid = uuid
                this.hubConfig = hubConfigGson
                // 存储 js 代码
            }.save() // 将数据存入 HubDatabase 数据库
            return true
        }
        return false
    }
}