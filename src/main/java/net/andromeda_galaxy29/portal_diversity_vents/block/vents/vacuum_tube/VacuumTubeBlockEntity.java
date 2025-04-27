package net.andromeda_galaxy29.portal_diversity_vents.block.vents.vacuum_tube;

import net.andromeda_galaxy29.portal_diversity_vents.block.ModBlockEntities;
import net.andromeda_galaxy29.portal_diversity_vents.block.WrenchableBlockEntity;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlock;
import net.andromeda_galaxy29.portal_diversity_vents.block.vents.PipeBlockEntity;
import net.andromeda_galaxy29.portal_diversity_vents.item.wrench.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VacuumTubeBlockEntity extends PipeBlockEntity implements WrenchableBlockEntity {

    // Curve connections from both sides
    // Pos is position of the other pipe
    // Dir is the side of the other pipe this one is connected to
    public BlockPos connectPosPositive = null;
    public Direction.AxisDirection connectDirPositive = null;
    public double connectLenPositive = 0;
    public BlockPos connectPosNegative = null;
    public Direction.AxisDirection connectDirNegative = null;
    public double connectLenNegative = 0;

    public VacuumTubeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VACUUM_TUBE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public @NotNull InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!(state.getBlock() instanceof VacuumTubeBlock)){
            return InteractionResult.PASS;
        }

        if (state.getValue(PipeBlock.AXIS) == context.getClickedFace().getAxis()){
            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            if(stack.hasTag()){
                BlockPos connectPos = NbtUtils.readBlockPos(
                        (CompoundTag) stack.getTag().get("ConnectPos"));
                Direction.AxisDirection connectDir = Direction.byName(
                        stack.getTag().get("ConnectDir").getAsString()).getAxisDirection();

                connect(getBlockPos(), context.getClickedFace().getAxisDirection(), connectPos, connectDir, true);

                stack.setTag(new CompoundTag());
            }else{
                ((WrenchItem) stack.getItem()).connect(stack, context.getClickedPos(), context.getClickedFace());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void connect(BlockPos pos, Direction.AxisDirection dir, BlockPos connectPos, Direction.AxisDirection connectDir, boolean connectOther){
        //Connects to the other pipe
        if (dir == Direction.AxisDirection.POSITIVE){
            connectPosPositive = connectPos;
            connectDirPositive = connectDir;
        }else{
            connectPosNegative = connectPos;
            connectDirNegative = connectDir;
        }
        updateLength(dir);

        if(!level.isClientSide()) {
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
        }

        //Connects the other pipe to this one
        VacuumTubeBlockEntity other = (VacuumTubeBlockEntity) level.getBlockEntity(connectPos);
        if (connectOther && other != null){
            other.connect(connectPos, connectDir, pos, dir, false);
        }
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);
        if (!(state.getBlock() instanceof VacuumTubeBlock)){
            return;
        }

        if(isConnected(Direction.AxisDirection.POSITIVE) && shouldRenderCurve(Direction.AxisDirection.POSITIVE)){
            push(level, pos, Direction.AxisDirection.POSITIVE, connectLenPositive);
        }
        if(isConnected(Direction.AxisDirection.NEGATIVE) && shouldRenderCurve(Direction.AxisDirection.NEGATIVE)){
            push(level, pos, Direction.AxisDirection.NEGATIVE, connectLenNegative);
        }
    }

    public void push(Level level, BlockPos pos, Direction.AxisDirection dir, double len){
        double collisionInterval = 1.0 / Math.round(len);
        double blockInterval = 1.0 / len;

        for(double i = collisionInterval/2; i <= 1; i += collisionInterval) {
            Direction.Axis axis = getBlockState().getValue(PipeBlock.AXIS);
            Vec3 currentPosition = pos.getCenter().relative(Direction.fromAxisAndDirection(axis, dir), 0.5).add(getOffsetInCurve(dir, i));
            List<Entity> entities = level.getEntities(null,
                    new AABB(currentPosition.add(0.5, 0.5, 0.5), currentPosition.subtract(0.5, 0.5, 0.5)));

            for (Entity entity : entities){
                if (!canTransport(entity)){
                    continue;
                }
                if (entity instanceof Player){
                    entity.hurtMarked = true; //Needs to be set or else players are unaffected
                }

                Vec3 push = pos.getCenter().add(getOffsetInCurve(dir, i + blockInterval * 2))
                        .subtract(entity.getBoundingBox().getCenter()).normalize().scale(0.7);
                entity.hasImpulse = true;
                entity.setDeltaMovement(push);
                entity.resetFallDistance();
            }
        }
    }

    public void updateLength(Direction.AxisDirection dir){
        double toSet;
        if(isConnected(dir)){
            toSet = getCurveLength(dir, 20);
        }else{
            toSet = 0;
        }

        if (dir == Direction.AxisDirection.POSITIVE){
            connectLenPositive = toSet;
        }else{
            connectLenNegative = toSet;
        }
    }

    public boolean isConnected(Direction.AxisDirection dir){
        if (dir == Direction.AxisDirection.POSITIVE){
            return connectPosPositive != null && connectDirPositive != null;
        }else{
            return connectPosNegative != null && connectDirNegative != null;
        }
    }

    public boolean shouldRenderCurve(Direction.AxisDirection dir){
        BlockPos connectPos = dir == Direction.AxisDirection.POSITIVE ? connectPosPositive : connectPosNegative;
        BlockPos pos = getBlockPos();

        if(pos.getX() < connectPos.getX()){
            return true;
        }else if(pos.getX() == connectPos.getX()) {
            if(pos.getY() < connectPos.getY()){
                return true;
            }else if(pos.getY() == connectPos.getY() && pos.getZ() < connectPos.getZ()) {
                return true;
            }
        }
        return false;
    }

    public Vec3 getOffsetInCurve(Direction.AxisDirection dir, double progress){
        if(!isConnected(dir)){
            return Vec3.ZERO;
        }

        BlockPos connectPos = dir == Direction.AxisDirection.POSITIVE ? connectPosPositive : connectPosNegative;
        Direction.AxisDirection connectDir = dir == Direction.AxisDirection.POSITIVE ? connectDirPositive : connectDirNegative;

        BlockState connectState = getLevel().getBlockState(connectPos);
        if (!(connectState.getBlock() instanceof VacuumTubeBlock)){
            return Vec3.ZERO;
        }

        Direction.Axis axis = getBlockState().getValue(PipeBlock.AXIS);
        Direction.Axis connectAxis = connectState.getValue(PipeBlock.AXIS);

        Vec3 offset = Vec3.ZERO.relative(Direction.fromAxisAndDirection(axis, dir), 0.5);
        Vec3 begin = getBlockPos().getCenter().relative(Direction.fromAxisAndDirection(axis, dir), 0.5);
        Vec3 end = connectPos.getCenter().relative(Direction.fromAxisAndDirection(connectAxis, connectDir), 0.5);

        if(progress < 0){
            return Vec3.ZERO.relative(Direction.fromAxisAndDirection(axis, dir), -1);
        }
        if(progress > 1){
            return end.subtract(begin).relative(Direction.fromAxisAndDirection(connectAxis, connectDir), -1);
        }

        double x = (end.x - begin.x);
        double y = (end.y - begin.y);
        double z = (end.z - begin.z);

        double smoothStep = 3 * Math.pow(progress, 2) - 2 * Math.pow(progress, 3);
        //Turns
        if(axis != connectAxis){
            double axisMultiplier = Math.sin(progress * Math.PI / 2);
            switch (axis){
                case X -> x *= axisMultiplier;
                case Y -> y *= axisMultiplier;
                case Z -> z *= axisMultiplier;
            }
            double connectAxisMultiplier = Math.cos(progress * Math.PI / 2 - Math.PI) + 1;
            switch (connectAxis){
                case X -> x *= connectAxisMultiplier;
                case Y -> y *= connectAxisMultiplier;
                case Z -> z *= connectAxisMultiplier;
            }
            switch (getOtherAxis(axis, connectAxis)){
                case X -> x *= smoothStep;
                case Y -> y *= smoothStep;
                case Z -> z *= smoothStep;
            }
        //S-Bends
        }else{
            switch (axis){
                case X -> {
                    x *= progress;
                    y *= smoothStep;
                    z *= smoothStep;
                }
                case Y -> {
                    x *= smoothStep;
                    y *= progress;
                    z *= smoothStep;
                }
                case Z -> {
                    x *= smoothStep;
                    y *= smoothStep;
                    z *= progress;
                }
            }
        }
        return offset.add(new Vec3(x, y, z));
    }

    public double getCurveLength(Direction.AxisDirection dir, int accuracy){
        double length = 0;
        Vec3 prevPos;
        Vec3 pos = getBlockPos().getCenter().add(getOffsetInCurve(dir, 0));
        for(double i = 1.0/accuracy; i <= 1; i += 1.0/accuracy) {
            prevPos = pos;
            pos = getBlockPos().getCenter().add(getOffsetInCurve(dir, i));

            length += pos.subtract(prevPos).length();
        }
        return length;
    }

    public Direction.Axis getOtherAxis(Direction.Axis axis1, Direction.Axis axis2){
        if ((axis1 == Direction.Axis.Z && axis2 == Direction.Axis.Y) ||
                (axis1 == Direction.Axis.Y && axis2 == Direction.Axis.Z)){
            return Direction.Axis.X;
        }else if ((axis1 == Direction.Axis.X && axis2 == Direction.Axis.Z) ||
                (axis1 == Direction.Axis.Z && axis2 == Direction.Axis.X)){
            return Direction.Axis.Y;
        }else if ((axis1 == Direction.Axis.X && axis2 == Direction.Axis.Y) ||
                (axis1 == Direction.Axis.Y && axis2 == Direction.Axis.X)){
            return Direction.Axis.Z;
        }
        return Direction.Axis.Z;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        if(connectPosPositive != null){
            tag.put("ConnectPosPositive", NbtUtils.writeBlockPos(connectPosPositive));
        }
        if(connectDirPositive != null){
            tag.putInt("ConnectDirPositive", connectDirPositive.getStep());
        }
        tag.putDouble("ConnectLenPositive", connectLenPositive);
        if(connectPosNegative != null){
            tag.put("ConnectPosNegative", NbtUtils.writeBlockPos(connectPosNegative));
        }
        if(connectDirNegative != null) {
            tag.putInt("ConnectDirNegative", connectDirNegative.getStep());
        }
        tag.putDouble("ConnectLenNegative", connectLenNegative);

        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        if(tag.contains("ConnectPosPositive")){
            connectPosPositive = NbtUtils.readBlockPos(tag.getCompound("ConnectPosPositive"));
        }else{
            connectPosPositive = null;
        }
        if(tag.contains("ConnectDirPositive")){
            connectDirPositive = tag.getInt("ConnectDirPositive") == 1 ?
                    Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        }else{
            connectDirPositive = null;
        }
        connectLenPositive = tag.getDouble("ConnectLenPositive");

        if(tag.contains("ConnectPosNegative")){
            connectPosNegative = NbtUtils.readBlockPos(tag.getCompound("ConnectPosNegative"));
        }else{
            connectPosNegative = null;
        }
        if(tag.contains("ConnectDirNegative")){
            connectDirNegative = tag.getInt("ConnectDirNegative") == 1 ?
                    Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        }else{
            connectDirNegative = null;
        }
        connectLenNegative = tag.getDouble("ConnectLenNegative");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
