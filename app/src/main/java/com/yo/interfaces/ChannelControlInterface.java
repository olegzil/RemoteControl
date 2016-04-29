package com.yo.interfaces;

public interface ChannelControlInterface {
	public boolean ChannelUp();
	public boolean ChannelDown();
	public boolean ChannelSet(Long value);
	public boolean CannelPrevious();
	public Long GetCurrentChannel();
}
