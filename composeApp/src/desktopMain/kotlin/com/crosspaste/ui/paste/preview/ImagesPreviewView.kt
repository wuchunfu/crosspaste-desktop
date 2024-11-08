package com.crosspaste.ui.paste.preview

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.crosspaste.paste.DesktopPasteMenuService
import com.crosspaste.paste.item.ImagesPasteItem
import com.crosspaste.paste.item.PasteFileCoordinate
import com.crosspaste.path.UserDataPathProvider
import com.crosspaste.realm.paste.PasteData
import org.koin.compose.koinInject

@Composable
fun ImagesPreviewView(
    pasteData: PasteData,
    onDoubleClick: () -> Unit,
) {
    pasteData.getPasteItem(ImagesPasteItem::class)?.let { pasteFiles ->
        val pasteMenuService = koinInject<DesktopPasteMenuService>()
        val userDataPathProvider = koinInject<UserDataPathProvider>()

        PasteSpecificPreviewContentView(
            pasteMainContent = {
                val imagePaths = pasteFiles.getFilePaths(userDataPathProvider)
                val pasteCoordinate = pasteData.getPasteCoordinate()
                LazyRow(
                    modifier =
                        Modifier.fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        onDoubleClick()
                                    },
                                )
                            },
                ) {
                    items(imagePaths.size) { index ->
                        val pasteFileCoordinate = PasteFileCoordinate(pasteCoordinate, imagePaths[index])
                        PasteContextMenuView(
                            items =
                                pasteMenuService.fileMenuItemsProvider(
                                    pasteData = pasteData,
                                    pasteItem = pasteFiles,
                                    index = index,
                                ),
                        ) {
                            SingleImagePreviewView(pasteFileCoordinate)
                        }
                        if (index != imagePaths.size - 1) {
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    }
                }
            },
            pasteRightInfo = { toShow ->
                PasteMenuView(pasteData = pasteData, toShow = toShow)
            },
        )
    }
}
