package com.example.dungeonans.Activity

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission_group.CAMERA
import android.R.attr
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.text.*
import android.webkit.*
import android.widget.*
import android.util.DisplayMetrics
import android.R.string.no
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import android.webkit.WebView
import androidx.core.widget.addTextChangedListener
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.webkit.ValueCallback
import android.widget.Toast

import android.content.ActivityNotFoundException
import android.database.Cursor
import android.hardware.SensorPrivacyManager.Sensors.CAMERA
import android.media.MediaRecorder.VideoSource.CAMERA
import android.os.Environment.getExternalStorageDirectory
import android.webkit.WebChromeClient.FileChooserParams
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.app.ActivityCompat.startActivityForResult
import java.lang.Exception
import java.security.Permissions
import java.text.SimpleDateFormat
import java.util.*
import android.webkit.JavascriptInterface
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toUri
import java.io.*
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.core.content.ContentProviderCompat.requireContext
import kotlin.collections.ArrayList
import android.annotation.SuppressLint
import android.content.CursorLoader

import android.provider.DocumentsContract
import android.R.attr.data
import android.R.attr.visibility
import android.opengl.Visibility

import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dungeonans.Adapter.CommunityRVAdapter
import com.example.dungeonans.DataClass.*
import com.example.dungeonans.R
import com.example.dungeonans.Retrofit.RetrofitClient
import com.example.dungeonans.Space.LinearSpacingItemDecoration
import com.example.dungeonans.Utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import kotlin.collections.HashMap


//코드 블록 사진 생길때, 없애고 webview 할때, edittext 할때, 처음 코드 블록이 생성됐을 때 index를 받아서 거따가 insert, delete 하기!



class AskPostActivity : AppCompatActivity() {

