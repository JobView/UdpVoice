
package com.ryong21.encode;


public class Speex  {

	/* quality
	 * 1 : 4kbps (very noticeable artifacts, usually intelligible)
	 * 2 : 6kbps (very noticeable artifacts, good intelligibility)
	 * 4 : 8kbps (noticeable artifacts sometimes)
	 * 6 : 11kpbs (artifacts usually only noticeable with headphones)
	 * 8 : 15kbps (artifacts not usually noticeable)
	 */
	private static final int DEFAULT_COMPRESSION = 8;
//	private Logger log = LoggerFactory.getLogger(Speex.class);

	public Speex() {
	}

	public void init() {
		load();	
		open(DEFAULT_COMPRESSION);
//		log.info("speex opened");		
	}
	
	private void load() {
		try {
			System.loadLibrary("speex");
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public native int open(int compression);
	public native int getFrameSize();
	/*
	 * 解码
	 * char encoded[] 编码后的语音数据
	 * int size 编码后的语音数据的长度
	 * short output[] 解码后的语音数据
	 * int max_buffer_size 保存解码后的数据的数组的最大长度
	 */
	public native int decode(byte encoded[], short lin[], int size);
	/*
	 * 压缩编码
	 * short lin[] 语音数据
	 * int size 语音数据长度
	 * char encoded[] 编码后保存数据的数组
	 * int max_buffer_size 保存编码数据数组的最大长度
	 */
	public native int encode(short lin[], int offset, byte encoded[], int size);
	public native void close();
	
}
