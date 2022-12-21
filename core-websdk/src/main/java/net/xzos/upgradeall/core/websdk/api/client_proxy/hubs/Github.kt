package net.xzos.upgradeall.core.websdk.api.client_proxy.hubs

import net.xzos.upgradeall.core.utils.asSequence
import net.xzos.upgradeall.core.utils.data_cache.DataCacheManager
import net.xzos.upgradeall.core.utils.versioning.VersioningUtils
import net.xzos.upgradeall.core.websdk.api.client_proxy.tryGetTimestamp
import net.xzos.upgradeall.core.websdk.api.client_proxy.versionCode
import net.xzos.upgradeall.core.websdk.api.web.http.HttpRequestData
import net.xzos.upgradeall.core.websdk.api.web.http.OkHttpApi
import net.xzos.upgradeall.core.websdk.api.web.proxy.OkhttpProxy
import net.xzos.upgradeall.core.websdk.base_model.ApiRequestData
import net.xzos.upgradeall.core.websdk.json.AssetGson
import net.xzos.upgradeall.core.websdk.json.ReleaseGson
import org.json.JSONArray
import org.json.JSONObject

internal class Github(
    dataCache: DataCacheManager, okhttpProxy: OkhttpProxy
) : BaseHub(dataCache, okhttpProxy) {
    override val uuid = "fd9b2602-62c5-4d55-bd1e-0d6537714ca0"

    override fun checkAppAvailable(data: ApiRequestData): Boolean {
        val appId = data.appId
        val owner = appId["owner"]
        val repo = appId["repo"]
        val request = OkHttpApi.getRequestBuilder().url("https://github.com/$owner/$repo/")
            .head().build()
        return OkHttpApi.call(request).execute().code != 404
    }

    override fun getRelease(
        data: ApiRequestData
    ): List<ReleaseGson>? {
        val appId = data.appId
        val owner = appId["owner"]
        val repo = appId["repo"]
        val url = "https://api.github.com/repos/$owner/$repo/releases"
        val requestData = HttpRequestData(url)
        val response = okhttpProxy.okhttpExecute(requestData)
            ?: return null
        val jsonArray = JSONArray(response.body.string())
        return jsonArray.asSequence<JSONObject>().map { json ->
            val name = data.other[VERSION_NUMBER_KEY]?.let {
                json.getString(it)
            } ?: json.getString("name").let {
                if (VersioningUtils.matchVersioningString(it) == null)
                    json.getString("tag_name")
                else it
            }
            val versionCode = data.other[VERSION_CODE_KEY]?.run {
                try {
                    json.optString(this).tryGetTimestamp()
                } catch (e: Throwable) {
                    null
                }
            }
            ReleaseGson(
                versionNumber = name,
                changelog = json.getString("body"),
                assetGsonList = json.getJSONArray("assets").asSequence<JSONObject>().map {
                    AssetGson(
                        fileName = it.getString("name"),
                        fileType = it.getString("content_type"),
                        downloadUrl = it.getString("browser_download_url")
                    )
                }.toList()
            ).versionCode(versionCode)
        }.toList()
    }

    companion object {
        private const val VERSION_NUMBER_KEY = "version_number_key"
        private const val VERSION_CODE_KEY = "version_code_key"
    }
}