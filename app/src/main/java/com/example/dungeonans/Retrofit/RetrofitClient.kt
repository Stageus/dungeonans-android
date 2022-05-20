package com.example.dungeonans.Retrofit

import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.dungeonans.App
import com.example.dungeonans.BuildConfig
import com.example.dungeonans.DataClass.*
import com.example.dungeonans.Utils.API
import com.example.dungeonans.Utils.Constants.TAG
import com.example.dungeonans.Utils.isJsonArray
import com.example.dungeonans.Utils.isJsonObject
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import java.util.logging.Handler

// 싱글턴
object RetrofitClient {
    // 레트로핏 클라이언트 선언
    private var retrofitClient: Retrofit? = null
//    private lateinit var retrofitClient: Retrofit
    // 레트로핏 클라이언트 가져오기
    fun getClient(baseUrl: String): Retrofit? {
        Log.d(TAG, "RetrofitClient - getClient() called")

        // okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        // 로그를 찍기 위해 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger{

            override fun log(message: String) {
//                Log.d(TAG, "RetrofitClient - log() called / message: $message")

                when {
                    message.isJsonObject() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    message.isJsonArray() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    else -> {
                        try {
                            Log.d(TAG, JSONObject(message).toString(4))
                        } catch (e: Exception) {
                            Log.d(TAG, message)
                        }
                    }
                }

            }

        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(loggingInterceptor)


        // 기본 파라매터 인터셉터 설정
        val baseParameterInterceptor : Interceptor = (object : Interceptor{

            override fun intercept(chain: Interceptor.Chain): Response {
                Log.d(TAG, "RetrofitClient - intercept() called")
                // 오리지날 리퀘스트
                val originalRequest = chain.request()

                // 쿼리 파라매터 추가하기
                val addedUrl = originalRequest.url.newBuilder().addQueryParameter("client_id", API.CLIENT_ID).build()

                val finalRequest = originalRequest.newBuilder()
                    .url(addedUrl)
                    .method(originalRequest.method, originalRequest.body)
                    .build()

                val response = chain.proceed(finalRequest)

                if (response.code != 200) {

                    android.os.Handler(Looper.getMainLooper()).post {
                        Toast.makeText(App.instance, "${response.code} 에러 입니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                return response
            }

        })


        // 위에서 설정한 기본파라매터 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(baseParameterInterceptor)

        // 커넥션 타임아웃
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        if(retrofitClient == null){
            // 레트로핏 빌더를 통해 인스턴스 생성
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                // 위에서 설정한 클라이언트로 레트로핏 클라이언트를 설정한다.
                .client(client.build())
                .build()
        }
        return retrofitClient
    }

    fun initClient() : Retrofit {
        val url = BuildConfig.SERVER_URL //서버 주소
        val gson = Gson()                   // 서버와 주고 받을 데이터 형식
        val clientBuilder = OkHttpClient.Builder().build()
        val connection = Retrofit.Builder()
            .baseUrl(url)
            .client(clientBuilder)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return connection
    }

    /** Community Api (worked by KYJ) **/
    interface CommunityApi{
        @GET("/board/community")
        fun getCommunityPost(@Header("start_index") start_index : Int, @Header("post_cnt") post_cnt : Int) : Call<PostData>

        @GET("/board/hot")
        fun getCommunityHotPost(@Header("start_index") start_index : Int, @Header("post_cnt") post_cnt : Int) : Call<PostData>
    }
    /** QnA Api (worked by KYJ) **/
    interface AskApi{
        @GET("/board/qna")
        fun getAskPost(@Query("board_req_format") board_req_format: board_req_format) : Call<PostData>

        @GET("/board/openQna")
        fun getUnAnsweredPost(@Query("board_req_format") board_req_format: board_req_format) : Call<PostData>

        @GET("/board/closedQna")
        fun getAnsweredPost(@Query("board_req_format") board_req_format: board_req_format) : Call<PostData>
    }
    /** QnA Api (worked by KYJ) **/

    /** Blog Api (worked by KYJ) **/
    interface BlogApi{
        @GET("/board/blog")
        fun sendBoardReq(@Query("board_req_format") board_req_format: board_req_format) : Call<PostData>
    }

    /** Comment Api (worked by KYJ) **/
    interface CommentApi{
        @GET("/comment")
        fun getComment(@Query("posting_index") posting_index: Int) : Call<Comment>

        @POST("/comment")
        fun postComment(@Header("auth") token: String?, @Body() comment_format_req : put_comment_req) : Call<NoneData>

        @DELETE("/comment")
        fun deleteComment(@Path("comment_index") comment_index : Int) : Call<NoneData>
    }

    interface ProfileApi{
        @GET("/profile")
        fun getMyProfile(@Header("auth") token : String) : Call<ProfileData>
    }

    //조수민
    interface SendQnAPostApi{
        @POST("/board")
        fun sendBoardReq(@Header("auth") token : String, @Body() board_ask_format: board_ask_format) : Call<AskPostResponse>
    }

    interface GetSpecificPostApi{
        @GET("/board")
        fun getPost(@Query("posting_index") posting_index : Int) : Call<ClickedPostData>
    }

    interface GetCommunityByTagApi{
        @POST("/board/commTag")
        fun getPost(@Body() board_req_format: board_req_format) : Call<ClickedPostData>
    }

    /** Login Api (worked by KJY) **/
    interface LoginApi {
        @POST("/account/login")
        fun postLogin(
            @Body loginData : LoginData
        ) :Call<LoginResponse>

        @GET("/logout")
        fun getLogout(
            @Header("auth") token: String
        ) :Call<LoginResponse>

        @POST("/account")
        fun postAccount(
            @Body registerData : RegisterData
        ) :Call<NoneData>
    }

    /** Search Api (worked by KJY) **/
    interface SearchApi {
        @POST("/search/community")
        fun postSearchCommunity(
            @Body query : QueryData
        ) : Call<PostData>

        @POST("/search/qna")
        fun postSearchAsk(
            @Body word : String
        ) : Call<PostData>

        @POST("/search/blog")
        fun postSearchBlog(
            @Body word : String
        ) : Call<PostData>

        @POST("/search/profile")
        fun postSearchProfile(
            @Body word : String
        ) : Call<PostData>
    }
}