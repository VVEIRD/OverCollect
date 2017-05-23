package de.rcblum.overcollect.ui.animation;

public interface IAnimatable {

	int getMinValue();

	int getMaxValue();

	void setValue(int v);

	int getCurrentValue();

	boolean isExpanding();

}