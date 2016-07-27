package byuie361.ispy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

public class CalibrateFragment extends Fragment {
    private View mMain;
    public Switch motor_run;
    public Button confirm;
    public ToggleButton leftReverse;
    public ToggleButton rightReverse;
    public Button fineLeftBtn;
    public Button fineRightBtn;
    public Button coarseLeftBtn;
    public Button coarseRightBtn;
    public Button fineLeftPanBtn;
    public Button fineRightPanBtn;
    public Button coarseLeftPanBtn;
    public Button coarseRightPanBtn;
    public Button fineLeftTiltBtn;
    public Button fineRightTiltBtn;
    public Button coarseLeftTiltBtn;
    public Button coarseRightTiltBtn;
    private static SharedPreferences prefs = null;
    private static SharedPreferences.Editor editor = null;
    private int offset = 0;
    private int offset_x = 0;
    private int offset_y = 0;
    private boolean forward0 = true;
    private boolean forward1 = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.calibrate, container, false);
        initMembers();
        setListeners();
        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        return mMain;
    }

    @Override
    public void onPause() {
        super.onPause();
        motor_run.setChecked(false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public static int getInt(String name, int def_val){
        return (prefs == null ? def_val : prefs.getInt(name, def_val));
    }

    public static void setInt(String name, int val) {
        if (prefs != null) {
            editor = prefs.edit();
            editor.putInt(name, val);
            editor.apply();
        }
    }

    public static boolean getBool(String name, boolean def_val){
        return (prefs == null ? def_val : prefs.getBoolean(name, def_val));
    }

    public static void setBool(String name, boolean val) {
        if (prefs != null) {
            editor = prefs.edit();
            editor.putBoolean(name, val);
            editor.apply();
        }
    }

    public void initMembers() {
        offset = getInt("offset", 0);
        offset_x = getInt("offset_x", 0);
        offset_y = getInt("offset_y", 0);
        forward0 = getBool("forward0", true);
        forward1 = getBool("forward1", true);
        motor_run = (Switch) mMain.findViewById(R.id.runBtn);
        confirm = (Button) mMain.findViewById(R.id.confirmBtn);
        leftReverse = (ToggleButton) mMain.findViewById(R.id.leftReverse);
        rightReverse = (ToggleButton) mMain.findViewById(R.id.rightReverse);
        fineLeftBtn = (Button) mMain.findViewById(R.id.fineLeftBtn);
        fineRightBtn = (Button) mMain.findViewById(R.id.fineRightBtn);
        coarseLeftBtn = (Button) mMain.findViewById(R.id.coarseLeftBtn);
        coarseRightBtn = (Button) mMain.findViewById(R.id.coarseRightBtn);
        fineLeftPanBtn = (Button) mMain.findViewById(R.id.fineLeftPanBtn);
        fineRightPanBtn = (Button) mMain.findViewById(R.id.fineRightPanBtn);
        coarseLeftPanBtn = (Button) mMain.findViewById(R.id.coarseLeftPanBtn);
        coarseRightPanBtn = (Button) mMain.findViewById(R.id.coarseRightPanBtn);
        fineLeftTiltBtn = (Button) mMain.findViewById(R.id.fineLeftTiltBtn);
        fineRightTiltBtn = (Button) mMain.findViewById(R.id.fineRightTiltBtn);
        coarseLeftTiltBtn = (Button) mMain.findViewById(R.id.coarseLeftTiltBtn);
        coarseRightTiltBtn = (Button) mMain.findViewById(R.id.coarseRightTiltBtn);
        leftReverse.setChecked(!forward0);
        rightReverse.setChecked(!forward1);
    }

    public void setListeners() {
        motor_run.setOnCheckedChangeListener(reverseListener);
        confirm.setOnClickListener(calibrateListener);
        leftReverse.setOnCheckedChangeListener(reverseListener);
        rightReverse.setOnCheckedChangeListener(reverseListener);
        fineLeftBtn.setOnClickListener(calibrateListener);
        fineRightBtn.setOnClickListener(calibrateListener);
        coarseLeftBtn.setOnClickListener(calibrateListener);
        coarseRightBtn.setOnClickListener(calibrateListener);
        fineLeftPanBtn.setOnClickListener(calibrateListener);
        fineRightPanBtn.setOnClickListener(calibrateListener);
        coarseLeftPanBtn.setOnClickListener(calibrateListener);
        coarseRightPanBtn.setOnClickListener(calibrateListener);
        fineLeftTiltBtn.setOnClickListener(calibrateListener);
        fineRightTiltBtn.setOnClickListener(calibrateListener);
        coarseLeftTiltBtn.setOnClickListener(calibrateListener);
        coarseRightTiltBtn.setOnClickListener(calibrateListener);
    }

    public CompoundButton.OnCheckedChangeListener reverseListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String data = "";
            switch (compoundButton.getId()) {
                case R.id.runBtn:
                    data =  (b ? "motor_run" : "motor_stop");
                    break;
                case R.id.leftReverse:
                    data = String.format("leftmotor%s", !b ? "True" : "False");
                    break;
                case R.id.rightReverse:
                    data = String.format("rightmotor%s", !b ? "True" : "False");
                    break;
            }

            iSpyCon.cal(data);
        }
    };

    public View.OnClickListener calibrateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.confirmBtn:
                    setBool("motor", motor_run.isChecked());
                    setBool("forward0", !leftReverse.isChecked());
                    setBool("forward1", !rightReverse.isChecked());
                    setInt("offset", offset);
                    setInt("offset_x", offset_x);
                    setInt("offset_y", offset_y);
                    iSpyCon.cmd(
                            "echo \"" +
                                    String.format("offset_x = %s\noffset_y = %s\noffset = %s\nforward0 = " +
                                                    "%s\nforward1 = %s\n ",
                                            offset_x, offset_y, offset, !leftReverse.isChecked() ? "True" : "False",  !rightReverse.isChecked() ? "True" : "False")
                                    + "\" > /home/pi/Sunfounder_Smart_Video_Car_Kit_for_RaspberryPi/client/config");
                    iSpyCon.cmd(3,
                            "echo \"" +
                            String.format("offset_x = %s\noffset_y = %s\noffset = %s\nforward0 = " +
                                    "%s\nforward1 = %s\n ",
                                    offset_x, offset_y, offset, !leftReverse.isChecked() ? "True" : "False",  !rightReverse.isChecked() ? "True" : "False")
                            + "\" > /home/pi/Sunfounder_Smart_Video_Car_Kit_for_RaspberryPi/server/config");
                    iSpyCon.cal("motor_stop");
                    break;
                case R.id.fineLeftBtn:
                    offset -= 1;
                    iSpyCon.cal( view.getTag().toString() + String.format("=%s", offset));
                    break;
                case R.id.fineRightBtn:
                    offset += 1;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset));
                    break;
                case R.id.fineLeftPanBtn:
                    offset_x -= 1;
                    iSpyCon.cal( view.getTag().toString() + String.format("=%s", offset_x));
                    break;
                case R.id.fineRightPanBtn:
                    offset_x += 1;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset_x));
                    break;
                case R.id.fineLeftTiltBtn:
                    offset_y -= 1;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset_y));
                    break;
                case R.id.fineRightTiltBtn:
                    offset_y += 1;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset_y));
                    break;
                case R.id.coarseLeftBtn:
                    offset -= 10;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset));
                    break;
                case R.id.coarseRightBtn:
                    offset += 10;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset));
                    break;
                case R.id.coarseLeftPanBtn:
                    offset_x -= 10;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset_x));
                    break;
                case R.id.coarseRightPanBtn:
                    offset_x += 10;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset_x));
                    break;
                case R.id.coarseLeftTiltBtn:
                    offset_y -= 10;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset_y));
                    break;
                case R.id.coarseRightTiltBtn:
                    offset_y += 10;
                    iSpyCon.cal(view.getTag().toString() + String.format("=%s", offset_y));
                    break;
            }
        }
    };
}