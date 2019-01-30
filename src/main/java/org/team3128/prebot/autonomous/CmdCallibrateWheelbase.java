package org.team3128.prebot.autonomous;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.kauailabs.navx.frc.AHRS;

import org.team3128.common.autonomous.primitives.CmdDelay;
import org.team3128.common.autonomous.primitives.CmdLambda;
import org.team3128.common.autonomous.primitives.CmdLog;
import org.team3128.common.drive.SRXTankDrive;
import org.team3128.common.util.Wheelbase;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdCallibrateWheelbase extends CommandGroup {
    public CmdCallibrateWheelbase(AHRS ahrs, double duration, double leftSpeed, double rightSpeed,
            Wheelbase wheelbase) {
        List<Wheelbase> calculatedWheelbases = new ArrayList<Wheelbase>();
        int numSamples = 5;

        for (int i = 0; i < numSamples; i++) {
            Wheelbase wb = new Wheelbase();

            addSequential(SRXTankDrive.getInstance().new CmdDetermineWheelbase(ahrs, duration, leftSpeed, rightSpeed, wb));
            addSequential(new CmdDelay(0.5));
            addSequential(new CmdLog("" + wb.wheelbase));

            calculatedWheelbases.add(wb);

        }

        addSequential(new CmdLambda(() -> {
            double averageWheelbase = 0;
            double averageLeftError = 0;
            double averageRightError = 0;

            for (Wheelbase cwb : calculatedWheelbases) {
                averageWheelbase += cwb.wheelbase;

                averageLeftError += cwb.leftVelocityError;
                averageRightError += cwb.rightVelocityError;
            }
            averageWheelbase /= numSamples;
            averageLeftError /= numSamples;
            averageRightError /= numSamples;

            wheelbase.wheelbase = averageWheelbase;

            wheelbase.leftVelocityError = averageLeftError;
            wheelbase.rightVelocityError = averageRightError;

            Log.info("AverageWheelBase", "" + averageWheelbase);
        }));
    }
}