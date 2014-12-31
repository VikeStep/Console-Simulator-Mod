package com.vikestep.consolesimulator;

import com.vikestep.consolesimulator.client.handlers.FPSHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy
{
    @Override
    public void init()
    {
        super.init();
        FMLCommonHandler.instance().bus().register(new FPSHandler());
    }
}
