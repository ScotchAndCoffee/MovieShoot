package com.scotchandcoffee.movieshoot;

public class SchedulingException extends RuntimeException {

	private static final long serialVersionUID = 7914412715059768017L;

	public SchedulingException(String msg) {
		super(msg);
	}

	public SchedulingException(String msg, Throwable t) {
		super(msg, t);
	}
}
