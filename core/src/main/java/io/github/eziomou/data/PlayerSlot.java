package io.github.eziomou.data;

public final class PlayerSlot {

    private PlayerSlot() {
    }

    public static boolean isRadiant(int playerSlot) {
        return (playerSlot & 128) == 0;
    }
}
