package nl.odalitadevelopments.menus.utils;

import nl.odalitadevelopments.menus.utils.version.ProtocolVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

final class ReflectionUtils {

    private ReflectionUtils() {
    }

    private static final String NM_PACKAGE = "net.minecraft";
    private static final String OBC_PACKAGE = "org.bukkit.craftbukkit";

    static Class<?> PACKET;
    static Class<?> ENTITY_PLAYER;
    static Class<?> ENTITY_HUMAN;
    static Class<?> PLAYER_CONNECTION;
    static Class<?> CONTAINER;
    static Class<?> CONTAINERS;
    static Class<?> IINVENTORY;
    static Class<?> NON_NULL_LIST;
    static Class<?> CHAT_BASE_COMPONENT;

    static Class<?> CRAFT_PLAYER;
    static Class<?> CRAFT_INVENTORY;
    static Class<?> MINECRAFT_INVENTORY;
    static Class<?> ITEM_STACK;
    static Class<?> CRAFT_ITEM_STACK;

    static Class<?> PACKET_PLAY_OUT_SET_SLOT;
    static Class<?> PACKET_PLAY_OUT_OPEN_WINDOW;

    static Method GET_NMS_ITEM_STACK;
    static Method GET_NMS_INVENTORY;
    static Method GET_NMS_INVENTORY_TYPE;
    static Method GET_NMS_INVENTORY_CONTENTS;
    static Method SET_LIST;
    static Method REFRESH_INVENTORY;
    static Method WINDOW_STATE_ID_METHOD;

    static Field GET_NMS_CONTAINER_ITEMS_1165;

    static Field ACTIVE_CONTAINER_FIELD;
    static Field WINDOW_ID_FIELD;
    static Field TITLE_FIELD;
    static Field MINECRAFT_INVENTORY_TITLE_FIELD;

    static Constructor<?> PACKET_PLAY_OUT_SET_SLOT_CONSTRUCTOR;
    static Constructor<?> PACKET_PLAY_OUT_OPEN_WINDOW_CONSTRUCTOR;

    static MethodHandle GET_PLAYER_HANDLE_METHOD;
    static MethodHandle GET_PLAYER_CONNECTION_METHOD;
    static MethodHandle SEND_PACKET_METHOD;

