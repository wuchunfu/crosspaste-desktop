package com.crosspaste.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.crosspaste.clean.CleanTime
import com.crosspaste.config.ConfigManager
import com.crosspaste.i18n.GlobalCopywriter
import com.crosspaste.realm.paste.PasteRealm
import com.crosspaste.ui.base.CustomRectangleSwitch
import com.crosspaste.ui.base.anglesUpDown
import com.crosspaste.ui.base.clock
import com.crosspaste.ui.base.database
import com.crosspaste.ui.base.file
import com.crosspaste.ui.base.hashtag
import com.crosspaste.ui.base.html
import com.crosspaste.ui.base.image
import com.crosspaste.ui.base.link
import com.crosspaste.ui.base.measureTextWidth
import com.crosspaste.ui.base.percent
import com.crosspaste.ui.base.text
import com.crosspaste.ui.base.trash
import com.crosspaste.utils.Quadruple
import com.crosspaste.utils.getFileUtils
import org.koin.compose.koinInject

@Composable
fun StoreSettingsContentView() {
    val density = LocalDensity.current
    val configManager = koinInject<ConfigManager>()
    val pasteRealm = koinInject<PasteRealm>()
    val copywriter = koinInject<GlobalCopywriter>()

    val fileUtils = getFileUtils()

    var pasteCount: Long? by remember { mutableStateOf(null) }
    var pasteFormatSize: String? by remember { mutableStateOf(null) }

    var textCount: Long? by remember { mutableStateOf(null) }
    var textFormatSize: String? by remember { mutableStateOf(null) }

    var urlCount: Long? by remember { mutableStateOf(null) }
    var urlFormatSize: String? by remember { mutableStateOf(null) }

    var htmlCount: Long? by remember { mutableStateOf(null) }
    var htmlFormatSize: String? by remember { mutableStateOf(null) }

    var imageCount: Long? by remember { mutableStateOf(null) }
    var imageFormatSize: String? by remember { mutableStateOf(null) }

    var fileCount: Long? by remember { mutableStateOf(null) }
    var fileFormatSize: String? by remember { mutableStateOf(null) }

    var allOrFavorite by remember { mutableStateOf(true) }

    val refresh: (Boolean) -> Unit = {
        val pasteResourceInfo = pasteRealm.getPasteResourceInfo(it)
        pasteCount = pasteResourceInfo.pasteCount
        pasteFormatSize = fileUtils.formatBytes(pasteResourceInfo.pasteSize)

        textCount = pasteResourceInfo.textCount
        textFormatSize = fileUtils.formatBytes(pasteResourceInfo.textSize)

        urlCount = pasteResourceInfo.urlCount
        urlFormatSize = fileUtils.formatBytes(pasteResourceInfo.urlSize)

        htmlCount = pasteResourceInfo.htmlCount
        htmlFormatSize = fileUtils.formatBytes(pasteResourceInfo.htmlSize)

        imageCount = pasteResourceInfo.imageCount
        imageFormatSize = fileUtils.formatBytes(pasteResourceInfo.imageSize)

        fileCount = pasteResourceInfo.fileCount
        fileFormatSize = fileUtils.formatBytes(pasteResourceInfo.fileSize)
    }

    LaunchedEffect(Unit) {
        refresh(allOrFavorite)
    }

    Text(
        modifier =
            Modifier.wrapContentSize()
                .padding(start = 32.dp, top = 5.dp, bottom = 5.dp),
        text = copywriter.getText("store_info"),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineSmall,
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
    )

    var nameMaxWidth by remember { mutableStateOf(96.dp) }

    val pasteTypes: Array<Quadruple<String, Painter, Long?, String?>> =
        arrayOf(
            Quadruple("pasteboard", hashtag(), pasteCount, pasteFormatSize),
            Quadruple("text", text(), textCount, textFormatSize),
            Quadruple("link", link(), urlCount, urlFormatSize),
            Quadruple("html", html(), htmlCount, htmlFormatSize),
            Quadruple("image", image(), imageCount, imageFormatSize),
            Quadruple("file", file(), fileCount, fileFormatSize),
        )

    val textStyle =
        TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            fontFamily = FontFamily.SansSerif,
        )

    for (property in pasteTypes) {
        nameMaxWidth = maxOf(nameMaxWidth, measureTextWidth(copywriter.getText(property.first), textStyle))
    }

    Column(
        modifier =
            Modifier.wrapContentSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomRectangleSwitch(
                    modifier = Modifier.width(96.dp).height(30.dp),
                    checked = allOrFavorite,
                    onCheckedChange = { newAllOrFavorite ->
                        allOrFavorite = newAllOrFavorite
                        refresh(allOrFavorite)
                    },
                    textStyle =
                        TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.SansSerif,
                        ),
                    checkedText = copywriter.getText("all_storage"),
                    uncheckedText = copywriter.getText("favorite_storage"),
                )
            }

            Row(
                modifier = Modifier.weight(0.25f),
                horizontalArrangement = Arrangement.End,
            ) {
                SettingsText(copywriter.getText("count"))
            }

            Row(
                modifier = Modifier.weight(0.3f),
                horizontalArrangement = Arrangement.End,
            ) {
                SettingsText(copywriter.getText("size"))
            }
        }

        HorizontalDivider(modifier = Modifier.padding(start = 35.dp))

        pasteTypes.forEachIndexed { index, quadruple ->
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = quadruple.second,
                    contentDescription = "pasteboard",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.width(8.dp))

                SettingsText(
                    copywriter.getText(quadruple.first),
                    modifier = Modifier.width(nameMaxWidth),
                )

                Row(
                    modifier = Modifier.weight(0.2f),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (quadruple.third != null) {
                        SettingsText("${quadruple.third}")
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(25.dp))
                    }
                }

                Row(
                    modifier = Modifier.weight(0.3f),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (quadruple.fourth != null) {
                        SettingsText(quadruple.fourth)
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(25.dp))
                    }
                }
            }

            if (index != pasteTypes.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(start = 35.dp))
            }
        }
    }

    SetStoragePathView()

    Text(
        modifier =
            Modifier.wrapContentSize()
                .padding(start = 32.dp, top = 5.dp, bottom = 5.dp),
        text = copywriter.getText("auto_cleanup_settings"),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineSmall,
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
    )

    Column(
        modifier =
            Modifier.wrapContentSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background),
    ) {
        SettingSwitchItemView(
            text = "expiration_cleanup",
            painter = trash(),
            getCurrentSwitchValue = { configManager.config.enableExpirationCleanup },
        ) {
            configManager.updateConfig("enableExpirationCleanup", it)
        }

        HorizontalDivider(modifier = Modifier.padding(start = 35.dp))

        SettingItemView(
            painter = clock(),
            text = "image_expiry_period",
            tint = MaterialTheme.colorScheme.onBackground,
        ) {
            var selectImageCleanTimeIndex by remember { mutableStateOf(configManager.config.imageCleanTimeIndex) }

            val imageCleanTime = CleanTime.entries[selectImageCleanTimeIndex]

            var imageCleanTimeValue by remember(copywriter.language()) {
                mutableStateOf("${imageCleanTime.quantity} ${copywriter.getText(imageCleanTime.unit)}")
            }
            val imageCleanTimeWidth = measureTextWidth(imageCleanTimeValue, SettingsTextStyle())

            var showImageCleanTimeMenu by remember { mutableStateOf(false) }

            Row(
                modifier =
                    Modifier.wrapContentWidth()
                        .clickable {
                            showImageCleanTimeMenu = !showImageCleanTimeMenu
                        },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsText(
                    modifier = Modifier.width(imageCleanTimeWidth),
                    text = imageCleanTimeValue,
                )

                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = anglesUpDown(),
                    contentDescription = "Image expiration time",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

            if (showImageCleanTimeMenu) {
                Popup(
                    alignment = Alignment.BottomEnd,
                    offset =
                        IntOffset(
                            with(density) { (-(imageCleanTimeWidth + 30.dp)).roundToPx() },
                            with(density) { (0.dp).roundToPx() },
                        ),
                    onDismissRequest = {
                        if (showImageCleanTimeMenu) {
                            showImageCleanTimeMenu = false
                        }
                    },
                    properties =
                        PopupProperties(
                            focusable = true,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                        ),
                ) {
                    CleanTimeMenuView(selectImageCleanTimeIndex) { index ->
                        configManager.updateConfig("imageCleanTimeIndex", index)
                        selectImageCleanTimeIndex = configManager.config.imageCleanTimeIndex
                        val currentImageCleanTime = CleanTime.entries[selectImageCleanTimeIndex]
                        imageCleanTimeValue =
                            "${currentImageCleanTime.quantity} ${copywriter.getText(currentImageCleanTime.unit)}"
                        showImageCleanTimeMenu = false
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(start = 35.dp))

        SettingItemView(
            painter = file(),
            text = "file_expiry_period",
            tint = MaterialTheme.colorScheme.onBackground,
        ) {
            var selectFileCleanTimeIndex by remember { mutableStateOf(configManager.config.fileCleanTimeIndex) }

            val fileCleanTime = CleanTime.entries[selectFileCleanTimeIndex]

            var fileCleanTimeValue by remember(copywriter.language()) {
                mutableStateOf("${fileCleanTime.quantity} ${copywriter.getText(fileCleanTime.unit)}")
            }
            val fileCleanTimeWidth = measureTextWidth(fileCleanTimeValue, SettingsTextStyle())

            var showFileCleanTimeMenu by remember { mutableStateOf(false) }

            Row(
                modifier =
                    Modifier.wrapContentWidth()
                        .clickable {
                            showFileCleanTimeMenu = !showFileCleanTimeMenu
                        },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.width(fileCleanTimeWidth),
                    text = fileCleanTimeValue,
                    style = SettingsTextStyle(),
                )

                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = anglesUpDown(),
                    contentDescription = "File Expiry Period",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

            if (showFileCleanTimeMenu) {
                Popup(
                    alignment = Alignment.BottomEnd,
                    offset =
                        IntOffset(
                            with(density) { (-(fileCleanTimeWidth + 30.dp)).roundToPx() },
                            with(density) { (0.dp).roundToPx() },
                        ),
                    onDismissRequest = {
                        if (showFileCleanTimeMenu) {
                            showFileCleanTimeMenu = false
                        }
                    },
                    properties =
                        PopupProperties(
                            focusable = true,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                        ),
                ) {
                    CleanTimeMenuView(selectFileCleanTimeIndex) { index ->
                        configManager.updateConfig("fileCleanTimeIndex", index)
                        selectFileCleanTimeIndex = configManager.config.fileCleanTimeIndex
                        val currentFileCleanTime = CleanTime.entries[selectFileCleanTimeIndex]
                        fileCleanTimeValue =
                            "${currentFileCleanTime.quantity} ${copywriter.getText(currentFileCleanTime.unit)}"
                        showFileCleanTimeMenu = false
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier =
            Modifier.wrapContentSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background),
    ) {
        SettingSwitchItemView(
            text = "threshold_cleanup",
            painter = trash(),
            getCurrentSwitchValue = { configManager.config.enableThresholdCleanup },
        ) {
            configManager.updateConfig("enableThresholdCleanup", it)
        }

        HorizontalDivider(modifier = Modifier.padding(start = 35.dp))

        SettingCounterItemView(
            text = "maximum_storage",
            painter = database(),
            unit = "MB",
            rule = { it >= 256 },
            getCurrentCounterValue = { configManager.config.maxStorage },
        ) {
            configManager.updateConfig("maxStorage", it)
        }

        HorizontalDivider(modifier = Modifier.padding(start = 35.dp))

        SettingCounterItemView(
            text = "cleanup_percentage",
            painter = percent(),
            unit = "%",
            rule = { it in 10..50 },
            getCurrentCounterValue = { configManager.config.cleanupPercentage.toLong() },
        ) {
            configManager.updateConfig("cleanupPercentage", it.toInt())
        }
    }
}