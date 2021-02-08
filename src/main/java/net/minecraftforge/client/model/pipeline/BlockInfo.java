package net.minecraftforge.client.model.pipeline;

import net.minecraft.block.Block;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockInfo
{
    private static final EnumFacing[] SIDES = EnumFacing.values();

    private IBlockAccess world;
    private Block block;
    private BlockPos blockPos;

    private final boolean[][][] translucent = new boolean[3][3][3];
    private final int[][][] s = new int[3][3][3];
    private final int[][][] b = new int[3][3][3];
    private final float[][][][] skyLight = new float[3][2][2][2];
    private final float[][][][] blockLight = new float[3][2][2][2];
    private final float[][][] ao = new float[3][3][3];

    private final int[] packed = new int[7];

    private boolean full;

    private float shx = 0, shy = 0, shz = 0;

    private int cachedTint = -1;
    private int cachedMultiplier = -1;

    public int getColorMultiplier(int tint)
    {
        if(cachedTint == tint) return cachedMultiplier;
        cachedTint = tint;
        cachedMultiplier = block.colorMultiplier(world, blockPos, tint);
        return cachedMultiplier;
    }

    public void updateShift()
    {
        updateShift(false);
    }

    public void updateShift(boolean ignoreY)
    {
        long rand = 0;
        if(block.getOffsetType() != EnumOffsetType.NONE)
        {
            rand = MathHelper.getCoordinateRandom(blockPos.getX(), ignoreY ? 0 : blockPos.getY(), blockPos.getZ());
            shx = ((float)((rand >> 16) & 0xF) / 0xF - .5f) * .5f;
            shz = ((float)((rand >> 24) & 0xF) / 0xF - .5f) * .5f;
            if(block.getOffsetType() == EnumOffsetType.XYZ)
            {
                shy = ((float)((rand >> 20) & 0xF) / 0xF - 1) * .2f;
            }
        }
    }

    public void setWorld(IBlockAccess world)
    {
        this.world = world;
        cachedTint = -1;
        cachedMultiplier = -1;
    }

    public void setBlock(Block block)
    {
        this.block = block;
        cachedTint = -1;
        cachedMultiplier = -1;
    }

    public void setBlockPos(BlockPos blockPos)
    {
        this.blockPos = blockPos;
        cachedTint = -1;
        cachedMultiplier = -1;
        shx = shy = shz = 0;
    }


    private float combine(int c, int s1, int s2, int s3, boolean t0, boolean t1, boolean t2, boolean t3)
    {
        if (c  == 0 && !t0) c  = Math.max(0, Math.max(s1, s2) - 1);
        if (s1 == 0 && !t1) s1 = Math.max(0, c - 1);
        if (s2 == 0 && !t2) s2 = Math.max(0, c - 1);
        if (s3 == 0 && !t3) s3 = Math.max(0, Math.max(s1, s2) - 1);
        return (float) (c + s1 + s2 + s3) * 0x20 / (4 * 0xFFFF);
    }

    public void updateLightMatrix()
    {
        for(int x = 0; x <= 2; x++)
        {
            for(int y = 0; y <= 2; y++)
            {
                for(int z = 0; z <= 2; z++)
                {
                    BlockPos pos = blockPos.add(x - 1, y - 1, z - 1);
                    Block block = world.getBlockState(pos).getBlock();
                    translucent[x][y][z] = block.getLightOpacity(world, pos) < 15;
                    int brightness = block.getMixedBrightnessForBlock(world, pos);
                    s[x][y][z] = (brightness >> 0x14) & 0xF;
                    b[x][y][z] = (brightness >> 0x04) & 0xF;
                    ao[x][y][z] = block.getAmbientOcclusionLightValue();
                }
            }
        }
            for(EnumFacing side : SIDES)
            {
                if (!block.doesSideBlockRendering(world, blockPos, side)) {
                    int x = side.getFrontOffsetX() + 1;
                    int y = side.getFrontOffsetY() + 1;
                    int z = side.getFrontOffsetZ() + 1;
                    s[x][y][z] = Math.max(s[1][1][1] - 1, s[x][y][z]);
                    b[x][y][z] = Math.max(b[1][1][1] - 1, b[x][y][z]);
                }
            }
        for(int x = 0; x < 2; x++)
        {
            for(int y = 0; y < 2; y++)
            {
                for(int z = 0; z < 2; z++)
                {
                    int x1 = x * 2;
                    int y1 = y * 2;
                    int z1 = z * 2;

                    int     sxyz = s[x1][y1][z1];
                    int     bxyz = b[x1][y1][z1];
                    boolean txyz = translucent[x1][y1][z1];

                    int     sxz = s[x1][1][z1], sxy = s[x1][y1][1], syz = s[1][y1][z1];
                    int     bxz = b[x1][1][z1], bxy = b[x1][y1][1], byz = b[1][y1][z1];
                    boolean txz = translucent[x1][1][z1], txy = translucent[x1][y1][1], tyz = translucent[1][y1][z1];

                    int     sx = s[x1][1][1], sy = s[1][y1][1], sz = s[1][1][z1];
                    int     bx = b[x1][1][1], by = b[1][y1][1], bz = b[1][1][z1];
                    boolean tx = translucent[x1][1][1], ty = translucent[1][y1][1], tz = translucent[1][1][z1];

                    skyLight  [0][x][y][z] = combine(sx, sxz, sxy, txz || txy ? sxyz : sx,
                        tx, txz, txy, txz || txy ? txyz : tx);
                    blockLight[0][x][y][z] = combine(bx, bxz, bxy, txz || txy ? bxyz : bx,
                        tx, txz, txy, txz || txy ? txyz : tx);

                    skyLight  [1][x][y][z] = combine(sy, sxy, syz, txy || tyz ? sxyz : sy,
                        ty, txy, tyz, txy || tyz ? txyz : ty);
                    blockLight[1][x][y][z] = combine(by, bxy, byz, txy || tyz ? bxyz : by,
                        ty, txy, tyz, txy || tyz ? txyz : ty);

                    skyLight  [2][x][y][z] = combine(sz, syz, sxz, tyz || txz ? sxyz : sz,
                        tz, tyz, txz, tyz || txz ? txyz : tz);
                    blockLight[2][x][y][z] = combine(bz, byz, bxz, tyz || txz ? bxyz : bz,
                        tz, tyz, txz, tyz || txz ? txyz : tz);
                }
            }
        }
    }

    public void updateFlatLighting()
    {
        full = block.isFullCube();
        packed[0] = block.getMixedBrightnessForBlock(world, blockPos);

        for (EnumFacing side : SIDES)
        {
            int i = side.ordinal() + 1;
            packed[i] = block.getMixedBrightnessForBlock(world, blockPos.offset(side));
        }
    }

    public IBlockAccess getWorld()
    {
        return world;
    }

    public Block getBlock()
    {
        return block;
    }

    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    public boolean[][][] getTranslucent()
    {
        return translucent;
    }

    public float[][][][] getSkyLight()
    {
        return skyLight;
    }

    public float[][][][] getBlockLight()
    {
        return blockLight;
    }

    public float[][][] getAo()
    {
        return ao;
    }

    public int[] getPackedLight() {
        return packed;
    }

    public boolean isFullCube() {
        return full;
    }

    public float getShx()
    {
        return shx;
    }

    public float getShy()
    {
        return shy;
    }

    public float getShz()
    {
        return shz;
    }

    public int getCachedTint()
    {
        return cachedTint;
    }

    public int getCachedMultiplier()
    {
        return cachedMultiplier;
    }
}
