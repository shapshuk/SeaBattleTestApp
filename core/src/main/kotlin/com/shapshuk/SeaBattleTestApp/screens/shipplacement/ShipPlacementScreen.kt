package com.shapshuk.SeaBattleTestApp.screens.shipplacement

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.shapshuk.SeaBattleTestApp.app.SeaBattleTestApp
import com.shapshuk.SeaBattleTestApp.screens.mainmenu.MainMenuScreen
import com.shapshuk.SeaBattleTestApp.util.ShipPlacement
import ktx.app.KtxScreen

class ShipPlacementScreen(private val game: SeaBattleTestApp) : KtxScreen {
    private val stage = Stage()
    private val skin = Skin(Gdx.files.internal("skin/uiskin.json"))
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val shaderBatch = SpriteBatch()

    private val gridSize = 10
    private var cellSize = 0f
    private val gridStartX = 200f
    private val gridStartY = 150f
    private val labelFontScale = 2f
    private val shipPlacement = ShipPlacement(gridSize)

    private val shipTexture = Texture(Gdx.files.internal("ship_square.png"))
    private val backButtonTexture = Texture(Gdx.files.internal("return_button.png"))
    private val backgroundTexture = Texture(Gdx.files.internal("sea_texture.jpg"))
    private val explosionTexture = Texture(Gdx.files.internal("explosion_texture.jpg"))

    private var shaderProgram: ShaderProgram

    private var uTouchPos = Vector2(0f, 0f)
    private var showExplosion = false

    init {
        val vertexShader = Gdx.files.internal("shaders/simple_vertex.glsl").readString()
        val fragmentShader = Gdx.files.internal("shaders/texture_mask_fragment.glsl").readString()

        shaderProgram = ShaderProgram(vertexShader, fragmentShader)

        setupUI()
        shipPlacement.placeShips() // Place ships initially
    }

    private fun setupUI() {
        // Back button
        val backButton = ImageButton(TextureRegionDrawable(backButtonTexture)).apply {
            setSize(80f, 80f)
            setPosition(40f, Gdx.graphics.height - height - 40f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.setScreen<MainMenuScreen>()
                }
            })
        }
        stage.addActor(backButton)

        // Rearrange button
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

        // Touch input listener
        stage.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val gridEndX = gridStartX + gridSize * cellSize
                val gridEndY = gridStartY + gridSize * cellSize

                if (x in gridStartX..gridEndX && y in gridStartY..gridEndY) {
                    showExplosion = true
                    uTouchPos.set(x, y)
                    return true
                }
                return false
            }
        })
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        clearScreen()

        batch.begin()
        batch.draw(backgroundTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()

        if (showExplosion) {
            renderExplosionEffect()
        }

        // Draw grid, labels, and ships
        drawGridAndLabels()
        drawShips()

        stage.act(delta)
        stage.draw()
    }

    private fun clearScreen() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT)
    }

    private fun renderExplosionEffect() {
        shaderBatch.begin()
        shaderBatch.shader = shaderProgram

        backgroundTexture.bind(1)
        shaderProgram.setUniformi("u_texture", 1)

        explosionTexture.bind(0)
        shaderProgram.setUniformi("u_newTexture", 0)

        shaderProgram.setUniformf("u_resolution", Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        shaderProgram.setUniformf("u_radius", 0.1f)

        val normalizedTouchPos = Vector2(uTouchPos.x / Gdx.graphics.width, uTouchPos.y / Gdx.graphics.height)
        shaderProgram.setUniformf("u_touchPos", normalizedTouchPos.x, normalizedTouchPos.y)

        shaderBatch.draw(explosionTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        shaderBatch.end()
    }

    private fun drawGridAndLabels() {
        cellSize = (Gdx.graphics.height - 2 * gridStartY) / gridSize

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.BLACK

        for (i in 0..gridSize) {
            shapeRenderer.line(gridStartX, gridStartY + i * cellSize, gridStartX + gridSize * cellSize, gridStartY + i * cellSize)
            shapeRenderer.line(gridStartX + i * cellSize, gridStartY, gridStartX + i * cellSize, gridStartY + gridSize * cellSize)
        }
        shapeRenderer.end()

        drawLabels()
    }

    private fun drawLabels() {
        for (i in 0 until gridSize) {
            val label = Label(('A' + i).toString(), skin).apply {
                setFontScale(labelFontScale)
                setPosition(gridStartX + i * cellSize + cellSize / 2f - 10f, gridStartY + gridSize * cellSize + 20f)
            }
            stage.addActor(label)
        }

        for (i in 0 until gridSize) {
            val label = Label((i + 1).toString(), skin).apply {
                setFontScale(labelFontScale)
                setPosition(gridStartX - 70f, Gdx.graphics.height - (gridStartY + i * cellSize + cellSize / 2f) - 10f)
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
                    batch.draw(shipTexture, x - 2f, y - 2f, cellSize + 4f, cellSize + 4f)
                }
            }
        }
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        showExplosion = false
    }

    override fun dispose() {
        // Dispose in the correct order to prevent resource leaks
        shaderBatch.dispose()
        batch.dispose()
        shapeRenderer.dispose()
        stage.dispose()

        backgroundTexture.dispose()
        shipTexture.dispose()
        backButtonTexture.dispose()
        explosionTexture.dispose()
        shaderProgram.dispose()
    }
}
