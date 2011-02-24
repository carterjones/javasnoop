package com.aspect.snoop.ui.choose.process;

/**
 *
 * @author Administrator
 */
class JarScanningException extends Exception {

	public JarScanningException(String message) {
		super(message);
	}

	public JarScanningException(Throwable ex) {
		super(ex.toString());
	}
}
