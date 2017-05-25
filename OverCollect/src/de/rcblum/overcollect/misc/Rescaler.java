package de.rcblum.overcollect.misc;

import java.util.List;

import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;

public class Rescaler
{
	public static void main(String[] args) {
		rescale1080pTo1440p();
	}
	
	public static void rescale1080pTo1440p() {
		List<OWItem> items = OWLib.getInstance().getItems("1920x1080");
		float rescale = 1440f/1080f;
		System.out.println(rescale);
		for (OWItem owItem : items) {
			owItem.rescale(rescale);
		}
	}
}
