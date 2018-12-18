package com.testnetdeve.custom.struct;

public final class AlarmMessage {

	private Header header;
	
	private Object body;

	public final Header getHeader() {
		return header;
	}

	public final void setHeader(Header header) {
		this.header = header;
	}

	public final Object getBody() {
		return body;
	}

	public final void setBody(Object body) {
		this.body = body;
	}
	
	/*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */

	@Override
	public String toString() {
		return "AlarmMessage {" +
				"header=" + header +
				", body=" + body +
				'}';
	}
}
