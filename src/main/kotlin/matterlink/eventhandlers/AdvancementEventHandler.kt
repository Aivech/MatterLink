package matterlink.eventhandlers

import matterlink.bridge.ApiMessage
import matterlink.bridge.MessageHandler
import matterlink.cfg
import matterlink.antiping
import net.minecraftforge.event.entity.player.AdvancementEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class AdvancementEventHandler {
    @SubscribeEvent
    fun handleAdvancements(event: AdvancementEvent) {
        if (cfg!!.relay.advancements && event.advancement.display != null) {
            val player = event.entityPlayer.name.antiping()
            val content = event.advancement.displayText.unformattedText
            MessageHandler.transmit(ApiMessage(
                    username = cfg!!.relay.systemUser,
                    text = "$player has earned the advancement $content"
            ))
        }
    }
}