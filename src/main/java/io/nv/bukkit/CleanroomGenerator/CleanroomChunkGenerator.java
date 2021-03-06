package io.nv.bukkit.CleanroomGenerator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CleanroomChunkGenerator extends ChunkGenerator {
    private final Logger logger;

    private BlockData[] layerBlock;
    private int[] layerHeight;

    public CleanroomChunkGenerator(CleanroomGenerator plugin) {
        this(plugin, "");
    }

    CleanroomChunkGenerator(CleanroomGenerator plugin, String id) {
        this.logger = plugin.getLogger();

        if (id == null || id.equals("")) {
            id = "."; // default to air
        }

        if (id.equals(".")) {
            // Void world early exit to simplify later code
            layerBlock = new BlockData[0];
            layerHeight = new int[0];
            return;
        }

        try {
            if (id.charAt(0) != '.') {
                // Unless the id starts with a '.' make the first layer bedrock
                id = "1|minecraft:bedrock|" + id;
            } else {
                // Else remove the . and don't add the bedrock
                id = id.substring(1);
            }

            String[] tokens = id.split("[|]");
            if ((tokens.length % 2) != 0) {
                throw new Exception("Uneven tokens");
            }

            int layerCount = tokens.length / 2;
            layerBlock = new BlockData[layerCount];
            layerHeight = new int[layerCount];

            for (int i = 0; i < layerCount; i++) {
                int j = i * 2;
                int height = Integer.parseInt(tokens[j]);
                if (height <= 0) {
                    logger.log(Level.WARNING, "Invalid height ''{0}''. Using 64 instead.", tokens[j]);
                    height = 64;
                }

                BlockData blockData;
                try {
                    blockData = Bukkit.createBlockData(tokens[j + 1]);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to lookup block ''{0}''. Using stone instead", new Object[]{ tokens[j + 1], e });
                    blockData = Material.STONE.createBlockData();
                }

                layerBlock[i] = blockData;
                layerHeight[i] = height;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to parse ID ''{0}''. using defaults '64,1'", new Object[]{ id, e });

            layerBlock = new BlockData[2];
            layerBlock[0] = Material.BEDROCK.createBlockData();
            layerBlock[1] = Material.STONE.createBlockData();

            layerHeight = new int[2];
            layerHeight[0] = 1;
            layerHeight[1] = 64;
        }
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);

        int y = 0;
        for(int i = 0; i < layerBlock.length; i++) {
            chunk.setRegion(0, y, 0, 16, y + layerHeight[i], 16, layerBlock[i]);
            y += layerHeight[i];
        }

        // Set bedrock under the spawn position
        if (layerHeight.length == 0 && chunkX == 0 && chunkZ == 0) {
            chunk.setBlock(0, 64, 0, Material.BEDROCK);
        }

        return chunk;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        if (!world.isChunkLoaded(0, 0)) {
            world.loadChunk(0, 0);
        }

        int highestBlock = world.getHighestBlockYAt(0, 0);
        if ((highestBlock <= 0) && (world.getBlockAt(0, 0, 0).getType() == Material.AIR)) {
            return new Location(world, 0, 64, 0);
        }

        return new Location(world, 0, highestBlock + 1, 0);
    }

    @Override
    public boolean isParallelCapable() {
        return true;
    }
}
