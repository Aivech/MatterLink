package civilengineering.eventhandlers

import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import civilengineering.cfg
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class DeathEventHandler {
    @SubscribeEvent
    fun handleLivingDeathEvent(event: LivingDeathEvent) {
        if (cfg!!.relay.deathEvents) {
            val entity = event.entityLiving
            if (entity is EntityPlayer) {
                var message: String = entity.getCombatTracker().deathMessage.unformattedText
                message = message[0].toString() + '\u200b' + message.substring(1) //antiping
                MessageHandler.transmit(ApiMessage(username = "Server", text = message))
            }
        }
    }
}
