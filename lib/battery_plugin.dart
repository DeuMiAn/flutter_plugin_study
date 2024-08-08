import 'package:hello/bettery_charging_state.dart';
import 'package:hello/hello_platform_interface.dart';

class BatteryPlugin {
  Future<String?> getPlatformVersion() {
    return HelloPlatform.instance.getPlatformVersion();
  }

  Future<int> getBatteryLevel() {
    return HelloPlatform.instance.getBatteryLevel();
  }

  Stream<BatteryState> getBatteryChaargingState() {
    return BatteryChargingState().getChargingStateStream();
  }
}
