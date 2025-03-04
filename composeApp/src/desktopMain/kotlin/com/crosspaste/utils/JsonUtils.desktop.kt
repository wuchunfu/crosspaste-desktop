package com.crosspaste.utils

import com.crosspaste.paste.item.ColorPasteItem
import com.crosspaste.paste.item.FilesPasteItem
import com.crosspaste.paste.item.HtmlPasteItem
import com.crosspaste.paste.item.ImagesPasteItem
import com.crosspaste.paste.item.PasteItem
import com.crosspaste.paste.item.RtfPasteItem
import com.crosspaste.paste.item.TextPasteItem
import com.crosspaste.paste.item.UrlPasteItem
import com.crosspaste.presist.DirFileInfoTree
import com.crosspaste.presist.FileInfoTree
import com.crosspaste.presist.SingleFileInfoTree
import com.crosspaste.realm.paste.PasteCollection
import com.crosspaste.realm.paste.PasteLabel
import com.crosspaste.realm.task.BaseExtraInfo
import com.crosspaste.realm.task.PasteTaskExtraInfo
import com.crosspaste.realm.task.PullExtraInfo
import com.crosspaste.realm.task.SyncExtraInfo
import com.crosspaste.serializer.Base64ByteArraySerializer
import io.realm.kotlin.serializers.MutableRealmIntKSerializer
import io.realm.kotlin.serializers.RealmAnyKSerializer
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.serializersModuleOf
import kotlinx.serialization.modules.subclass

actual fun getJsonUtils(): JsonUtils {
    return DesktopJsonUtils
}

object DesktopJsonUtils : JsonUtils {

    override val JSON: Json =
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            serializersModule =
                SerializersModule {
                    // use in http request
                    serializersModuleOf(ByteArray::class, Base64ByteArraySerializer())

                    // use in paste data
                    serializersModuleOf(MutableRealmIntKSerializer)
                    serializersModuleOf(RealmAnyKSerializer)
                    polymorphic(PasteItem::class) {
                        subclass(ColorPasteItem::class)
                        subclass(FilesPasteItem::class)
                        subclass(HtmlPasteItem::class)
                        subclass(ImagesPasteItem::class)
                        subclass(RtfPasteItem::class)
                        subclass(TextPasteItem::class)
                        subclass(UrlPasteItem::class)
                    }

                    polymorphic(RealmObject::class) {
                        subclass(PasteLabel::class)
                        subclass(PasteCollection::class)
                    }

                    polymorphic(FileInfoTree::class) {
                        subclass(SingleFileInfoTree::class)
                        subclass(DirFileInfoTree::class)
                    }

                    // use in paste task
                    polymorphic(PasteTaskExtraInfo::class) {
                        subclass(BaseExtraInfo::class)
                        subclass(SyncExtraInfo::class)
                        subclass(PullExtraInfo::class)
                    }
                }
        }
}
