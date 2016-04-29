package com.yo.interfaces;

public interface DiscoveryInterface {
	public boolean Start(ConnectedDeviceAvailableInterface device, UIThreadAccessInterface uiaccess, ResultNotificationInterface rni);
	public boolean Stop();
}
