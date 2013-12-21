package jw.bznetwork.client.data;

import java.io.Serializable;

import org.apache.commons.fileupload.ProgressListener;

public class UploadStatus implements Serializable
{
    public int getProgress()
    {
        return progress;
    }
    
    public void setProgress(int progress)
    {
        this.progress = progress;
    }
    
    public int getTotal()
    {
        return total;
    }
    
    public void setTotal(int total)
    {
        this.total = total;
    }
    
    public boolean isFinished()
    {
        return finished;
    }
    
    public void setFinished(boolean finished)
    {
        this.finished = finished;
    }
    
    private int progress;
    private int total;
    private boolean finished;
    private boolean failed;
    
    public boolean isFailed()
    {
        return failed;
    }
    
    public void setFailed(boolean failed)
    {
        this.failed = failed;
    }
    
    public UploadStatus()
    {
    }
    
}
