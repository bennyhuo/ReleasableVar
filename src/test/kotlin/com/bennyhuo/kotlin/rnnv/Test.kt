package com.bennyhuo.kotlin.rnnv

import org.junit.Test

class Test {
    @Test
    fun test() {
        val foo = Foo()
        assert(!foo::bar.isInitialized)
        foo.bar = "HelloWorld"
        assert(foo::bar.isInitialized)
        assert(foo.bar == "HelloWorld")
        foo::bar.release()
        assert(!foo::bar.isInitialized)
        try {
            foo.bar
            assert(false)
        } catch (e: Exception) {
            assert(true)
        }
    }

    @Test
    fun testIdentity(){
        val list1 = List(5){
            Foo().apply { bar = "HelloWorld" }
        }
        val foo = Foo()
        foo.bar = "HelloWorld"
        val list2 = List(5){
            Foo().apply { bar = "HelloWorld" }
        }
        foo::bar.release()
        list1.forEach { assert(it::bar.isInitialized) }
        list2.forEach { assert(it::bar.isInitialized) }
    }

    @Test
    fun testWeak(){
        val count = 100
        instantiateFoos(100)
        System.gc()
        System.gc()
        Thread.sleep(5000)
        System.gc()
        System.gc()
        println("Refs: ${releasableRefs.size}")
        assert(releasableRefs.size < count)
    }

    private fun instantiateFoos(count: Int){
        repeat(count){
            Foo().bar = "HelloWorld"
        }
    }
}

class Foo {
    var bar by releasableNotNull<String>()

    private val id = 0

    override fun equals(other: Any?): Boolean {
        return id == (other as? Foo)?.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}