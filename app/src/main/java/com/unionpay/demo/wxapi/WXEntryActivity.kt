package com.unionpay.demo.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessView
import com.tencent.mm.opensdk.modelbiz.WXOpenBusinessWebview
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.unionpay.demo.bean.Constant

/**
 * 微信回调
 */
class WXEntryActivity : Activity(), IWXAPIEventHandler {
    private var api: IWXAPI? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false)
        try {
            val intent = intent
            api?.handleIntent(intent, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent")
        setIntent(intent)
        api!!.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq) {
        Log.d(TAG, "onReq $req")
        when (req.type) {
            ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX -> goToGetMsg()
            ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX -> goToShowMsg(req as ShowMessageFromWX.Req)
            else -> {
            }
        }
        finish()
    }

    override fun onResp(resp: BaseResp) {
        val result = 0
        Log.d(TAG, "onResp ${resp.errCode},${resp.errStr} resp" )
        if (resp.type == ConstantsAPI.COMMAND_SUBSCRIBE_MESSAGE) {
            val subscribeMsgResp = resp as SubscribeMessage.Resp
            val text = String.format(
                "openid=%s\ntemplate_id=%s\nscene=%d\naction=%s\nreserved=%s",
                subscribeMsgResp.openId,
                subscribeMsgResp.templateID,
                subscribeMsgResp.scene,
                subscribeMsgResp.action,
                subscribeMsgResp.reserved
            )
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
        if (resp.type == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            val launchMiniProgramResp = resp as WXLaunchMiniProgram.Resp
            val text = String.format(
                "openid=%s\nextMsg=%s\nerrStr=%s",
                launchMiniProgramResp.openId,
                launchMiniProgramResp.extMsg,
                launchMiniProgramResp.errStr
            )
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
        if (resp.type == ConstantsAPI.COMMAND_OPEN_BUSINESS_VIEW) {
            val launchMiniProgramResp = resp as WXOpenBusinessView.Resp
            val text = String.format(
                "openid=%s\nextMsg=%s\nerrStr=%s\nbusinessType=%s",
                launchMiniProgramResp.openId,
                launchMiniProgramResp.extMsg,
                launchMiniProgramResp.errStr,
                launchMiniProgramResp.businessType
            )
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
        if (resp.type == ConstantsAPI.COMMAND_OPEN_BUSINESS_WEBVIEW) {
            val response = resp as WXOpenBusinessWebview.Resp
            val text = String.format(
                "businessType=%d\nresultInfo=%s\nret=%d",
                response.businessType,
                response.resultInfo,
                response.errCode
            )
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
        finish()
    }

    private fun goToGetMsg() {
        Log.d(TAG, "goToGetMsg")
        finish()
    }

    private fun goToShowMsg(showReq: ShowMessageFromWX.Req) {
        val wxMsg = showReq.message
        val obj = wxMsg.mediaObject as WXAppExtendObject
        val msg = StringBuffer()
        msg.append("description: ")
        msg.append(wxMsg.description)
        msg.append("\n")
        msg.append("extInfo: ")
        msg.append(obj.extInfo)
        msg.append("\n")
        msg.append("filePath: ")
        msg.append(obj.filePath)
        Log.d(TAG, "goToShowMsg")
        finish()
    }

    companion object {
        private const val TAG = "WXEntryActivity"
    }
}