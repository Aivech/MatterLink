package civilengineering.eventhandlers

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import civilengineering.util.Util.antiping
import civilengineering.bridge.ApiMessage
import civilengineering.bridge.MessageHandler
import civilengineering.cfg

class JoinLeaveHandler {
    @SubscribeEvent
    fun handleJoinEvent(event: PlayerEvent.PlayerLoggedInEvent) {
        if(cfg!!.relay.joinLeave) {
            val player:String = event.player.name.antiping()
            MessageHandler.transmit(ApiMessage(
                    username = "Server",
                    text = "$player has connected to the server."
            ))
        }
    }

    @SubscribeEvent
    fun handleLeaveEvent(event: PlayerEvent.PlayerLoggedOutEvent) {
        if(cfg!!.relay.joinLeave) {
            val player:String = event.player.name.antiping()
            MessageHandler.transmit(ApiMessage(
                    username = "Server",
                    text = "$player has disconnected from the server."
            ))
        }
    }
}