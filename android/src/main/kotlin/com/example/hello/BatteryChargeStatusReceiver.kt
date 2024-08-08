import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

interface BatteryChargeStatusListener{
    fun onBatteryStatusChanged(status: String)

}


class BatteryChargeStatusReceiver : BroadcastReceiver() {

    private var callback: BatteryChargeStatusListener? = null

    fun setListener(callback: BatteryChargeStatusListener) {
        this.callback=callback;

    }

    fun unregisterListener() {
        this.callback = null
    }
    override fun onReceive(context: Context, intent: Intent) {
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        when (status){
            BatteryManager.BATTERY_STATUS_CHARGING -> callback?.onBatteryStatusChanged("charging")
            BatteryManager.BATTERY_STATUS_FULL -> callback?.onBatteryStatusChanged("full")
            BatteryManager.BATTERY_STATUS_DISCHARGING -> callback?.onBatteryStatusChanged("discharging")
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> callback?.onBatteryStatusChanged("not Charging")
            else -> callback?.onBatteryStatusChanged("unknown")    
        }
    }        
}   