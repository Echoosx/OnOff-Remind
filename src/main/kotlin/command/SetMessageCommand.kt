package org.echoosx.mirai.plugin.command

import kotlinx.coroutines.TimeoutCancellationException
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.isUser
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.nextEventAsync
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.isContentBlank
import net.mamoe.mirai.utils.MiraiExperimentalApi
import org.echoosx.mirai.plugin.OnOffRemind
import org.echoosx.mirai.plugin.data.ContactMessage.offRemindFriend
import org.echoosx.mirai.plugin.data.ContactMessage.offRemindGroup
import org.echoosx.mirai.plugin.data.ContactMessage.onRemindFriend
import org.echoosx.mirai.plugin.data.ContactMessage.onRemindGroup
import org.echoosx.mirai.plugin.data.RemindMessage.offRemindMessage
import org.echoosx.mirai.plugin.data.RemindMessage.onRemindMessage
import java.lang.NumberFormatException

object SetMessageCommand: CompositeCommand(
    OnOffRemind,
    "remind",
    description = "设置提示消息"
) {
    private val logger = OnOffRemind.logger
    private const val WAIT_REPLY_TIMEOUT_MS = 30000L

    @OptIn(MiraiExperimentalApi::class)
    @SubCommand("on","开启")
    suspend fun CommandSender.onMsg() {
        if(this.isUser()){
            try {
                sendMessage("请在${WAIT_REPLY_TIMEOUT_MS/1000}秒内发送Bot启动消息（发送空白内容可清空预设）")
                val msg = subject.nextEventAsync<MessageEvent>(
                    WAIT_REPLY_TIMEOUT_MS,
                    coroutineContext = this.coroutineContext
                ){it.sender == user}.await()
                if(msg.message.isContentBlank()){
                    setRemindMessage(true,null)
                    sendMessage("已清空预设消息")
                }else{
                    setRemindMessage(true,msg.message)
                    sendMessage("OK")
                }
            }catch (e:TimeoutCancellationException){
                sendMessage("超时，本次任务已取消")
            }
        }
    }

    @OptIn(MiraiExperimentalApi::class)
    @SubCommand("off","关闭")
    suspend fun CommandSender.offMsg() {
        if(this.isUser()){
            try {
                sendMessage("请在${WAIT_REPLY_TIMEOUT_MS/1000}秒内发送Bot关闭消息（发送空白内容可清空预设）")
                val msg = subject.nextEventAsync<MessageEvent>(
                    WAIT_REPLY_TIMEOUT_MS,
                    coroutineContext = this.coroutineContext
                ){it.sender == user}.await()
                if(msg.message.isContentBlank()){
                    setRemindMessage(false,null)
                    sendMessage("已清空预设消息")
                }else{
                    setRemindMessage(false,msg.message)
                    sendMessage("OK")
                }
            }catch (e:TimeoutCancellationException){
                sendMessage("超时，本次任务已取消")
            }
        }
    }

    @SubCommand("add","添加")
    suspend fun CommandSender.add(contact:String, type:String = "all"){
        try{
            if(contact.startsWith("g")){
                val groupId = contact.drop(1).toLong()
                if(bot!!.groups.contains(groupId)){
                    when(type) {
                        "on" -> {
                            onRemindGroup.add(groupId)
                            sendMessage("OK")
                        }
                        "off" -> {
                            offRemindGroup.add(groupId)
                            sendMessage("OK")
                        }
                        "all" -> {
                            onRemindGroup.add(groupId)
                            offRemindGroup.add(groupId)
                            sendMessage("OK")
                        }
                        else -> {
                            sendMessage("参数有误！")
                        }
                    }
                }else{
                    sendMessage("该群组不存在！")
                }
            }else if(contact.startsWith("u")){
                val userId = contact.drop(1).toLong()
                if(bot!!.friends.contains(userId)){
                    when(type) {
                        "on" -> {
                            onRemindFriend.add(userId)
                            sendMessage("OK")
                        }
                        "off" -> {
                            offRemindFriend.add(userId)
                            sendMessage("OK")
                        }
                        "all" -> {
                            onRemindFriend.add(userId)
                            offRemindFriend.add(userId)
                            sendMessage("OK")
                        }
                        else -> {
                            sendMessage("参数有误！")
                        }
                    }
                }
            }else {
                try {
                    val id = contact.toLong()
                    if(bot!!.groups.contains(id)) {
                        when (type) {
                            "on" -> {
                                onRemindGroup.add(id)
                                sendMessage("OK")
                            }
                            "off" -> {
                                offRemindGroup.add(id)
                                sendMessage("OK")
                            }
                            "all" -> {
                                onRemindGroup.add(id)
                                offRemindGroup.add(id)
                                sendMessage("OK")
                            }
                            else -> {
                                sendMessage("参数有误！")
                            }
                        }
                    }
                    if(bot!!.friends.contains(id)){
                        when (type) {
                            "on" -> {
                                onRemindFriend.add(id)
                                sendMessage("OK")
                            }
                            "off" -> {
                                offRemindFriend.add(id)
                                sendMessage("OK")
                            }
                            "all" -> {
                                onRemindFriend.add(id)
                                offRemindFriend.add(id)
                                sendMessage("OK")
                            }
                            else -> {
                                sendMessage("参数有误！")
                            }
                        }
                    }
                }catch (e:NumberFormatException){
                    sendMessage("参数有误！")
                }
            }
        }catch(e:Throwable){
            sendMessage("添加失败！")
            logger.error(e.message)
        }
    }

    @SubCommand("remove","删除")
    suspend fun CommandSender.remove(contact: String, type: String = "all"){
        try {
            if(contact.startsWith("g")){
                val groupId = contact.drop(1).toLong()
                when (type) {
                    "on" -> {
                        onRemindGroup.remove(groupId)
                        sendMessage("OK")
                    }
                    "off" -> {
                        offRemindGroup.remove(groupId)
                        sendMessage("OK")
                    }
                    "all" -> {
                        onRemindGroup.remove(groupId)
                        offRemindGroup.remove(groupId)
                        sendMessage("OK")
                    }
                    else -> {
                        sendMessage("参数有误！")
                    }
                }
            }else if (contact.startsWith("u")){
                val userId = contact.drop(1).toLong()
                when (type) {
                    "on" -> {
                        onRemindFriend.remove(userId)
                        sendMessage("OK")
                    }
                    "off" -> {
                        offRemindFriend.remove(userId)
                        sendMessage("OK")
                    }
                    "all" -> {
                        onRemindFriend.remove(userId)
                        offRemindFriend.remove(userId)
                        sendMessage("OK")
                    }
                    else -> {
                        sendMessage("参数有误！")
                    }
                }
            }else {
                try {
                    val id = contact.toLong()
                    when (type) {
                        "on" -> {
                            onRemindFriend.remove(id)
                            onRemindGroup.remove(id)
                            sendMessage("OK")
                        }
                        "off" -> {
                            offRemindFriend.remove(id)
                            offRemindGroup.remove(id)
                            sendMessage("OK")
                        }
                        "all" -> {
                            onRemindFriend.remove(id)
                            onRemindGroup.remove(id)
                            offRemindFriend.remove(id)
                            offRemindGroup.remove(id)
                            sendMessage("OK")
                        }
                        else -> {
                            sendMessage("参数有误！")
                        }
                    }
                } catch (e: NumberFormatException) {
                    sendMessage("参数有误！")
                }
            }
        }catch (e:Throwable){
            sendMessage("删除失败！")
            logger.error(e.message)
        }
    }

    @SubCommand("list","列表")
    suspend fun CommandSender.list(){
        try {
            val message = buildMessageChain {
                appendLine("====上线通知====")
                for (id in onRemindGroup){
                    if(bot!!.groups.contains(id)){
                        appendLine("群组[${bot!!.groups[id]!!.name}](${id})")
                    }else{
                        appendLine("群组[Unknown](${id})")
                    }
                }
                for (id in onRemindFriend){
                    if(bot!!.friends.contains(id)){
                        appendLine("好友[${bot!!.friends[id]!!.nick}](${id})")
                    }else{
                        appendLine("好友[Unknown](${id})")
                    }
                }
                appendLine("====下线通知====")
                for (id in offRemindGroup){
                    if(bot!!.groups.contains(id)){
                        appendLine("群组[${bot!!.groups[id]!!.name}](${id})")
                    }else{
                        appendLine("群组[Unknown](${id})")
                    }
                }
                for (id in offRemindFriend){
                    if(bot!!.friends.contains(id)){
                        appendLine("好友[${bot!!.friends[id]!!.nick}](${id})")
                    }else{
                        appendLine("好友[Unknown](${id})")
                    }
                }
            }
            sendMessage(message)
        }catch (e:Throwable){
            sendMessage("列表显示失败！")
            logger.error(e.message)
        }
    }

    private fun setRemindMessage(isOn:Boolean, messageChain: MessageChain?){
        if(isOn){
            onRemindMessage = messageChain?.serializeToMiraiCode() ?: ""
            logger.info("已设置Bot启动消息 \"${onRemindMessage}\"")
        }else{
            offRemindMessage = messageChain?.serializeToMiraiCode() ?: ""
            logger.info("已设置Bot关闭消息 \"${offRemindMessage}\"")
        }
    }
}