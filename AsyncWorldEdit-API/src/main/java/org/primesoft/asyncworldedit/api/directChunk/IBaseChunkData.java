/*
 * AsyncWorldEdit API
 * Copyright (c) 2015, SBPrime <https://github.com/SBPrime/>
 * Copyright (c) AsyncWorldEdit API contributors
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted free of charge provided that the following 
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution,
 * 3. Redistributions of source code, with or without modification, in any form 
 *    other then free of charge is not allowed,
 * 4. Redistributions in binary form in any form other then free of charge is 
 *    not allowed.
 * 5. Any derived work based on or containing parts of this software must reproduce 
 *    the above copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided with the 
 *    derived work.
 * 6. The original author of the software is allowed to change the license 
 *    terms or the entire license of the software as he sees fit.
 * 7. The original author of the software is allowed to sublicense the software 
 *    or its parts using any license terms he sees fit.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.primesoft.asyncworldedit.api.directChunk;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.world.block.BlockStateHolder;

/**
 *
 * @author SBPrime
 */
public interface IBaseChunkData extends ISimpleChunkData {
    /**
     * Set chunk block
     *
     * @param x X coordinate inside chunk (0-15)
     * @param y Y coordinate inside chunk (0-15)
     * @param z Z coordinate inside chunk (0-15)
     * @param b WorldEdit block
     */
    void setBlock(int x, int y, int z, BlockStateHolder b);


    /**
     * Set chunk tile eneity
     *
     * @param x X coordinate inside chunk (0-15)
     * @param y Y coordinate inside chunk (0-15)
     * @param z Z coordinate inside chunk (0-15)
     * @param id Material ID of the tile entitiy
     * @param ct Tile entity NBT data
     */
    void setTileEntity(int x, int y, int z, int id, CompoundTag ct);
    
    
        /**
     * Set chunk block
     *
     * @param x X coordinate inside chunk (0-15)
     * @param y Y coordinate inside chunk (0-15)
     * @param z Z coordinate inside chunk (0-15)
     * @param b WorldEdit block
     * @param emission The block emission level
     */
    void setBlockAndEmission(int x, int y, int z, BlockStateHolder b, byte emission);


    /**
     * Set chunk tile eneity
     *
     * @param x X coordinate inside chunk (0-15)
     * @param y Y coordinate inside chunk (0-15)
     * @param z Z coordinate inside chunk (0-15)
     * @param id Material ID of the tile entitiy
     * @param ct Tile entity NBT data
     * @param emission The block emission level
     */
    void setTileEntityAndEmission(int x, int y, int z, int id, CompoundTag ct, byte emission);
    

    /**
     * Remove entity from chunk
     *
     * @param entity
     * @return
     */
    boolean removeEntity(ISerializedEntity entity);

    /**
     * Add entity to chunk
     *
     * @param entity
     */
    void addEntity(ISerializedEntity entity);

    /**
     * Add entity to chunk
     *
     * @param pos
     * @param entity
     * @return serialized entity
     */
    ISerializedEntity addEntity(Vector3 pos, Entity entity);

    /**
     * Get block from chunk data
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    BlockStateHolder getBlock(int x, int y, int z);

    /**
     * Get the chunk entities
     *
     * @return
     */
    ISerializedEntity[] getEntity();   
}
