package player;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WavPlayer implements LineListener {
	private static final int SECONDS_IN_HOUR = 3600;
	private static final int SECONDS_IN_MINUTE = 60;
	
	private Clip audioClip;
	
	private boolean isPaused;
	private boolean isCompleted;
	private boolean isStopped;
	
	public void stop() {
		isStopped = true;
	}
	
	public void pause() {
		isPaused = true;
	}
	
	public void resume() {
		isPaused = false;
	}
	
	public Clip getAudioClip() {
		return audioClip;
	}
	
	/***
	 * 装载音乐文件
	 * @param filepath
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public void load(String filepath)
		throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		File audioFile = new File(filepath);
		
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
		
		AudioFormat format = audioInputStream.getFormat();
		
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		
		audioClip = (Clip) AudioSystem.getLine(info);
		
		audioClip.addLineListener(this);
		
		audioClip.open(audioInputStream);
	}
	
	/***
	 * 开始播放一个给定的 audio 文件
	 * @throws IOException
	 */
	public void play() throws IOException {
		audioClip.start();
		
		isPaused = false;
		isCompleted = false;
		
		while(!isCompleted) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				if(isPaused) {
					audioClip.stop();
				} else {
					audioClip.start();
				}
			}
		}
		
		audioClip.close();
	}

	/***
	 * 监听audio line events 
	 * 了解何时音乐播放停止
	 */
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if(type == LineEvent.Type.STOP) {
			if(isStopped || !isPaused)
				isCompleted = true;
		}
	}
	
	public long getClipSecondLength() {
		return audioClip.getMicrosecondLength() / 1_000_000;
	}
	
	public String getClipLengthString() {
		String length = "";
		long hour = 0;
		long minute = 0;
		long seconds = audioClip.getMicrosecondLength() / 1_000_000;
		
		if(seconds >= SECONDS_IN_HOUR) {
			hour = seconds / SECONDS_IN_HOUR;
			length += String.format("%02:", hour);
		}
		else length += "00:";
		
		minute = seconds - hour * SECONDS_IN_HOUR;
		if(minute >= SECONDS_IN_MINUTE) {
			minute = minute / SECONDS_IN_MINUTE;
			length += String.format("%02d:", minute);
		}
		else {
			minute = 0;
			length += "00:";
		}
		
		seconds = seconds - hour * SECONDS_IN_HOUR - minute *SECONDS_IN_MINUTE;
		
		length += String.format("%02d", seconds);
		
		return length;
	}
	
}