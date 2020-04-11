package com.food.ordering.swaggy.data.local

class Resource<T> {
    @JvmField
    var status = 0
    @JvmField
    var data: T? = null
    var message = ""
    fun loading(): Resource<T> {
        val resource = Resource<T>()
        resource.status = LOADING
        resource.message = "Loading"
        return resource
    }

    fun success(value: T): Resource<T> {
        val resource = Resource<T>()
        resource.status = SUCCESS
        resource.message = "Success"
        resource.data = value
        return resource
    }

    fun error(message: String): Resource<T> {
        val resource = Resource<T>()
        resource.status = ERROR
        resource.message = message
        return resource
    }

    fun offline(): Resource<T> {
        val resource = Resource<T>()
        resource.status = NO_INTERNET
        resource.message = "No Internet Connection"
        return resource
    }

    fun empty(): Resource<*> {
        val resource = Resource<T>()
        resource.status = EMPTY
        return resource
    }

    companion object {
        const val SUCCESS = 0
        const val ERROR = 1
        const val NO_INTERNET = 2
        const val EMPTY = 3
        const val LOADING = 4
    }
}