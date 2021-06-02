package com.university.exam8.ui.dataLoader

import android.util.Log.d
import android.view.View
import com.university.exam8.ui.interfaces.FutureCallBack
import com.university.exam8.App
import com.university.exam8.R
import com.university.exam8.ui.dataLoader.DataLoader.RetrofitApi.Companion.BASE_URL
import com.university.exam8.ui.tools.Tools
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.http.*
import java.util.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object DataLoader {

    private const val HTTP_200_OK = 200
    private const val HTTP_201_CREATED = 201
    private const val HTTP_400_BAD_REQUEST = 400
    private const val HTTP_401_UNAUTHORIZED = 401
    private const val HTTP_404_NOT_FOUND = 404
    private const val HTTP_500_INTERNAL_SERVER_ERROR = 500
    private const val HTTP_204_NO_CONTENT = 204
    private var retrofit =  Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create()).build()

    private var api = retrofit.create(RetrofitApi::class.java)

    fun postRequest(
        loadingView: View? = null,
        path: String,
        parameters: HashMap<String, String>,
        callback: FutureCallBack<String>
    ) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (Tools.isInternetAvailable()) {
            val call = api.postRequest(path, parameters)
            call.enqueue(baseCallback(loadingView, parameters, callback))
        } else
            callback.error(
                App.instance.getContext().getString(R.string.incorrect_request),
                App.instance.getContext().getString(R.string.no_internet)
            )
    }

    fun postRequestWithMultipartBody(
        loadingView: View? = null,
        path: String,
        files: ArrayList<MultipartBody.Part>,
        parameters: HashMap<String, String>,
        callback: FutureCallBack<String>
    ) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (Tools.isInternetAvailable()) {
            val call = api.postRequestWithMultipartBody(path, files, parameters)
            call.enqueue(baseCallback(loadingView, parameters, callback))
        } else
            callback.error(
                App.instance.getContext().getString(R.string.incorrect_request),
                App.instance.getContext().getString(R.string.no_internet)
            )
    }

    fun getRequest(
        loadingView: View? = null,
        path: String,
        parameters: MutableMap<String, String>? = null,
        callback: FutureCallBack<String>
    ) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (Tools.isInternetAvailable()) {
            val call = api.getRequest(path)
            call.enqueue(baseCallback(loadingView, parameters, callback))
        } else
            callback.error(
                App.instance.getContext().getString(R.string.incorrect_request),
                App.instance.getContext().getString(R.string.no_internet)
            )
    }

    fun getDelete(loadingView: View? = null, path: String, callback: FutureCallBack<String>) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (Tools.isInternetAvailable()) {
            val call = api.getDelete(path)
            call.enqueue(baseCallback(loadingView, null, callback))
        } else
            callback.error(
                App.instance.getContext().getString(R.string.incorrect_request),
                App.instance.getContext().getString(R.string.no_internet)
            )
    }

    fun getDeleteWithBody(
        loadingView: View? = null,
        path: String,
        requestBody: Map<String, @JvmSuppressWildcards RequestBody>,
        callback: FutureCallBack<String>
    ) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (Tools.isInternetAvailable()) {
            val call = api.getDelete(path, requestBody)
            val parameters = mutableMapOf<String, String>()
            for (item in requestBody) {
                parameters[item.key] = item.value.toString()
            }
            call.enqueue(baseCallback(loadingView, parameters, callback))
        } else
            callback.error(
                App.instance.getContext().getString(R.string.incorrect_request),
                App.instance.getContext().getString(R.string.no_internet)
            )
    }

    fun putRequest(
        loadingView: View? = null,
        path: String,
        parameters: MutableMap<String, String>,
        callback: FutureCallBack<String>
    ) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (Tools.isInternetAvailable()) {
            val call = api.putRequest(path, parameters)
            call.enqueue(baseCallback(loadingView, parameters, callback))
        } else
            callback.error(
                App.instance.getContext().getString(R.string.incorrect_request),
                App.instance.getContext().getString(R.string.no_internet)
            )
    }

    fun getRequestWithParameters(
        loadingView: View? = null,
        path: String,
        parameters: MutableMap<String, String>,
        callback: FutureCallBack<String>
    ) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (Tools.isInternetAvailable()) {
            val call = api.getRequestWithParameters(path, parameters)
            call.enqueue(baseCallback(loadingView, null, callback))
        } else
            callback.error(
                App.instance.getContext().getString(R.string.incorrect_request),
                App.instance.getContext().getString(R.string.no_internet)
            )
    }


    fun postRequestWithBody(
        loadingView: View? = null,
        path: String,
        body: RequestBody,
        callback: FutureCallBack<String>
    ) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (Tools.isInternetAvailable()) {
            val call = api.postRequestWithRequestBody(path, body)
            call.enqueue(baseCallback(loadingView, mutableMapOf(), callback))
        } else
            callback.error("", App.instance.getContext().getString(R.string.no_internet))
    }


    private fun baseCallback(
        loadingView: View? = null,
        parameters: MutableMap<String, String>?,
        callback: FutureCallBack<String>
    ): Callback<String> {
        return object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                d("serverResponse", " onFailure " + t.message)
                callback.error(
                    App.instance.getContext().getString(R.string.incorrect_request),
                    App.instance.getContext().getString(R.string.an_error_occurred_please_try_again)
                )
                if (loadingView != null)
                    Tools.viewVisibility(loadingView)
            }

            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                handleResponseCode(loadingView, response, parameters, callback)
            }
        }
    }

    private fun handleResponseCode(
        loadingView: View? = null,
        response: retrofit2.Response<String>,
        parameters: MutableMap<String, String>?,
        callback: FutureCallBack<String>
    ) {
        if (loadingView != null)
            Tools.viewVisibility(loadingView)
        if (response.code() == HTTP_200_OK || response.code() == HTTP_201_CREATED)
            try {
                d("serverResponse", " success " + response.body()!!)
                callback.done(response.body()!!)
            } catch (e: JSONException) {
                callback.error(
                    App.instance.getContext().resources.getString(R.string.incorrect_request),
                    App.instance.getContext().getString(R.string.an_error_occurred_please_try_again)
                )
            }
        else if (response.code() == HTTP_400_BAD_REQUEST)
            handleError(response.errorBody()!!.string(), parameters, callback)
        else if (response.code() == HTTP_401_UNAUTHORIZED) {
            handleError(response.errorBody()!!.string(), parameters, callback)
        } else if (response.code() == HTTP_404_NOT_FOUND)
            handleError(response.errorBody()!!.string(), parameters, callback)
        else if (response.code() == HTTP_500_INTERNAL_SERVER_ERROR)
            handleError(response.errorBody()!!.string(), parameters, callback)
        else if (response.code() == HTTP_204_NO_CONTENT)
            handleError("", parameters, callback)
        else {
            callback.error(
                App.instance.getContext().resources.getString(R.string.incorrect_request),
                App.instance.getContext().getString(R.string.an_error_occurred_please_try_again)
            )
        }
    }

    private fun handleError(
        message: String,
        parameters: MutableMap<String, String>?,
        callback: FutureCallBack<String>
    ) {


    }

    interface RetrofitApi {
        companion object {
            val BASE_URL = "http://139.162.207.17/api/m/v2/"
        }

        @DELETE("{path}")
        fun getDelete(@Path("path") path: String): Call<String>

        @Multipart
        @HTTP(method = "DELETE", path = "{path}", hasBody = true)
        fun getDelete(
            @Path("path") path: String,
            @PartMap parameters: Map<String, @JvmSuppressWildcards RequestBody>
        ): Call<String>

        @FormUrlEncoded
        @POST("{path}")
        fun postRequest(
            @Path("path") path: String,
            @FieldMap parameters: Map<String, String>
        ): Call<String>

        @Multipart
        @POST("{path}")
        fun postRequestWithMultipartBody(
            @Path("path") path: String,
            @Part files: List<MultipartBody.Part>,
            @PartMap parameters: Map<String, String>
        ): Call<String>

        @GET("{path}")
        fun getRequest(@Path("path") path: String): Call<String>

        @GET("{path}")
        fun getRequestWithParameters(
            @Path("path") path: String,
            @QueryMap(encoded = false) parameters: Map<String, String>
        ): Call<String>

        @Multipart
        @PUT("{path}")
        fun putMultipartBody(
            @Path("path") path: String,
            @Part files: List<MultipartBody.Part>,
            @PartMap parameters: Map<String, @JvmSuppressWildcards RequestBody>
        ): Call<String>

        @POST("{path}")
        fun postRequestWithRequestBody(
            @Path("path") path: String,
            @Body body: RequestBody
        ): Call<String>

        @FormUrlEncoded
        @PUT("{path}")
        fun putRequest(
            @Path("path") path: String,
            @FieldMap parameters: Map<String, String>
        ): Call<String>
    }
}