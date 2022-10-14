package com.pujiarto.ppidwebview

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pujiarto.ppidwebview.ui.theme.PpidWebviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PpidWebviewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Content()
                }
            }
        }
    }
}

@Composable
fun Content(){
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Scaffold(
        topBar = { TopAppBar(
            title = {
                Text(
                    "PPID Lombok Barat",
                    color = Color.White
                ) },
            backgroundColor = MaterialTheme.colors.primary,
        ) },
        content = { it.apply {  }
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { isRefreshing = true },
            ) {
                MainContent()
            }
        }
    )
}

@Composable
fun MainContent(){

    var backEnabled by remember { mutableStateOf(false)}
    var webView: WebView? = null
    val visibility = remember { mutableStateOf(false)}
    val url = "https://ppid.lombokbaratkab.go.id/"
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (visibility.value){
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colors.primary,
                backgroundColor = Color.Gray,
                )
        }
        AndroidView(
            modifier = Modifier,
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = object : WebViewClient() {
                        override  fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?){
                            visibility.value = true
                            backEnabled = view.canGoBack()
                        }
                        override fun onPageFinished(view: WebView?, url: String?) {
                            visibility.value = false
                        }
                        //for older android api version <24
                        @Deprecated("Deprecated in Java")
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                            if (url.startsWith("tel:") || url.startsWith("whatsapp:")) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                context.startActivity(intent)
                                return true
                            }
                            return false
                        }
                        //for android api version >24
                        @RequiresApi(Build.VERSION_CODES.N)
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                            val urls = request.url.toString()
                            if (urls.startsWith("tel:") || urls.startsWith("whatsapp:")) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(urls)
                                context.startActivity(intent)
                                return true
                            }
                            return false
                        }
                    }
                    @SuppressLint("SetJavaScriptEnabled")
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                    webView = this
                    webView?.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
                        val request = DownloadManager.Request(Uri.parse(url))
                        request.setMimeType(mimeType)
                        //------------------------COOKIE!!------------------------
                        val cookies = CookieManager.getInstance().getCookie(url)
                        request.addRequestHeader("cookie", cookies)
                        //------------------------COOKIE!!------------------------
                        request.addRequestHeader("User-Agent", userAgent)
                        request.setDescription("Downloading file...")
                        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            URLUtil.guessFileName(url, contentDisposition, mimeType)
                        )
                        val dm = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
                        dm!!.enqueue(request)
                        Toast.makeText(
                            context,
                            "Downloading File",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }, update = {
                webView = it
            })
        BackHandler(enabled = backEnabled) {
            webView?.goBack()
        }
    }
}




