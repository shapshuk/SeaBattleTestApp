package com.shapshuk.SeaBattleTestApp.util

import kotlin.random.Random

class ShipPlacement(private val gridSize: Int) {
    private val grid = Array(gridSize) { Array(gridSize) { false } } // False means cell is empty
    private val ships = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1) // Ship lengths

    fun placeShips() {
        resetGrid()
        for (shipSize in ships) {
            var placed = false
            while (!placed) {
                val orientation = Random.nextBoolean() // True = horizontal, False = vertical
                val startX = Random.nextInt(gridSize)
                val startY = Random.nextInt(gridSize)

                if (canPlaceShip(startX, startY, shipSize, orientation)) {
                    placeShip(startX, startY, shipSize, orientation)
                    placed = true
                }
            }
        }
        println(getGrid().joinToString("\n") { it.joinToString(" ") })
    }

    private fun canPlaceShip(x: Int, y: Int, size: Int, horizontal: Boolean): Boolean {
        for (i in 0 until size) {
            val checkX = if (horizontal) x + i else x
            val checkY = if (horizontal) y else y + i

            if (checkX !in 0 until gridSize || checkY !in 0 until gridSize || grid[checkY][checkX] || !hasSpacing(checkX, checkY)) {
                return false
            }
        }
        return true
    }

    private fun hasSpacing(x: Int, y: Int): Boolean {
        for (dx in -1..1) {
            for (dy in -1..1) {
                val checkX = x + dx
                val checkY = y + dy

                if (checkX in 0 until gridSize && checkY in 0 until gridSize && grid[checkY][checkX]) {
                    return false
                }
            }
        }
        return true
    }

    private fun placeShip(x: Int, y: Int, size: Int, horizontal: Boolean) {
        for (i in 0 until size) {
            val placeX = if (horizontal) x + i else x
            val placeY = if (horizontal) y else y + i
            grid[placeY][placeX] = true
        }
    }

    private fun resetGrid() {
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                grid[row][col] = false
            }
        }
    }

    fun getGrid(): Array<Array<Boolean>> = grid
}

