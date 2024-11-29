package com.dieyteixeira.componentes.ui.elements.games.game_memory

import android.content.Context
import android.content.SharedPreferences

fun saveOnePlayerRecord(
    context: Context,
    winnerName: String,
    gameMoves: Int,
    gameGrid: String,
    gameName: String
) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Cria a chave
    val recordMovesKey = "${gameName}_${gameGrid}_record_moves"
    val recordPlayerKey = "${gameName}_${gameGrid}_record_player"

    // Recupera o recorde atual de movimentos
    val currentRecordMoves = sharedPreferences.getInt(recordMovesKey, Int.MAX_VALUE)

    // Verifica se o novo número de movimentos é menor que o recorde atual
    if (gameMoves > 0 && gameMoves < currentRecordMoves) {
        // Salva o novo recorde de movimentos
        editor.putInt(recordMovesKey, gameMoves)
        // Salva o nome do jogador que alcançou o recorde
        editor.putString(recordPlayerKey, winnerName)
    }

    // Aplica as mudanças
    editor.apply()
}

fun getOnePlayerRecord(
    context: Context,
    gameGrid: String,
    gameName: String
): Pair<Int, String?> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)

    // Cria as chaves
    val recordMovesKey = "${gameName}_${gameGrid}_record_moves"
    val recordPlayerKey = "${gameName}_${gameGrid}_record_player"

    // Recupera os dados
    val recordMoves = sharedPreferences.getInt(recordMovesKey, 0) // Valor padrão: Int.MAX_VALUE (se nenhum recorde)
    val recordPlayer = sharedPreferences.getString(recordPlayerKey, "-") // Valor padrão: "No record"

    // Retorna o recorde como um par de valores (movimentos, jogador)
    return Pair(recordMoves, recordPlayer)
}

fun clearOnePlayerRecord(context: Context, gameGrid: String, gameName: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Cria as chaves para os registros
    val recordMovesKey = "${gameName}_${gameGrid}_record_moves"
    val recordPlayerKey = "${gameName}_${gameGrid}_record_player"

    // Remove as chaves de recorde
    editor.remove(recordMovesKey)
    editor.remove(recordPlayerKey)

    // Aplica as mudanças
    editor.apply()
}

fun saveTwoPlayersVictory(
    context: Context,
    winnerName: String,
    opponentName: String,
    gameName: String,
) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Cria a chave
    val key = "${gameName}_${winnerName}vs${opponentName}"

    // Recupera o número de vitórias do jogador e incrementa
    val currentWins = sharedPreferences.getInt(key, 0)
    val newWins = currentWins + 1

    // Salva o novo número de vitórias
    editor.putInt(key, newWins)
    editor.apply()
}

fun getTwoPlayersVictories(
    context: Context,
    winnerName: String,
    opponentName: String,
    gameName: String,
): Int {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)

    // Cria a chave
    val key = "${gameName}_${winnerName}vs${opponentName}"

    // Recupera o número de vitórias
    return sharedPreferences.getInt(key, 0)
}