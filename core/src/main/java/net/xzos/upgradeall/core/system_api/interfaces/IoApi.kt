package net.xzos.upgradeall.core.system_api.interfaces

import net.xzos.upgradeall.core.data.json.gson.AppConfigGson
import net.xzos.upgradeall.core.server_manager.module.applications.AppInfo


// 平台相关 IO 互操作
interface IoApi {

    // 注释相应平台的下载软件
    fun downloadFile(
        isDebug: Boolean,
        fileName: String, url: String, headers: Map<String, String> = mapOf(),
        externalDownloader: Boolean
    )

    // 查询软件信息
    fun getAppVersionNumber(targetChecker: AppConfigGson.AppConfigBean.TargetCheckerBean?): String?

    // 获取软件信息列表
    fun getAppInfoList(type: String): List<AppInfo>?
}