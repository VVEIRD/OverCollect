package de.rcblum.overcollect.ui.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import de.rcblum.overcollect.utils.Helper;

public class FileUtils {

	public static class OWFileFilter extends FileFilter {

		String ext = null;

		public OWFileFilter(String ext) {
			this.ext = ext;
		}

		@Override
		public boolean accept(File f) {
			if (f == null)
				Helper.info(this.getClass(), f);
			return (f != null && f.toString().endsWith(this.ext)) || (f != null && f.isDirectory()) ? true : false;
		}

		@Override
		public String getDescription() {
			return "Spreadsheet Files (*" + this.ext + ")";
		}

		public String getExtension() {
			return ext;
		}

	}
}
