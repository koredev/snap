package com.jameswoo.snapcamera

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class HandlerElement(val handler: Handler) : AbstractCoroutineContextElement(Key) {
    @Suppress("NOTHING_TO_INLINE")
    companion object Key : CoroutineContext.Key<HandlerElement> {
        inline operator fun invoke(looper: Looper) = HandlerElement(Handler(looper))
        inline operator fun invoke(thread: HandlerThread) = HandlerElement(Handler(thread.looper))
    }
}

fun CoroutineContext.requireHandler(): Handler = this[HandlerElement]?.handler ?: throw IllegalStateException("Required HandlerElement not found in the coroutineContext")