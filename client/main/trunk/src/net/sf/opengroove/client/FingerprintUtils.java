package net.sf.opengroove.client;

import net.sf.opengroove.client.storage.Contact;
import net.sf.opengroove.client.storage.LocalUser;
import net.sf.opengroove.common.security.UserFingerprint;

/**
 * Contains methods that extract security keys from various OpenGroove classes
 * and then delegate to {@link UserFingerprint} for the actual fingerprint
 * generation.
 * 
 * @author Alexander Boyd
 * 
 */
public class FingerprintUtils
{
    public String fingerprint(Contact contact)
    {
        if (!contact.isHasKeys())
            return null;
        return UserFingerprint.fingerprint(contact
            .getRsaEncPub(), contact.getRsaEncMod(),
            contact.getRsaSigPub(), contact.getRsaSigMod());
    }
    
    public String fingerprint(LocalUser user)
    {
        return UserFingerprint.fingerprint(user
            .getRsaEncPub(), user.getRasEncMod(), user
            .getRsaSigPub(), user.getRsaSigMod());
    }
}
