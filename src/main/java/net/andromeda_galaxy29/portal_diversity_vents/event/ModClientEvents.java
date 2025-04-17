package net.andromeda_galaxy29.portal_diversity_vents.event;

import net.andromeda_galaxy29.portal_diversity_vents.PortalDiversityVentsMod;
import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.vacuum_tube.VacuumTubeBlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PortalDiversityVentsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(ModBlockEntities.VACUUM_TUBE_BLOCK_ENTITY.get(),
                VacuumTubeBlockEntityRenderer::new);
    }
}
