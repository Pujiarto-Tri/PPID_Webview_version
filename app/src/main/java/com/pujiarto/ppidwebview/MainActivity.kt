package com.pujiarto.ppidwebview

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
    Scaffold(
        topBar = { TopAppBar(title = { Text("PPID Lombok Barat", color = Color.White) }, backgroundColor = MaterialTheme.colors.primary) },
        content = { MainContent() }
    )
}

@Composable
fun MainContent(){

    var backEnabled by remember { mutableStateOf(false)}
    var webView: WebView? = null

    val visibility = remember { mutableStateOf(true)}

    val url = "https://ppid.lombokbaratkab.go.id/"

    if (visibility.value){
        CircularProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            strokeWidth = 4.dp
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
            }
                @SuppressLint("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                loadUrl(url)
                webView = this
                visibility.value = false

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




