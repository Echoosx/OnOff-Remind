package org.echoosx.mirai.plugin.command

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.utils.MiraiLogger
import org.echoosx.mirai.plugin.OnOffRemind
import org.echoosx.mirai.plugin.data.ContactMessage.offRemindFriend
import org.echoosx.mirai.plugin.data.ContactMessage.offRemindGroup
import org.echoosx.mirai.plugin.data.RemindMessage.offRemindMessage
import kotlin.system.exitProcess

object ShutdownCommand:SimpleCommand(
    OnOffRemind,
    "stop","shutdown","exit"
) {
    private val logger = OnOffRemind.logger
    private val closingLock = Mutex()

    @OptIn(DelicateCoroutinesApi::class, ConsoleExperimentalApi::class)
    @Handler
    suspend fun CommandSender.stop(){
        GlobalScope.launch {
            kotlin.runCatching {
                closingLock.withLock {
                    if(offRemindMessage.isNotEmpty()) {
                        bot!!.groups.forEach {
                            if (it.id in offRemindGroup) {
                                it.sendMessage(offRemindMessage.deserializeMiraiCode())
                                logger.info("向Group(${it.id})发送了Bot离线提醒")
                            }
                        }
                        bot!!.friends.forEach {
                            if (it.id in offRemindFriend) {
                                it.sendMessage(offRemindMessage.deserializeMiraiCode())
                                logger.info("向Friend(${it.id})发送了Bot离线提醒")
                            }
                        }
                    }
                    if (!MiraiConsole.isActive) return@withLock
                    kotlin.runCatching {
                        Bot.instances.forEach { bot ->
                            lateinit var logger: MiraiLogger
                            kotlin.runCatching {
                                logger = bot.logger
                                bot.closeAndJoin()
                            }.onFailure { t ->
                                kotlin.runCatching { logger.error("Error in closing bot", t) }
                            }
                        }
                        MiraiConsole.job.cancelAndJoin()
                    }
                }
            }.exceptionOrNull()?.let(MiraiConsole.mainLogger::error)
            exitProcess(0)
        }
    }
}