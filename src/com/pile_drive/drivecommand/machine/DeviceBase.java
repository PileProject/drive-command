package com.pile_drive.drivecommand.machine;

import java.util.HashMap;

import com.pile_drive.drivecommand.command.CommandBase;
import com.pile_drive.drivecommand.model.ProtocolBase;

public abstract class DeviceBase {
	private final int mPort;
	private final ProtocolBase mProtocol;
	
	/**
	 * Constructor
	 * 
	 * @param port
	 */
	public DeviceBase(int port, ProtocolBase protocol) {
		if (port < 0 || port > 3) throw new IllegalArgumentException("The port should be between 0 to 3.");
		mPort = port;
		mProtocol = protocol;
	}
	
	/**
	 * Execute command
	 * 
	 * @param command
	 * @return result
	 */
	protected HashMap<String, Object> exec(CommandBase command) {
		return mProtocol.exec(mPort, command);
	}
	
	public abstract DeviceType getDeviceType();
}
