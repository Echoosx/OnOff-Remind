package org.echoosx.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object RemindMessage:AutoSavePluginData("message") {
    @ValueDescription("启动提示")
    var onRemindMessage:String by value()

    @ValueDescription("关闭提示")
    var offRemindMessage:String by value()
}