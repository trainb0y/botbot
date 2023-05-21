package io.github.trainb0y.botbot.extensions

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.linkButton
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.getJumpUrl
import com.kotlindiscord.kord.extensions.utils.permissionsForMember
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel


class ChannelLinkExtension : Extension() {
    override val name = "channel-link"

    override suspend fun setup() {
        ephemeralSlashCommand(::ChannelLinkArgs) {
            name = "channel-link"
            description = "Link a conversation between channels"

            check {
                hasPermission(Permission.SendMessages)
            }

            action {
                val from = (this.channel.fetchChannel() as TextChannel)
                val to = (arguments.channel.fetchChannel() as TextChannel)

                // check whether can send messages in new channel
                if (!to.fetchChannel().permissionsForMember(user).contains(Permission.SendMessages)) {
                    respond {
                        this.content = "You don't have permission to send messages in ${channel.mention}"
                    }
                    return@action
                }

                // link
                val link = to.createMessage("\u200E")

                val back = from.createMessage {
                    components {
                        linkButton {
                            this.url = link.getJumpUrl()
                            this.label = "\uD83D\uDD17 to #${to.name}"
                        }
                    }
                }
                link.edit {
                    //content = null
                    components {
                        linkButton {
                            this.url = back.getJumpUrl()
                            this.label = "\uD83D\uDD17 from #${from.name}"
                        }
                    }
                }

                respond {
                    content = "Created channel link to ${to.mention}"
                }
            }
        }
    }

    inner class ChannelLinkArgs : Arguments() {
        val channel: Channel by channel {
            name = "channel"
            description = "The channel to link to"
            requiredChannelTypes = mutableSetOf(ChannelType.GuildText)
        }
    }
}