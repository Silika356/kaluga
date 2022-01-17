package ru.avem.kspem.communication.model

abstract class DeviceController : IDeviceController {
    override var isResponding: Boolean = true
}
