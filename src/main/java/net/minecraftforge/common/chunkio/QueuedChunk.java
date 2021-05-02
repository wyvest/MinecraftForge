package net.minecraftforge.common.chunkio;


class QueuedChunk {
    final int x;
    final int z;
    final net.minecraft.world.chunk.storage.AnvilChunkLoader loader;
    final net.minecraft.world.World world;
    final net.minecraft.world.gen.ChunkProviderServer provider;
    net.minecraft.nbt.NBTTagCompound compound;
    private static final String NEW_LINE = System.getProperty("line.separator");

    public QueuedChunk(int x, int z, net.minecraft.world.chunk.storage.AnvilChunkLoader loader, net.minecraft.world.World world, net.minecraft.world.gen.ChunkProviderServer provider) {
        this.x = x;
        this.z = z;
        this.loader = loader;
        this.world = world;
        this.provider = provider;
    }

    @Override
    public int hashCode() {
        return (x * 31 + z * 29) ^ world.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof QueuedChunk) {
            QueuedChunk other = (QueuedChunk) object;
            return x == other.x && z == other.z && world == other.world;
        }

        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getName() +
            " {" + NEW_LINE +
            " x: " + x + NEW_LINE +
            " z: " + z + NEW_LINE +
            " loader: " + loader + NEW_LINE +
            " world: " + world.getWorldInfo().getWorldName() + NEW_LINE +
            " dimension: " + world.provider.getDimensionId() + NEW_LINE +
            " provider: " + world.provider.getClass().getName() + NEW_LINE +
            "}";
    }
}
