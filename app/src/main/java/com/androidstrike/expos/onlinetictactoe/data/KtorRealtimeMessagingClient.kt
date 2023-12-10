package com.androidstrike.expos.onlinetictactoe.data

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
* This class will be the actual implementation of the communication between our application and the socket backend
* It will override the functions defined in the RealtimeMessagingClient interface
* We will also ensure that we pass the client (user making the request) to the implementation
* */
class KtorRealtimeMessagingClient(
    private val client: HttpClient
): RealtimeMessagingClient {

    //reference for the current connection session
    private var session: WebSocketSession? = null
    override fun getGameStateStream(): Flow<GameState> {
        return flow {
            /*
            * In this return block,
            * we will fetch the updated game state by using the client in the parameter to connect to the web socket session,
            * and then parse the response (in JSON format) to the GameState data model class
            * */
            session = client.webSocketSession {
                url("ws://192.168.43.181") //the url is the address that all players will have to be connected to
            }
            //the below assignment will be automatically triggered whenever there is a change from the server
            // and map the JSON to the GameState
            val gameStates = session!! //use the session...
                .incoming //...to get all incoming data (messages) from the server...
                .consumeAsFlow() //indicate to use them in 'stream' (e dy play e dy show)...
                .filterIsInstance<Frame.Text>() //filter to get just Text frames (because that's what was defined at the backend)
                .mapNotNull { Json.decodeFromString<GameState>(it.readText()) } //decode the JSON object being received to the GameState model class

            //return the fetched game state in the flow
            emitAll(gameStates)
        }
    }

    /*
    * This function handles the user sending data to the server
    * It takes a parameter of the MakeTurn model class, which contains the value and posiiton the user wants to play on the board
    * */
    override suspend fun sendAction(action: MakeTurn) {
        // use the session to send an outgoing message text
        //pass the outgoing message in the same expected format from the backend
        session?.outgoing?.send(
            Frame.Text("make_turn#${Json.encodeToString(action)}")
        )
    }

    override suspend fun close() {
        session?.close()
        session = null
    }
}