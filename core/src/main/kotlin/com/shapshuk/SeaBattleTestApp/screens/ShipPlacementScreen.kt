package com.shapshuk.SeaBattleTestApp.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.shapshuk.SeaBattleTestApp.util.ShipPlacement
import com.shapshuk.SeaBattleTestApp.app.SeaBattleTestApp
import ktx.app.KtxScreen

class ShipPlacementScreen(private val game: SeaBattleTestApp) : KtxScreen {
    private val stage = Stage()
    private val skin = Skin(Gdx.files.internal("skin/uiskin.json"))
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()

    private val gridSize = 10 // Grid dimensions (10x10)
    private var cellSize = 0f // Calculated dynamically
    private val gridStartX = 200f // Left side offset for the grid
    private val gridStartY = 150f // Bottom offset for the grid
    private val labelFontScale = 2f // Font size scaling factor
    private val shipPlacement = ShipPlacement(gridSize)
    private val shipTexture = Texture(Gdx.files.internal("ship_square.png"))
    private val backButtonTexture = Texture(Gdx.files.internal("return_button.png"))

    init {
        val backButton = ImageButton(TextureRegionDrawable(backButtonTexture)).apply {
            setSize(80f, 80f)
            setPosition(40f, Gdx.graphics.height - height - 40f) // Top-left corner
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    println("Back Button Clicked!")
                    game.setScreen<MainMenuScreen>()
                }
            })
        }
        stage.addActor(backButton)

        val button = TextButton("Rearrange Ships", skin).apply {
            setSize(300f, 120f)
            setScale(labelFontScale)
            setPosition(Gdx.graphics.width * 0.75f, Gdx.graphics.height / 2f - height / 2f)
            label.setFontScale(labelFontScale)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    shipPlacement.placeShips() // Rearrange ships
                }
            })
        }
        stage.addActor(button)

        // Place ships for the first time
        shipPlacement.placeShips()
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        // Clear the screen
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT)

        // Draw grid, labels, and ships
        drawGridAndLabels()
        drawShips()

        // Render the stage (button)
        stage.act(delta)
        stage.draw()
    }

    private fun drawGridAndLabels() {
        // Calculate cell size
        cellSize = (Gdx.graphics.height - 2 * gridStartY) / gridSize

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK

        for (i in 0..gridSize) {
            // Horizontal lines
            shapeRenderer.line(gridStartX, gridStartY + i * cellSize, gridStartX + gridSize * cellSize, gridStartY + i * cellSize)
            // Vertical lines
            shapeRenderer.line(gridStartX + i * cellSize, gridStartY, gridStartX + i * cellSize, gridStartY + gridSize * cellSize)
        }
        shapeRenderer.end()

        drawLabels()
    }

    private fun drawLabels() {
        // Column labels (letters)
        for (i in 0 until gridSize) {
            val label = Label(('A' + i).toString(), skin).apply {
                setFontScale(labelFontScale)
                setPosition(gridStartX + i * cellSize + cellSize / 2f - 10f, gridStartY + gridSize * cellSize + 20f)
            }
            stage.addActor(label)
        }

        // Row labels (numbers)
        for (i in 0 until gridSize) {
            val label = Label((i + 1).toString(), skin).apply {
                setFontScale(labelFontScale)
                setPosition(gridStartX - 70f,  Gdx.graphics.height - (gridStartY + i * cellSize + cellSize / 2f) - 10f)
            }
            stage.addActor(label)
        }
    }

    private fun drawShips() {
        val grid = shipPlacement.getGrid()
        batch.begin()

        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col]) {
                    val x = gridStartX + col * cellSize
                    val y = gridStartY + row * cellSize
                    // Added offsets to overlap texture sides
                    batch.draw(shipTexture, x-2f, y-2f, cellSize+4f, cellSize+4f)
                }
            }
        }
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
        shipTexture.dispose()
        backButtonTexture.dispose()
    }
}
