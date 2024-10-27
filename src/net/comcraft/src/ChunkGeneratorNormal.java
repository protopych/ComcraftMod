/*
 * Copyright (C) 2013 Piotr Wójcik
 *
 */
package net.comcraft.src;

import java.util.Random;

/**
 *
 * @author Piotr Wójcik
 */
public class ChunkGeneratorNormal extends ChunkGenerator {

    private SimplexNoise heightNoise;
    private SimplexNoise flowersNoise;
    private SimplexNoise treesNoise;
    private SimplexNoise biomNoise;
    private Random random;
    private boolean generateTrees;

    public ChunkGeneratorNormal(long seed, boolean generateTrees) {
        super(seed);

        random = new Random(seed);

        heightNoise = new SimplexNoise(new Random(seed));
        flowersNoise = new SimplexNoise(new Random(seed - 231));
        treesNoise = new SimplexNoise(new Random(seed - 781));
        biomNoise = new SimplexNoise(new Random(seed - 6281));
        
        this.generateTrees = generateTrees;
    }

    public ChunkStorage[] generateChunk(int chunkX, int chunkZ) {
        ChunkStorage[] blockStorages = new ChunkStorage[8];

        for (int i = 0; i < 8; ++i) {
            blockStorages[i] = new ChunkStorage();
        }

        int[][] heightLevel = new int[4][4];
        int[][] biomTab = new int[4][4];

        for (int z = 0; z < 4; ++z) {
            for (int x = 0; x < 4; ++x) {
                float m = (float) biomNoise.noise2D((chunkX * 4 + x) / 85f, (chunkZ * 4 + z) / 85f);

                int biom = m > 0.35 ? 1 : 0;

                biomTab[x][z] = biom;
                
                float n = (float) heightNoise.noise2D((chunkX * 4 + x) / 24f, (chunkZ * 4 + z) / 24f);

                int level = 9 + (int) Math.floor(n * (biom == 0 ? 5 : 2));

                heightLevel[x][z] = level;
            }
        }

        for (int y = 0; y < 32; ++y) {
            ChunkStorage blockStorageTemp = blockStorages[y >> 2];

            for (int z = 0; z < 4; ++z) {
                for (int x = 0; x < 4; ++x) {
                    if (blockStorageTemp.getBlockID(x, y & 3, z) != 0) {
                        continue;
                    }
                    
                    int id = 0;
                    int metadata = 0;
                    
                    int level = heightLevel[x][z];

                    int biom = biomTab[x][z];
                    
                    if (y == 0) {
                        id = Block.bedrock.blockID;
                    } else if (y >= 1 && y <= 2) {
                        id = Block.stone.blockID;
                    } else if (y >= 3 && y <= 10) {
                        int i = random.nextInt(y);

                        if (i == 0) {
                            id = Block.stone.blockID;
                        } else {
                            id = biom == 0 ? Block.dirt.blockID : Block.sandStone.blockID;
                        }
                    }

                    if (y < level - 1 && y >= 11) {
                        id = biom == 0 ? Block.dirt.blockID : Block.sand.blockID;
                    }
                    if (y == level - 1) {
                        id = biom == 0 ? Block.dirt.blockID : Block.sand.blockID;
                    }
                    if (y == level) {
                        id = biom == 0 ? Block.grass.blockID : Block.sand.blockID;
                    }
                    if (y == level + 1) {
                        id = 0;

                        if (biom == 0 && (chunkX * 4 + x) % (random.nextInt(2) + 1) == 0 && (chunkZ * 4 + z) % (random.nextInt(2) + 1) == 0) {
                            float n = (float) flowersNoise.noise2D((chunkX * 4 + x) / 24f, (chunkZ * 4 + z) / 24f);

                            if (n > 0.6f && n < 0.69f) {
                                id = Block.redFlower.blockID;
                            }

                            if (n < -0.6f && n > -0.69f) {
                                id = Block.yellowFlower.blockID;
                            }
                        }
                        
                        if (biom == 0 && generateTrees && (chunkX * 4 + x) % (random.nextInt(2) + 1) == 0 && (chunkZ * 4 + z) % (random.nextInt(2) + 1) == 0) {
                            float n = (float) treesNoise.noise2D((chunkX * 4 + x) / 75f, (chunkZ * 4 + z) / 75f);

                            if (n > 0.75f) {
                                id = Block.treePlant.blockID;
                            }
                        }
                    }

                    if (y > level + 1) {
                        id = 0;
                    }

                    blockStorageTemp.setBlockID(x, y & 3, z, id);
                    blockStorageTemp.setBlockMetadata(x, y & 3, z, metadata);
                }
            }
        }

        return blockStorages;
    }

    private void genereateTree(ChunkStorage[] blocksStorages, Random random, int x, int y, int z, Block wood) {
        for (int i = 0; i < 6; ++i) {
            blocksStorages[(i + y) >> 2].setBlockID(x, (y + i) & 3, z, wood.blockID);
        }

        for (int i = 2; i < 6; ++i) {
            for (int zT = -1; zT <= 1; ++zT) {
                for (int xT = -1; xT <= 1; ++xT) {
                    if (random.nextInt(5) == 0) {
                        continue;
                    }

                    blocksStorages[(i + y) >> 2].setBlockID(x + xT, (y + i) & 3, z + zT, Block.leaves.blockID);
                }
            }
        }

        blocksStorages[(6 + y) >> 2].setBlockID(x, (y + 6) & 3, z, Block.leaves.blockID);
    }
}