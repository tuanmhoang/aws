package com.tuanmhoang.log.enums;

public enum ProcessedOrderType {
	ACCEPTED("accepted"),
	REJECTED("rejected");;

	private String processedType;

	private ProcessedOrderType(String processedType) {
		this.processedType = processedType;
	}

	public String getProcessedType() {
		return processedType;
	}

}
