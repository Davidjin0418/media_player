package control;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import javax.swing.SwingWorker;

/**
 * BackwardFarwardWorker class extends SwingWorker, it is used for playing the
 * media file fast forward or backward based on the option
 */
public class BackwardFarwardWorker extends SwingWorker<Void, Void> {
	private int options;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;

	/**
	 * constructor of the worker
	 * 
	 * @param option
	 *            0 for forward ,1 for backward
	 * 
	 * @param m
	 *            the media player component
	 * 
	 */
	public BackwardFarwardWorker(int option, EmbeddedMediaPlayerComponent m) {
		options = option;
		mediaPlayerComponent = m;
	}

	@Override
	protected Void doInBackground() throws Exception {
		if (options == 0) {
			while (!this.isCancelled()) {
				Thread.sleep(100);
				mediaPlayerComponent.getMediaPlayer().skip(500);
			}
		} else if (options == 1) {
			while (!this.isCancelled()) {
				Thread.sleep(100);
				mediaPlayerComponent.getMediaPlayer().skip(-500);
			}
		}
		return null;
	}

	public void cancelWorker() {
		if (!this.isCancelled()) {
			this.cancel(true);
		}
	}

}