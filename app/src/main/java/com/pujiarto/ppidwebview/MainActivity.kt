package com.pujiarto.ppidwebview

import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pujiarto.ppidwebview.ui.theme.PpidWebviewTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PpidWebviewTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MainContent() {

    var backEnabled by remember { mutableStateOf(false)}
    var webView: WebView? = null

    val url = "http://ppidbaru.lombokbaratkab.go.id/"

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
                    backEnabled = view.canGoBack()
                }
            }
                settings.javaScriptEnabled = true

                loadUrl(url)
                webView = this
        }
    }, update = {
        webView = it
    })

    BackHandler(enabled = backEnabled) {
        webView?.goBack()
    }

}