    var cameraPath = ""
    var mWebViewImageUpload: ValueCallback<Array<Uri>>? = null
    var width: Float = 0.0f
    var islanBtnClicked = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_ask_post)

        var body_webview = findViewById<WebView>(R.id.body_webview)
        body_webview.settings.javaScriptEnabled = true
        body_webview.settings.domStorageEnabled = true
        body_webview.settings.allowContentAccess = true
        body_webview.settings.allowFileAccess = true
        var hiddenlinear = findViewById<LinearLayout>(R.id.hiddenlinear)
        var linear = findViewById<WebView>(R.id.body_webview)
        var imageButton = findViewById<ImageButton>(R.id.mybutton_image)
        var chartButton = findViewById<ImageButton>(R.id.mybutton_chart)
        var lanButton = findViewById<ImageButton>(R.id.mybutton_lan)
        var CodeButton = findViewById<ImageButton>(R.id.mybutton_code)

        lanButton.setOnClickListener(){
            if (islanBtnClicked == 0){
                lanButton.setBackgroundResource(R.drawable.aboutlan_clicked_icon)
                hiddenlinear.visibility= View.VISIBLE
                islanBtnClicked = 1
            }
            else if (islanBtnClicked == 1){
                lanButton.setBackgroundResource(R.drawable.aboutlan_icon)
                hiddenlinear.visibility= View.INVISIBLE
                islanBtnClicked = 0
            }
        }


        var weburl = "file:///android_asset/auto_highlight.html"

        class WebBrideg(private val mContext: Context) {
            @JavascriptInterface
            fun getmywidth(): Float {

                width = linear.width.toFloat()
                return width
            }

            @JavascriptInterface
            fun showToast(code: String) {
                Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show()
            }

            @JavascriptInterface
            fun getwidth(px: Float): Float {
                var resources = resources
                val metrics: DisplayMetrics = resources.getDisplayMetrics()
                val dp = px * (DisplayMetrics.DENSITY_DEFAULT / metrics.densityDpi.toFloat())
                return dp
            }

            @JavascriptInterface
            fun getText(innerHTML: String, changeText: String) {
                var myString = innerHTML
                var myText = changeText
                Log.d("qwe", myString)
                Log.d("asdf", myText)

            }

            @JavascriptInterface
            fun getHtml(html: String) {
                //위 자바스크립트가 호출되면 여기로 html이 반환됨
                var source = html
                Log.e("html: ", source)

            }

            @JavascriptInterface
            fun gettest() {
                Toast.makeText(mContext, "Asdfasdf", Toast.LENGTH_SHORT).show()
            }

            @JavascriptInterface
            fun getTitle() : String{
                var getedit = findViewById<EditText>(R.id.title_edittext)
                var mytext = getedit.text.toString()
                return getedit.text.toString()
            }

            @JavascriptInterface
            fun SendPostToApi(title : String ,content : String){

                var retrofit = RetrofitClient.initClient()
                var data = board_ask_format("2",title,content,"",
                    language_tag(true,true,true,true,
                            true,true,true,true,true,true)
                    )
                var sendPostToApi = retrofit.create(RetrofitClient.SendQnAPostApi::class.java)
                sendPostToApi.sendBoardReq(PrefManager.getUserToken(),data).enqueue(object : Callback<AskPostResponse>{
                    override fun onFailure(call: Call<AskPostResponse>, t: Throwable) {
                        Toast.makeText(this@AskPostActivity,"서버 연결이 불안정합니다",Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<AskPostResponse>,
                        response: Response<AskPostResponse>
                    ) {
                        Log.d("qwer",title)
                        Log.d("zxcv",content)
                        Toast.makeText(mContext, "API 시작!", Toast.LENGTH_SHORT).show()
                        Log.d("TAG" , "response success : ${response.body()?.success}")
                        Log.d("TAG" , "errmsg : ${response.body()?.errmsg}")
                    }
                })

            }


        }

        body_webview.addJavascriptInterface(WebBrideg(this), "Android2")
        body_webview.setWebViewClient(WebViewClient())

        body_webview?.apply {
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    body_webview.loadUrl("javascript:myupdate2()")

                }
            }
        }


        fun createImageFile(): File? {
            val timeStap = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "img_" + timeStap + "_"
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(imageFileName, ".jpg", storageDir)
        }

        var launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    if (intent == null) {
                        val results = arrayOf(Uri.parse(cameraPath))

                        mWebViewImageUpload!!.onReceiveValue(results!!)
                    } else {
                        val results = intent!!.data!!
                        mWebViewImageUpload!!.onReceiveValue(arrayOf(results!!))

                        val inputStream = getFullPathFromUri(this,results)
                        body_webview.loadUrl("javascript:updateImage("+'"'+inputStream.toString()+'"'+")")

                    }
                } else {
                    mWebViewImageUpload!!.onReceiveValue(null)
                    mWebViewImageUpload = null
                }
            }

        body_webview.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {

                try {
                    mWebViewImageUpload = filePathCallback!!
                    var takePictureIntent: Intent?
                    takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        var photoFile: File?
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", photoFile.toString())

                        if (photoFile != null) {
                            cameraPath = "file:${photoFile.absolutePath}"
                            takePictureIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile)
                            )
                        } else {
                            takePictureIntent = null
                        }

                    }
                    val contentSelectionIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    contentSelectionIntent.type = "image/*"
                    var intentArray: Array<Intent?>

                    if (takePictureIntent != null) {
                        intentArray = arrayOf(takePictureIntent)
                        Log.d("take", intentArray.toString())
                    } else {
                        intentArray = takePictureIntent?.get(0)!!
                    }

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "조수민진짜개멋있다")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    launcher.launch(chooserIntent)
                } catch (e: Exception) {

                }
                return true
            }

        }

        body_webview.loadUrl(weburl)


    }




//    interface GetQnAPostApi{
//        @POST("/board/qna")
//        fun sendBoardReq(@Body() board_req_format: board_req_format) : Call<QnAPostData>
//    }


}

fun getFullPathFromUri(ctx: Context, fileUri: Uri?): String? {
    var fullPath: String? = null
    val column = "_data"
    var cursor = ctx.contentResolver.query(fileUri!!, null, null, null, null)
    if (cursor != null) {
        cursor.moveToFirst()
        var document_id = cursor.getString(0)
        if (document_id == null) {
            for (i in 0 until cursor.columnCount) {
                if (column.equals(cursor.getColumnName(i), ignoreCase = true)) {
                    fullPath = cursor.getString(i)
                    break
                }
            }
        } else {
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
            cursor.close()
            val projection = arrayOf(column)
            try {
                cursor = ctx.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
                )
                if (cursor != null) {
                    cursor.moveToFirst()
                    fullPath = cursor.getString(cursor.getColumnIndexOrThrow(column))
                }
            } finally {
                if (cursor != null) cursor.close()
            }
        }
    }
    return fullPath
}




















