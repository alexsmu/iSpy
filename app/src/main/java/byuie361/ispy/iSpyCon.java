package byuie361.ispy;

import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class iSpyCon {
    private static final String host = "192.168.2.4";
    private static final int CTRLPORT = 21567;
    private static final int CMDPORT = 21500;
    private static final int CALPORT = 21547;
    public static Socket cmd_server = null;
    public static Socket ctrl_server = null;
    public static Socket cal_server = null;
    public static PrintWriter cmd_sender = null;
    public static PrintWriter ctrl_sender = null;
    public static PrintWriter cal_sender = null;
    public static BufferedReader cmd_result;
    public static String out = "uninitialized";
    public static final Semaphore cmd_connection = new Semaphore(1, true);
    public static final Semaphore ctrl_connection = new Semaphore(1, true);
    public static final Semaphore cal_connection = new Semaphore(1, true);
    public static boolean camMove = false;
    public static int tireMove = 0;
    public static int motorMove = 0;

    public static void onDestroy() {
        try {
            cmd_connection.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (cmd_server != null)
                cmd_server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (ctrl_server != null)
                ctrl_server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (cal_server != null)
                cal_server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int cmd(final String command) {
            new Thread(new Runnable() {
                @Override
                public synchronized void run() {
                    try {
                    cmd_connection.acquire();
                    out = send_cmd(command);
                    cmd_connection.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        return 0;
    }

    public static int cmd( final int update, String... cmnds ) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... cmds) {
                try {
                    cmd_connection.acquire();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                out = "";
                for (String command : cmds) {
                    out = send_cmd(command);
                   // Escape early if cancel() is called
                    if (isCancelled()) break;
                }
                cmd_connection.release();
                return out;
            }

            @Override
            protected void onPreExecute() { if (update == 1) MainActivity.title_red(); }

            @Override
            protected void onProgressUpdate(Void... values) {}

            @Override
            protected void onPostExecute(String result) {
                if (update == 1) MainActivity.title_green();
                else if (update ==3)
                    MainActivity.calSaved();
            }
        }.execute(cmnds);
        return 0;
    }

    public static void ctrl(final int id, int act, String tag) {
        new Thread(new iSpyClient(id, act, tag)).start();
    }

    public static void cal(String val) {
        new Thread(new iSpyCali(val)).start();
    }

    public static String send_cmd(String cmd) {
        String result;
        String line;
        if (cmd_server == null || cmd_server.isClosed()) {
            try {
                cmd_server = new Socket(host, CMDPORT);
                cmd_sender = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(cmd_server.getOutputStream())), true);
                cmd_result = new BufferedReader(new InputStreamReader(cmd_server.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
                return "no connection";
            }
        }
        cmd_sender.print(cmd);
        cmd_sender.flush();
        result = "";
        try {
            while (true) {
                line = cmd_result.readLine();
                if (line.equals(String.format("%c", 4)))
                    break;
                if (line.length() > 0)
                    result += line;
                else
                    result += "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            cmd_server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("-------RESULT------",result);
        return result;
    }

    public static class iSpyClient implements Runnable {
        public int id, action;
        public String cntrl;

        iSpyClient(int i, int act, String tag) {
            id = i;
            action = act;
            cntrl = tag;
        }

        @Override
        public void run() {
            try {
                ctrl_connection.acquire();
                ctrl_server = new Socket(host, CTRLPORT);
                ctrl_sender = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(ctrl_server.getOutputStream())), true);

                switch (id) {
                    case R.id.fwdBtn:
                    case R.id.backBtn:
                        if (action != MotionEvent.ACTION_UP) {
                            ctrl_sender.print(cntrl);
                            ctrl_sender.flush();
                            Thread.sleep(35);
                        }
                        else
                        {
                            ctrl_sender.print("stop");
                            ctrl_sender.flush();
                            Thread.sleep(35);
                            motorMove = 0;
                        }
                        break;
                    case R.id.leftBtn:
                    case R.id.rightBtn:
                        if (action != MotionEvent.ACTION_UP) {
                            ctrl_sender.print(cntrl);
                            ctrl_sender.flush();
                        }
                        Thread.sleep(35);
                        tireMove = 0;
                        break;
                    case R.id.carHomeBtn:
                    case R.id.camHomeBtn:
                    case R.id.camDwnBtn:
                    case R.id.camLeftBtn:
                    case R.id.camRightBtn:
                    case R.id.camUpBtn:
                        if (action != MotionEvent.ACTION_UP) {
                            ctrl_sender.print(cntrl);
                            ctrl_sender.flush();
                        }
                        Thread.sleep(35);
                        camMove = false;
                        break;
                    case R.id.speedSeeker:
                        ctrl_sender.print(String.format("speed%d", action));
                        ctrl_sender.flush();
                        Thread.sleep(35);
                        break;
                }
                ctrl_server.close();
                ctrl_connection.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class iSpyCali implements Runnable {
        public String cal;
        iSpyCali(String val) {
            cal = val;
        }

        @Override
        public void run() {
            try {
                cal_connection.acquire();
                cal_server = new Socket(host, CALPORT);
                cal_sender = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(cal_server.getOutputStream())), true);
                cal_sender.print(cal);
                cal_sender.flush();
                cal_server.close();
                Thread.sleep(50);
                cal_connection.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

