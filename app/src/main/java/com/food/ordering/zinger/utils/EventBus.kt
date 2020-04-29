package com.food.ordering.zinger.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map

object EventBus {
    @ExperimentalCoroutinesApi
    val bus: BroadcastChannel<Any> = BroadcastChannel(1)

    @ExperimentalCoroutinesApi
    fun send(o: Any) {
        CoroutineScope(Dispatchers.IO).launch {
            bus.send(o)
        }
    }

    @ExperimentalCoroutinesApi
    inline fun <reified T> asChannel(): ReceiveChannel<T> {
        return bus.openSubscription().filter { it is T }.map { it as T }
    }
}