package com.shapshuk.SeaBattleTestApp.screens.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.shapshuk.SeaBattleTestApp.app.SeaBattleTestApp
import com.shapshuk.SeaBattleTestApp.screens.shipplacement.ShipPlacementScreen
import ktx.app.KtxScreen

class MainMenuScreen(private val game: SeaBattleTestApp) : KtxScreen {
    private val stage: Stage = Stage()
    private val batch = SpriteBatch()
    private val skin = Skin(Gdx.files.internal("skin/uiskin.json")) // Use a default UI skin
    private val labelFontScale = 2f
    private val backgroundTexture = Texture(Gdx.files.internal("sea_texture.jpg"))

    init {
        val button = TextButton("Setup Ships", skin).apply {
            setSize(400f, 100f)
            setPosition(Gdx.graphics.width / 2f - width / 2f, Gdx.graphics.height / 2f)
            label.setFontScale(labelFontScale)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.setScreen<ShipPlacementScreen>()
                }
            })
        }
        stage.addActor(button)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT)

        batch.begin()
        batch.draw(backgroundTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
        batch.dispose()
        backgroundTexture.dispose()
    }
}
