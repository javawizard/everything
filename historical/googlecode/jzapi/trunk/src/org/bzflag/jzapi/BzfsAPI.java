package org.bzflag.jzapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides methods for interacting with BZFS. This can only be called from
 * within a plugin.
 * 
 * @author Alexander Boyd
 * 
 */
public class BzfsAPI
{
    
    static final long MAX_UID_VALUE = 0x6FFFFFFFl;
    
    public static final int SERVER_PLAYER = -2;
    
    public static final int ALL_PLAYERS = -1;
    /**
     * I have no idea why this is here. It should be removed after making sure
     * that nothing (in particular, native methods) uses it.
     */
    static boolean nothing;
    
    protected static interface AlternateOrdinal
    {
        public int alt();
    }
    
    public static class TypeEventHandlerPair
    {
        public EventType type;
        public BzfsEventHandler handler;
        
        public TypeEventHandlerPair(EventType type,
            BzfsEventHandler handler)
        {
            super();
            this.type = type;
            this.handler = handler;
        }
        
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result =
                prime
                    * result
                    + ((handler == null) ? 0 : handler
                        .hashCode());
            result =
                prime
                    * result
                    + ((type == null) ? 0 : type.hashCode());
            return result;
        }
        
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final TypeEventHandlerPair other =
                (TypeEventHandlerPair) obj;
            if (handler == null)
            {
                if (other.handler != null)
                    return false;
            }
            else if (!handler.equals(other.handler))
                return false;
            if (type == null)
            {
                if (other.type != null)
                    return false;
            }
            else if (!type.equals(other.type))
                return false;
            return true;
        }
    }
    
    public static enum EventType
    {
        nullEvent, capture, playerDie, playerSpawn,
        zoneEntry, zoneExit, playerJoin, playerPart,
        rawChatMessage, filteredChatMessage,
        unknownSlashCommand, getPlayerSpawnPos,
        getAutoTeam, allowPlayer, tick, getWorld,
        getPlayerInfo, allowSpawn, listServerUpdate, ban,
        hostBanNotify, hostBanModify, idBan, kick, kill,
        playerPaused, messageFiltered, gameStart, gameEnd,
        slashCommand, playerAuth, serverMessage, shotFired,
        anointRabbit, newRabbit, reload, playerUpdate,
        netDataSend, netDataReceive, logging,
        flagTransferred, flagGrabbed, flagDropped,
        shotEnded, newNonPlayerConnection,
        idleNewNonPlayerConnection, playerCollision,
        flagReset, worldFinalized, allowCTFCapture,
        allowFlagGrab, allowKillCommand, reportFiled,
        teleport, playerSentCustomData,
        playerCustomDataChanged, BZDBChange, lastEvent
    }
    
    public static enum TeamType
    {
        ROGUE, RED, GREEN, BLUE, PURPLE, RABBIT, HUNTER,
        OBSERVER, ADMINISTRATOR, AUTOMATIC, NONE;
    }
    
    public static enum GameType
    {
        /**
         * A game type where players are on teams. Players are not allowed to
         * kill teammates, but they can kill everyone else.
         */
        teamFFA,
        /**
         * A game type where players are on teams, and where the objective is to
         * capture the opponent's flag.
         */
        classicCTF,
        /**
         * A game type with two teams, the hunter team and the rabbit team. The
         * hunter team is supposed to kill the rabbit team. Only one person is
         * on the rabbit team at a time, and if a hunter kills that person, then
         * the hunter becomes the rabbit. Hunters are not allowed to kill each
         * other.
         */
        rabbit,
        /**
         * A game type where all players are rogue. A player may kill anyone
         * else on the server.
         */
        openFFA
    }
    
    public static enum ShotType
    {
        none,
        /**
         * A standard shot. This is the type of shot that is fired if a player
         * is not holding a flag.
         */
        standard,
        /**
         * A guided missile shot. This type of shot is fired if the player is
         * holding the Guided Missile flag. A guided missile cannot kill a tank
         * that is within a certain range of the person that fired the guided
         * missile. Guided missiles cannot lock onto tanks with Stealth.
         */
        GM,
        /**
         * A laser shot. Lasers typically take much longer to reload. Tanks with
         * Cloaking cannot be killed by a laser shot.
         */
        laser,
        /**
         * A thief shot. Thief shots are similar to laser shots, but they have a
         * range comparable to that of a normal shot (whereas lasers typically
         * go on indefinitely). If a thief shot hits a player, then that
         * player's flag is transferred to the player that fired the thief shot
         * (even for bad flags).
         */
        thief,
        /**
         * A super bullet shot. Super bullets can go through walls (and hence
         * can kill tanks with Oscillation Overthruster that are inside a
         * building), and can kill zoned tanks.
         */
        superBullet,
        /**
         * A phantom shot. This is the type of shot fired by a zoned tank. To
         * become zoned, a tank must pick up a Phantom Zone flag and jump
         * through a teleporter. Jumping through a teleporter again will return
         * the tank (and it's shot type) to normal.
         */
        phantom,
        /**
         * A shockwave shot. Shockwaves project outward in a spherical manner,
         * instead of being aimed in one direction. If a player enters the
         * region of a shockwave (with the exception of the player that fired
         * the shockwave), that player is immediately killed.
         */
        shockwave,
        /**
         * A ricochet shot. This shot type is only useful on servers where
         * ricochet is disabled but where Ricochet flags are present. Ricochet
         * shots bounce off of walls and buildings.
         */
        rico,
        /**
         * A machine gun shot. Machine gun shots are not very different from
         * normal shots, except that they may go faster on some servers, and
         * that they typically have a reduced lifespan.
         */
        machine,
        /**
         * An invisible bullet. This type of shot is visible out the window but
         * not on the radar. Other than that, it functions like a normal shot.
         */
        invisible,
        /**
         * A cloaked shot. This type of shot is visible on the radar but not out
         * the window. The shot's glow is still visible out the window. Other
         * than that, it functions like a normal shot.
         */
        cloaked,
        /**
         * A rapid fire shot. These shots typically go faster than normal shots.
         */
        rapidFire,
        /**
         * The last shot type enum constant. Shots should never have this type.
         */
        last
    }
    
    /**
     * The quality of a flag.
     * 
     * @author Alexander Boyd
     * 
     */
    public static enum FlagQuality
    {
        /**
         * A good flag. These flags can be dropped by the player at will.
         */
        good,
        /**
         * A bad flag. These flags typically cannot be dropped by the player at
         * will.
         */
        bad, last
    }
    
    /**
     * The current status of a player.
     * 
     * @author Alexander Boyd
     * 
     */
    public static enum PlayerStatus
    {
        /**
         * Indicates that the player is currently dead.
         */
        dead,
        /**
         * Indicates that the player is currently alive, but not in any other
         * state.
         */
        alive,
        /**
         * Indicates that the player is currently paused. A player will
         * transition to {@link #dead} if a teammate gets hit with genocide or
         * if the player's flag is captured.
         */
        paused,
        /**
         * Indicates that the player is currently exploding. This occurs right
         * after the player gets killed, and persists until the "limbo" screen
         * is shown to the player, at which point the player transitions to
         * {@link #dead}.
         */
        exploding,
        /**
         * Indicates that the player is currently teleporting.
         */
        teleporting,
        /**
         * Indicates that the player is curently sealed. This typically only
         * happens when the player has Oscillation Overthruster and is driving
         * through a building, although it occasionally occurs when the player
         * lands a jump next to a building. Players cannot jump or drive
         * backwards when they are sealed, and unless they have Oscillation
         * Overthruster, they cannot move.
         */
        sealed
    }
    
    public static enum BanListType
    {
        ipList, idList, hostList
    }
    
    /**
     * The reason why a player died.
     * 
     * @author Alexander Boyd
     * 
     */
    public static enum PlayerDeathReason
    {
        /**
         * Indicates that a player died because TODO: When is this actually
         * triggered?
         */
        killed,
        /**
         * Indicates that a player died because of TODO: When is this actually
         * triggered, in particular, instead of killed?
         */
        shot,
        /**
         * Indicates that a player died because they were run over. This
         * typically occurs if the player has BU and another player drives over
         * them, or the other player has SR.
         */
        runOver,
        /**
         * Indicates that a player died because their team's flag was captured.
         * Unlike the other death reasons, this one doesn't usually result in a
         * loss of points.
         */
        captured,
        /**
         * Indicates that a player died because a teammate was hit with
         * genocide.
         */
        genocide,
        /**
         * Indicates that a player died because they self-destructed.
         */
        selfDestruct,
        /**
         * Indicates that a player died because they fell below the water level.
         */
        water,
        /**
         * Indicates that a player died because they came in contact with a
         * physics driver marked as DEATH.
         */
        physics
    }
    
    public static enum WorldObjectType
    {
        empty, solid, teleporter, worldWeapon
    }
    
    public static enum SolidWorldObjectType
    {
        wall, box, base, pyramid, mesh, arc, cone, sphere,
        tetra, unknown
    }
    
    private static final Enum[][] enums =
        new Enum[][] { EventType.values(),
            TeamType.values(), GameType.values(),
            ShotType.values(), FlagQuality.values(),
            PlayerStatus.values(),
            WorldObjectType.values(),
            SolidWorldObjectType.values(),
            PlayerDeathReason.values(),
            BanListType.values() };
    
    /**
     * This method is called from the java native plugin code itself, not from
     * within BzfsAPI. It returns the enum constant at the specified index for
     * the specified enum.
     * 
     * @param enumId
     *            The id of the enum. Look at {@link #enums} for a list of which
     *            enums have which ids.
     * @param constantIndex
     *            The index of the enum constant to retrieve. 0 is the first
     *            constant.
     * @return The enum constant of that type
     */
    private static Enum getEnumConstant(int enumId,
        int constantIndex)
    {
        if (enumId < 0 || enumId >= enums.length)
        {
            System.out
                .println("invalid enum id referenced at index "
                    + enumId);
            return null;
        }
        Enum[] constants = enums[enumId];
        if (constantIndex < 0
            && constants[0].getClass() == TeamType.class)
        {
            if (constantIndex == -2)
                return TeamType.AUTOMATIC;
            if (constantIndex == -1)
                return TeamType.NONE;
        }
        if (constantIndex < 0
            || constantIndex >= constants.length)
        {
            System.out
                .println("invalid enum constant index for id "
                    + enumId
                    + " and index "
                    + constantIndex);
            return null;
        }
        return constants[constantIndex];
    }
    
    public static int getEnumTypeIndex(String enumName)
    {
        for (int i = 0; i < enums.length; i++)
        {
            Enum[] list = enums[i];
            if (list[0].getClass().getSimpleName().equals(
                enumName))
                return i;
        }
        return -1;
    }
    
    public static final String getEnumIndexClass(
        String enumName)
    {
        return enums[getEnumTypeIndex(enumName)][0]
            .getClass().getName();
    }
    
    private static int getEnumIndex(Enum constant)
    {
        if (constant == TeamType.AUTOMATIC)
            return -2;
        if (constant == TeamType.NONE)
            return -1;
        return constant.ordinal();
    }
    
    /**
     * This method is called from the native code, not from Java, although it
     * can be. It returns the integer constant that corresponds to the native
     * enum represented.
     * 
     * @param constant
     * @return
     */
    @SuppressWarnings("unchecked")
    public static int getEnumOrdinal(Enum constant)
    {
        return getEnumIndex(constant);
    }
    
    private static HashMap<TypeEventHandlerPair, Long> eventHandlerMap =
        new HashMap<TypeEventHandlerPair, Long>();
    
    private static volatile long nextEventHandlerId = 1;
    
    public static synchronized native void registerEventHandler(
        int eventType, BzfsEventHandler handler);
    
    public static native void removeEventHandler(
        int eventType, BzfsEventHandler handler);
    
    /**
     * Returns a list of player indexes (player indexes are the same as player
     * IDs) that represent the players that are currently on the server.
     * 
     * @return
     */
    public static native int[] getPlayerIndexList();
    
    /**
     * Unused, will probably be deleted in the future.
     * 
     * @return
     */
    public static native long[] getFunctionPointers();
    
    /**
     * This actually doesn't really do anything. When I added this, I thought
     * that player IDs and player indexes were different, but they are actually
     * the same. The only purpose of this method, then, is that it will return
     * -1 instead of the input value if the player at the specified index
     * doesn't exist.
     * 
     * @param playerIndex
     * @return
     */
    public static native int getPlayerId(int playerIndex);
    
    /**
     * Gets the player id of the player using the specified callsign.
     * 
     * @param callsign
     * @return
     */
    public static native int getPlayerIdByCallsign(
        String callsign);
    
    public static native BasePlayerRecord getPlayerRecord(
        int player);
    
    public static native void freePlayerRecord(
        BasePlayerRecord record);
}
