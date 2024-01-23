package com.clipevery.dao.sync

import com.clipevery.dto.sync.SyncInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant

class SyncRuntimeInfoRealm(private val realm: Realm): SyncRuntimeInfoDao {

    override fun getAllSyncRuntimeInfos(): List<SyncRuntimeInfo> {
        return realm.query(SyncRuntimeInfo::class).find()
    }

    override fun getSyncRuntimeInfo(appInstanceId: String): SyncRuntimeInfo? {
        return realm.query(SyncRuntimeInfo::class, "appInstanceId == $0", appInstanceId).first().find()
    }

    override fun updateConnectState(appInstanceId: String, connectState: Int) {
        realm.writeBlocking {
            realm.query(SyncRuntimeInfo::class, "appInstanceId == $0", appInstanceId)
                .first()
                .find()?.let {
                    it.connectState = connectState
                }
        }
    }

    override fun updateConnectInfo(appInstanceId: String, connectState: Int, connectHostAddress: String) {
        realm.writeBlocking {
            realm.query(SyncRuntimeInfo::class, "appInstanceId == $0", appInstanceId)
                .first()
                .find()?.let {
                    it.connectState = connectState
                    it.connectHostAddress = connectHostAddress
                }
        }
    }

    override fun updateAllowSend(appInstanceId: String, allowSend: Boolean) {
        realm.writeBlocking {
            realm.query(SyncRuntimeInfo::class, "appInstanceId == $0", appInstanceId)
                .first()
                .find()?.let {
                    it.allowSend = allowSend
                }
        }
    }

    override fun updateAllowReceive(appInstanceId: String, allowReceive: Boolean) {
        realm.writeBlocking {
            realm.query(SyncRuntimeInfo::class, "appInstanceId == $0", appInstanceId)
                .first()
                .find()?.let {
                    it.allowReceive = allowReceive
                }
        }
    }

    private fun updateSyncRuntimeInfo(syncRuntimeInfo: SyncRuntimeInfo, syncInfo: SyncInfo) {
        var hasModify = false
        if (syncRuntimeInfo.appVersion != syncInfo.appInfo.appVersion) {
            syncRuntimeInfo.appVersion = syncInfo.appInfo.appVersion
            hasModify = true
        }

        if (syncRuntimeInfo.userName != syncInfo.appInfo.userName) {
            syncRuntimeInfo.userName = syncInfo.appInfo.userName
            hasModify = true
        }

        if (syncRuntimeInfo.deviceId != syncInfo.endpointInfo.deviceId) {
            syncRuntimeInfo.deviceId = syncInfo.endpointInfo.deviceId
            hasModify = true
        }

        if (syncRuntimeInfo.deviceName != syncInfo.endpointInfo.deviceName) {
            syncRuntimeInfo.deviceName = syncInfo.endpointInfo.deviceName
            hasModify = true
        }

        if (syncRuntimeInfo.platformName != syncInfo.endpointInfo.platform.name) {
            syncRuntimeInfo.platformName = syncInfo.endpointInfo.platform.name
            hasModify = true
        }

        if (syncRuntimeInfo.platformVersion != syncInfo.endpointInfo.platform.version) {
            syncRuntimeInfo.platformVersion = syncInfo.endpointInfo.platform.version
            hasModify = true
        }

        if (syncRuntimeInfo.platformArch != syncInfo.endpointInfo.platform.arch) {
            syncRuntimeInfo.platformArch = syncInfo.endpointInfo.platform.arch
            hasModify = true
        }

        if (syncRuntimeInfo.platformBitMode != syncInfo.endpointInfo.platform.bitMode) {
            syncRuntimeInfo.platformBitMode = syncInfo.endpointInfo.platform.bitMode
            hasModify = true
        }

        if (syncRuntimeInfo.hostInfoList != syncInfo.endpointInfo.hostInfoList) {
            syncRuntimeInfo.hostInfoList = syncInfo.endpointInfo.hostInfoList.toRealmList()
            hasModify = true
        }

        if (syncRuntimeInfo.port != syncInfo.endpointInfo.port) {
            syncRuntimeInfo.port = syncInfo.endpointInfo.port
            hasModify = true
        }

        if (hasModify) {
            syncRuntimeInfo.modifyTime = RealmInstant.now()
        }
    }

    private fun createSyncRuntimeInfo(syncInfo: SyncInfo): SyncRuntimeInfo {
        return SyncRuntimeInfo().apply {
            appInstanceId = syncInfo.appInfo.appInstanceId
            appVersion = syncInfo.appInfo.appVersion
            userName = syncInfo.appInfo.userName
            deviceId = syncInfo.endpointInfo.deviceId
            deviceName = syncInfo.endpointInfo.deviceName
            platformName = syncInfo.endpointInfo.platform.name
            platformArch = syncInfo.endpointInfo.platform.arch
            platformBitMode = syncInfo.endpointInfo.platform.bitMode
            platformVersion = syncInfo.endpointInfo.platform.version
            hostInfoList = syncInfo.endpointInfo.hostInfoList.toRealmList()
            port = syncInfo.endpointInfo.port
            createTime = RealmInstant.now()
        }
    }

    override fun inertOrUpdate(syncInfo: SyncInfo) {
        realm.writeBlocking {
            realm.query(SyncRuntimeInfo::class, "appInstanceId == $0", syncInfo.appInfo.appInstanceId)
                .first()
                .find()?.let {
                    updateSyncRuntimeInfo(it, syncInfo)
                } ?: run {
                    copyToRealm(createSyncRuntimeInfo(syncInfo))
                }
        }
    }

    override fun inertOrUpdate(syncInfos: List<SyncInfo>): List<String> {
        return realm.writeBlocking {
            return@writeBlocking buildList {
                syncInfos.forEach { syncInfo ->
                    realm.query(
                        SyncRuntimeInfo::class,
                        "appInstanceId == $0",
                        syncInfo.appInfo.appInstanceId
                    )
                        .first()
                        .find()?.let {
                            updateSyncRuntimeInfo(it, syncInfo)
                        } ?: run {
                            copyToRealm(createSyncRuntimeInfo(syncInfo))
                            add(syncInfo.appInfo.appInstanceId)
                    }
                }
            }
        }
    }

    override fun deleteSyncRuntimeInfo(appInstanceId: String) {
        realm.writeBlocking {
            realm.query(SyncRuntimeInfo::class, "appInstanceId == $0", appInstanceId)
                .first()
                .find()?.let {
                    delete(it)
                }
        }
    }
}