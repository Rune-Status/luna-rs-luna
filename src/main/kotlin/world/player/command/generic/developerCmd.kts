package world.player.command.generic

import api.predef.*
import api.predef.ext.*
import io.luna.game.model.item.Bank.DynamicBankInterface
import io.luna.game.model.item.Item
import io.luna.game.model.mob.Npc
import io.luna.game.model.mob.Player
import io.luna.game.model.mob.block.Animation
import io.luna.game.model.mob.block.Graphic
import io.luna.game.model.mob.bot.Bot
import io.luna.game.model.mob.inter.NameInputInterface
import io.luna.game.model.mob.inter.StandardInterface
import io.luna.game.model.mob.varp.Varp
import io.luna.net.msg.out.MusicMessageWriter
import io.luna.net.msg.out.SoundMessageWriter
import io.luna.util.CacheDumpUtils
import world.player.crystalChest.CrystalChestDropTable
import java.lang.Boolean.parseBoolean


/**
 * A command that sends a client config.
 */
cmd("config", RIGHTS_DEV) {
    val id = asInt(0)
    val value = if (args.size == 1) 0 else asInt(1)
    plr.sendMessage("config[$id] = $value")
    plr.sendVarp(Varp(id, value))
}

/**
 * A command that re-dumps cache definitions.
 */
cmd("dumpcache", RIGHTS_DEV) {
    game.submit { CacheDumpUtils.dump() }.addListener({ plr.sendMessage("Cache dump complete.") }, game.executor)
    plr.sendMessage("Dumping cache data...")
}

/**
 * A command that creates and logs in bots. Arguments for amount and if their equipment should be randomized.
 */
cmd("bots", RIGHTS_DEV) {
    val count = if (args.isNotEmpty()) asInt(0) else 1
    val randomEquipment = if (args.size == 2) parseBoolean(args[1]) else false
    repeat(count) {
        val bot = Bot.Builder(ctx).build()
        bot.login()
        if (randomEquipment) {
            bot.randomizeEquipment()
        }
    }
}

/**
 * Deletes a saved record of a player.
 */
cmd("delete", RIGHTS_DEV) {
    plr.interfaces.open(object : NameInputInterface() {
        override fun onNameInput(player: Player?, value: String?) {
            plr.newDialogue().empty("Are you sure you wish to delete all records for '$value' ?")
                .options("Yes",
                         {
                             world.persistenceService.delete(value)
                                 .addListener({ plr.sendMessage("Done, deleted $value") },
                                              game.executor); plr.interfaces.close()
                         },
                         "No",
                         { plr.interfaces.close() }).open()
        }
    })
}

/**
 * A command that spawns a non-player character.
 */
cmd("npc", RIGHTS_DEV) {
    val npc = Npc(ctx, asInt(0), plr.position)
    world.addNpc(npc)
}

/**
 * A command that spawns an object.
 */
cmd("object", RIGHTS_DEV) {
    val pos = plr.position
    world.addObject(id = asInt(0),
                    x = pos.x,
                    y = pos.y,
                    z = pos.z,
                    plr = plr)
}

cmd("roll", RIGHTS_DEV) {
    val times = asInt(0)
    plr.interfaces.open(object : DynamicBankInterface("Drop simulation for 'Crystal chest'") {
        override fun buildDisplayItems(player: Player?): MutableList<Item> {
            val items = arrayListOf<Item>()
            repeat(times) {
                items += CrystalChestDropTable.roll(plr, plr)
            }
            return items
        }
    })
}

/**
 * A command that sends the current position.
 */
cmd("mypos", RIGHTS_DEV) {
    plr.sendMessage(plr.position)
    plr.sendMessage(plr.chunk)
    plr.sendMessage(plr.position.region)
}

/**
 * A command that will play music.
 */
cmd("music", RIGHTS_DEV) {
    val id = asInt(0)
    plr.queue(MusicMessageWriter(id))
}

/**
 * A command that opens an interface.
 */
cmd("interface", RIGHTS_DEV) {
    val id = asInt(0)
    plr.interfaces.open(StandardInterface(id))
}

/**
 * A command that plays a sound.
 */
cmd("sound", RIGHTS_DEV) {
    val id = asInt(0)
    plr.queue(SoundMessageWriter(id, 0, 0))
}

/**
 * A command that plays a graphic.
 */
cmd("graphic", RIGHTS_DEV) {
    val id = asInt(0)
    plr.graphic(Graphic(id))
}

/**
 * A command that plays an animation.
 */
cmd("animation", RIGHTS_DEV) {
    val id = asInt(0)
    plr.animation(Animation(id))
}
