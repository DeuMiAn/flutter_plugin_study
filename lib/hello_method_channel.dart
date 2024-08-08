import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'hello_platform_interface.dart';

/// An implementation of [HelloPlatform] that uses method channels.
class MethodChannelHello extends HelloPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('battery_plugin_method_channel');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<int> getBatteryLevel() async {
    // FlutterEngin으로 부터  getBatteryLevel에 값을 int로 넘겨받기
    final level = await methodChannel.invokeMethod('getBatteryLevel');
    return level!;
  }
}
