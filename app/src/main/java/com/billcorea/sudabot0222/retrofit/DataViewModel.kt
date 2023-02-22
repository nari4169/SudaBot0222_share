package com.billcorea.sudabot0222.retrofit

import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataViewModel(application: Application) : AndroidViewModel(application) {

    val chatLists = mutableStateListOf<ChatData>()

    var infoText = mutableStateOf( "")

    fun doAppend(sender: String, sendText: String) {
        var chatData = ChatData(id=sender, chatMessage = sendText, convMessage = "")
        chatLists.add(chatData)

        doKoGPTChat(sendText)
    }

    private fun doKoGPTChat(sendText: String) {

        var prompt = ""
        if (infoText.value != "") {
            prompt = "정보:" + infoText.value + "Q:" + sendText
        }

        RetrofitApi.create().doKoGPT(RequestBean(prompt = "$sendText A:", max_tokens = 32, temperature = 0.3, top_p = 0.85))
            .enqueue(object : Callback<ResponseBean> {
                override fun onResponse(
                    call: Call<ResponseBean>,
                    response: Response<ResponseBean>
                ) {
                    val resp = response.body()?.generations
                    if (resp != null) {
                        var respText = ""
                        for (respItem in resp) {
                            respText += respItem.text
                        }
                        var idx = respText.indexOf("Q:")
                        if (respText.indexOf("B:") > -1) idx = respText.indexOf("B:")
                        else if (respText.indexOf("A:") > -1) idx = respText.indexOf("A:")
                        var respSend = respText
                        if (idx > -1) {
                            respSend = respText.substring(0, idx -1)
                        }
                        chatLists.add(ChatData("koGPT", sendText, "$respSend ..."))
                        Log.e("", "append $respText")
                    }
                }

                override fun onFailure(call: Call<ResponseBean>, t: Throwable) {
                    Log.e("", "koGPT error ${t.localizedMessage}")
                }
            })

    }

    fun doClearData() {
        chatLists.clear()
    }
}