package org.echoosx.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.utils.info
import org.echoosx.mirai.plugin.command.SetMessageCommand
import org.echoosx.mirai.plugin.command.ShutdownCommand
import org.echoosx.mirai.plugin.data.ContactMessage
import org.echoosx.mirai.plugin.data.ContactMessage.onRemindFriend
import org.echoosx.mirai.plugin.data.ContactMessage.onRemindGroup
import org.echoosx.mirai.plugin.data.RemindMessage
import org.echoosx.mirai.plugin.data.RemindMessage.onRemindMessage

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "org.echoosx.mirai.plugin.OnOff-Message",
        name = "OnOff-Message",
        version = "1.0.0"
    ) {
        author("Echoosx")
    }
) {
    override fun onEnable() {
        RemindMessage.reload()
        ContactMessage.reload()
        SetMessageCommand.register()
        ShutdownCommand.register(override = true)
        logger.info { "Plugin loaded" }

        val eventChannel = GlobalEventChannel.parentScope(this)
        eventChannel.filter { onRemindMessage.isNotEmpty() }.subscribeOnce<BotOnlineEvent> {
            bot.groups.forEach {
                if(it.id in onRemindGroup) {
                    it.sendMessage(onRemindMessage.deserializeMiraiCode())
                    logger.info("向Group(${it.id})发送了Bot上线提醒")
                }
            }
            bot.friends.forEach{
                if(it.id in onRemindFriend) {
                    it.sendMessage(onRemindMessage.deserializeMiraiCode())
                    logger.info("向Friend(${it.id})发送了Bot上线提醒")
                }
            }
        }
    }
}
