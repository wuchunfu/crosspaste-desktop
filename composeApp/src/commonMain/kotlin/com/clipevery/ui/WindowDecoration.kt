package com.clipevery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clipevery.LocalKoinApplication
import com.clipevery.PageType
import com.clipevery.i18n.GlobalCopywriter

@Composable
fun WindowDecoration(currentPage: MutableState<PageType>, title: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        color = MaterialTheme.colors.background,
        shape = RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        )
    ) {
        DecorationUI(currentPage, title)
    }
}

@Composable
fun DecorationUI(currentPage: MutableState<PageType>, title: String) {
    val current = LocalKoinApplication.current
    val copywriter = current.koin.get<GlobalCopywriter>()

    val customFontFamily = FontFamily(
        Font(resource = "font/BebasNeue.otf", FontWeight.Normal)
    )

    Box(
        modifier = Modifier.background(Color.Black)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    startY = 0.0f,
                    endY = 3.0f
                )
            ),
    ) {
        Row(modifier = Modifier.align(Alignment.CenterStart)
            .wrapContentWidth(),
            horizontalArrangement = Arrangement.Center) {
            Column(Modifier.weight(0.5f)
                .align(Alignment.CenterVertically)
                .offset(y = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.padding(16.dp)
                    .align(Alignment.Start)
                    .clickable { currentPage.value = PageType.HOME }) {
                    Icon(imageVector = arrowLeft(),
                        contentDescription = null,
                        tint = Color(0xFF1672FF))
                    Text(
                        text = copywriter.getText("Return"),
                        color = Color(0xFF1672FF),
                        fontWeight = FontWeight.Light
                    )
                }
            }
            Column(Modifier.weight(0.5f)
                .align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier.padding(16.dp)
                        .align(Alignment.End),
                    text = copywriter.getText(title),
                    color = Color.White,
                    fontSize = 25.sp,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontFamily = customFontFamily
                )
            }

        }
    }
}