package com.example.hello

import BatteryChargeStatusListener
import BatteryChargeStatusReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.lang.Exception

/** HelloPlugin */
class HelloPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var batteryChargeingStateEventChannel : EventChannel

  private  lateinit var context: Context
  private  var batteryChargeStateReceiver: BatteryChargeStatusReceiver? =null


  /// 플러그인이 Flutter 엔진에 연결될 때 호출됩니다
  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    //context 초기화 및 MethodChannel, EventChannel을 설정
    context=flutterPluginBinding.applicationContext

    //메서드 호출을 처리를 위해
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, METHOD_CHANNEL_NAME)
    channel.setMethodCallHandler(this)

    //이벤트 스트림 처리를 위해
    batteryChargeingStateEventChannel= EventChannel(flutterPluginBinding.binaryMessenger,BATTERY_CHARGING_STATE_EVENT_CHANNEL_NAME)
    batteryChargeingStateEventChannel.setStreamHandler(object : EventChannel.StreamHandler{
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        registerBatteryChargingStateReceiver(events)
      }

      override fun onCancel(arguments: Any?) {
        unRegisterBatteryChargingStateReceiver()
      }
    })
  }

  /// 플러그인이 Flutter 엔진에서 분리될 때 호출
  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    // MethodChannel, EventChannel 핸들러 제거
    channel.setMethodCallHandler(null)
    batteryChargeingStateEventChannel.setStreamHandler(null)
  }

  /// Flutter에서 호출된 메서드를 처리
  override fun onMethodCall(call: MethodCall, result: Result) {
    when(call.method){
      "getPlatformVersion"->result.success("Android 몰루")
      // 배터리 레벨을 반환함
      "getBatteryLevel"->getBatteryLevel(result)
      else->result.notImplemented()
    }
  }

  //배터리 레벨을 가져와서 결과를 반환함
  private fun getBatteryLevel(result: MethodChannel.Result) {
    //안드로이드 API 레벨에 따라 배텉리 정보를 가져오는 방법이틀림
    try{
      //API 레벨 21 이상에서 BatterManager를 사용하고 이하에서는 브로드캐스트 릴시버 사용
      val batteryLevel = if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
        var batteryManager=context.getSystemService(Context.BATTERY_SERVICE)as BatteryManager
        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
      }else {
        var intent= ContextWrapper(context).registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) *100 /intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1)

      }
      result.success(batteryLevel)

    }catch ( e: Exception){
      result.error(e.javaClass.simpleName, e.message, null)
    }

  }




  /// 배터리 충전상태 변경을 수신하기 위해 브로드캐스트 리시버 등록
  private fun registerBatteryChargingStateReceiver(events: EventChannel.EventSink?) {
    //배터리 상태 변경을 감지하고 리스너를 통해 이벤트  전송함
    batteryChargeStateReceiver=BatteryChargeStatusReceiver()

    batteryChargeStateReceiver?.setListener(object : BatteryChargeStatusListener{
      override fun onBatteryStatusChanged(status: String) {
        events?.success(status)
      }
    })
    context.registerReceiver(batteryChargeStateReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
  }

  ///배터리 충전 상태 변경 수신을 중지하기 위해 브로드 캐스트 리시버 해젲
  private fun unRegisterBatteryChargingStateReceiver() {
    batteryChargeStateReceiver?.unregisterListener()
    context.unregisterReceiver(batteryChargeStateReceiver)
    batteryChargeStateReceiver=null
  }


  /// 컴패니언 객체 클래스 정적 상수 정의하는 친구
  companion object{
    private  val TAG: String = HelloPlugin::class.java.simpleName

    private const val METHOD_CHANNEL_NAME="battery_plugin_method_channel"
    private  const val BATTERY_CHARGING_STATE_EVENT_CHANNEL_NAME="battery_charging_state_event_channel_name"
  }

}
