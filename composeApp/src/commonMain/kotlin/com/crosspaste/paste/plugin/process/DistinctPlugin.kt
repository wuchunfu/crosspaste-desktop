package com.crosspaste.paste.plugin.process

import com.crosspaste.paste.item.PasteItem
import com.crosspaste.path.UserDataPathProvider
import com.crosspaste.realm.paste.PasteType
import io.realm.kotlin.MutableRealm

class DistinctPlugin(userDataPathProvider: UserDataPathProvider) : PasteProcessPlugin {

    private val firstPlugin = FirstPlugin(userDataPathProvider)

    private val childPlugins =
        mapOf(
            Pair(
                PasteType.IMAGE_TYPE,
                MultiImagesPlugin(userDataPathProvider),
            ),
            Pair(
                PasteType.FILE_TYPE,
                MultFilesPlugin(userDataPathProvider),
            ),
            Pair(PasteType.TEXT_TYPE, firstPlugin),
            Pair(PasteType.COLOR_TYPE, firstPlugin),
            Pair(PasteType.URL_TYPE, firstPlugin),
            Pair(PasteType.HTML_TYPE, firstPlugin),
            Pair(PasteType.RTF_TYPE, firstPlugin),
        )

    override fun process(
        pasteItems: List<PasteItem>,
        realm: MutableRealm,
        source: String?,
    ): List<PasteItem> {
        return pasteItems.groupBy { it.getPasteType() }.map { (pasteType, items) ->
            val plugin = childPlugins[pasteType]
            plugin?.process(items, realm, source) ?: items
        }.flatten()
    }
}

class FirstPlugin(private val userDataPathProvider: UserDataPathProvider) : PasteProcessPlugin {
    override fun process(
        pasteItems: List<PasteItem>,
        realm: MutableRealm,
        source: String?,
    ): List<PasteItem> {
        return if (pasteItems.isEmpty()) {
            listOf()
        } else {
            for (pasteAppearItem in pasteItems.drop(1)) {
                pasteAppearItem.clear(realm, userDataPathProvider)
            }
            listOf(pasteItems.first())
        }
    }
}
