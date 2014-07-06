package com.pile_drive.drivecommand.model.ev3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.util.Log;

import com.pile_drive.drivecommand.command.CommandBase;
import com.pile_drive.drivecommand.model.CommandType;
import com.pile_drive.drivecommand.model.ProtocolBase;
import com.pile_drive.drivecommand.model.com.ICommunicator;

public class Ev3Protocol extends ProtocolBase implements Ev3Constants {
	private static final String KEY_VALUE = "value";
	private static final int TIMEOUT = 1000;
	private static final String TAG = "Ev3Protocol";
	
	public Ev3Protocol(ICommunicator comm) {
		super(comm);
	}
	
	@Override
	public void open() throws IOException {
		getCommunicator().open();
	}
	
	@Override
	public void close() {
		getCommunicator().close();
	}
	
	@Override
	public HashMap<String, Object> exec(int port, CommandBase cmd) {
		HashMap<String, Object> res = new HashMap<String, Object>();
		CommandType type = cmd.getCommandType();
		switch (type) {
			case GET_COLOR_ILLUMINANCE: {
				// TOOD: NXT has own color sensor (NXT_COLOR).
				short[] values = getPercentValue(port, EV3_COLOR, COL_REFLECT, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case GET_COLOR_RGB: {
				// TOOD: NXT has own color sensor (NXT_COLOR).
				// Something wrong
				float[] values = getSiValue(port, EV3_COLOR, COL_RGB, 3);
				res.put(KEY_VALUE, values);
				break;
			}
			case GET_GYRO_ANGLE: {
				float[] value = getSiValue(port, EV3_GYRO, GYRO_ANGLE, 1);
				res.put(KEY_VALUE, (int) value[0]);
				break;
			}
			case GET_GYRO_RATE: {
				float[] value = getSiValue(port, EV3_GYRO, GYRO_RATE, 1);
				res.put(KEY_VALUE, (int) value[0]);
				break;
			}
			case GET_LINE_VALUE: {
				// TODO: NOT TESTED
				short[] values = getPercentValue(port, NXT_LIGHT, LIGHT_REFLECT, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case GET_RANGEFINDER_DIST: {
				float[] values = getSiValue(port, EV3_ULTRASONIC, US_CM, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case GET_REMOTECONTROLLER_BUTTON: {
				// TODO: NOT TESTED
				float[] values = getSiValue(port, EV3_IR, IR_REMOTE, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case GET_REMOTECONTROLLER_DIST: {
				// TODO: NOT TESTED
				float[] values = getSiValue(port, EV3_IR, IR_PROX, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case GET_SERVO_ANGLE: {
				float[] values = getSiValue((0x10 | port), L_MOTOR, L_MOTOR_DEGREE, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case GET_SOUND_DB: {
				float[] values = getSiValue(port, NXT_SOUND, SOUND_DB, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case GET_TOUCH_COUNT: {
				float[] values = getSiValue(port, EV3_TOUCH, TOUCH_BUMPS, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case GET_TOUCH_TOUCHED: {
				float[] values = getSiValue(port, EV3_TOUCH, TOUCH_TOUCH, 1);
				res.put(KEY_VALUE, (int) values[0]);
				break;
			}
			case SET_BUZZER_BEEP: {
				// TODO: maybe OK.
				soundTone(50, 600, 200);
				break;
			}
			case SET_BUZZER_OFF: {
				throw new UnsupportedOperationException("SET BUZZER OFF Operation hasn't been implemented yet");
			}
			case SET_BUZZER_ON: {
				throw new UnsupportedOperationException("SET BUZZER ON Operation hasn't been implemented yet");
			}
			case SET_LED_OFF: {
				throw new UnsupportedOperationException("SET LED OFF Operation hasn't been implemented yet");
			}
			case SET_LED_ON: {
				throw new UnsupportedOperationException("SET LED ON Operation hasn't been implemented yet");
			}
			case SET_MOTOR_SPEED: {
				HashMap<String, Object> args = cmd.getArgs();
				int speed = (Integer) args.get("speed");
				setOutputState(port, speed);
				break;
			}
			case SET_SERVO_ANGLE: {
				throw new UnsupportedOperationException("SET SERVO ANGLE Operation hasn't been implemented yet");
			}
			default: {
				throw new UnsupportedOperationException("This Operation hasn't been implemented yet");
			}
		}
		return res;
	}
	
	/**
	 * Get SI unit value
	 * 
	 * @param port
	 *            The port of the device
	 * @param type
	 *            The device type
	 * @param mode
	 *            The mode of the device
	 * @param nvalue
	 *            The number of the response value
	 * @return
	 */
	@SuppressLint("NewApi")
	private float[] getSiValue(int port, int type, int mode, int nvalue) {
		ByteCodeFormatter byteCode = new ByteCodeFormatter();
		byteCode.addOpCode(DIRECT_COMMAND_REPLY); // Command Types
		
		// TODO: NOT TESTED
		byteCode.addGlobalAndLocalBufferSize(4 * nvalue, 0);
		byteCode.addOpCode(INPUT_DEVICE);
		byteCode.addOpCode(READY_SI);
		byteCode.addParameter(LAYER_MASTER);
		byteCode.addParameter((byte) port);
		byteCode.addParameter((byte) type);
		byteCode.addParameter((byte) mode);
		byteCode.addParameter((byte) nvalue); // number of values
		byteCode.addGlobalIndex((byte) 0x00);
		
		// Send message
		getCommunicator().write(byteCode.byteArray(), TIMEOUT);
		
		byte[] reply = readData();
		
		// Check the validity of the response
		// boolean valid = (reply[2] == DIRECT_COMMAND_SUCCESS);
		
		// Read the SI unit value in float type
		float[] result = new float[nvalue];
		for (int i = 0; i < nvalue; i++) {
			byte[] data = Arrays.copyOfRange(reply, 3 + 4 * i, 7 + 4 * i);
			result[i] = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
		}
		return result;
	}
	
	/**
	 * Get percent value
	 * 
	 * @param port
	 *            The port of the device
	 * @param type
	 *            The device type
	 * @param mode
	 *            The mode of the device
	 * @param nvalue
	 *            The number of the response value
	 * @return
	 */
	private short[] getPercentValue(int port, int type, int mode, int nvalue) {
		ByteCodeFormatter byteCode = new ByteCodeFormatter();
		byteCode.addOpCode(DIRECT_COMMAND_REPLY); // Command Types
		
		// TODO: NOT TESTED
		byteCode.addGlobalAndLocalBufferSize(1 * nvalue, 0);
		byteCode.addOpCode(INPUT_DEVICE);
		byteCode.addOpCode(READY_PCT);
		byteCode.addParameter(LAYER_MASTER);
		byteCode.addParameter((byte) port);
		byteCode.addParameter((byte) type);
		byteCode.addParameter((byte) mode);
		byteCode.addParameter((byte) nvalue);
		byteCode.addGlobalIndex((byte) 0x00);
		
		// Send message
		getCommunicator().write(byteCode.byteArray(), TIMEOUT);
		
		byte[] reply = readData();
		
		// Check the validity of the response
		// boolean valid = (reply[2] == DIRECT_COMMAND_SUCCESS);
		
		// Read the percent value in short type
		short[] result = new short[nvalue];
		for (int i = 0; i < nvalue; i++) {
			result[i] = (short) reply[3 + i];
		}
		return result;
	}
	
	private byte getRealPortNumber(int port) {
		switch (port) {
			case 0:
				return 0x01;
			case 1:
				return 0x02;
			case 2:
				return 0x04;
			case 3:
				return 0x08;
			default:
				return 0x00; // something wrong
		}
	}
	
	/**
	 * Set output device condition.
	 * 
	 * @param port
	 *            The port of the device
	 * @param speed
	 *            The speed of the device
	 */
	private void setOutputState(int port, int speed) {
		ByteCodeFormatter byteCode = new ByteCodeFormatter();
		
		// Convert port number
		byte outputPort = getRealPortNumber(port);
		
		byteCode.addOpCode(DIRECT_COMMAND_NOREPLY);
		byteCode.addGlobalAndLocalBufferSize(0, 0);
		
		byteCode.addOpCode(OUTPUT_POWER);
		byteCode.addParameter(LAYER_MASTER);
		byteCode.addParameter(outputPort);
		byteCode.addParameter((byte) speed);
		
		byteCode.addOpCode(OUTPUT_START);
		byteCode.addParameter(LAYER_MASTER);
		byteCode.addParameter(outputPort);
		
		// Send message
		getCommunicator().write(byteCode.byteArray(), TIMEOUT);
	}
	
	/**
	 * Make a sound
	 * 
	 * @param volume
	 *            The volume of the sound (0 ~ 100 [%])
	 * @param freq
	 *            The frequency
	 * @param duration
	 *            The duration of the tone
	 */
	private void soundTone(int volume, int freq, int duration) {
		ByteCodeFormatter byteCode = new ByteCodeFormatter();
		
		byteCode.addOpCode(DIRECT_COMMAND_REPLY);
		byteCode.addGlobalAndLocalBufferSize(0, 0);
		
		byteCode.addOpCode(SOUND_CONTROL);
		byteCode.addOpCode(SOUND_TONE);
		byteCode.addParameter((byte) volume);
		byteCode.addParameter((short) freq);
		byteCode.addParameter((short) duration);
		
		// Send message
		getCommunicator().write(byteCode.byteArray(), TIMEOUT);
	}
	
	/**
	 * Read data from the device
	 * 
	 * @return
	 */
	private byte[] readData() {
		ICommunicator com = getCommunicator();
		
		// Calculate the size of response by reading 2 bytes.
		byte[] header = com.read(2, TIMEOUT);
		int numBytes = (int) ((0x00ff & header[0]) | (header[1] << 8));
		
		// Get result
		byte[] result = com.read(numBytes, TIMEOUT);
		Log.d(TAG, "read: " + result.length + " bytes");
		
		return result;
	}
}
