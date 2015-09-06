package unit.drivecommand.machine;

import com.pileproject.drivecommand.machine.MachineBase;
import com.pileproject.drivecommand.machine.MachineStatus;
import com.pileproject.drivecommand.machine.device.port.InputPort;
import com.pileproject.drivecommand.machine.device.port.OutputPort;
import com.pileproject.drivecommand.model.ProtocolBase;

import org.testng.annotations.Test;

import java.io.IOException;

import mockit.Expectations;
import mockit.Mocked;

public class MachineBaseTest {
	@Mocked
	ProtocolBase protocol;
	private final OutputPort OUT_PORT = new OutputPort() {
		@Override
		public boolean isValid(ProtocolBase protocol) {
			return true;
		}

		@Override
		public boolean isInvalid(ProtocolBase protocol) {
			return false;
		}

		@Override
		public int getRaw() {
			return 1;
		}
	};
	private final InputPort IN_PORT = new InputPort() {
		@Override
		public boolean isValid(ProtocolBase protocol) {
			return true;
		}

		@Override
		public boolean isInvalid(ProtocolBase protocol) {
			return false;
		}

		@Override
		public int getRaw() {
			return 1;
		}
	};

	private MachineBase newMachineBase(ProtocolBase protocol) {
		return new MachineBase(protocol) {
			@Override
			public MachineStatus fetchStatus() {
				return null;
			}

			@Override
			public boolean applyStatus(MachineStatus status) {
				return false;
			}
		};
	}

	@Test
	public void connect() throws IOException {
		new Expectations() {{
			protocol.open();
		}};
		MachineBase machineBase = newMachineBase(protocol);
		machineBase.connect();
	}
	
	@Test
	public void disconnect() {
		new Expectations() {{
			protocol.close();
		}};
		MachineBase machineBase = newMachineBase(protocol);
		machineBase.disconnect();
	}

//	@Test
//	public void getMotorFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createMotor(OUT_PORT) instanceof Motor);
//	}
//
//	@Test
//	public void getServomotorFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createServomotor(OUT_PORT) instanceof Servomotor);
//	}
//
//	@Test
//	public void getBuzzerFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createBuzzer(OUT_PORT) instanceof Buzzer);
//	}
//
//	@Test
//	public void getLedFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createLed(OUT_PORT) instanceof Led);
//	}
//
//	@Test
//	public void getLineSensorFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createLineSensor(IN_PORT) instanceof LineSensor);
//	}
//
//	@Test
//	public void getGyroFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createGyroSensor(IN_PORT) instanceof GyroSensor);
//	}
//
//	@Test
//	public void getTouchSensorFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createTouchSensor(IN_PORT) instanceof TouchSensor);
//	}
//
//	@Test
//	public void getColorSensorFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createColorSensor(IN_PORT) instanceof ColorSensor);
//	}
//
//	@Test
//	public void getRangefinderFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createRangefinder(IN_PORT) instanceof Rangefinder);
//	}
//
//	@Test
//	public void getSoundSensorFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createSoundSensor(IN_PORT) instanceof SoundSensor);
//	}
//
//	@Test
//	public void getRemoteControlReceiverFromMachine() {
//		MachineBase machineBase = newMachineBase(protocol);
//		AssertJUnit.assertTrue(machineBase.createRemoteControlReceiver(IN_PORT) instanceof RemoteControlReceiver);
//	}
}