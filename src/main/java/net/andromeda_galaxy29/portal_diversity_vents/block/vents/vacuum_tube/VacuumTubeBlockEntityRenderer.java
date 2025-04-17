package net.andromeda_galaxy29.portal_diversity_vents.block.vents.vacuum_tube;

import com.mojang.blaze3d.vertex.PoseStack;
import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class VacuumTubeBlockEntityRenderer implements BlockEntityRenderer<VacuumTubeBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public VacuumTubeBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(VacuumTubeBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        if(blockEntity.isConnected(Direction.AxisDirection.POSITIVE) && blockEntity.shouldRenderCurve(Direction.AxisDirection.POSITIVE)){
            renderConnection(Direction.AxisDirection.POSITIVE, blockEntity, v, poseStack, multiBufferSource, packedLight, packedOverlay);
        }
        if(blockEntity.isConnected(Direction.AxisDirection.NEGATIVE) && blockEntity.shouldRenderCurve(Direction.AxisDirection.NEGATIVE)){
            renderConnection(Direction.AxisDirection.NEGATIVE, blockEntity, v, poseStack, multiBufferSource, packedLight, packedOverlay);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(VacuumTubeBlockEntity blockEntity) {
        return true;
    }

    public void renderConnection(Direction.AxisDirection dir, VacuumTubeBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay){
        double length = dir == Direction.AxisDirection.POSITIVE ? blockEntity.connectLenPositive : blockEntity.connectLenNegative;
        double interval = 1.0 / Math.round(length / (13/16.0));
        Vec3 prevPos;
        Vec3 pos = new Vec3(0, 0, 0);
        for(double i = interval/2; i <= 1; i += interval) {
            prevPos = pos;
            pos = blockEntity.getOffsetInCurve(dir, i);

            Vec3 dirVector = prevPos.vectorTo(pos);
            Vec3 zVector = new Vec3(0, 0, -1);
            Vec3 horVector = new Vec3(dirVector.x, 0, dirVector.z);

            float xAngle = (float) Math.acos(horVector.dot(dirVector) / (horVector.length() * dirVector.length()))
                    * (dirVector.y < 0 ? -1 : 1);
            Quaternionf xRotation = new Quaternionf().fromAxisAngleRad(new Vector3f(1, 0, 0), xAngle);
            float yAngle = (float) Math.acos(zVector.dot(horVector) / (zVector.length() * horVector.length()))
                    * (horVector.x > 0 ? -1 : 1);
            Quaternionf yRotation = new Quaternionf().fromAxisAngleRad(new Vector3f(0, 1, 0), yAngle);

            Quaternionf rotation;
            if(!Float.isNaN(xAngle)){ // Sometimes xAngle is NaN for whatever reason
                rotation = yRotation.mul(xRotation);
            }else{
                rotation = yRotation;
            }

            poseStack.pushPose();
            poseStack.translate(pos.x, pos.y, pos.z);
            poseStack.rotateAround(rotation, 0.5f, 0.5f, 0.5f);
            blockRenderer.renderSingleBlock(ModBlocks.VACUUM_TUBE.get().defaultBlockState(), poseStack,
                    multiBufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }
}
