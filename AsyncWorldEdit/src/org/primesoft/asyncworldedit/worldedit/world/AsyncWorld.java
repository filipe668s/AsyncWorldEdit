/*
 * The MIT License
 *
 * Copyright 2014 SBPrime.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primesoft.asyncworldedit.worldedit.world;

import com.sk89q.worldedit.BiomeType;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EntityType;
import com.sk89q.worldedit.LocalEntity;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.World;
import java.util.UUID;
import org.bukkit.scheduler.BukkitScheduler;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.BlocksHubIntegration;
import org.primesoft.asyncworldedit.ConfigProvider;
import org.primesoft.asyncworldedit.PlayerWrapper;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.blockPlacer.entries.RegenerateEntry;
import org.primesoft.asyncworldedit.taskdispatcher.TaskDispatcher;
import org.primesoft.asyncworldedit.blockPlacer.entries.WorldExtentActionEntry;
import org.primesoft.asyncworldedit.blockPlacer.entries.WorldExtentFuncEntry;
import org.primesoft.asyncworldedit.blockPlacer.entries.WorldExtentFuncEntryEx;
import org.primesoft.asyncworldedit.utils.Action;
import org.primesoft.asyncworldedit.utils.Func;
import org.primesoft.asyncworldedit.utils.FuncEx;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.CancelabeEditSession;
import org.primesoft.asyncworldedit.worldedit.WorldAsyncTask;
import org.primesoft.asyncworldedit.worldedit.WorldeditOperations;

/**
 *
 * @author SBPrime
 */
public class AsyncWorld implements World {

    /**
     * Wrap the world (if needed)
     *
     * @param world
     * @param player
     * @return
     */
    public static AsyncWorld wrap(World world, UUID player) {
        if (world == null) {
            return null;
        }

        if (world instanceof AsyncWorld) {
            return (AsyncWorld) world;
        }

        return new AsyncWorld(world, player);
    }

    /**
     * The plugin
     */
    private final AsyncWorldEditMain m_plugin;

    /**
     * Bukkit schedule
     */
    private final BukkitScheduler m_schedule;

    /**
     * Player wraper
     */
    private final PlayerWrapper m_wrapper;

    /**
     * The player
     */
    private final UUID m_player;

    /**
     * The parrent world
     */
    private final World m_parent;

    /**
     * The bukkit world
     */
    private final org.bukkit.World m_bukkitWorld;

    /**
     * The block placer
     */
    private final BlockPlacer m_blockPlacer;

    /**
     * The dispather
     */
    private final TaskDispatcher m_dispatcher;

    /**
     * The blocks hub
     */
    private final BlocksHubIntegration m_blocksHub;

    public AsyncWorld(World world, UUID player) {
        m_plugin = AsyncWorldEditMain.getInstance();
        m_player = player;
        m_wrapper = m_plugin.getPlayerManager().getPlayer(player);
        m_schedule = m_plugin.getServer().getScheduler();
        m_blockPlacer = m_plugin.getBlockPlacer();
        m_dispatcher = m_plugin.getTaskDispatcher();
        m_blocksHub = m_plugin.getBlocksHub();

        m_parent = world;
        if (world instanceof BukkitWorld) {
            m_bukkitWorld = ((BukkitWorld) world).getWorld();
        } else {
            m_bukkitWorld = AsyncWorldEditMain.getInstance().getServer().getWorld(world.getName());
        }
    }

    @Override
    public String getName() {
        return m_dispatcher.performSafe(new Func<String>() {
            @Override
            public String Execute() {
                return m_parent.getName();
            }
        });
    }

    @Override
    public int getMaxY() {
        return m_dispatcher.performSafe(new Func<Integer>() {
            @Override
            public Integer Execute() {
                return m_parent.getMaxY();
            }
        });
    }

