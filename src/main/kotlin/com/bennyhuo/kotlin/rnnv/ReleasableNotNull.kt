package com.bennyhuo.kotlin.rnnv

import java.util.*
import kotlin.collections.HashMap
import kotlin.jvm.internal.*
import kotlin.properties.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

fun <T : Any> releasableNotNull() = ReleasableNotNull<T>()

internal lateinit var releasableRefs: WeakIdentityMap<Any, MutableMap<String, ReleasableNotNull<*>>>

private val isReflectionEnabled by lazy {
            try {
                Class.forName("kotlin.reflect.jvm.internal.KClassImpl")
                println("Use Reflect.")
                true
            } catch (e: Exception) {
                releasableRefs = WeakIdentityMap()
                false
            }
        }

class ReleasableNotNull<T : Any> : ReadWriteProperty<Any, T> {

    private var value: T? = null

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (!isReflectionEnabled && this.value == null) {
            var map = releasableRefs[thisRef]
            if(map == null){
                map = HashMap()
                releasableRefs[thisRef] = map
            }
            map[property.name] = this
        }
        this.value = value
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("Not Initialized or released already.")
    }

    fun isInitialized() = value != null

    fun release() {
        value = null
    }
}

val <R> KProperty0<R>.isInitialized: Boolean
    get() {
        if (isReflectionEnabled) {
            isAccessible = true
            return (getDelegate() as? ReleasableNotNull<*>)?.isInitialized()
                ?: throw IllegalAccessException("Delegate is null or is not an instance of ReleasableNotNull.")
        }
        return (this as? CallableReference)?.let {
            releasableRefs[it.boundReceiver]?.get(this.name)?.isInitialized()
        } ?: false
    }

fun <R> KProperty0<R>.release() {
    if (isReflectionEnabled) {
        isAccessible = true
        return (getDelegate() as? ReleasableNotNull<*>)?.release()
            ?: throw IllegalAccessException("Delegate is null or is not an instance of ReleasableNotNull.")
    }
    (this as? CallableReference)?.let {
        releasableRefs[it.boundReceiver]?.get(this.name)?.release()
    }
}