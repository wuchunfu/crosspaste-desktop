package com.crosspaste.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.crosspaste.app.AppSize
import com.crosspaste.app.AppTokenApi
import com.crosspaste.app.DesktopAppSize
import com.crosspaste.app.DesktopAppWindowManager
import com.crosspaste.notification.ToastManager
import com.crosspaste.ui.base.DialogService
import com.crosspaste.ui.base.ToastView
import com.crosspaste.ui.devices.TokenView
import com.crosspaste.ui.theme.CrossPasteTheme.Theme
import com.crosspaste.utils.GlobalCoroutineScope.mainCoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

fun hideWindow(appWindowManager: DesktopAppWindowManager) {
    mainCoroutineDispatcher.launch(CoroutineName("Hide CrossPaste")) {
        appWindowManager.unActiveMainWindow()
    }
}

@Composable
fun CrossPasteMainWindowContent() {
    val appSize = koinInject<AppSize>() as DesktopAppSize
    val appWindowManager = koinInject<DesktopAppWindowManager>()
    val appTokenApi = koinInject<AppTokenApi>()
    val dialogService = koinInject<DialogService>()
    val screenProvider = koinInject<ScreenProvider>()
    val toastManager = koinInject<ToastManager>()
    val toast by toastManager.toast.collectAsState()

    Theme {
        Box(
            modifier =
                Modifier
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                hideWindow(appWindowManager)
                            },
                            onTap = {
                                hideWindow(appWindowManager)
                            },
                            onLongPress = {
                                hideWindow(appWindowManager)
                            },
                            onPress = {},
                        )
                    }
                    .clip(RoundedCornerShape(appSize.mainShadowSize))
                    .fillMaxSize()
                    .padding(appSize.mainShadowPaddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .shadow(appSize.mainShadowSize, appSize.appRoundedCornerShape)
                        .fillMaxSize()
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        .pointerInput(Unit) {
                            // Avoid click-through
                            detectTapGestures()
                        },
                contentAlignment = Alignment.Center,
            ) {
                ThemeBackground()

                Column(
                    Modifier
                        .clip(appSize.appRoundedCornerShape)
                        .fillMaxWidth()
                        .focusTarget()
                        .focusRequester(appWindowManager.mainFocusRequester),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    screenProvider.CrossPasteScreen()
                }

                toast?.let {
                    ToastView(toast = it) {
                        toastManager.cancel()
                    }
                }

                val dialog by dialogService.dialogs.collectAsState()

                dialog.firstOrNull()?.content()

                val showToken by appTokenApi.showToken.collectAsState()

                if (showToken) {
                    TokenView()
                }
            }
        }
    }
}
