package byuie361.ispy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ControlFragment extends Fragment {
    private View mMain;
    public CtrlListener ctrl_listener = new CtrlListener();
    public static WebView wv;
    public Button fwdBtn, backBtn, leftBtn, rightBtn, carHome, camHome, camUp, camDn, camLeft, camRight;
    public SeekBar speed;
    public TextView speedTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.control, container, false);
        initMembers();
        setListeners();

        return mMain;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        speed.setProgress(0);
    }
    
    public void initMembers() {
        fwdBtn = (Button) mMain.findViewById(R.id.fwdBtn);
        backBtn = (Button) mMain.findViewById(R.id.backBtn);
        leftBtn = (Button) mMain.findViewById(R.id.leftBtn);
        rightBtn = (Button) mMain.findViewById(R.id.rightBtn);
        carHome = (Button) mMain.findViewById(R.id.carHomeBtn);
        camHome = (Button) mMain.findViewById(R.id.camHomeBtn);
        camUp = (Button) mMain.findViewById(R.id.camUpBtn);
        camDn = (Button) mMain.findViewById(R.id.camDwnBtn);
        camLeft = (Button) mMain.findViewById(R.id.camLeftBtn);
        camRight = (Button) mMain.findViewById(R.id.camRightBtn);
        wv = (WebView) mMain.findViewById(R.id.webView);
        speed = (SeekBar) mMain.findViewById(R.id.speedSeeker);
        speedTxt = (TextView)mMain.findViewById(R.id.speedTxt);
        speedTxt.setText(String.format("Speed: %d", 0));
    }

    public void setListeners() {
        fwdBtn.setOnTouchListener(ctrl_listener);
        backBtn.setOnTouchListener(ctrl_listener);
        leftBtn.setOnTouchListener(ctrl_listener);
        rightBtn.setOnTouchListener(ctrl_listener);
        carHome.setOnTouchListener(ctrl_listener);
        camHome.setOnTouchListener(ctrl_listener);
        camUp.setOnTouchListener(ctrl_listener);
        camDn.setOnTouchListener(ctrl_listener);
        camLeft.setOnTouchListener(ctrl_listener);
        camRight.setOnTouchListener(ctrl_listener);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speedTxt.setText(String.format("Speed: %d", i));
                iSpyCon.ctrl(R.id.speedSeeker, i, seekBar.getTag().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        wv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
    }

    public class CtrlListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int pressType = event.getAction();
            switch (v.getId()) {
                case R.id.fwdBtn:
                    if (iSpyCon.motorMove != 1 || pressType == MotionEvent.ACTION_UP) {
                        iSpyCon.motorMove = 1;
                        iSpyCon.ctrl(v.getId(), pressType, v.getTag().toString());
                    }
                    break;
                case R.id.backBtn:
                    if (iSpyCon.motorMove != -1 || pressType == MotionEvent.ACTION_UP) {
                        iSpyCon.motorMove = -1;
                        iSpyCon.ctrl(v.getId(), pressType, v.getTag().toString());
                    }
                    break;
                case R.id.leftBtn:
                    if (iSpyCon.tireMove != -1 ) {
                        iSpyCon.tireMove = -1;
                        iSpyCon.ctrl(v.getId(), pressType, v.getTag().toString());
                    }
                    break;
                case R.id.rightBtn:
                    if (iSpyCon.tireMove != 1 ) {
                        iSpyCon.tireMove = 1;
                        iSpyCon.ctrl(v.getId(), pressType, v.getTag().toString());
                    }
                    break;
                case R.id.carHomeBtn:
                case R.id.camHomeBtn:
                case R.id.camDwnBtn:
                case R.id.camLeftBtn:
                case R.id.camRightBtn:
                case R.id.camUpBtn:
                    if (!iSpyCon.camMove) {
                        iSpyCon.camMove = true;
                        iSpyCon.ctrl(v.getId(), pressType, v.getTag().toString());
                    }
                    break;
            }
            return true;
        }
    }
}