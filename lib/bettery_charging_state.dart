import 'package:flutter/services.dart';

enum BatteryState {
  unknown,
  charging,
  discharging,
  notCharging,
  full,
}

class BatteryChargingState {
  static final BatteryChargingState _instance =
      BatteryChargingState._internal();
  factory BatteryChargingState() => _instance;

  BatteryChargingState._internal();

  final EventChannel _eventChannel =
      const EventChannel('battery_charging_state_event_channel_name');

  Stream<BatteryState> getChargingStateStream() {
    return _eventChannel
        .receiveBroadcastStream()
        .map((event) => _parseBatteryChargingState(event));
  }
}

BatteryState _parseBatteryChargingState(String event) {
  switch (event) {
    case 'full':
      return BatteryState.unknown;
    case 'charging':
      return BatteryState.charging;
    case 'discharging':
      return BatteryState.discharging;
    default:
      return BatteryState.unknown;
  }
}
