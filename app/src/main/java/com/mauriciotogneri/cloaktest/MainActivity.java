package com.mauriciotogneri.cloaktest;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.mauriciotogneri.cloack.Cloak;
import com.mauriciotogneri.cloack.CloakKey;
import com.mauriciotogneri.cloack.EncryptedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
                runTest3();
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

    private void runTest3() throws Exception
    {
        String stringValue = "Peter";
        int intValue = 123;
        float floatValue = 123.456f;
        long longValue = 1234567;
        boolean booleanValue = (System.currentTimeMillis() % 2 == 0);
        Set<String> setValue = new HashSet<>();
        setValue.add("A");
        setValue.add("B");
        setValue.add("C");

        EncryptedPreferences preferences = new EncryptedPreferences(this, "my_preferences", true);
        Editor editor = preferences.edit();
        editor.clear();
        editor.putString("stringValue", stringValue);
        editor.putInt("intValue", intValue);
        editor.putFloat("floatValue", floatValue);
        editor.putLong("longValue", longValue);
        editor.putBoolean("booleanValue", booleanValue);
        editor.putStringSet("setValue", setValue);
        editor.commit();

        String loadedStringValue = preferences.getString("stringValue", null);
        if (stringValue.equals(loadedStringValue))
        {
            Log.d("TEST", loadedStringValue);
        }
        else
        {
            throw new RuntimeException();
        }

        int loadedInt = preferences.getInt("intValue", 0);
        if (intValue == loadedInt)
        {
            Log.d("TEST", String.valueOf(loadedInt));
        }
        else
        {
            throw new RuntimeException();
        }

        float loadedFloat = preferences.getFloat("floatValue", 0);
        if (floatValue == loadedFloat)
        {
            Log.d("TEST", String.valueOf(loadedFloat));
        }
        else
        {
            throw new RuntimeException();
        }

        long loadedLong = preferences.getLong("longValue", 0);
        if (longValue == loadedLong)
        {
            Log.d("TEST", String.valueOf(loadedLong));
        }
        else
        {
            throw new RuntimeException();
        }

        boolean loadedBoolean = preferences.getBoolean("booleanValue", false);
        if (booleanValue == loadedBoolean)
        {
            Log.d("TEST", String.valueOf(loadedBoolean));
        }
        else
        {
            throw new RuntimeException();
        }

        Set<String> loadedSet = preferences.getStringSet("setValue", null);
        if (setValue.equals(loadedSet))
        {
            Log.d("TEST", Arrays.toString(loadedSet.toArray()));
        }
        else
        {
            throw new RuntimeException();
        }

        File file = new File("/data/data/com.mauriciotogneri.cloaktest/shared_prefs/my_preferences.xml");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;

        while ((st = br.readLine()) != null)
        {
            Log.d("TEST", st);
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