package com.fiz.tetriswithlife.gameScreen.domain.models

import com.fiz.tetriswithlife.gameScreen.domain.models.character.Character
import com.fiz.tetriswithlife.gameScreen.domain.models.character.Location
import org.junit.Assert
import org.junit.Test

class CharacterTest {

    @Test
    fun whenNoWayUp_shouldNoFindWay() {
        val game = Game(Grid(13, 25))
        game.grid.space[12][0].block = 1
        game.grid.space[12][1].block = 1
        game.grid.space[12][2].block = 1
        game.grid.space[13][2].block = 1
        game.grid.space[13][3].block = 1
        game.grid.space[14][3].block = 1
        game.grid.space[14][4].block = 1
        game.grid.space[14][5].block = 1
        game.grid.space[15][5].block = 1
        game.grid.space[16][5].block = 1
        game.grid.space[16][6].block = 1
        game.grid.space[16][7].block = 1
        game.grid.space[17][7].block = 1
        game.grid.space[18][7].block = 1
        game.grid.space[19][7].block = 1
        game.grid.space[19][6].block = 1
        game.grid.space[20][7].block = 1
        game.grid.space[20][8].block = 1
        game.grid.space[20][9].block = 1
        game.grid.space[21][9].block = 1
        game.grid.space[21][10].block = 1
        game.grid.space[22][10].block = 1
        game.grid.space[23][10].block = 1
        game.grid.space[24][10].block = 1
        game.grid.space[21][11].block = 1
        game.grid.space[21][12].block = 1
        val character = Character(
            Location(Coordinate(5.0, 24.0))
        )

        val isPathUp = game.isPathUp(
            character.location.position.posTile,
            game.getFullCopySpace()
        )

        Assert.assertFalse(isPathUp)
    }
}