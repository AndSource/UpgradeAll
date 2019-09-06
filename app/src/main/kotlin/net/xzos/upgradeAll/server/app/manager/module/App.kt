package net.xzos.upgradeAll.server.app.manager.module

import net.xzos.upgradeAll.database.RepoDatabase
import net.xzos.upgradeAll.utils.VersionChecker
import org.litepal.LitePal
import org.litepal.extension.find

data class App(private val appDatabaseId: Int) {
    val updater: Updater = Updater(appDatabaseId)

    val isLatest: Boolean
        get() {
            val latestVersion = updater.latestVersion
            val installedVersion = installedVersion
            return VersionChecker.compareVersionNumber(installedVersion, latestVersion)
        }

    // 获取已安装版本号
    val installedVersion: String?
        get() {
            val versionChecker = versionChecker
            return versionChecker?.version
        }

    // 获取数据库 VersionChecker 数据
    private val versionChecker: VersionChecker?
        get() {
            val repoDatabase: RepoDatabase? = LitePal.find(appDatabaseId.toLong())
            val versionChecker = repoDatabase?.versionChecker
            return VersionChecker(inputVersionCheckerString = versionChecker)
        }
}
