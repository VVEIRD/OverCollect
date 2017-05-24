package de.rcblum.overcollect.ui.animation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.Timer;

public class Animation implements ActionListener {
	IAnimatable animatable = null;
	Timer t = null;

	public Animation(IAnimatable animatable) {
		this.animatable = Objects.requireNonNull(animatable);
		this.t = new Timer(25, this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.animatable.isExpanding() && this.animatable.getCurrentValue() < this.animatable.getMaxValue()) {
			int step = (this.animatable.getMaxValue() - this.animatable.getMinValue()) / 20;
			if (this.animatable.getMaxValue() - this.animatable.getCurrentValue() < step)
				this.animatable.setValue(this.animatable.getMaxValue());
			else
				this.animatable.setValue(this.animatable.getCurrentValue() + step);
		} else if (!this.animatable.isExpanding()
				&& this.animatable.getCurrentValue() > this.animatable.getMinValue()) {
			int step = (this.animatable.getMaxValue() - this.animatable.getMinValue()) / 20;
			if (this.animatable.getCurrentValue() - this.animatable.getMinValue() < step)
				this.animatable.setValue(this.animatable.getMinValue());
			else
				this.animatable.setValue(this.animatable.getCurrentValue() - step);
		}
	}

	public void start() {
		t.start();
	}

	public void stop() {
		t.stop();
	}
}
