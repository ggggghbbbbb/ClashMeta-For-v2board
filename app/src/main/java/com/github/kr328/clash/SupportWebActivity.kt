package com.github.kr328.clash

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class SupportWebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)
        val html = """
            <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <script type="text/javascript">
                    window.$crisp=[];
                    window.CRISP_WEBSITE_ID="78e887a8-f770-48ad-9087-e526bd06f80c";
                    (function(){
                        d=document;
                        s=d.createElement("script");
                        s.src="https://client.crisp.chat/l.js";
                        s.async=1;
                        d.getElementsByTagName("head")[0].appendChild(s);
                    })();
                </script>
              </head>
              <body>
                <h3>在线客服</h3>
              </body>
            </html>
        """.trimIndent()
        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
    }
}