    @Override
    public boolean isValidBlockType(final int i) {
        return m_dispatcher.performSafe(new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                return m_parent.isValidBlockType(i);
            }
        });
    }

    @Override
    public boolean usesBlockData(final int i) {
        return m_dispatcher.performSafe(new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                return m_parent.usesBlockData(i);
            }
        });
    }

    @Override
    public Mask createLiquidMask() {
        return m_dispatcher.performSafe(new Func<Mask>() {
            @Override
            public Mask Execute() {
                return m_parent.createLiquidMask();
            }
        });
    }

    @Override
    public int getBlockType(final Vector vector) {
        return m_dispatcher.performSafe(new Func<Integer>() {
            @Override
            public Integer Execute() {
                return m_parent.getBlockType(vector);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public int getBlockData(final Vector vector) {
        return m_dispatcher.performSafe(new Func<Integer>() {

            @Override
            public Integer Execute() {
                return m_parent.getBlockData(vector);
            }
        }, m_bukkitWorld, vector);
    }

    /**
     * Decide on the player UUID
     *
     * @param asyncParams
     * @return
     */
    private UUID getPlayer(BaseAsyncParams... asyncParams) {
        UUID result = m_player;

        for (BaseAsyncParams param : asyncParams) {
            if (!param.isEmpty()) {
                UUID player = param.getPlayer();
                if (player != ConfigProvider.DEFAULT_USER) {
                    result = player;
                }
            }
        }
        return result;
    }

    @Override
    public boolean setBlock(Vector vector, BaseBlock bb, final boolean bln) throws WorldEditException {
        final DataAsyncParams<BaseBlock> paramBlock = DataAsyncParams.extract(bb);
        final DataAsyncParams<Vector> paramVector = DataAsyncParams.extract(vector);

        final BaseBlock newBlock = paramBlock.getData();
        final Vector v = paramVector.getData();
        final UUID player = getPlayer(paramBlock, paramVector);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        FuncEx<Boolean, WorldEditException> func = new FuncEx<Boolean, WorldEditException>() {

            @Override
            public Boolean Execute() throws WorldEditException {
                final BaseBlock oldBlock = m_parent.getBlock(v);

                if (oldBlock.equals(newBlock)) {
                    return false;
                }

                final boolean result = m_parent.setBlock(v, newBlock, bln);
                if (result) {
                    logBlock(v, player, oldBlock, newBlock);
                }

                return result;
            }
        };

        if (paramBlock.isAsync() || paramVector.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntryEx(this, paramBlock.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean setBlockType(Vector vector, final int i) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                final BaseBlock oldBlock = m_parent.getBlock(v);
                if (oldBlock.getType() == i) {
                    return false;
                }

                final boolean result = m_parent.setBlockType(v, i);
                if (result) {
                    logBlock(v, player, oldBlock, new BaseBlock(i, oldBlock.getData()));
                }

                return result;
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean setBlockTypeFast(Vector vector, final int i) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                final BaseBlock oldBlock = m_parent.getBlock(v);
                if (oldBlock.getType() == i) {
                    return false;
                }

                final boolean result = m_parent.setBlockTypeFast(v, i);
                if (result) {
                    logBlock(v, player, oldBlock, new BaseBlock(i, oldBlock.getData()));
                }

                return result;
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public void setBlockData(Vector vector, final int i) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                final BaseBlock oldBlock = m_parent.getBlock(v);
                if (oldBlock.getData() == i) {
                    return false;
                }
                m_parent.setBlockData(v, i);
                logBlock(v, player, oldBlock, new BaseBlock(oldBlock.getType(), i));
                return true;
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
            return;
        }

        func.Execute();
    }

    @Override
    public void setBlockDataFast(Vector vector, final int i) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                final BaseBlock oldBlock = m_parent.getBlock(v);
                if (oldBlock.getData() == i) {
                    return false;
                }

                m_parent.setBlockDataFast(v, i);
                logBlock(v, player, oldBlock, new BaseBlock(oldBlock.getType(), i));
                return true;
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
            return;
        }

        func.Execute();
    }

    @Override
    public boolean setTypeIdAndData(Vector vector, final int i, final int i1) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                final BaseBlock oldBlock = m_parent.getBlock(v);

                if (oldBlock.getType() == i && oldBlock.getData() == i1) {
                    return false;
                }

                final boolean result = m_parent.setTypeIdAndData(v, i, i1);
                if (result) {
                    logBlock(v, player, oldBlock, new BaseBlock(i, i1));
                }

                return result;
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean setTypeIdAndDataFast(Vector vector, final int i, final int i1) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                final BaseBlock oldBlock = m_parent.getBlock(v);
                if (oldBlock.getType() == i && oldBlock.getData() == i1) {
                    return false;
                }

                final boolean result = m_parent.setTypeIdAndDataFast(v, i, i1);
                if (result) {
                    logBlock(v, player, oldBlock, new BaseBlock(i, i1));
                }

                return result;
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public int getBlockLightLevel(final Vector vector) {
        return m_dispatcher.performSafe(new Func<Integer>() {
            @Override
            public Integer Execute() {
                return m_parent.getBlockLightLevel(vector);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public boolean clearContainerBlockContents(final Vector vector) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                return m_parent.clearContainerBlockContents(vector);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public BiomeType getBiome(final Vector2D vd) {
        return m_dispatcher.performSafe(new Func<BiomeType>() {
            @Override
            public BiomeType Execute() {
                return m_parent.getBiome(vd);
            }
        }, m_bukkitWorld, new Vector(vd.getX(), 0, vd.getZ()));
    }

    @Override
    public void setBiome(Vector2D vd, final BiomeType bt) {
        final DataAsyncParams<Vector2D> param = DataAsyncParams.extract(vd);
        final Vector2D v = param.getData();
        final UUID player = getPlayer(param);
        final Vector tmpV = new Vector(v.getX(), 0, v.getZ());

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, tmpV)) {
            return;
        }

        Action func = new Action() {
            @Override
            public void Execute() {
                m_parent.setBiome(v, bt);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            m_blockPlacer.addTasks(player,
                    new WorldExtentActionEntry(this, param.getJobId(), tmpV, func));
            return;
        }

        func.Execute();
    }

    @Override
    public void dropItem(Vector vector, final BaseItemStack bis, final int i) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return;
        }

        Action func = new Action() {
            @Override
            public void Execute() {
                m_parent.dropItem(v, bis, i);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            m_blockPlacer.addTasks(player,
                    new WorldExtentActionEntry(this, param.getJobId(), v, func));
            return;
        }

        func.Execute();
    }

    @Override
    public void dropItem(final Vector vector, final BaseItemStack bis) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return;
        }

        Action func = new Action() {
            @Override
            public void Execute() {
                m_parent.dropItem(v, bis);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            m_blockPlacer.addTasks(player,
                    new WorldExtentActionEntry(this, param.getJobId(), v, func));
            return;
        }

        func.Execute();
    }

    @Override
    public void simulateBlockMine(Vector vector) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return;
        }

        Action func = new Action() {
            @Override
            public void Execute() {
                m_parent.simulateBlockMine(v);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            m_blockPlacer.addTasks(player,
                    new WorldExtentActionEntry(this, param.getJobId(), v, func));
            return;
        }

        func.Execute();
    }

    @Override
    public LocalEntity[] getEntities(final Region region) {
        return m_dispatcher.performSafe(new Func<LocalEntity[]>() {
            @Override
            public LocalEntity[] Execute() {
                return m_parent.getEntities(region);
            }
        });
    }

    @Override
    public int killEntities(final LocalEntity... les) {
        return m_dispatcher.performSafe(new Func<Integer>() {
            @Override
            public Integer Execute() {
                return m_parent.killEntities(les);
            }
        });
    }

    @Override
    public int killMobs(final Vector vector, final int i) {
        return m_dispatcher.performSafe(new Func<Integer>() {
            @Override
            public Integer Execute() {
                return m_parent.killMobs(vector, i);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public int killMobs(final Vector vector, final int i, final boolean bln) {
        return m_dispatcher.performSafe(new Func<Integer>() {
            @Override
            public Integer Execute() {
                return m_parent.killMobs(vector, i, bln);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public int killMobs(final Vector vector, final double d, final int i) {
        return m_dispatcher.performSafe(new Func<Integer>() {
            @Override
            public Integer Execute() {
                return m_parent.killMobs(vector, d, i);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public int removeEntities(final EntityType et, final Vector vector, final int i) {
        return m_dispatcher.performSafe(new Func<Integer>() {
            @Override
            public Integer Execute() {
                return m_parent.removeEntities(et, vector, i);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public boolean regenerate(final Region region, final EditSession editSession) {
        boolean isAsync = checkAsync(WorldeditOperations.regenerate);
        if (!isAsync) {
            return m_parent.regenerate(region, editSession);
        }

        final int jobId = getJobId();
        final EditSession session;
        final JobEntry job;

        if (editSession instanceof AsyncEditSession) {
            AsyncEditSession aSession = (AsyncEditSession) editSession;
            session = new CancelabeEditSession(aSession, aSession.getMask(), jobId);
            job = new JobEntry(m_player, (CancelabeEditSession) session, jobId, "regenerate");
        } else {
            session = editSession;
            job = new JobEntry(m_player, jobId, "regenerate");
        }

        m_blockPlacer.addJob(m_player, job);

        final int maxY = getMaxY();
        m_schedule.runTaskAsynchronously(m_plugin, new WorldAsyncTask(m_bukkitWorld, session,
                m_player, "regenerate", m_blockPlacer, job) {
                    @Override
                    public void task(EditSession editSession, org.bukkit.World world) throws MaxChangedBlocksException {
                        doRegen(editSession, region, maxY, world, jobId);
                    }

                });

        return true;
    }

    /**
     * Perfrom the regen operation
     *
     * @param eSession
     * @param region
     * @param world
     */
    private void doRegen(EditSession eSession, Region region, int maxY, org.bukkit.World world, int jobId) {
        BaseBlock[] history = new BaseBlock[16 * 16 * (maxY + 1)];

        for (Vector2D chunk : region.getChunks()) {
            Vector min = new Vector(chunk.getBlockX() * 16, 0, chunk.getBlockZ() * 16);

            // First save all the blocks inside
            for (int x = 0; x < 16; ++x) {
                for (int y = 0; y < (maxY + 1); ++y) {
                    for (int z = 0; z < 16; ++z) {
                        Vector pt = min.add(x, y, z);
                        int index = y * 16 * 16 + z * 16 + x;
                        history[index] = eSession.getBlock(pt);
                    }
                }
            }

            m_blockPlacer.addTasks(m_player, new RegenerateEntry(jobId, world, chunk));

            // Then restore
            for (int x = 0; x < 16; ++x) {
                for (int y = 0; y < (maxY + 1); ++y) {
                    for (int z = 0; z < 16; ++z) {
                        Vector pt = min.add(x, y, z);
                        int index = y * 16 * 16 + z * 16 + x;

                        // We have to restore the block if it was outside
                        if (!region.contains(pt)) {
                            eSession.smartSetBlock(pt, history[index]);
                        } else { // Otherwise fool with history
                            eSession.rememberChange(pt, history[index],
                                    eSession.rawGetBlock(pt));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean generateTree(final TreeGenerator.TreeType tt, final EditSession es, Vector vector) throws MaxChangedBlocksException {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        FuncEx<Boolean, MaxChangedBlocksException> func = new FuncEx<Boolean, MaxChangedBlocksException>() {
            @Override
            public Boolean Execute() throws MaxChangedBlocksException {
                return m_parent.generateTree(tt, es, v);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntryEx(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean generateTree(final EditSession es, Vector vector) throws MaxChangedBlocksException {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        FuncEx<Boolean, MaxChangedBlocksException> func = new FuncEx<Boolean, MaxChangedBlocksException>() {
            @Override
            public Boolean Execute() throws MaxChangedBlocksException {
                return m_parent.generateTree(es, v);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntryEx(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean generateBigTree(final EditSession es, Vector vector) throws MaxChangedBlocksException {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        FuncEx<Boolean, MaxChangedBlocksException> func = new FuncEx<Boolean, MaxChangedBlocksException>() {
            @Override
            public Boolean Execute() throws MaxChangedBlocksException {
                return m_parent.generateBigTree(es, v);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntryEx(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean generateBirchTree(final EditSession es, Vector vector) throws MaxChangedBlocksException {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        FuncEx<Boolean, MaxChangedBlocksException> func = new FuncEx<Boolean, MaxChangedBlocksException>() {
            @Override
            public Boolean Execute() throws MaxChangedBlocksException {
                return m_parent.generateBirchTree(es, v);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntryEx(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean generateRedwoodTree(final EditSession es, Vector vector) throws MaxChangedBlocksException {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        FuncEx<Boolean, MaxChangedBlocksException> func = new FuncEx<Boolean, MaxChangedBlocksException>() {
            @Override
            public Boolean Execute() throws MaxChangedBlocksException {
                return m_parent.generateRedwoodTree(es, v);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntryEx(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean generateTallRedwoodTree(final EditSession es, Vector vector) throws MaxChangedBlocksException {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        FuncEx<Boolean, MaxChangedBlocksException> func = new FuncEx<Boolean, MaxChangedBlocksException>() {
            @Override
            public Boolean Execute() throws MaxChangedBlocksException {
                return m_parent.generateTallRedwoodTree(es, v);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntryEx(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public void checkLoadedChunk(final Vector vector) {
        m_dispatcher.performSafe(new Action() {
            @Override
            public void Execute() {
                m_parent.checkLoadedChunk(vector);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public void fixAfterFastMode(final Iterable<BlockVector2D> itrbl) {
        m_dispatcher.performSafe(new Action() {
            @Override
            public void Execute() {
                m_parent.fixAfterFastMode(itrbl);
            }
        });
    }

    @Override
    public void fixLighting(final Iterable<BlockVector2D> itrbl) {
        m_dispatcher.performSafe(new Action() {
            @Override
            public void Execute() {
                m_parent.fixLighting(itrbl);
            }
        });
    }

    @Override
    public boolean playEffect(Vector vector, final int i, final int i1) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                return m_parent.playEffect(v, i, i1);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public boolean queueBlockBreakEffect(final Platform pltform, Vector vector, final int i, final double d) {
        final DataAsyncParams<Vector> param = DataAsyncParams.extract(vector);
        final Vector v = param.getData();
        final UUID player = getPlayer(param);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        Func<Boolean> func = new Func<Boolean>() {
            @Override
            public Boolean Execute() {
                return m_parent.queueBlockBreakEffect(pltform, v, i, d);
            }
        };

        if (param.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntry(this, param.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public Vector getMinimumPoint() {
        return m_dispatcher.performSafe(new Func<Vector>() {
            @Override
            public Vector Execute() {
                return m_parent.getMinimumPoint();
            }
        });
    }

    @Override
    public Vector getMaximumPoint() {
        return m_dispatcher.performSafe(new Func<Vector>() {
            @Override
            public Vector Execute() {
                return m_parent.getMaximumPoint();
            }
        });
    }

    @Override
    public BaseBlock getBlock(final Vector vector) {
        return m_dispatcher.performSafe(new Func<BaseBlock>() {
            @Override
            public BaseBlock Execute() {
                return m_parent.getBlock(vector);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public BaseBlock getLazyBlock(final Vector vector) {
        return m_dispatcher.performSafe(new Func<BaseBlock>() {
            @Override
            public BaseBlock Execute() {
                return m_parent.getLazyBlock(vector);
            }
        }, m_bukkitWorld, vector);
    }

    @Override
    public boolean setBlock(final Vector vector, final BaseBlock bb) throws WorldEditException {
        final DataAsyncParams<BaseBlock> paramBlock = DataAsyncParams.extract(bb);
        final DataAsyncParams<Vector> paramVector = DataAsyncParams.extract(vector);

        final BaseBlock newBlock = paramBlock.getData();
        final Vector v = paramVector.getData();
        final UUID player = getPlayer(paramBlock, paramVector);

        if (!m_blocksHub.canPlace(player, m_bukkitWorld, v)) {
            return false;
        }

        FuncEx<Boolean, WorldEditException> func = new FuncEx<Boolean, WorldEditException>() {

            @Override
            public Boolean Execute() throws WorldEditException {
                final BaseBlock oldBlock = m_parent.getBlock(vector);

                if (oldBlock.equals(newBlock)) {
                    return false;
                }

                final boolean result = m_parent.setBlock(vector, newBlock);
                if (result) {
                    logBlock(vector, player, oldBlock, newBlock);
                }

                return result;
            }
        };

        if (paramBlock.isAsync() || paramVector.isAsync() || !m_dispatcher.isMainTask()) {
            return m_blockPlacer.addTasks(player,
                    new WorldExtentFuncEntryEx(this, paramBlock.getJobId(), v, func));
        }

        return func.Execute();
    }

    @Override
    public Operation commit() {
        return m_dispatcher.performSafe(new Func<Operation>() {
            @Override
            public Operation Execute() {
                return m_parent.commit();
            }
        });
    }

    /**
     * Log placed block using blocks hub
     */
    private void logBlock(Vector location, UUID player, BaseBlock oldBlock, BaseBlock newBlock) {
        m_blocksHub.logBlock(player, m_bukkitWorld, location, oldBlock, newBlock);
    }

    /**
     * This function checks if async mode is enabled for specific command
     *
     * @param operation
     */
    private boolean checkAsync(WorldeditOperations operation) {
        return ConfigProvider.isAsyncAllowed(operation) && (m_wrapper == null || m_wrapper.getMode());
    }

    /**
     * Get next job id for current player
     *
     * @return Job id
     */
    private int getJobId() {
        return m_blockPlacer.getJobId(m_player);
    }
}