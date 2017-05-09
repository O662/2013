package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.*;

public class Cyborgs extends IterativeRobot {

    private static final double SLOW_SPEED = 0.50;
    private static final double FAST_SPEED = 0.95;

    private Joystick Controller;
    private SpeedController FrontLeft, FrontRight, BackLeft, BackRight, ShoulderLeft, ShoulderRight, PickupLeft, PickupRight;
    private RobotDrive Drive;
    private double sens;
    private boolean hasChangedSpeed;
    private final DriverStationLCD lcd = DriverStationLCD.getInstance();

    public void robotInit() {
        JoystickInit();
        SpeedControllerInit();
        RobotDriveInit();

        hasChangedSpeed = false;
        sens = SLOW_SPEED;
    }

    public void JoystickInit() {
        Controller = new Joystick(1);
    }

    public void SpeedControllerInit() {
        FrontLeft = new Talon(1);
        FrontRight = new Talon(2);
        BackLeft = new Talon(3);
        BackRight = new Talon(4);

        ShoulderLeft = new Victor(5);
        ShoulderRight = new Victor(6);

        PickupLeft = new Victor(7);
        PickupRight = new Victor(8);
    }

    public void RobotDriveInit() {
        Drive = new RobotDrive(FrontLeft, FrontRight, BackLeft, BackRight);
    }

    public void autonomousInit() {
        getWatchdog().setEnabled(true);
    }

    public void autonomousContinuous() {

    }

    public void autonomousPeriodic() {
        getWatchdog().feed();

        FrontLeft.set(0.5);
        FrontRight.set(-0.5);
        BackLeft.set(-0.5);
        BackRight.set(0.5);
        Timer.delay(1);
        FrontLeft.set(0);
        FrontRight.set(0);
        BackLeft.set(0);
        BackRight.set(0);

    }

    public void disabledInit() {
    }

    public void disabledPeriodic() {
    }

    public void teleopPeriodic() {
        //kThrottle = X rotation
        //sens = [0,1.0], the speed of the robot

        // Drive
        Drive.mecanumDrive_Cartesian(getDeadZone(-Controller.getAxis(Joystick.AxisType.kY), 0.25) * sens,
                getDeadZone(-Controller.getAxis(Joystick.AxisType.kThrottle), 0.25) * sens,
                getDeadZone(Controller.getAxis(Joystick.AxisType.kX), 0.25) * sens, 0);

        // Lift
        if (Controller.getRawButton(5)) {
            ShoulderLeft.set(-1);
            ShoulderRight.set(-1);
        } else if (Controller.getRawButton(6)) {
            ShoulderLeft.set(1);
            ShoulderRight.set(1);
        } else {
            ShoulderLeft.set(0);
            ShoulderRight.set(0);
        }

        // Pickup
        if (Controller.getRawButton(1)) {
            PickupLeft.set(-1);
            PickupRight.set(1);
        } else if (Controller.getRawButton(2)) {
            PickupLeft.set(1);
            PickupRight.set(-1);
        } else {
            PickupLeft.set(0);
            PickupRight.set(0);
        }

        // Increase Speed
        if (Controller.getRawButton(9) && !hasChangedSpeed) {
            sens = (sens == FAST_SPEED) ? SLOW_SPEED : FAST_SPEED;
            hasChangedSpeed = true;
        } else if (!Controller.getRawButton(9) && hasChangedSpeed) {
            hasChangedSpeed = false;
        }
        lcd.println(DriverStationLCD.Line.kUser5, 1, "Max Speed: " + sens);
        lcd.updateLCD();
    }

    private double getDeadZone(double num, double dead) {
        if (num < 0) {
            if (num < dead) {
                return num;
            } else {
                return 0;
            }
        } else {
            if (num > dead) {
                return num;
            } else {
                return 0;
            }
        }
    }

}
