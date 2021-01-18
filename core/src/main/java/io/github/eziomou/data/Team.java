package io.github.eziomou.data;

public class Team {

    public static boolean isSameTeam(PublicPlayerMatch first, PublicPlayerMatch second) {
        return first.isRadiant() == second.isRadiant();
    }
}
