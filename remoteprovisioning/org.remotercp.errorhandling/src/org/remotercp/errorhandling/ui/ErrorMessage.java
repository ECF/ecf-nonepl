package org.remotercp.errorhandling.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.eclipse.swt.graphics.Image;

public class ErrorMessage {

	private String text;

	private Image image;

	private Date date;

	private Level severity;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm");

	public ErrorMessage(String text, Image image, Level severity) {
		this.text = text;
		this.image = image;
		this.date = new Date();
		this.severity = severity;
	}

	public String getText() {
		return this.text;
	}

	public Image getImage() {
		return this.image;
	}

	public String getDate() {
		return dateFormat.format(this.date);
	}

	public Level getSeverity() {
		return this.severity;
	}
}
