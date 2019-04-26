package player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.sound.sampled.Clip;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * PlayingTimer 类用于音乐播放的计时
 * 格式为 HH:mm:ss
 * 并且同时对 slider 进行更新
 * @author dingfeng
 *
 */
public class PlayingTimer extends Thread{
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private boolean isRunning = false;
	private boolean isPause = false;
	private boolean isReset = false;
	private long startTime;
	private long pauseTime;
	
	private JLabel labelRecordTime;
	private JSlider slider;
	private Clip audioClip;
	
	public void setAudioClip(Clip audioClip) {
		this.audioClip = audioClip;
	}
	
	public PlayingTimer(JLabel labelRecordTime, JSlider slider) {
		this.labelRecordTime = labelRecordTime;
		this.slider = slider;
	}

	public void reset() {
		isReset = true;
		isRunning = false;
	}
	
	public void pauseTimer() {
		isPause = true;
	}
	
	public void resumeTimer() {
		isPause = false;
	}
	
	/**
	 * 生成时间的字符串
	 * 格式 HH:mm:ss
	 * @return
	 */
	private String toTimeString() {
		long now = System.currentTimeMillis();
		Date current = new Date(now - startTime - pauseTime);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timeCounter = dateFormat.format(current);
		return timeCounter;
	}
	
	@Override
	public void run() {
		isRunning = true;
		
		startTime = System.currentTimeMillis();
		
		while (isRunning) {
			try {
				Thread.sleep(1000);
				if(!isPause) {
					if(audioClip != null && audioClip.isRunning()) {
						labelRecordTime.setText(toTimeString());
						int currentSecond = (int)
								audioClip.getMicrosecondPosition() / 1_000_000;
						slider.setValue(currentSecond);
					}
				} else {
					pauseTime += 100;
				}
			} catch (InterruptedException e) {
				if(isReset) {
					slider.setValue(0);
					labelRecordTime.setText("00:00:00");
					isRunning = false;
					break;
				}
			}
		}
	}
}
