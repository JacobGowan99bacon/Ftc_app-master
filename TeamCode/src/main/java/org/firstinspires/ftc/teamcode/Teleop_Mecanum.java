/*
ADB guide can be found at:
https://ftcprogramming.wordpress.com/2015/11/30/building-ftc_app-wirelessly/
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Arrays;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Mecanum_TeleOp", group="TeleOp")
public class Teleop_Mecanum<opModeIsActive> extends LinearOpMode {

    //This gives deadzones for the motors.
    private static final double TRIGGERTHRESHOLD = .2;
    private static final double ACCEPTINPUTTHRESHOLD = .15;

    //Emphasis on current controller reading (vs current motor power) on the drive train
    private static final double SCALEDPOWER = 1;

    private static DcMotor front_left, back_left, front_right, back_right;
    private double slowSpeed = 0.25;
    private double strafeSpeed = 1;
    private double driveSpeed = 1;

    @Override
    public void runOpMode() {
        //this declares the motors
        front_left = hardwareMap.dcMotor.get(UniversalConstants.LEFT1NAME);

        back_left = hardwareMap.dcMotor.get(UniversalConstants.LEFT2NAME);

        front_right = hardwareMap.dcMotor.get(UniversalConstants.RIGHT1NAME);

        back_right = hardwareMap.dcMotor.get(UniversalConstants.RIGHT2NAME);

        front_right.setDirection(DcMotorSimple.Direction.FORWARD);

        front_left.setDirection(DcMotorSimple.Direction.REVERSE);

        back_right.setDirection(DcMotorSimple.Direction.FORWARD);

        back_left.setDirection(DcMotorSimple.Direction.REVERSE);

        DcMotor slide_left = hardwareMap.get(DcMotor.class, "slide_left");

        DcMotor slide_right = hardwareMap.get(DcMotor.class, "slide_right");

        slide_right.setDirection(DcMotorSimple.Direction.REVERSE);

        slide_left.setDirection(DcMotorSimple.Direction.FORWARD);


        //this declares the servos
       Servo rightFoundation = hardwareMap.servo.get("rightFoundation");

       Servo leftFoundation = hardwareMap.servo.get("leftFoundation");

       Servo intakeArm = hardwareMap.servo.get("intakeArm");

       Servo intakeGrabber = hardwareMap.servo.get("intakeGrabber");


       waitForStart();
        //init loop
       while(opModeIsActive()) {

           //this assigns buttons to the slides
           double leftslide = gamepad2.left_stick_y;
           double rightslide = gamepad2.left_stick_y;
           slide_left.setPower(leftslide);
           slide_right.setPower(rightslide);

           //this assigns variables for the slide limit
           double hangingMotorCountsPerInch = 2240; //ticks per one rotation of the motor for a rev 40:1 hd hex motor
           double hangingPulleyDiameter = 0.1968503937007874015748031496063;       //diameter in inches of the spool/pulley that has string on it
           double hangingGearRatio = 60/40;          //Gear ratio between motor and final output axle (if no gear ratio, just set equal to 1)
           double ticksPerHangingRev = hangingMotorCountsPerInch * hangingGearRatio;  //Calculates the ticks per rotaion of the OUTPUT AXLE, not the motor.  If gear ratio is 1:1, this will be the same as hangingMotorCountsPerInch
           double ticksPerHangingInch =  (ticksPerHangingRev/(hangingPulleyDiameter * 3.14159265)); //Calculates how many ticks of the motor's output axle it takes to make the slide go up 1 inch

           double hangingLimit = 30; //amount of inches extension you want at the very top.  Recommend .25-.5 inches lower than actual full extension, just to be safe.

               //this assigns buttons to all of the servos
           if (gamepad1.a) {
               rightFoundation.setPosition(1);
               leftFoundation.setPosition(0);
           }
           if (gamepad1.x) {
               rightFoundation.setPosition(0.5);
               leftFoundation.setPosition(1);
           }
           if (gamepad2.a) {
               intakeArm.setPosition(0);
           }
           if (gamepad2.x) {
               intakeGrabber.setPosition(0);
           }
           if (gamepad2.y) {
               intakeArm.setPosition(1);
           }
           if (gamepad2.b) {
               intakeGrabber.setPosition(0.5);
           }
           //This part assigns buttons/joysticks for driving the chassis
           double inputY = Math.abs(gamepad1.left_stick_y) > ACCEPTINPUTTHRESHOLD ? gamepad1.left_stick_y : 0;
           double inputX = Math.abs(gamepad1.left_stick_x) > ACCEPTINPUTTHRESHOLD ? -gamepad1.left_stick_x : 0;
           double inputC = Math.abs(gamepad1.right_stick_x) > ACCEPTINPUTTHRESHOLD ? -gamepad1.right_stick_x : 0;
           double BIGGERTRIGGER = gamepad1.left_trigger > gamepad1.right_trigger ? gamepad1.left_trigger : gamepad1.right_trigger;

           if (BIGGERTRIGGER > TRIGGERTHRESHOLD) { //If we have enough pressure on a trigger
               if ((Math.abs(inputY) > Math.abs(inputX)) && (Math.abs(inputY) > Math.abs(inputC))) { //If our forwards motion is the largest motion vector
                   inputY /= 5 * BIGGERTRIGGER; //slow down our power inputs
                   inputX /= 5 * BIGGERTRIGGER; //slow down our power inputs
                   inputC /= 5 * BIGGERTRIGGER; //slow down our power inputs
               } else if ((Math.abs(inputC) > Math.abs(inputX)) && (Math.abs(inputC) > Math.abs(inputY))) { //and if our turing motion is the largest motion vector
                   inputY /= 4 * BIGGERTRIGGER; //slow down our power inputs
                   inputX /= 4 * BIGGERTRIGGER; //slow down our power inputs
                   inputC /= 4 * BIGGERTRIGGER; //slow down our power inputs
               } else if ((Math.abs(inputX) > Math.abs(inputY)) && (Math.abs(inputX) > Math.abs(inputC))) { //and if our strafing motion is the largest motion vector
                   inputY /= 3 * BIGGERTRIGGER; //slow down our power inputs
                   inputX /= 3 * BIGGERTRIGGER; //slow down our power inputs
                   inputC /= 3 * BIGGERTRIGGER; //slow down our power inputs
               }
           }
           //Use the larger trigger value to scale down the inputs.
           arcadeMecanum(inputY, inputX, inputC, front_left, front_right, back_left, back_right);
       }
    }
    // y - forwards
    // x - side
    // c - rotation
    public static void arcadeMecanum(double y, double x, double c, DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight) {

        //this allows the robot to turn, strafe, and drive
        double leftFrontVal = y + x + c;
        double rightFrontVal = y - x - c;
        double leftBackVal = y - x + c;
        double rightBackVal = y + x - c;

        double strafeVel;
        double driveVel;
        double turnVel;
        {
            driveVel = 0;
            strafeVel = 0;
            turnVel = 0;
            
            double leftFrontVel = -driveVel - strafeVel + turnVel;
            double rightFrontVel = -driveVel + strafeVel - turnVel;
            double leftRearVel = -driveVel + strafeVel + turnVel;
            double rightRearVel = -driveVel - strafeVel - turnVel;
            double[] vels = {leftFrontVel, rightFrontVel, leftRearVel, rightRearVel};

            Arrays.sort(vels);
            if (vels[3] > 1) {
                leftFrontVel /= vels[3];
                rightFrontVel /= vels[3];
                leftRearVel /= vels[3];
                rightRearVel /= vels[3];
            }
            frontLeft.setPower(leftFrontVel);
            frontRight.setPower(rightFrontVel);
            backLeft.setPower(leftRearVel);
            backRight.setPower(rightRearVel);
        }

        double[] wheelPowers = {rightFrontVal, leftFrontVal, leftBackVal, rightBackVal};
        Arrays.sort(wheelPowers);
        if (wheelPowers[3] > 1) {
            leftFrontVal /= wheelPowers[3];
            rightFrontVal /= wheelPowers[3];
            leftBackVal /= wheelPowers[3];
            rightBackVal /= wheelPowers[3];
        }
        double scaledPower = SCALEDPOWER;
        //This puts power to the wheels in the correct way to make it turn.
        front_left.setPower(leftFrontVal * scaledPower + frontLeft.getPower() * (+scaledPower));
        back_left.setPower(leftBackVal * scaledPower + backLeft.getPower() * (+scaledPower));
        front_right.setPower(rightFrontVal * scaledPower + frontRight.getPower() * (1 - scaledPower));
        back_right.setPower(rightBackVal * scaledPower + backRight.getPower() * (1 - scaledPower));
    }
}