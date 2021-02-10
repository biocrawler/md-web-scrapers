package org.perpetualnetworks.mdcrawlerconsumer.utils.lzw;

/**
 * Specifies that implementing classes support checksum computation.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public interface Checksum {

    /**
     * Computes the checksum for the specified byte array.
     *
     * @param data the byte array for which to compute the checksum
     * @return the checksum for the specified byte array
     */
    public byte[] compute(byte[] data);

}
