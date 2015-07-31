package com.example.udpvoice;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import com.ryong21.encode.Speex;

import test.UDPClient;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * 播放
 * 
 * @author Administrator
 * 
 */
public class MyAudioTrack extends Thread {
	
	private LengthReceiver receiver;
//	protected AudioTrack m_out_trk;
	
	private List<AudioTrack> tracks;
	protected int m_out_buf_size;
	protected byte[] m_out_bytes;
	DatagramPacket rec;
	protected boolean m_keep_running;
	private DatagramSocket ds;
	
	private Speex speex;
	private int frameSize;
	

	public void init() {
		initData();
		ds = UDPClient.getInstance().getDatagramSocket();
		m_keep_running = true;
		//speex编解码
        speex = new Speex();
        speex.init();
        frameSize = speex.getFrameSize();
	}
	
	public void initData(){
		tracks = new ArrayList<AudioTrack>();
		for(int i = 0 ; i<1 ;i++){
			AudioTrack track = getAudioTrack();
			track.play();
			tracks.add(track);
		}
	}
	
	private AudioTrack getAudioTrack(){
		 m_out_buf_size = AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		AudioTrack m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, m_out_buf_size,
				AudioTrack.MODE_STREAM);
		m_out_bytes = new byte[m_out_buf_size];
		rec = new DatagramPacket(m_out_bytes, m_out_bytes.length);
		return m_out_trk;
	}

	public void free() {
		m_keep_running = false;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			Log.d("sleep exceptions...\n", "");
		}
	}

	@Override
	public void run() {
		Log.i("wzf", "接收初始化");
		int length = 0;
		while (m_keep_running) {
			try {
				ds.receive(rec);
				length = rec.getLength();
				Log.i("wzf", "接收数据:"+length);
				if(receiver!=null){
					receiver.length(length);
				}
				
			  /**
               * 解码
               */
               
              byte[]  rawData= new byte[1024];//完整编码数据
              System.arraycopy(rec.getData(), 0, rawData, 0, rec.getLength());
              short[] rcvProcessedData = new short[frameSize];//解码数据

              int desize;
              synchronized (tracks) {
                  desize = speex.decode(rawData, rcvProcessedData, frameSize);
              }
              if (desize > 0) {
            	  tracks.get(0).write(rcvProcessedData, 0, desize);
                  System.out.println("speex解码成功！");
              }
				
				
				
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for(AudioTrack track:tracks){
			track.stop();
			track = null;
		}
		ds.close();
	}

	
	
	public void setReceiver(LengthReceiver receiver) {
		this.receiver = receiver;
	}

	public void playSound(int[] sum, int length) {
//		byte[] bytes_pkg;
//		bytes_pkg = m_out_bytes.clone();
//		short cunutType = Tools.getShort(m_out_bytes);
//		byte[] src = Tools.getByteArray(m_out_bytes);
//		length -=2;
//		
////		if(tracks.get(cunutType).getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){
////			tracks.get(cunutType).play();		
////		}
//		Log.i("wzf"+cunutType, "接收数据：" + (sum[cunutType] += length) + "*****" + length);
//		tracks.get(cunutType).write(src, 0, length);
		
		
//		switch (name) {
//		case 0:
//			if(tracks[0].getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){
//				tracks[0].play();		
//			}
//			Log.i("wzf"+0, "接收数据：" + (sum[0] += length) + "*****" + length);
//			tracks[0].write(src, 0, length);
//			break;
//		case 1:
//			if(tracks[1].getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){
//				tracks[1].play();		
//			}
//			Log.i("wzf"+1, "接收数据：" + (sum[1] += length) + "*****" + length);
//			tracks[1].write(src, 0, length);
//			break;
//		case 2:
//			if(tracks[2].getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){
//				tracks[2].play();		
//			}
//			Log.i("wzf"+2, "接收数据：" + (sum[2] += length) + "*****" + length);
//			tracks[2].write(src, 0, length);
//			break;
//		case 3:
//			Log.i("wzf"+3, "接收数据：" + (sum[3] += length) + "*****" + length);
//			tracks[3].write(src, 0, length);
//			break;
//
//		}
		
	}

}
