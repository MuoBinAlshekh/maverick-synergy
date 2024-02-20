package com.sshtools.synergy.nio;

import com.sshtools.common.ssh.AbstractRequestFuture;
import com.sshtools.synergy.ssh.Connection;

public class ConnectRequestFuture extends AbstractRequestFuture {

	ProtocolEngine transport;
	Connection<?> con;
	String host;
	int port;
	Throwable exception;
	
	ConnectRequestFuture() {
	}
	
	ConnectRequestFuture(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void connected(ProtocolEngine transport, Connection<?> con) {
		this.transport = transport;
		this.con = con;
		super.done(true);
	}
	
	void failed() {
		super.done(false);
	}
	
	public ProtocolEngine getTransport() {
		return transport;
	}
	
	public Connection<?> getConnection() {
		return con;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public Throwable getLastError() {
		return exception;
	}

	public void setLastError(Throwable exception) {
		this.exception = exception;
	}
}
