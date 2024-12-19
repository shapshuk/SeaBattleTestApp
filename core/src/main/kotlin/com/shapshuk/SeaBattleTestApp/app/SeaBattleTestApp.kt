package com.shapshuk.SeaBattleTestApp.app

import com.shapshuk.SeaBattleTestApp.screens.mainmenu.MainMenuScreen
import com.shapshuk.SeaBattleTestApp.screens.shipplacement.ShipPlacementScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class SeaBattleTestApp : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(MainMenuScreen(this))
        addScreen(ShipPlacementScreen(this))
        setScreen<MainMenuScreen>()
    }
}
