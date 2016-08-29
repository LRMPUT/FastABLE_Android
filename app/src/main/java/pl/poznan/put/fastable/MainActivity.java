package pl.poznan.put.fastable;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String TAG = "FastABLE";

    boolean isComputing = false;
    Handler mHandler;

//    int[] trainLengths;
    String[] trainImagesFiles;
//    int testLength;
    String[] testImagesFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Loading OpenCV libraries
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this,
                mLoaderCallback)) {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
        }

        // Handler for handling messages from computing thread
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                TextView statusTextView = (TextView)findViewById(R.id.textViewStatus);
                TextView oaTextView = (TextView)findViewById(R.id.textViewOpenABLE);
                TextView faTextView = (TextView)findViewById(R.id.textViewFastABLE);

                // Displaying computation times
                Bundle bundle = msg.getData();
                oaTextView.setText(String.format("OpenABLE processing time: %.2f ms.", bundle.getFloat("openABLE")));
                faTextView.setText(String.format("FastABLE processing time: %.2f ms.", bundle.getFloat("fastABLE")));

                statusTextView.setText("Computing done");
                isComputing = false;

                // Enable button
                Button btn = (Button) findViewById(R.id.button);
                btn.setEnabled(true);
            }
        };
    }

    // Callback called after OpenCV libraries are loaded
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    Toast.makeText(MainActivity.this,
                            "Loaded all libraries", Toast.LENGTH_LONG).show();

                    // Enable button
                    Button btn = (Button) findViewById(R.id.button);
                    btn.setEnabled(true);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    // Button click callback
    public void onButtonClick(View v){
        Log.d(TAG, "onButtonClick()");

        TextView statusTextView = (TextView)findViewById(R.id.textViewStatus);
        // if currently not computing
        if(!isComputing) {
            statusTextView.setText("Computing");

            // Disable button
            Button btn = (Button) findViewById(R.id.button);
            btn.setEnabled(false);

            // Runnable object for computing OpenABLe and FastABLE in a separate thread
            Runnable curRunnable = new Runnable(){
                public void run(){
                    //Perform computation
                    Log.d(TAG, "Starting computation");

                    // Config file
                    String configFile = String.format(Locale.getDefault(), Environment
                            .getExternalStorageDirectory().toString()
                            + "/FastABLE"
                            + "/config.txt");

                    // Train data

                    // Lengths of segments
                    int[] trainLengths;
                    {
                        String trainPath = String.format(Locale.getDefault(), Environment
                                .getExternalStorageDirectory().toString()
                                + "/FastABLE"
                                + "/train/");

                        // Get segment dirs inside training dir
                        File trainDir = new File(trainPath);
                        File trainDirsArr[] = trainDir.listFiles();
                        Log.d(TAG, "Train segments: " + trainDirsArr.length);

                        // Read segment dirs and put into ArrayList
                        ArrayList<String> trainImgsList = new ArrayList<String>();
                        ArrayList<Integer> trainLengthsList = new ArrayList<Integer>();
                        for (int d = 0; d < trainDirsArr.length; d++) {
                            if (trainDirsArr[d].isDirectory()) {
                                File curTrainFiles[] = trainDirsArr[d].listFiles();
                                Log.d(TAG, "Train dir:" + trainDirsArr[d].getName());
                                Log.d(TAG, "Train size:" + curTrainFiles.length);
                                trainLengthsList.add(curTrainFiles.length);
                                for (int i = 0; i < curTrainFiles.length; ++i) {
                                    trainImgsList.add(curTrainFiles[i].getAbsoluteFile().toString());
                                }
                            }
                        }
                        // Move data to arrays
                        trainLengths = new int[trainLengthsList.size()];
                        for (int i = 0; i < trainLengthsList.size(); ++i) {
                            trainLengths[i] = trainLengthsList.get(i);
                        }
                        trainImagesFiles = trainImgsList.toArray(new String[trainImgsList.size()]);
                    }

                    // Test data

                    // Length of test data
                    int testLength = 0;
                    {
                        String testPath = String.format(Locale.getDefault(), Environment
                                .getExternalStorageDirectory().toString()
                                + "/FastABLE"
                                + "/test/");

                        File testDir = new File(testPath);
                        File testFiles[] = testDir.listFiles();
                        Log.d(TAG, "Test size: " + testFiles.length);

                        testLength = testFiles.length;
                        testImagesFiles = new String[testFiles.length];
                        for (int i = 0; i < testFiles.length; ++i) {
                            testImagesFiles[i] = testFiles[i].getAbsoluteFile().toString();
                        }
                    }

                    // Run NDK function to compute results
                    float[] res = computeJni(configFile,
                                            trainImagesFiles,
                                            trainLengths,
                                            testImagesFiles,
                                            testLength);

                    // Send message to UI thread
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putFloat("openABLE", res[0]);
                    bundle.putFloat("fastABLE", res[1]);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);

                    Log.d(TAG, "Computation complete, message sent");
                }
            };

            // Start thread
            Thread curThread = new Thread(curRunnable);
            curThread.start();

            isComputing = true;
        }
    }

    static {
        System.loadLibrary("fastable-android-jni");
    }

    // NDK function for computing visual place recognition
    public native float[] computeJni(String configFile,
                                     String[] trainImgsArray,
                                     int[] trainLengths,
                                     String[] testImgsArray,
                                     int testLength);
}
