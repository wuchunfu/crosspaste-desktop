package com.crosspaste.net

import com.crosspaste.realm.sync.HostInfo
import com.crosspaste.utils.buildUrl
import com.crosspaste.utils.ioDispatcher
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

class TelnetHelper(private val pasteClient: PasteClient) {

    private val logger = KotlinLogging.logger {}

    suspend fun switchHost(
        hostInfoList: List<HostInfo>,
        port: Int,
        timeout: Long = 500L,
    ): HostInfo? {
        if (hostInfoList.isEmpty()) return null

        val result = CompletableDeferred<HostInfo?>()
        val mutex = Mutex()
        val scope = CoroutineScope(ioDispatcher)

        hostInfoList.forEach { hostInfo ->
            scope.launch(CoroutineName("SwitchHost")) {
                try {
                    if (telnet(hostInfo, port, timeout)) {
                        mutex.withLock {
                            if (!result.isCompleted) {
                                result.complete(hostInfo)
                            }
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }

        return try {
            withTimeout(timeout) { result.await() }
        } catch (_: TimeoutCancellationException) {
            null
        } finally {
            scope.cancel()
        }
    }

    private suspend fun telnet(
        hostInfo: HostInfo,
        port: Int,
        timeout: Long,
    ): Boolean {
        return try {
            val httpResponse =
                pasteClient.get(timeout = timeout) { urlBuilder ->
                    buildUrl(urlBuilder, hostInfo.hostAddress, port)
                    buildUrl(urlBuilder, "sync", "telnet")
                }
            logger.info { "httpResponse.status = ${httpResponse.status.value} $hostInfo:$port" }

            httpResponse.status.value == 200
        } catch (e: Exception) {
            logger.debug(e) { "telnet $hostInfo fail" }
            false
        }
    }
}
