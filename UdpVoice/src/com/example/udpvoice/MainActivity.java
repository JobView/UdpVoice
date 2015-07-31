package com.example.udpvoice;

import test.UDPClient;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private EditText et_ip;
	private EditText et_port;
	private TextView tv_message;

	protected MyAudioTrack m_player;
	protected MyAudioRecord m_recorder;

	private Button btn_send_init;
	private Button btn_send_stop;
	private Button btn_play_init;
	private Button btn_play_stop;
	private int sumR;
	private int sumS;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				sumR += msg.arg1;
				tv_message.setText("接收消息:" + sumR);
				break;

			case 1:
				sumS += msg.arg1;
				tv_message.setText("发送消息:" + sumS);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		UDPClient.getInstance();
		initView();
		initData();

		getIp();
	}

	private void getIp() {
		String ip = Tools.getLocalIpAddress();
		tv_message.setText("ip:" + ip);
	}

	

	private void initData() {

	}

	private void initView() {
		et_ip = (EditText) findViewById(R.id.et_ip);
		et_port = (EditText) findViewById(R.id.et_port);
		tv_message = (TextView) findViewById(R.id.tv_message);
		btn_send_init = (Button) findViewById(R.id.btn_send_init);
		btn_send_init.setOnClickListener(this);
		btn_send_stop = (Button) findViewById(R.id.btn_send_stop);
		btn_send_stop.setOnClickListener(this);
		btn_play_init = (Button) findViewById(R.id.btn_play_init);
		btn_play_init.setOnClickListener(this);
		btn_play_stop = (Button) findViewById(R.id.btn_play_stop);
		btn_play_stop.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send_init:
			String ip = et_ip.getText().toString();
//			int port = Integer.valueOf(et_port.getText().toString());
			m_recorder = new MyAudioRecord(ip, 0);
			m_recorder.setReceiver(new LengthReceiver() {

				@Override
				public void length(int length) {
					Message message = new Message();
					message.what = 1;
					message.arg1 = length;
					handler.sendMessage(message);
				}
			});
			m_recorder.init();
			m_recorder.start();
			break;
		case R.id.btn_send_stop:
			if (m_recorder != null) {
				m_recorder.free();
				m_recorder = null;
			}
			break;
		case R.id.btn_play_init:
			m_player = new MyAudioTrack();
			m_player.setReceiver(new LengthReceiver() {

				@Override
				public void length(int length) {
					Message message = new Message();
					message.what = 0;
					message.arg1 = length;
					handler.sendMessage(message);
				}
			});
			m_player.init();
			m_player.start();
			break;
		case R.id.btn_play_stop:
			if (m_player != null) {
				m_player.free();
				m_player = null;
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.KEYCODE_BACK) {
			int pid = android.os.Process.myPid();
			android.os.Process.killProcess(pid);
		}
		return super.onKeyDown(keyCode, event);
	}

}
