package control;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import javax.swing.SwingWorker;

public class BackwardFarwardWorker extends SwingWorker<Void, Void> {
	int options;
	EmbeddedMediaPlayerComponent mediaPlayerComponent;
	// options:0 for forward ,1 for backward.
	public BackwardFarwardWorker(int option,EmbeddedMediaPlayerComponent m) {
		options = option;
		mediaPlayerComponent=m;
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