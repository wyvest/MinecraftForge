package net.minecraftforge.client.model;

import net.minecraft.util.EnumFacing;

import javax.vecmath.Matrix4f;

/*
 * Replacement interface for ModelRotation to allow custom transformations of vanilla models.
 * You should probably use TRSRTransformation directly.
 */
public interface ITransformation {
    Matrix4f getMatrix();

    EnumFacing rotate(EnumFacing facing);

    int rotate(EnumFacing facing, int vertexIndex);
}
