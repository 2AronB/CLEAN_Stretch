package dev.clean.stretch.catalog

/**
 * The move library. Each move lives in the file for its body area (the region of its first main
 * muscle): NeckMoves, ShoulderMoves, ArmMoves, BackMoves, CoreMoves, HipMoves, LegMoves. `CatalogTest`
 * checks every move is in the right file and listed here exactly once.
 *
 * Instructions are Editorial (standard exercise technique); hold defaults come from [S2][S3];
 * warm-up flows follow EVIDENCE §6–7 [S3]; Strengthen holds (P-5) and foam rolling (P-10) as
 * documented in EVIDENCE. Poses were tuned with tools/pose-preview and Pose Studio.
 */
object SampleCatalog {

    /**
     * Every move, in library order (the Stretches tab lists them in this order). New moves go at
     * the end so existing users see a familiar order.
     */
    val all: List<Stretch> = listOf(
        LegMoves.seatedForwardFold, LegMoves.standingQuad, HipMoves.halfKneelingHipFlexor,
        BackMoves.childsPose, LegMoves.wallCalf, BackMoves.catCow, NeckMoves.neckFlexion,
        ShoulderMoves.chestOpener, BackMoves.wallLat, HipMoves.kneeToChest, HipMoves.butterfly,
        LegMoves.bentKneeCalf, LegMoves.standingHamstring, CoreMoves.sphinx, LegMoves.lyingHamstring,
        LegMoves.downwardDog, BackMoves.upperBack, HipMoves.standingLunge, BackMoves.overheadReach,
        CoreMoves.standingBackBend, HipMoves.standingKneeHug, LegMoves.sideLyingQuad,
        LegMoves.kneelingQuad, LegMoves.towelCalf, ShoulderMoves.wallChest, CoreMoves.cobra,
        ShoulderMoves.crossBodyShoulder, BackMoves.puppy, NeckMoves.chinTuck, BackMoves.wallAngel,
        HipMoves.activeLegRaise, HipMoves.gluteBridge, CoreMoves.plank, LegMoves.calfRaiseHold,
        BackMoves.rollUpperBack, LegMoves.rollCalves, LegMoves.rollQuads, LegMoves.legSwings,
        ShoulderMoves.armSwings, LegMoves.hipHinge, LegMoves.ankleRocks, HipMoves.lungeReach,
        NeckMoves.neckNods, NeckMoves.neckSideBend, BackMoves.standingSideBend, HipMoves.figureFour,
        BackMoves.kneeDropTwist, BackMoves.seatedTwist, BackMoves.threadNeedle, HipMoves.wideLegFold,
        HipMoves.sideLunge, ArmMoves.overheadTriceps, CoreMoves.torsoTurns, HipMoves.sideLegRaise,
        HipMoves.recliningButterfly, ArmMoves.wristFlexor, ArmMoves.wristExtensor, ArmMoves.prayerStretch,
        LegMoves.standingShin, LegMoves.kneelingShin, LegMoves.anklePumps,
        ArmMoves.wallBiceps, ArmMoves.standingBiceps, ArmMoves.seatedBiceps,
    )

    fun byKey(key: String): Stretch? = all.firstOrNull { it.key == key }


    /**
     * Easier → harder pairs for the same body area (EVIDENCE §5.4). Used for the Easier/Harder
     * buttons; CatalogTest checks every pair resolves and shares a main area.
     */
    val variantPairs: List<Pair<String, String>> = listOf(
        "lying_hamstring" to "standing_hamstring",
        "standing_hamstring" to "seated_forward_fold",
        "seated_forward_fold" to "downward_dog",
        "side_lying_quad" to "standing_quad",
        "standing_quad" to "kneeling_quad",
        "standing_lunge_hip_flexor" to "half_kneeling_hip_flexor",
        "chest_opener" to "wall_chest",
        "overhead_reach" to "wall_lat",
        "wall_lat" to "puppy",
        "standing_back_bend" to "sphinx",
        "sphinx" to "cobra",
        "towel_calf" to "wall_calf",
        "reclining_butterfly" to "butterfly",
        "butterfly" to "wide_leg_fold",
        "knee_drop_twist" to "seated_twist",
        "standing_shin" to "kneeling_shin",
        "standing_biceps" to "seated_biceps",
    )

    fun easier(key: String): Stretch? = variantPairs.firstOrNull { it.second == key }?.first?.let(::byKey)

    fun harder(key: String): Stretch? = variantPairs.firstOrNull { it.first == key }?.second?.let(::byKey)

    /** Muscles that at least one move stretches or works as a main muscle (new muscles may have none yet). */
    val musclesWithMoves: List<Muscle> by lazy { Muscle.entries.filter { m -> all.any { m in it.primary } } }
}
