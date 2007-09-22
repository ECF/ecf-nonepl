 /**
 * 
 */
package com.yakasoftware.telephony.iax.iaxclient.util;


 /**
 * Sound
 * 
 * @author Roland Ndaka Fru
 *
 */
public class Sound {
    /** the lower frequency */	
	private int f1;
    /** the upper frequency */	
	private int f2;	
    /** sound data */	
	private short   data;
    /** length of sample */	
	private long    len;
    /** should the library free() the data after it is played? */	
	private int     malloced;
    /** 0 for outputSelected, 1 for ringSelected */	
	private int     channel;
    /** number of times to repeat (-1 = infinite) */	
	private int     repeat;
    /** internal use: current play position */	
	private long    pos;
    /** internal use: sound ID */	
	private int     id;
	/** internal use: next in list */	
	private Sound next;
	
	/**
	 * @param f1
	 * @param f2
	 * @param data IGNORED
	 * @param len
	 * @param malloced IGNORED
	 * @param channel IGNORED
	 * @param repeat
	 * @param pos
	 * @param id
	 * @param next IGNORED
	 */
	public Sound(int f1, int f2, short data, long len, int malloced, int channel, int repeat,
			long pos, int id, Sound next) {
		super();
		this.f1 = f1;
		this.f2 = f2;
		this.data = data;
		this.len = len;
		this.malloced = malloced;
		this.channel = channel;
		this.repeat = repeat;
		this.pos = pos;
		this.id = id;
		this.next = next;
	}
	
	/**
	 * @return the f1
	 */
	public int getF1() {
		return f1;
	}

	/**
	 * @return the f2
	 */
	public int getF2() {
		return f2;
	}	

	/**
	 * @return the data
	 */
	public short getData() {
		return data;
	}

	/**
	 * @return the len
	 */
	public long getLen() {
		return len;
	}

	/**
	 * @return the malloced
	 */
	public int getMalloced() {
		return malloced;
	}

	/**
	 * @return the channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * @return the repeat
	 */
	public int getRepeat() {
		return repeat;
	}

	/**
	 * @return the pos
	 */
	public long getPos() {
		return pos;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the next
	 */
	public Sound getNext() {
		return next;
	}	
}
