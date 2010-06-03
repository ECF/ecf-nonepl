package org.eclipse.ecf.examples.remoteservices.quotes.consumer;

import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.eclipse.swt.SWT;

import com.remainsoftware.osgilloscope.OSGilloscope;

/**
 * Dispatch an osgilloscope.
 * 
 */
public abstract class Dispatcher {

	final String FLATLINE = "C:/Users/jongw/workspaces/nntp/com.remainsoftware.snippets/Beep EKG Flatline 1.WAV";
	final String BEEP = "C:/Users/jongw/workspaces/nntp/com.remainsoftware.snippets/25882__acclivity__Beep1000.wav";

	/**
	 * Play a sound clip.
	 * 
	 */
	public class Clipper {
		Clip clip = null;
		String oldFile = "";

		public void playClip(String file, int loop) {
			try {

				if (clip == null || !file.equals(oldFile)) {
					oldFile = file;
					clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(new File(file)));
				}
				if (clip.isActive())
					return;
				// clip.stop(); << Alternative

				clip.setFramePosition(0);
				clip.loop(loop);

			} catch (Exception e) {
			}
		}
	}

	public abstract void setValue(int value);

	Clipper clipper = new Clipper();

	/**
	 * @wbp.parser.entryPoint
	 */
	public void dispatch() {

		init();

		Runnable runnable = new Runnable() {
			int pulse = 0;

			public void run() {

				getGilloscope().setPercentage(isPercentage());
				getGilloscope().setTailSize(
						isTailSizeMax() ? OSGilloscope.TAILSIZE_MAX
								: getTailSize());
				getGilloscope().setSteady(isSteady(), getSteadyPosition());
				getGilloscope().setFade(getFade());
				getGilloscope().setTailFade(getTailFade());
				getGilloscope().setConnect(mustConnect());

				getGilloscope().redraw();
				pulse++;

				if (pulse == getPulse()) {
					pulse = 0;
					if (isServiceActive()) {
						getGilloscope().setForeground(
								getGilloscope().getDisplay().getSystemColor(
										SWT.COLOR_GREEN));
						// setValue(pulse);

					} else {
						if (isSoundRequired())
							clipper.playClip(getInactiveSoundfile(), 0);
						getGilloscope().setForeground(
								getGilloscope().getDisplay().getSystemColor(
										SWT.COLOR_RED));
					}
				}
				getGilloscope().getDisplay().timerExec(getDelayloop(), this);
			}
		};
		getGilloscope().getDisplay().timerExec(getDelayloop(), runnable);

	}

	public void init() {
	}

	public int getPulse() {
		return 40;
	}

	public String getActiveSoundfile() {
		return BEEP;
	}

	public int getDelayloop() {
		return 10;
	}

	public abstract OSGilloscope getGilloscope();

	// {
	// return gilloscope;
	// }

	public String getInactiveSoundfile() {
		return FLATLINE;
	}

	public boolean isTailSizeMax() {
		return true;
	}

	public boolean isPercentage() {
		return true;
	}

	public boolean isServiceActive() {
		return true;
	}

	public boolean isSoundRequired() {
		return false;
	}

	public int getTailSize() {
		return OSGilloscope.TAILSIZE_MAX;
	}

	public boolean isSteady() {
		return false;
	}

	public int getSteadyPosition() {
		return 200;
	}

	public boolean getFade() {
		return true;

	}

	public int getTailFade() {
		return 25;
	}

	public boolean mustConnect() {
		return false;
	}

}
