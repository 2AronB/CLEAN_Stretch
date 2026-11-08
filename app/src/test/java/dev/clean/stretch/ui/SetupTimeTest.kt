package dev.clean.stretch.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class SetupTimeTest {
    @Test
    fun setupTimeFollowsTheMove() {
        // child's pose (kneeling) -> standing quad (standing, two sides) -> wall calf (standing, two sides)
        val setups = buildSteps(listOf("childs_pose", "standing_quad", "wall_calf")).filter { it.setup }.map { it.setupMs }
        assertEquals(
            listOf(SETUP_FIRST_MS, SETUP_NEW_POSITION_MS, SETUP_SIDE_SWITCH_MS, SETUP_SAME_POSITION_MS, SETUP_SIDE_SWITCH_MS),
            setups,
        )
    }
}
