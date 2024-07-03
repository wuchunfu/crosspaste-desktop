package com.crosspaste.path

import com.crosspaste.app.AppFileType
import com.crosspaste.clip.item.ClipFiles
import com.crosspaste.exception.ClipException
import com.crosspaste.exception.StandardErrorCode
import com.crosspaste.presist.DirFileInfoTree
import com.crosspaste.presist.FileInfoTree
import com.crosspaste.presist.FilesIndexBuilder
import com.crosspaste.utils.FileUtils
import java.nio.file.Path
import kotlin.io.path.name

interface PathProvider {
    fun resolve(
        fileName: String? = null,
        appFileType: AppFileType,
    ): Path {
        val path =
            when (appFileType) {
                AppFileType.APP -> clipAppPath
                AppFileType.USER -> clipUserPath
                AppFileType.LOG -> clipLogPath.resolve("logs")
                AppFileType.ENCRYPT -> clipEncryptPath.resolve("encrypt")
                AppFileType.DATA -> clipDataPath.resolve("data")
                AppFileType.HTML -> clipUserPath.resolve("html")
                AppFileType.ICON -> clipUserPath.resolve("icons")
                AppFileType.FAVICON -> clipUserPath.resolve("favicon")
                AppFileType.IMAGE -> clipUserPath.resolve("images")
                AppFileType.VIDEO -> clipUserPath.resolve("videos")
                AppFileType.FILE -> clipUserPath.resolve("files")
                AppFileType.KCEF -> clipUserPath.resolve("kcef")
            }

        autoCreateDir(path)

        return fileName?.let {
            path.resolve(fileName)
        } ?: path
    }

    fun resolve(
        basePath: Path,
        path: String,
        autoCreate: Boolean = true,
        isFile: Boolean = false,
    ): Path {
        val newPath = basePath.resolve(path)
        if (autoCreate) {
            if (isFile) {
                autoCreateDir(newPath.parent)
            } else {
                autoCreateDir(newPath)
            }
        }
        return newPath
    }

    fun resolve(
        appInstanceId: String,
        dateString: String,
        clipId: Long,
        clipFiles: ClipFiles,
        isPull: Boolean,
        filesIndexBuilder: FilesIndexBuilder? = null,
    ) {
        val basePath = resolve(appFileType = clipFiles.getAppFileType())
        val clipIdPath =
            basePath.resolve(appInstanceId)
                .resolve(dateString)
                .resolve(clipId.toString())
        if (isPull) {
            autoCreateDir(clipIdPath)
        }

        val fileInfoTreeMap = clipFiles.getFileInfoTreeMap()

        for (filePath in clipFiles.getFilePaths()) {
            fileInfoTreeMap[filePath.fileName.name]?.let {
                resolveFileInfoTree(clipIdPath, filePath.fileName.name, it, isPull, filesIndexBuilder)
            }
        }
    }

    private fun resolveFileInfoTree(
        basePath: Path,
        name: String,
        fileInfoTree: FileInfoTree,
        isPull: Boolean,
        filesIndexBuilder: FilesIndexBuilder?,
    ) {
        if (fileInfoTree.isFile()) {
            val filePath = basePath.resolve(name)
            if (isPull) {
                if (!fileUtils.createEmptyClipFile(filePath, fileInfoTree.size)) {
                    throw ClipException(
                        StandardErrorCode.CANT_CREATE_FILE.toErrorCode(),
                        "Failed to create file: $filePath",
                    )
                }
            }
            filesIndexBuilder?.addFile(filePath, fileInfoTree.size)
        } else {
            val dirPath = basePath.resolve(name)
            if (isPull) {
                autoCreateDir(dirPath)
            }
            val dirFileInfoTree = fileInfoTree as DirFileInfoTree
            dirFileInfoTree.getTree().forEach { (subName, subFileInfoTree) ->
                resolveFileInfoTree(dirPath, subName, subFileInfoTree, isPull, filesIndexBuilder)
            }
        }
    }

    private fun autoCreateDir(path: Path) {
        if (!path.toFile().exists()) {
            if (!path.toFile().mkdirs()) {
                throw ClipException(StandardErrorCode.CANT_CREATE_DIR.toErrorCode(), "Failed to create directory: $path")
            }
        }
    }

    val fileUtils: FileUtils

    val userHome: Path

    val clipAppPath: Path

    val clipAppJarPath: Path

    val clipUserPath: Path

    val clipLogPath: Path get() = clipUserPath

    val clipEncryptPath get() = clipUserPath

    val clipDataPath get() = clipUserPath
}