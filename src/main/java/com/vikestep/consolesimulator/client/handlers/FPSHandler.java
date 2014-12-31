package com.vikestep.consolesimulator.client.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;

public class FPSHandler
{
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        //CINEMATIC WOW
        Minecraft.getMinecraft().gameSettings.limitFramerate = 30;
    }
}
