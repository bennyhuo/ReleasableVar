# ReleasableNonNullVars

This is a delegate for those non null vars of which values will be set to null. 

# Use Case

Suppose you have a class called `MainActivity` with a nonnull property `image` of which type is a Bitmap. You want release that bitmap and make it available for gc when `MainActivity` destroyed, but `image` cannot be set to `null`.

```kotlin
class MainActivity: Activity {
    lateinit var image: Bitmap
    
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        image = Bitmap.create(...)
    }
    
    override fun onDestroy(){
        super.onDestroy()
        image.recycle()
        image = null // You cannot do that!!
    }
}
```

Otherwise if you make `image` nullable, the whole bunch of use cases of `image` will be `image?.xxx` with the annoying question mark.

So the `ReleasableNonNull` delegate comes to help.

```kotlin
class MainActivity: Activity {
    var image by releasableNotNull<Bitmap>()
    
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        image = Bitmap.create(...)
    }
    
    override fun onDestroy(){
        super.onDestroy()
        image.recycle()
        ::image.release() // You simply make the backing value null, thus making the gc of this Bitmap instance possible. 
    }
}
```

You can use `::image.isInitialized` to test whether it has been initialized and `::image.release()` to release the backing field.

Works like a charm.

# Use in your project

It has been deployed to jCenter.

```
compile "com.bennyhuo.kotlin:releasable-nonnull-vars:1.0"
```

# Issue

Please feel free to issue and pull request.

# License

[MIT License](LICENSE)