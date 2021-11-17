package com.example.okhttpdemo

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    //首先创建OkHttpClient实例对象    为请求器 请求工具
    private lateinit var okHttpClient: OkHttpClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        okHttpClient = OkHttpClient()
        getbutton.setOnClickListener {
            getSync()
        }
        getbutton2.setOnClickListener {
            getAsync()
        }
        postbutton.setOnClickListener {
            postSync()
        }
        postbutton2.setOnClickListener {
            postAsunc()
        }
    }
    //get同步请求
    fun getSync(){
        object : Thread(){
            //同步请求需单开子线程
            override fun run() {
                //代表请求对象 Request进行封装请求数据
                //get请求参数加在url之后
                val request = Request.Builder().url("https://httpbin.org/get?a=100").build()
                //将实例对象交给请求器，通过call方法将其传入进请求器
                //准备好请求的Call对象
                val call = okHttpClient.newCall(request)
                //进行请求
                //response 是获得请求之后的服务器响应数据
                //会阻塞在此
                val response = call.execute()
                //                       响应体字符串数据
                Log.i("getSync:", response.body!!.string())
            }
        }.start()
    }
    //get异步请求
    fun getAsync(){
        //代表请求对象 Request进行封装请求数据
        //get请求参数加在url之后
        val request = Request.Builder().url("https://httpbin.org/get?a=100&b=sb").build()
        //将实例对象交给请求器，通过call方法将其传入进请求器
        //准备好请求的Call对象
        val call = okHttpClient.newCall(request)
        //异步请求需要传递回调对象给enqueue方法
        //enqueue 内部会创建子线程
        call.enqueue(object : Callback{
            //服务器请求失败
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            //服务器请求成功
            override fun onResponse(call: Call, response: Response) {
                //请求成功不一定能交互成功
                //所以要判断一下是否属于返回的正确请求码
                if(response.isSuccessful){
                    Log.i("GetAsync",response.body!!.string())
                }
            }
        })
    }
    //post同步请求
    fun  postSync() {
        //post的请求必须放入请求体
        //form表单形式提交我们的请求体
        object : Thread() {
            override fun run() {

                val postBody = """
            |Releases
        |--------
        |
        | * _1.0_ May 6, 2013
        | * _1.1_ June 15, 2013
        | * _1.2_ August 11, 2013
        """.trimIndent()

                val request = Request.Builder().url("https://httpbin.org/post")
                        //                  请求体的数据编码格式
                    .post(postBody.toRequestBody(MEDIA_TYPE_MARKDOWN)).build()
                //进行请求
                //response 是获得请求之后的服务器响应数据
                //传入请求体准备请求         会阻塞在此
                okHttpClient.newCall(request).execute().use {
                    if (!it.isSuccessful)throw IOException("Unexpected code $it")
                    Log.i("POSTSync:",it.body!!.string())
                }
            }
        }.start()
    }
    companion object {
        val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaType()
    }
    //post异步请求
    fun postAsunc(){
        val postBody = """
            |Releases
        |--------
        |
        | * _1.0_ May 6, 2013
        | * _1.1_ June 15, 2013
        | * _1.2_ August 11, 2013
        """.trimIndent()
        val request = Request.Builder().url("https://httpbin.org/post")
            .post(postBody.toRequestBody(MEDIA_TYPE_MARKDOWN)).build()
        val call = okHttpClient.newCall(request)
        //异步请求需要传递回调对象给enqueue方法
        //enqueue 内部会创建子线程
        call.enqueue(object : Callback{
            //服务器请求失败
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            //服务器请求成功
            override fun onResponse(call: Call, response: Response) {
                //请求成功不一定能交互成功+
                //所以要判断一下是否属于返回的正确请求码
                if(response.isSuccessful){
                    Log.i("PostAsync",response.body!!.string())
                }
            }
        })
    }
}