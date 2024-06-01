package com.clipevery.ui.base

import com.clipevery.i18n.GlobalCopywriter
import com.clipevery.platform.currentPlatform
import com.clipevery.utils.getFileUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.exists

class DesktopUISupport(
    private val toastManager: ToastManager,
    private val copywriter: GlobalCopywriter,
) : UISupport {

    private val logger = KotlinLogging.logger {}

    private val fileUtils = getFileUtils()

    override fun openUrlInBrowser(url: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            toastManager.setToast(
                Toast(
                    messageType = MessageType.Error,
                    "${copywriter.getText("Cant_open_browser")} $url",
                ),
            )
        }
    }

    override fun openEmailClient(email: String) {
        val uriText = "mailto:$email"
        val mailURI = URI(uriText)

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
            Desktop.getDesktop().mail(mailURI)
        } else {
            toastManager.setToast(
                Toast(
                    messageType = MessageType.Error,
                    "${copywriter.getText("Official email")} $email",
                ),
            )
        }
    }

    override fun openHtml(html: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            fileUtils.createTempFile(
                html.toByteArray(),
                fileUtils.createRandomFileName("html"),
            )?.let { path ->
                val desktop = Desktop.getDesktop()
                desktop.browse(path.toFile().toURI())
            }
        } else {
            toastManager.setToast(
                Toast(
                    messageType = MessageType.Error,
                    copywriter.getText("Failed_to_open_Html_pasteboard"),
                ),
            )
        }
    }

    override fun browseFile(filePath: Path) {
        if (filePath.exists()) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                val desktop = Desktop.getDesktop()
                desktop.browseFileDirectory(filePath.toFile())
            } else {
                if (currentPlatform().isWindows() && openFileInExplorer(filePath.toFile())) {
                    return
                }

                toastManager.setToast(
                    Toast(
                        messageType = MessageType.Error,
                        copywriter.getText("Failed_to_browse_File_pasteboard"),
                    ),
                )
            }
        } else {
            toastManager.setToast(
                Toast(
                    messageType = MessageType.Error,
                    copywriter.getText("File_not_found"),
                ),
            )
        }
    }

    private fun openFileInExplorer(file: File): Boolean {
        try {
            val filePath = file.absolutePath
            val command = "explorer.exe /select,\"$filePath\""
            Runtime.getRuntime().exec(command)
            return true
        } catch (e: Exception) {
            logger.error(e) { "Failed to open file in explorer" }
            return false
        }
    }

    override fun openImage(imagePath: Path) {
        if (imagePath.exists()) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(imagePath.toFile())
            } else {
                toastManager.setToast(
                    Toast(
                        messageType = MessageType.Error,
                        copywriter.getText("Failed_to_open_Image_pasteboard"),
                    ),
                )
            }
        } else {
            toastManager.setToast(
                Toast(
                    messageType = MessageType.Error,
                    copywriter.getText("File_not_found"),
                ),
            )
        }
    }

    override fun openText(text: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            fileUtils.createTempFile(
                text.toByteArray(),
                fileUtils.createRandomFileName("txt"),
            )?.let { path ->
                Desktop.getDesktop().open(path.toFile())
            }
        } else {
            toastManager.setToast(
                Toast(
                    messageType = MessageType.Error,
                    copywriter.getText("Failed_to_open_Text_pasteboard"),
                ),
            )
        }
    }
}