    static {
        try {
            ProtocolVersion version = ProtocolVersion.getServerVersion();
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            PACKET = nmsClass("network.protocol", "Packet");
            ENTITY_PLAYER = nmsClass("server.level", "EntityPlayer");
            ENTITY_HUMAN = nmsClass("world.entity.player", "EntityHuman");
            PLAYER_CONNECTION = nmsClass("server.network", "PlayerConnection");
            CONTAINER = nmsClass("world.inventory", "Container");
            CONTAINERS = nmsClass("world.inventory", "Containers");
            IINVENTORY = nmsClass("world", "IInventory");
            NON_NULL_LIST = nmsClass("core", "NonNullList");
            CHAT_BASE_COMPONENT = nmsClass("network.chat", "IChatBaseComponent");

            CRAFT_PLAYER = obcClass("entity.CraftPlayer");
            CRAFT_INVENTORY = obcClass("inventory.CraftInventory");
            MINECRAFT_INVENTORY = obcClass("inventory.CraftInventoryCustom$MinecraftInventory");
            ITEM_STACK = nmsClass("world.item", "ItemStack");
            CRAFT_ITEM_STACK = obcClass("inventory.CraftItemStack");

            PACKET_PLAY_OUT_SET_SLOT = nmsClass("network.protocol.game", "PacketPlayOutSetSlot");
            PACKET_PLAY_OUT_OPEN_WINDOW = nmsClass("network.protocol.game", "PacketPlayOutOpenWindow");

            GET_NMS_ITEM_STACK = CRAFT_ITEM_STACK.getMethod("asNMSCopy", ItemStack.class);
            GET_NMS_INVENTORY = CRAFT_INVENTORY.getMethod("getInventory");
            GET_NMS_INVENTORY_CONTENTS = IINVENTORY.getMethod("getContents");
            SET_LIST = List.class.getMethod("set", int.class, Object.class);
            TITLE_FIELD = CONTAINER.getDeclaredField("title");
            MINECRAFT_INVENTORY_TITLE_FIELD = MINECRAFT_INVENTORY.getDeclaredField("title");

            ACTIVE_CONTAINER_FIELD = Arrays.stream(ENTITY_HUMAN.getFields())
                    .filter(field -> field.getType().isAssignableFrom(CONTAINER))
                    .findFirst().orElseThrow(NoSuchFieldException::new);

            if (version.isHigherOrEqual(ProtocolVersion.MINECRAFT_1_19)) {
                GET_NMS_INVENTORY_TYPE = CONTAINER.getMethod("a");
                REFRESH_INVENTORY = CONTAINER.getMethod("b");
                WINDOW_STATE_ID_METHOD = CONTAINER.getMethod("j");

                WINDOW_ID_FIELD = CONTAINER.getField("j");

                PACKET_PLAY_OUT_SET_SLOT_CONSTRUCTOR = PACKET_PLAY_OUT_SET_SLOT.getConstructor(int.class, int.class, int.class, ITEM_STACK);
            } else if (version.isHigherOrEqual(ProtocolVersion.MINECRAFT_1_18)) {
                GET_NMS_INVENTORY_TYPE = CONTAINER.getMethod("a");
                REFRESH_INVENTORY = CONTAINER.getMethod("b");
                WINDOW_STATE_ID_METHOD = CONTAINER.getMethod("j");

                WINDOW_ID_FIELD = CONTAINER.getField("j");

                PACKET_PLAY_OUT_SET_SLOT_CONSTRUCTOR = PACKET_PLAY_OUT_SET_SLOT.getConstructor(int.class, int.class, int.class, ITEM_STACK);
            } else if (version.isHigherOrEqual(ProtocolVersion.MINECRAFT_1_17)) {
                GET_NMS_INVENTORY_TYPE = CONTAINER.getMethod("getType");
                REFRESH_INVENTORY = CONTAINER.getMethod("updateInventory");

                if (version.isEqual(ProtocolVersion.MINECRAFT_1_17_1)) {
                    WINDOW_STATE_ID_METHOD = CONTAINER.getMethod("getStateId");
                    PACKET_PLAY_OUT_SET_SLOT_CONSTRUCTOR = PACKET_PLAY_OUT_SET_SLOT.getConstructor(int.class, int.class, int.class, ITEM_STACK);
                }

                WINDOW_ID_FIELD = CONTAINER.getField("j");

                PACKET_PLAY_OUT_SET_SLOT_CONSTRUCTOR = PACKET_PLAY_OUT_SET_SLOT.getConstructor(int.class, int.class, ITEM_STACK);
            } else {
                GET_NMS_INVENTORY_TYPE = CONTAINER.getMethod("getType");
                GET_NMS_CONTAINER_ITEMS_1165 = CONTAINER.getDeclaredField("items");
                REFRESH_INVENTORY = ENTITY_PLAYER.getMethod("a", CONTAINER, NON_NULL_LIST);

                WINDOW_ID_FIELD = CONTAINER.getField("windowId");

                PACKET_PLAY_OUT_SET_SLOT_CONSTRUCTOR = PACKET_PLAY_OUT_SET_SLOT.getConstructor(int.class, int.class, ITEM_STACK);
            }

            PACKET_PLAY_OUT_OPEN_WINDOW_CONSTRUCTOR = PACKET_PLAY_OUT_OPEN_WINDOW.getConstructor(int.class, CONTAINERS, CHAT_BASE_COMPONENT);

            Field playerConnectionField = Arrays.stream(ENTITY_PLAYER.getFields())
                    .filter(field -> field.getType().isAssignableFrom(PLAYER_CONNECTION))
                    .findFirst().orElseThrow(NoSuchFieldException::new);

            Method sendPacketMethod = Arrays.stream(PLAYER_CONNECTION.getMethods())
                    .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0] == PACKET)
                    .findFirst().orElseThrow(NoSuchMethodException::new);

            GET_PLAYER_HANDLE_METHOD = lookup.findVirtual(CRAFT_PLAYER, "getHandle", MethodType.methodType(ENTITY_PLAYER));
            GET_PLAYER_CONNECTION_METHOD = lookup.unreflectGetter(playerConnectionField);
            SEND_PACKET_METHOD = lookup.unreflect(sendPacketMethod);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static synchronized void sendPacket(Player player, Object packet) {
        try {
            Object entityPlayer = GET_PLAYER_HANDLE_METHOD.invoke(player);
            Object playerConnection = GET_PLAYER_CONNECTION_METHOD.invoke(entityPlayer);
            SEND_PACKET_METHOD.invoke(playerConnection, packet);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    static Object createChatBaseComponent(String string) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return CHAT_BASE_COMPONENT.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + string + "\"}");
    }

    private static String getServerProtocolVersion() {
        String bv = Bukkit.getServer().getClass().getPackage().getName();
        return bv.substring(bv.lastIndexOf('.') + 1);
    }

    private static String nmsClassName(String post1_17package, String className) {
        String classPackage = post1_17package == null || post1_17package.isEmpty() ? NM_PACKAGE : NM_PACKAGE + '.' + post1_17package;
        return classPackage + '.' + className;
    }

    private static Class<?> nmsClass(String post1_17package, String className) throws ClassNotFoundException {
        return Class.forName(nmsClassName(post1_17package, className));
    }

    private static String obcClassName(String className) {
        return OBC_PACKAGE + '.' + getServerProtocolVersion() + '.' + className;
    }

    private static Class<?> obcClass(String className) throws ClassNotFoundException {
        return Class.forName(obcClassName(className));
    }
}