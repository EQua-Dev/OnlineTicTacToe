package com.androidstrike.expos.onlinetictactoe.data

import kotlinx.coroutines.flow.Flow

/*
* This interface handles the communication between our application and the socket backend
* */
interface RealtimeMessagingClient {
    // This function will keep constant check of the online game state and whenever there is a change in the game state, it updates ours
    fun getGameStateStream() : Flow<GameState>
    //This function will enable the user tp send actions over to the socket backend server
    suspend fun sendAction (action: MakeTurn)
    //This function will handle the user's request to close the connection
    suspend fun close()
}