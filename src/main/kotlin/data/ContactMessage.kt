package org.echoosx.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object ContactMessage:AutoSavePluginData("Contacts") {
    @ValueDescription("上线通知 群")
    val onRemindGroup:MutableSet<Long> by value()

    @ValueDescription("上线通知 用户")
    val onRemindFriend:MutableSet<Long> by value()

    @ValueDescription("下线通知 群")
    val offRemindGroup:MutableSet<Long> by value()

    @ValueDescription("下线通知 用户")
    val offRemindFriend:MutableSet<Long> by value()
}