package com.crosspaste.clip.service

import com.crosspaste.app.AppInfo
import com.crosspaste.clip.ClipCollector
import com.crosspaste.clip.ClipItemService
import com.crosspaste.clip.item.HtmlClipItem
import com.crosspaste.dao.clip.ClipItem
import com.crosspaste.utils.DesktopFileUtils
import com.crosspaste.utils.getEncryptUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import io.realm.kotlin.MutableRealm
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

class HtmlItemService(appInfo: AppInfo) : ClipItemService(appInfo) {

    companion object HtmlItemService {

        const val HTML_ID = "text/html"

        private val encryptUtils = getEncryptUtils()
    }

    val logger = KotlinLogging.logger {}

    override fun getIdentifiers(): List<String> {
        return listOf(HTML_ID)
    }

    override fun createPreClipItem(
        clipId: Long,
        itemIndex: Int,
        identifier: String,
        transferable: Transferable,
        clipCollector: ClipCollector,
    ) {
        HtmlClipItem().apply {
            this.identifier = identifier
        }.let {
            clipCollector.preCollectItem(itemIndex, this::class, it)
        }
    }

    override fun doLoadRepresentation(
        transferData: Any,
        clipId: Long,
        itemIndex: Int,
        dataFlavor: DataFlavor,
        dataFlavorMap: Map<String, List<DataFlavor>>,
        transferable: Transferable,
        clipCollector: ClipCollector,
    ) {
        if (transferData is String) {
            val html = extractHtml(transferData)
            val htmlBytes = html.toByteArray()
            val md5 = encryptUtils.md5(htmlBytes)
            val relativePath =
                DesktopFileUtils.createClipRelativePath(
                    appInstanceId = appInfo.appInstanceId,
                    clipId = clipId,
                    fileName = "html2Image.png",
                )
            val update: (ClipItem, MutableRealm) -> Unit = { clipItem, realm ->
                realm.query(HtmlClipItem::class, "id == $0", clipItem.id).first().find()?.apply {
                    this.html = html
                    this.relativePath = relativePath
                    this.size = htmlBytes.size.toLong()
                    this.md5 = md5
                }
            }
            clipCollector.updateCollectItem(itemIndex, this::class, update)
        }
    }

    private fun extractHtml(inputStr: String): String {
        // this is microsoft html format
        // https://learn.microsoft.com/zh-cn/windows/win32/dataxchg/html-clipboard-format?redirectedfrom=MSDN
        return if (inputStr.startsWith("Version:")) {
            extractHtmlFromMicrosoftHtml(inputStr)
        } else {
            inputStr
        }
    }

    private fun extractHtmlFromMicrosoftHtml(inputStr: String): String {
        val start = inputStr.indexOfFirst { it == '<' }
        return if (start != -1) {
            inputStr.substring(start)
        } else {
            inputStr
        }
    }
}