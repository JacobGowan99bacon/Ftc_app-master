package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;


@Autonomous(name="LeftLineDriveShort", group="Pushbot")
public class AutoDrivetoLine extends LinearOpMode {

    HardwarePushbot robot = new HardwarePushbot();
    private ElapsedTime runtime = new ElapsedTime();

    static final double CRATER_SPEED = 1;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        DcMotor front_left = hardwareMap.get(DcMotor.class, "front_left");
        DcMotor back_left = hardwareMap.get(DcMotor.class, "back_left");
        DcMotor front_right = hardwareMap.get(DcMotor.class, "front_right");
        DcMotor back_right = hardwareMap.get(DcMotor.class, "back_right");


        front_left.setDirection(DcMotor.Direction.FORWARD);
        back_left.setDirection(DcMotor.Direction.REVERSE);
        front_right.setDirection(DcMotor.Direction.FORWARD);
        back_right.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 0.35)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();

            front_left.setPower(CRATER_SPEED);
            back_left.setPower(-CRATER_SPEED);
            front_right.setPower(-CRATER_SPEED);
            back_right.setPower(CRATER_SPEED);


        }
    }
}