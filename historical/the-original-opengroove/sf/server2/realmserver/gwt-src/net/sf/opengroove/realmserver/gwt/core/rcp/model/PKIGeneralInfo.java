package net.sf.opengroove.realmserver.gwt.core.rcp.model;

import java.io.Serializable;

public class PKIGeneralInfo implements Serializable
{
    private String certFingerprint;
    private boolean hasSignedCert;
    private boolean isCertValidDate;
    private String certExpiresOn;
    
    public PKIGeneralInfo(String certFingerprint,
        boolean hasSignedCert, boolean isCertValidDate,
        String certExpiresOn)
    {
        super();
        this.certFingerprint = certFingerprint;
        this.hasSignedCert = hasSignedCert;
        this.isCertValidDate = isCertValidDate;
        this.certExpiresOn = certExpiresOn;
    }
    
    public PKIGeneralInfo()
    {
        super();
    }
    
    public String getCertFingerprint()
    {
        return certFingerprint;
    }
    
    public boolean isHasSignedCert()
    {
        return hasSignedCert;
    }
    
    public boolean isCertValidDate()
    {
        return isCertValidDate;
    }
    
    public String getCertExpiresOn()
    {
        return certExpiresOn;
    }
    
    public void setCertFingerprint(String certFingerprint)
    {
        this.certFingerprint = certFingerprint;
    }
    
    public void setHasSignedCert(boolean hasSignedCert)
    {
        this.hasSignedCert = hasSignedCert;
    }
    
    public void setCertValidDate(boolean isCertValidDate)
    {
        this.isCertValidDate = isCertValidDate;
    }
    
    public void setCertExpiresOn(String certExpiresOn)
    {
        this.certExpiresOn = certExpiresOn;
    }
}
