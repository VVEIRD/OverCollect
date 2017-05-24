package de.rcblum.overcollect.ui.animation;

public interface IAnimatable {

	int getCurrentValue();

	int getMaxValue();

	int getMinValue();

	boolean isExpanding();

	void setValue(int v);

}