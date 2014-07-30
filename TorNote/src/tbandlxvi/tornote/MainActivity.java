package tbandlxvi.tornote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tbandlxvi.tornote.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends Activity 
{
  private final static String PROGRAM_NAME = "TorNote";

  private final static String filename = "tornote_data.txt";

  private final static String lineSep = System.getProperty("line.separator");
  
  private String lastReadTime = "";
  
  EditText et = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    et = (EditText) findViewById(R.id.editText);
    
    loadOldContents();
  }
  
  @Override
  protected void onPause()
  {
    storeCurrentContents();
    super.onPause();
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    loadOldContents();
  }
   
  @Override
  public boolean onCreateOptionsMenu(Menu menu) 
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) 
  {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.action_timestamp:
        insertTimeStamp();
        return true;
      case R.id.action_save:
        storeCurrentContents();
        return true;
      case R.id.action_help:
        showHelp();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void loadOldContents()
  {
    SimpleDateFormat sdfYmdHmsLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long cal = Calendar.getInstance().getTime().getTime();
    Date date1 = new Date(cal);
    lastReadTime = sdfYmdHmsLong.format(date1);
    
    FileInputStream fis = null;
    try
    {
      fis = openFileInput(filename);
    }
    catch (IOException e)
    {
      // File did not exist - thats OK.
      return;
    }
    
    InputStreamReader inputStreamReader = new InputStreamReader(fis);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    StringBuilder sb = new StringBuilder();
    String line;
    
    try
    {
      while ((line = bufferedReader.readLine()) != null) 
      {
        if (sb.length() > 0)
        {
          sb.append(lineSep);
        }
        sb.append(line);
      }
    }
    catch (IOException e)
    {
      // Unexpected error.
      showMessageBox("Error reading file", e.toString());

      try 
      {
        if (bufferedReader != null)
          bufferedReader.close();
        if (fis != null)
          fis.close();
      }
      catch (IOException e2)
      {
        showMessageBox("Error close file", e.toString());
      }

      return;
    }
    
    try 
    {
      if (bufferedReader != null)
        bufferedReader.close();
      if (fis != null)
        fis.close();
    }
    catch (IOException e2)
    {
      showMessageBox("Error closing file", e2.toString());
    }
    
    et.setText(sb);
  }
  
  private void storeCurrentContents()
  {
    FileOutputStream fos = null;
    try
    {
      fos = openFileOutput(filename, MODE_PRIVATE);
    }
    catch (IOException e)
    {
      showMessageBox("Error opening file", e.toString());
      return;
    }    
    
    
    OutputStreamWriter osw = new OutputStreamWriter(fos);
    BufferedWriter writer = new BufferedWriter(osw);
    try
    {
      writer.write(et.getText().toString());
    }
    catch (IOException e)
    {
      showMessageBox("Error write file", e.toString());

      try 
      {
        if (writer != null)
          writer.close();
        if (fos != null)
          fos.close();
      }
      catch (IOException e2)
      {
        showMessageBox("Error close file", e2.toString());
      }  
      
      return;      
    }

    try 
    {
      if (writer != null)
        writer.close();
      if (fos != null)
        fos.close();
    }
    catch (IOException e2)
    {
      showMessageBox("Error close file", e2.toString());
    }    
  }
  
  private void insertTimeStamp()
  {
    SimpleDateFormat sdfYmdHmsLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    long cal = Calendar.getInstance().getTime().getTime();
    Date date1 = new Date(cal);
    String ts = sdfYmdHmsLong.format(date1);
    
    int p1 = et.getSelectionStart();
    int p2 = et.getSelectionEnd();
    et.getText().replace(Math.min(p1, p2), Math.max(p1, p2), ts, 0, ts.length());
  }
 
  private void showHelp()
  {
    int i = 0;
    
    i = et.getText().toString().length();
    
    StringBuilder msg = new StringBuilder();
    
    try
    {
      msg.append(String.format("%s v %s (versionCode %d)", PROGRAM_NAME,
          getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
          getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
    }
    catch (Exception e) 
    {
      showMessageBox("Error", "Failed retrieving version. " + e.toString());
      e.printStackTrace();
    }    
    
    msg.append(lineSep);
    msg.append(lineSep);
    
    msg.append(PROGRAM_NAME + " is a minimalistic and simplistic note taking app.\n" +
        "It fires up immediately - no waiting for some web connection.\n" + 
        "It is safe - requires no permissions at all.\n" + 
        "The notes are saved automatically when leaving the app. If, for some reason, you want " +
        "to save during writing - use the Save menu option.\n\n" +
        String.format("Number of chars: %d\n" +
        "Data read from storage: %s" +
        "", i, lastReadTime));
    
    showMessageBox("Help", msg.toString());
  }
  
  protected void showMessageBox(String title, String msg)
  {
    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
    
    dlgAlert.setMessage(msg);
    dlgAlert.setTitle(title);
    dlgAlert.setPositiveButton("OK", null);
    dlgAlert.setCancelable(true);
    dlgAlert.create().show();     
  }
  
}
