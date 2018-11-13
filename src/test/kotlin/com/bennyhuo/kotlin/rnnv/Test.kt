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
}

class Foo {
    var bar by releasableNotNull<String>()
}