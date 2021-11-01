package com.tuanmhoang.log.enums;

public enum FileExtension {
	TXT(".txt");

	private String extension;

	private FileExtension(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}

}
