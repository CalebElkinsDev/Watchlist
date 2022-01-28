package com.elkins.watchlist.network

import com.elkins.watchlist.utility.Resource
import retrofit2.HttpException
import java.net.SocketTimeoutException

open class NetworkResponseHandler {

    companion object {
        fun <T : Any> handleSuccess(data: T): Resource<T> {
            return Resource.success(data)
        }

        fun <T : Any> handleException(e: Exception): Resource<T> {
            return when (e) {
                is SocketTimeoutException -> Resource.error("Socket Timeout", null)
                is HttpException -> Resource.error(msg = e.message(), null)

                else -> Resource.error("Other Error", null)
            }
        }

        fun <T: Any> handleEmptyResults(): Resource<T> {
            return Resource.error("No Results", null)
        }
    }
}