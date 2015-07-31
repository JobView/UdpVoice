package com.example.udpvoice;

import java.util.LinkedList;

import com.ryong21.encode.Speex;

import test.UDPClient;
import android.R.integer;
import android.R.string;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * 录音
 * 
 * @author Administrator
 */
public class MyAudioRecord extends Thread {
	private LengthReceiver receiver;
	protected AudioRecord m_in_rec;
	protected int m_in_buf_size;
	protected byte[] m_in_bytes;
	protected boolean m_keep_running;
	protected LinkedList<byte[]> m_in_q;
	private UDPClient client;
	private Speex speex;
	private int frameSize;

	private String ip;
	private int port;
	public MyAudioRecord(String ip,int port){
		this.ip = ip;
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			byte[] bytes_pkg;
			m_in_rec.startRecording();
			int length = 0;
			byte [] src;
			Log.i("wzf","发送初始化");
			while (m_keep_running) {
				
				
				
				int totleByte = 0;
                short[] bufferRead = new short[frameSize];  
                byte[] processedData = new byte[1024];
                short[] rawdata = new short[1024];
                  
                /**
                 * 声音采集
                 * pcm数据通过speex编码
                 */
                int bufferReadResult = m_in_rec.read(bufferRead, 0,frameSize); 
                synchronized (m_in_rec) {
                    System.arraycopy(bufferRead, 0, rawdata, 0, bufferReadResult);
                    totleByte = speex.encode(rawdata, 0, processedData, bufferReadResult);// 编码后的总字节长度
                     
                    byte[] srcs = new byte[totleByte]; 
                    System.arraycopy(processedData, 0, srcs, 0, totleByte);
                    if (totleByte != 0) {
                        Log.i("SPEEX", "编码成功 字节数组长度 = " + totleByte
                                + " ， short[] 长度 = " + bufferReadResult);
                        
                        if(receiver!=null){
    						receiver.length(totleByte);
    					}
    					client.doCommand(srcs, "192.168.2.213", 2015);
                    } else {
                        System.out.println("speex编码失败！");
                    }
                }
              
                 
//                /**
//                 * 解码
//                 */
//                 
//                short[] rcvProcessedData = new short[160];
//                byte[]  rawData= new byte[256];
//
//                System.arraycopy(processedData, 0, rawData, 0, bufferReadResult);
//                int desize;
//                synchronized (audioTrack) {
//                    desize = speex.decode(processedData, rcvProcessedData, 160);
//                }
//                if (desize > 0) {
//                    audioTrack.write(rcvProcessedData, 0, desize);
//                    System.out.println("speex解码成功！");
//                }
				
				
				
				/*length =m_in_rec.read(m_in_bytes, 0, 160);
				bytes_pkg = m_in_bytes.clone();
				src = new byte[length];
				System.arraycopy(bytes_pkg, 0, src, 0, length);
				int volum = getVolume(length, src);
				*//**
				 * "192.168.2.110"
				 *//*
				if(volum>2500){
					if(receiver!=null){
						receiver.length(length);
					}
					client.doCommand(src, "192.168.2.213", 2015);					
				}*/
			}

			m_in_rec.stop();
			m_in_rec = null;
			m_in_bytes = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.run();
	}
	

	private int getVolume(int r, byte[] bytes_pkg) {
		// way 1
		int v = 0;
		// 将 buffer 内容取出，进行平方和运算
		for (byte aBytes_pkg : bytes_pkg) {
			// 这里没有做运算的优化，为了更加清晰的展示代码
			v += aBytes_pkg * aBytes_pkg;
		}
		// 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
		// 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。
		int volume = (int) (v / (float) r);
		return volume;
	}
	
	public void init() {
		client = UDPClient.getInstance();
		m_in_buf_size = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, m_in_buf_size);

		m_in_bytes = new byte[160];
		m_keep_running = true;
		m_in_q = new LinkedList<byte[]>();
		
		//speex编解码
        speex = new Speex();
        speex.init();
        frameSize = speex.getFrameSize();
		
	}
	
	public void setReceiver(LengthReceiver receiver) {
		this.receiver = receiver;
	}
	
	public void free() {
		m_keep_running = false;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			Log.d("sleep exceptions...\n", "");
		}
	}
}
