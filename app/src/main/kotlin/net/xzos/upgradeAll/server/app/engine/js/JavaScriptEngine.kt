package net.xzos.upgradeAll.server.app.engine.js

import net.xzos.upgradeAll.server.ServerContainer
import net.xzos.upgradeAll.server.app.engine.api.CoreApi
import org.json.JSONObject

class JavaScriptEngine internal constructor(
        private val logObjectTag: Array<String>,
        URL: String?,
        jsCode: String?,
        enableLogJsCode: Boolean = true
) : CoreApi {

    private val javaScriptCoreEngine: JavaScriptCoreEngine = JavaScriptCoreEngine(logObjectTag, URL, jsCode)

    init {
        if (enableLogJsCode) {
            Log.i(this.logObjectTag, TAG, String.format("JavaScriptCoreEngine: jsCode: \n%s", jsCode))  // 只打印一次 JS 脚本
        }
    }

    override suspend fun getDefaultName(): String? {
        return try {
            javaScriptCoreEngine.getDefaultName()
        } catch (e: Throwable) {
            Log.e(logObjectTag, TAG, "defaultName: 脚本执行错误, ERROR_MESSAGE: $e")
            null
        }
    }

    override suspend fun getReleaseNum(): Int {
        return try {
            javaScriptCoreEngine.getReleaseNum()
        } catch (e: Throwable) {
            Log.e(logObjectTag, TAG, "releaseNum: 脚本执行错误, ERROR_MESSAGE: $e")
            0
        }
    }

    override suspend fun getVersioning(releaseNum: Int): String? {
        return when {
            releaseNum < 0 -> null
            releaseNum >= 0 -> try {
                javaScriptCoreEngine.getVersioning(releaseNum)
            } catch (e: Throwable) {
                Log.e(logObjectTag, TAG, "getVersioning: 脚本执行错误, ERROR_MESSAGE: $e")
                null
            }
            else -> null
        }
    }

    override suspend fun getChangelog(releaseNum: Int): String? {
        return when {
            releaseNum < 0 -> null
            releaseNum >= 0 -> try {
                javaScriptCoreEngine.getChangelog(releaseNum)
            } catch (e: Throwable) {
                Log.e(logObjectTag, TAG, "getChangelog: 脚本执行错误, ERROR_MESSAGE: $e")
                null
            }
            else -> null
        }
    }

    override suspend fun getReleaseDownload(releaseNum: Int): JSONObject {
        return when {
            releaseNum < 0 -> JSONObject()
            releaseNum >= 0 -> try {
                javaScriptCoreEngine.getReleaseDownload(releaseNum)
            } catch (e: Throwable) {
                Log.e(logObjectTag, TAG, "getReleaseDownload: 脚本执行错误, ERROR_MESSAGE: $e")
                JSONObject()
            }
            else -> JSONObject()
        }
    }

    companion object {
        private const val TAG = "JavaScriptEngine"
        private val Log = ServerContainer.Log
    }
}