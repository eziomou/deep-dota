package io.github.eziomou.data;

public enum Hero {
    ANTI_MAGE(1, "antimage"),
    AXE(2, "axe"),
    BANE(3, "bane"),
    BLOODSEEKER(4, "bloodseeker"),
    CRYSTAL_MAIDEN(5, "crystal_maiden"),
    DROW_RANGER(6, "drow_ranger"),
    EARTHSHAKER(7, "earthshaker"),
    JUGGERNAUT(8, "juggernaut"),
    MIRANA(9, "mirana"),
    MORPHLING(10, "morphling"),
    NEVERMORE(11, "nevermore"),
    PHANTOM_LANCER(12, "phantom_lancer"),
    PUCK(13, "puck"),
    PUDGE(14, "pudge"),
    RAZOR(15, "razor"),
    SAND_KING(16, "sand_king"),
    STORM_SPIRIT(17, "storm_spirit"),
    SVEN(18, "sven"),
    TINY(19, "tiny"),
    VENGEFULSPIRIT(20, "vengefulspirit"),
    WINDRUNNER(21, "windrunner"),
    ZUUS(22, "zuus"),
    KUNKKA(23, "kunkka"),
    LINA(25, "lina"),
    LION(26, "lion"),
    SHADOW_SHAMAN(27, "shadow_shaman"),
    SLARDAR(28, "slardar"),
    TIDEHUNTER(29, "tidehunter"),
    WITCH_DOCTOR(30, "witch_doctor"),
    LICH(31, "lich"),
    RIKI(32, "riki"),
    ENIGMA(33, "enigma"),
    TINKER(34, "tinker"),
    SNIPER(35, "sniper"),
    NECROLYTE(36, "necrolyte"),
    WARLOCK(37, "warlock"),
    BEASTMASTER(38, "beastmaster"),
    QUEENOFPAIN(39, "queenofpain"),
    VENOMANCER(40, "venomancer"),
    FACELESS_VOID(41, "faceless_void"),
    SKELETON_KING(42, "skeleton_king"),
    DEATH_PROPHET(43, "death_prophet"),
    PHANTOM_ASSASSIN(44, "phantom_assassin"),
    PUGNA(45, "pugna"),
    TEMPLAR_ASSASSIN(46, "templar_assassin"),
    VIPER(47, "viper"),
    LUNA(48, "luna"),
    DRAGON_KNIGHT(49, "dragon_knight"),
    DAZZLE(50, "dazzle"),
    RATTLETRAP(51, "rattletrap"),
    LESHRAC(52, "leshrac"),
    FURION(53, "furion"),
    LIFE_STEALER(54, "life_stealer"),
    DARK_SEER(55, "dark_seer"),
    CLINKZ(56, "clinkz"),
    OMNIKNIGHT(57, "omniknight"),
    ENCHANTRESS(58, "enchantress"),
    HUSKAR(59, "huskar"),
    NIGHT_STALKER(60, "night_stalker"),
    BROODMOTHER(61, "broodmother"),
    BOUNTY_HUNTER(62, "bounty_hunter"),
    WEAVER(63, "weaver"),
    JAKIRO(64, "jakiro"),
    BATRIDER(65, "batrider"),
    CHEN(66, "chen"),
    SPECTRE(67, "spectre"),
    ANCIENT_APPARITION(68, "ancient_apparition"),
    DOOM_BRINGER(69, "doom_bringer"),
    URSA(70, "ursa"),
    SPIRIT_BREAKER(71, "spirit_breaker"),
    GYROCOPTER(72, "gyrocopter"),
    ALCHEMIST(73, "alchemist"),
    INVOKER(74, "invoker"),
    SILENCER(75, "silencer"),
    OBSIDIAN_DESTROYER(76, "obsidian_destroyer"),
    LYCAN(77, "lycan"),
    BREWMASTER(78, "brewmaster"),
    SHADOW_DEMON(79, "shadow_demon"),
    LONE_DRUID(80, "lone_druid"),
    CHAOS_KNIGHT(81, "chaos_knight"),
    MEEPO(82, "meepo"),
    TREANT(83, "treant"),
    MAGI(84, "magi"),
    UNDYING(85, "undying"),
    RUBIC(86, "rubic"),
    DISRUPTOR(87, "disruptor"),
    NYX_ASSASSIN(88, "nyx_assassin"),
    NAGA_SIREN(89, "naga_siren"),
    KEEPER_OF_THE_LIGHT(90, "keeper_of_the_light"),
    WISP(91, "wisp"),
    VISAGE(92, "visage"),
    SLARK(93, "slark"),
    MEDUSA(94, "medusa"),
    TROLL_WARLORD(95, "troll_warlord"),
    CENTAUR(96, "centaur"),
    MAGNATAUR(97, "magnataur"),
    SHREDDER(98, "shredder"),
    BRISTLEBACK(99, "bristleback"),
    TUSK(100, "tusk"),
    SKYWRATH_MAGE(101, "skywrath_mage"),
    ABADDON(102, "abaddon"),
    ELDER_TITAN(103, "elder_titan"),
    LEGION_COMMANDER(104, "legion_commander"),
    TECHIES(105, "techies"),
    EMBER_SPIRIT(106, "ember_spirit"),
    EARTH_SPIRIT(107, "earth_spirit"),
    UNDERLORD(108, "underlord"),
    TERRORBLADE(109, "terrorblade"),
    PHOENIX(110, "phoenix"),
    ORACLE(111, "oracle"),
    WINTER_WYVERN(112, "winter_wyvern"),
    ARC_WARDEN(113, "arc_warden"),
    MONKEY_KING(114, "monkey_king"),
    DARK_WILLOW(119, "dark_willow"),
    PANGOLIER(120, "pangolier"),
    GRIMSTROKE(121, "grimstroke"),
    HOODWINK(123, "hoodwink"),
    VOID_SPIRIT(126, "void_spirit"),
    SNAPFIRE(128, "snapfire"),
    MARS(129, "mars");

    private static final String NAME_PREFIX = "npc_dota_hero_";

    private final int id;
    private final String name;

    Hero(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return NAME_PREFIX + name;
    }
}