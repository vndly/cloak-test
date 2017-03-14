package com.mauriciotogneri.cloaktest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.mauriciotogneri.cloack.Cloak;
import com.mauriciotogneri.cloack.CloakKey;

import java.util.Arrays;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        findViewById(R.id.start).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                start();
            }
        });
    }

    private void start()
    {
        Cloak cloak = new Cloak(this);

        if (cloak.isAvailable())
        {
            try
            {
                runTest1(cloak);
                runTest2(cloak);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this, "CLOAK NOT AVAILABLE", Toast.LENGTH_SHORT).show();
        }
    }

    private void runTest1(Cloak cloak) throws Exception
    {
        cloak.reset();

        byte[] sampleText1 = new byte[] {1, 2, 3};
        byte[] encrypted1 = cloak.encrypt(sampleText1);
        byte[] decrypted1 = cloak.decrypt(encrypted1);
        log("TEST 1: " + Arrays.equals(sampleText1, decrypted1));

        String sampleText2 = "This is a sample text!";
        String encrypted2 = cloak.encrypt(sampleText2);
        String decrypted2 = cloak.decrypt(encrypted2);
        log("TEST 2: " + sampleText2.equals(decrypted2));
    }

    private void runTest2(Cloak cloak) throws Exception
    {
        CloakKey key = cloak.key();

        for (int i = 0; i < 1024; i++)
        {
            byte[] array = generateArray(i);
            log("TEST " + i + ": " + Arrays.toString(array));

            byte[] encrypted1 = cloak.encrypt(array, key);
            byte[] decrypted1 = cloak.decrypt(encrypted1, key);
            boolean testA = Arrays.equals(array, decrypted1);

            if (!testA)
            {
                throw new RuntimeException();
            }

            log("TEST " + i + "A: OK");

            String sampleText2 = new String(array, "UTF-8");
            String encrypted2 = cloak.encrypt(sampleText2, key);
            String decrypted2 = cloak.decrypt(encrypted2, key);

            boolean testB = sampleText2.equals(decrypted2);

            if (!testB)
            {
                throw new RuntimeException();
            }

            log("TEST " + i + "B: OK");
        }
    }

    private byte[] generateArray(int length)
    {
        byte[] result = new byte[length];

        for (int i = 0; i < length; i++)
        {
            result[i] = (byte) (i - 128);
        }

        return result;
    }

    private void log(String text)
    {
        Log.d("CLOAK", text);
    }
}