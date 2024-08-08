import 'dart:async';
import 'package:flutter/material.dart';
import 'package:hello/battery_plugin.dart';
import 'package:rxdart/rxdart.dart';

class BatteryPluginScreen extends StatefulWidget {
  const BatteryPluginScreen({super.key});

  static const String routeName = "/battery_plugin_screen";

  @override
  State<BatteryPluginScreen> createState() => _BatteryPluginScreenState();
}

class _BatteryPluginScreenState extends State<BatteryPluginScreen> {
  int _batteryPercent = -1;
  String _batteryStatus = 'none';
  String _batteryLevelStreamSubscribeText = 'subscribe';

  StreamSubscription? _batteryStateSubscription;

  void _getBatteryLevel() async {
    _batteryPercent = await BatteryPlugin().getBatteryLevel();
    setState(() {});
  }

  void _subscribeToBatteryChargingState() async {
    if (_batteryStateSubscription != null) {
      _batteryStateSubscription?.cancel();
      setState(() {
        _batteryStatus = 'none';
        _batteryLevelStreamSubscribeText = 'subscribe';
      });
      return;
    }
    _batteryStateSubscription =
        BatteryPlugin().getBatteryChaargingState().doOnCancel(() {
      _batteryStateSubscription = null;
    }).listen((event) {
      _batteryStatus = event.name;
      _batteryLevelStreamSubscribeText = 'stop';
      setState(() {});
    }, onError: (e) {
      setState(() {
        _batteryStatus = 'subscribe error: $e';
        _batteryLevelStreamSubscribeText = 'subscribe';
      });
    }, onDone: () {
      _batteryLevelStreamSubscribeText = 'subscribe';
    }, cancelOnError: true);
  }

  @override
  void dispose() {
    _batteryStateSubscription?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      appBar: AppBar(
        leading: IconButton(
          onPressed: () {},
          icon: const Icon(Icons.arrow_back),
        ),
        title: const Text('Battery Plugin'),
      ),
      body: SafeArea(
          child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          const SizedBox(
            height: 8,
          ),
          Text(
            '배터리 퍼센트 : $_batteryPercent',
            style: const TextStyle(fontSize: 16),
          ),
          TextButton(
            onPressed: _getBatteryLevel,
            child: const Text('배터리 용량 조회하기'),
          ),
          Text(
            '배터리 상태 : $_batteryStatus',
            style: const TextStyle(fontSize: 16),
          ),
          TextButton(
            onPressed: _subscribeToBatteryChargingState,
            child: Text(_batteryLevelStreamSubscribeText),
          ),
        ],
      )),
    );
  }
}
