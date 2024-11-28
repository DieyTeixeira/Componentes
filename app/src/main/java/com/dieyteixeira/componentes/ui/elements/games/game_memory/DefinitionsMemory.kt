package com.dieyteixeira.componentes.ui.elements.games.game_memory

enum class GameMode(val displayName: String) {
    OnePlayer("1 Player"),
    TwoPlayers("2 Players")
}

data class GridSize(val rows: Int, val columns: Int)

data class GameConfig(val gridSize: GridSize, val gameMode: GameMode)

data class MemoryGameState(
    val grid: List<Int>,
    val revealed: MutableList<Boolean>,
    val matched: MutableList<Boolean>,
    var firstChoice: Int?,
    var secondChoice: Int?,
    var movesPlayer: Int,
    var currentPlayer: Int,
    val player1Name: String,
    val player2Name: String,
    val rows: Int,
    val columns: Int,
    val matchedPairs: MutableMap<Int, Int>
)