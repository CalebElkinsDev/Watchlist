package com.elkins.watchlist.network

import android.util.Log
import com.elkins.watchlist.utility.Resource
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class NetworkResponseHandler {

    companion object {
        fun <T : Any> handleSuccess(data: T): Resource<T> {
            return Resource.success(data)
        }

        fun <T : Any> handleException(e: Exception): Resource<T> {
            return when (e) {
                is SocketTimeoutException -> {
                    Log.e("SocketTimeoutException", "NetworkResponseHandler, SocketTimeoutException")
                    Resource.error(Resource.NetworkErrorType.SOCKET_TIMEOUT,"Socket Timeout", null)
                }
                is HttpException -> {
                    Log.e("Http Exception", "NetworkResponseHandler, HTTPException")
                    Resource.error(Resource.NetworkErrorType.HTTP_EXCEPTION, "HTTP Exception", null)
                }
                is UnknownHostException -> {
                    Log.e("Http Exception", "NetworkResponseHandler, UnknownHostException")
                    Resource.error(Resource.NetworkErrorType.UNKNOWN_HOST,"Unknown Host Exception", null)
                }

                else -> {
                    Resource.error(Resource.NetworkErrorType.OTHER,"Other Error; ${e.javaClass.canonicalName}", null)
                }
            }
        }

        fun <T: Any> handleEmptyResults(): Resource<T> {
            return Resource.error(Resource.NetworkErrorType.NO_RESULTS,"No Results", null)
        }
    }
}