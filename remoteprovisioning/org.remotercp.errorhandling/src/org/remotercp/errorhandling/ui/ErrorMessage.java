package org.remotercp.errorhandling.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.graphics.Image;

public class ErrorMessage {

	private String text;

	private Image image;

	private Date date;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"dd.MM.yyyy HH:mm");

	public ErrorMessage(String text, Image image) {
		this.text = text;
		this.image = image;
		this.date = new Date();
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
}
