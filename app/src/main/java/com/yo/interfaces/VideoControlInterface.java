package com.yo.interfaces;

public interface VideoControlInterface {
	public boolean StartVideo(String URL);
	public boolean StopVideo();
	public boolean QueueVideo();
	public boolean ClearQueue();
	public String GetVideoSource();
}
